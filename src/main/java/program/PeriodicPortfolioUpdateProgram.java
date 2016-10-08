package program;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import core.ExecutionSummary;
import core.Stock;
import core.StockBuilder;
import core.StockSorter;
import core.TradeSummary;
import file.FileNameGenerator;

public class PeriodicPortfolioUpdateProgram extends AbstractProgram {

	private String[] args;

	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	public static void main(String[] args) {
		PeriodicPortfolioUpdateProgram main = new PeriodicPortfolioUpdateProgram();
		main.setArgs(args);
		main.startExecute(args);
	}

	public long getTimerInterval() {
		return getIntegerValue(this.args, "interval") * 60 * 1000;
	}

	public void execute(final boolean force, String args[]) {
		GregorianCalendar currentTime = new GregorianCalendar();
		System.out.println("Pass started at " + currentTime.getTime());

		if (force) {
			doWork(force, args);
			return;
		}
		/*
		 * if(!InternetService.up()){ System.err.println("Internet is not Up.");
		 * return; }
		 */
		if (currentTime.get(Calendar.HOUR_OF_DAY) > 17 && currentTime.get(Calendar.HOUR_OF_DAY) < 20) {
			doWork(force, args);
		}

		if (currentTime.get(Calendar.HOUR_OF_DAY) > 17 || currentTime.get(Calendar.HOUR_OF_DAY) < 2) {
			System.out.println("Since time crossed 5 PM Markets would have been closed. Hence not running this time");
			// return;
		}
		if (currentTime.get(Calendar.DAY_OF_WEEK) == 7 || currentTime.get(Calendar.DAY_OF_WEEK) == 1) {
			System.out.println("Won't Run on Sat or Sun");
			// return;
		}
		doWork(force, args);
	}

	private void doWork(final boolean force, String args[]) {
		long start = System.currentTimeMillis();
		boolean fullReport = getBooleanValue(args, "fullreport");
		int maxRetries = getIntegerValue(args, "maxretry");
		String specificStock = getValue(args, "specificstock", "None");
		StockBuilder builder = new StockBuilder();
		builder.setMode(getValue(args, "mode", "remote"));
		builder.setInputFile(getValue(args, "filepath", ""));
		builder.setFullReport(fullReport);
		builder.setWorkBook();

		double totalProfit = 0;
		Map<String, Stock> stocks = builder.read(specificStock);
		builder.updateStocks(stocks, maxRetries);
		List<Stock> stockList = new ArrayList<Stock>();
		Iterator<Stock> values = stocks.values().iterator();
		while (values.hasNext()) {
			Stock stock = values.next();
			if (stock.getTotalQuantity() > 0 || fullReport) {
				stockList.add(stock);
			}
			totalProfit += stock.getProfitRealised();
		}

		TradeSummary tradeSummary = builder.getTradeSummary();
		tradeSummary.setTotalProfit(totalProfit);
		prepareReport(stockList, tradeSummary, prepareExecutionSummary(tradeSummary, start),
				getBooleanValue(args, "sendmail"));
	}

	private ExecutionSummary prepareExecutionSummary(TradeSummary tradeSummary, long start) {
		ExecutionSummary executionSummary = new ExecutionSummary();
		GregorianCalendar currentTime = new GregorianCalendar();
		executionSummary.setExecutionTime(currentTime.getTime());
		currentTime.add(Calendar.MINUTE, getIntegerValue(this.args, "interval"));
		executionSummary.setNextExecutionTime(currentTime.getTime());
		long elapsed = System.currentTimeMillis() - start;
		executionSummary.setTimeToExecute(elapsed);
		return executionSummary;
	}

	private void prepareReport(List<Stock> stockList, TradeSummary tradeSummary, ExecutionSummary executionSummary,
			boolean sendmail) {
		Collections.sort(stockList, new StockSorter());
		String df = new SimpleDateFormat("yyyy-MM-dd-hh-mm").format(new Date());
		String generatedFileName = FileNameGenerator.getTmpDir() + "PeriodicReport-" + df + ".pdf";
		try {
			OutputStream file = new FileOutputStream(generatedFileName);
			Document document = new Document();
			PdfWriter.getInstance(document, file);
			document.open();
			appendToDocument(document, stockList,tradeSummary);
			appendToDocument(document,tradeSummary);
			document.close();
			file.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(generatedFileName);
	}
	
	private void appendToDocument(Document document,TradeSummary tradeSummary) throws DocumentException{
		NumberFormat format = new DecimalFormat("#,###,###,##0.00");
		NumberFormat xformat = new DecimalFormat("#,###,###,##0");
		document.add(new Paragraph("Total Turn Over : " + format.format(tradeSummary.getTotalTurnOver())));
		document.add(new Paragraph("Total Buys : " + xformat.format(tradeSummary.getNoOfBuyTrades())));
		document.add(new Paragraph("Total Sells : " + xformat.format(tradeSummary.getNoOfSellTrades())));
		
	}
	
	private void appendToDocument(Document document,List<Stock> stocks,TradeSummary tradeSummary) throws DocumentException{
		PdfPTable table = new PdfPTable(10);
		String headers[] = new String[]{"Name","#","Avg","Mkt","Ivt","Cur","Gain","Realized Profit","Look","B"};
		table.setWidths(new float[]{12f,7f,12f,12f,13f,13f,13f,13f,7f,3f});
		addHeaders(table,headers);
		NumberFormat format = new DecimalFormat("#,###,###,##0.00");
		NumberFormat xformat = new DecimalFormat("#,###,###,##0");
		double tp = 0,ti = 0,tm = 0,ip = 0;
		for(Stock stock : stocks){
			tp += stock.getProfitRealised();
			ti += (stock.getAverage() * stock.getTotalQuantity());
			tm += (stock.getCurrentPrice() * stock.getTotalQuantity());
			ip += ((stock.getCurrentPrice() - stock.getAverage()) * stock.getTotalQuantity());
		}
		
		document.add(new Paragraph("Total Profit : " + format.format(tradeSummary.getTotalProfit())));
		document.add(new Paragraph("Total Investment : " + format.format(ti)));
		document.add(new Paragraph("Total Market Value : " + format.format(tm)));
		document.add(new Paragraph("Imaginary Profit : " + format.format(ip)));
		
		document.add(new Paragraph("\n\n"));
		
		for(Stock stock : stocks){
			List<Object> row = new ArrayList<Object>();
			row.add(stock.getName());
			row.add(stock.getTotalQuantity());
			row.add(format.format(stock.getAverage()));
			row.add(format.format(stock.getCurrentPrice()));
			row.add(xformat.format(stock.getTotalQuantity()*stock.getAverage()));
			row.add(xformat.format(stock.getTotalQuantity()*stock.getCurrentPrice()));
			row.add(format.format(stock.getTotalQuantity() * (stock.getCurrentPrice() - stock.getAverage())));
			row.add(format.format(stock.getProfitRealised()));
			row.add(stock.getLookOutTrades());
			row.add(""+stock.getBroker().charAt(0));
			addRow(row, table,stock.getAverage() > stock.getCurrentPrice() ? BaseColor.RED : BaseColor.ORANGE);
		}
		
		document.add(table);
	}
	
	protected void addRow(List<Object> row,PdfPTable table,BaseColor bcolor){
		for(Object z : row){
			PdfPCell cell = null;
			if(z instanceof java.lang.Double){
				String x = new DecimalFormat("#,###,###,##0.00").format(Double.parseDouble(z.toString()));
				cell = new PdfPCell(new Paragraph(x));
			}
			if(z instanceof String){
				cell = new PdfPCell(new Paragraph(z.toString()));
			}
			if(z instanceof Integer || z instanceof Long){
				cell = new PdfPCell(new Paragraph(z.toString()));
			}
			if(z instanceof java.lang.Number){
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			}
			cell.setBackgroundColor(bcolor);
			table.addCell(cell);
		}
	}

}
