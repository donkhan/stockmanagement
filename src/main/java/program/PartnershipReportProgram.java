package program;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import core.Dividend;
import core.ProfitTransaction;
import core.StakeHolder;
import core.StakeHolderTransaction;
import core.Stock;
import core.StockBuilder;
import core.Trade;
import file.FileNameGenerator;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import profit.ProfitCalendarInterface;
import program.profitcalculation.DailyProfitCalculationProgram;


public class PartnershipReportProgram extends AbstractProgram{

	public static void main(String args[]){
		PartnershipReportProgram cp = new PartnershipReportProgram();
		cp.startExecute(args);
	}

	@Override
	protected long getTimerInterval() {
		return -1;
	}

	private Collection<StakeHolder> stakeHolders = new ArrayList<StakeHolder>();
	
	double totalInvestment = 0;
	private void findStakeHoldersShare(){
		for(StakeHolder stakeHolder : stakeHolders){
			totalInvestment += stakeHolder.getTotalInvestment();
		}
		for(StakeHolder stakeHolder : stakeHolders){
			stakeHolder.setSharePercentage(stakeHolder.getTotalInvestment()/totalInvestment);
		}
	}
	
	
	@Override
	protected void execute(boolean force, String[] args) {
		StockListingProgram sp = new StockListingProgram();
		Double totalProfit = 0d;
		StockBuilder builder = new StockBuilder();
		List<Stock> stocks = sp.build(builder, totalProfit, false, 12, "None", "");
		Workbook wb = builder.getWorkBook();
		double cash = findCash(wb);
		TradeListingProgram tp = new TradeListingProgram();
		DividendListingProgram dlp = new DividendListingProgram();
		
		List<Trade> trades = tp.build(getValue(args,"filepath",""));
		List<Dividend> dividends = dlp.build(getValue(args,"filepath",""));
		double totalDividend = dlp.getTotalDividend(dividends);
		
		
		
		DailyProfitCalculationProgram dpp = new DailyProfitCalculationProgram();
		List<ProfitCalendarInterface> profitCalendarList = dpp.process(dpp.buildMap(new String[]{}));
		readShareHolding(wb);
		findStakeHoldersShare();	
		
		for(StakeHolder stakeHolder : stakeHolders){
			try {
				String fileName = FileNameGenerator.getTmpDir() + stakeHolder.getName() + "-PartnerShipReport.pdf";
				System.out.println(fileName);
				OutputStream file = new FileOutputStream(fileName);
				Document document = new Document();
				PdfWriter.getInstance(document, file);
				document.open();
				addHeader(document,stakeHolder.getName());
				addProfitDetails(document,trades,stakeHolder,totalDividend);
				addInvestmentSummary(document,stocks,stakeHolder,cash);
				dpp.appendToDocument(document, profitCalendarList);
				dlp.appendToDocument(document, dividends);
				tp.appendToDocument(document, trades);
				sp.appendToDocument(document, stocks);
				addFooter(document);
				document.close();
				file.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private Map<String,ProfitTransaction> profitTransactionReferences = new HashMap<String,ProfitTransaction>();
	private void readShareHolding(Workbook wb) {
		Sheet sheet = wb.getSheet(2);
		int rows = sheet.getRows();
		Map<String,StakeHolder> map = new HashMap<String,StakeHolder>();
		
		for(int i = 1;i<rows;i++){
			Cell cell = sheet.getCell(0,i);
			String name = cell.getContents();
			StakeHolder sh = map.get(name);
			if(sh == null){
				sh = new StakeHolder(name);
				map.put(name, sh);
			}
			StakeHolderTransaction sht = new StakeHolderTransaction();
			cell = sheet.getCell(1,i);
			String date = cell.getContents();
			if(date == null || "".equals(date)){
				break;
			}
			StringTokenizer tokenizer = new StringTokenizer(date,"/");
			Calendar gc = new GregorianCalendar();
			gc.set(Calendar.MONTH, Integer.parseInt(tokenizer.nextToken())-1);
			gc.set(Calendar.DATE, Integer.parseInt(tokenizer.nextToken()));
			gc.set(Calendar.YEAR, Integer.parseInt(tokenizer.nextToken()));
			sht.setTransactionDate(gc);
			sht.setAmount(Double.parseDouble(sheet.getCell(2, i).getContents()));
			sh.add(sht);
		}
		
		Calendar today = new GregorianCalendar();
		sheet = wb.getSheet(4);
		rows = sheet.getRows();
		
		for(int i = 0;i<rows;i++){
			Cell cell = sheet.getCell(1,i);
			String name = cell.getContents();
			cell = sheet.getCell(0,i);
			String date = cell.getContents();
			StringTokenizer tokenizer = new StringTokenizer(date,"/");
			Calendar gc = new GregorianCalendar();
			gc.set(Calendar.MONTH, Integer.parseInt(tokenizer.nextToken())-1);
			gc.set(Calendar.DATE, Integer.parseInt(tokenizer.nextToken()));
			gc.set(Calendar.YEAR, Integer.parseInt(tokenizer.nextToken()));
			if(today.get(Calendar.MONTH) == gc.get(Calendar.MONTH) && today.get(Calendar.YEAR) == gc.get(Calendar.YEAR)){
				ProfitTransaction pt = new ProfitTransaction();
				pt.setDate(gc);
				pt.setReferenceId(sheet.getCell(2,i).getContents());
				profitTransactionReferences.put(name,pt);
			}
		}
		stakeHolders = map.values();
	}

	private double findCash(Workbook wb) {
		Sheet sheet = wb.getSheet(3);
		int rows = sheet.getRows();
		double cash = 0;
		for(int i = 0;i<rows;i++){
			Cell cell = sheet.getCell(2,i);
			cash += Double.parseDouble(cell.getContents());
		}
		System.out.println("Cash " + cash);
		return cash;
	}

	private void addInvestmentSummary(Document document, List<Stock> stocks,StakeHolder stakeHolder,double cash) throws DocumentException {
		document.add(new Paragraph("\n"));
		double totalMarketValue = 0d;
		for(Stock s : stocks){
			totalMarketValue += s.getTotalQuantity() * s.getCurrentPrice();
		}
		
		PdfPTable investmentTable = new PdfPTable(3);
		String percentage = new DecimalFormat("#,###,###,##0.00").format(stakeHolder.getSharePercentage()*100);
		addHeaders(investmentTable,new String[]{" ","Company","Yours " + percentage + "%"});
		
		List<Object> row = new ArrayList<Object>();
		row.add("Total Investment");
		row.add(totalInvestment);
		row.add(totalInvestment*stakeHolder.getSharePercentage());
		addRow(row,investmentTable);
		
		row = new ArrayList<Object>();
		row.add("Cash");
		row.add(cash);
		row.add(cash*stakeHolder.getSharePercentage());
		addRow(row,investmentTable);
		
		double investedAmount = totalInvestment - cash;
		
		row = new ArrayList<Object>();
		row.add("Invested Amount");
		row.add(investedAmount);
		row.add(investedAmount*stakeHolder.getSharePercentage());
		addRow(row,investmentTable);
		
		row = new ArrayList<Object>();
		row.add("Total Market Value");
		row.add(totalMarketValue);
		row.add(totalMarketValue*stakeHolder.getSharePercentage());
		addRow(row,investmentTable);
		
		double unr =  totalMarketValue - investedAmount;
		row = new ArrayList<Object>();
		row.add("UnRealized Loss/Gain");
		row.add(unr);
		row.add(unr*stakeHolder.getSharePercentage());
		addRow(row,investmentTable);
		
		document.add(investmentTable);
	}

	private void addProfitDetails(
			Document document, 
			List<Trade> trades, 
			StakeHolder stakeHolder,double totalDividend) throws DocumentException{
		document.add(new Paragraph("\n"));
		double totalProfit = 0d;
		for(Trade t: trades){
			totalProfit += t.getProfit();
		}
		totalProfit += totalDividend;
		double shortTermCapitalGainTax = totalProfit * 10 /100;
		double companyShare = totalProfit * 15 /100;
		double netEquity = totalProfit - shortTermCapitalGainTax - companyShare;
		
		double share = netEquity * stakeHolder.getSharePercentage();
				
		
		PdfPTable profitTable = new PdfPTable(2);
		List<Object> row = new ArrayList<Object>();
		row.add("Profit Made");
		row.add(totalProfit);
		addRow(row,profitTable);
		
		row = new ArrayList<Object>();
		row.add("Short Term Capital Gain Tax 10%");
		row.add(shortTermCapitalGainTax);
		addRow(row,profitTable);
		
		row = new ArrayList<Object>();
		row.add("Company Share 15%");
		row.add(companyShare);
		addRow(row,profitTable);
		
		row = new ArrayList<Object>();
		row.add("Net Profit");
		row.add(netEquity);
		addRow(row,profitTable);
		
		row = new ArrayList<Object>();
		String percentage = new DecimalFormat("#,###,###,##0.00").format(stakeHolder.getSharePercentage()*100);
		row.add("Your Share @ " + percentage + "%");
		row.add(share);
		addRow(row,profitTable);
		
		row = new ArrayList<Object>();
		row.add("Credited On");
		ProfitTransaction pt = profitTransactionReferences.get(stakeHolder.getName());
		if(pt == null){
			row.add(" ");
		}else{
			row.add(pt.getDate().getTime().toString());
			
		}
		addRow(row,profitTable);
		
		row = new ArrayList<Object>();
		row.add("Transaction Reference No");
		if(pt == null){
			row.add(" ");
		}else{
			row.add(pt.getReferenceId());
		}
		
		addRow(row,profitTable);
		document.add(profitTable);
	}

	

	private void addHeader(Document document, String stakeHolder)  throws DocumentException{
		GregorianCalendar gc = new GregorianCalendar();
		String month = (gc.get(Calendar.MONTH) + 1) + "-" + gc.get(Calendar.YEAR); 
		Font font = new Font(FontFamily.TIMES_ROMAN, 10, Font.BOLD, new BaseColor(0, 0, 0)); 
		Paragraph header = new Paragraph("Dear " + stakeHolder + "\n\nKindly go through the Monthly Report for " + month);
		header.setFont(font);
		document.add(header);
	}

	private void addFooter(Document document)  throws DocumentException{
		Font font = new Font(FontFamily.TIMES_ROMAN, 10, Font.BOLD, new BaseColor(0, 0, 0)); 
		Paragraph footer = new Paragraph("\n\n\n                                                             Thank you");
		footer.setFont(font);
		document.add(footer);
	}
}
