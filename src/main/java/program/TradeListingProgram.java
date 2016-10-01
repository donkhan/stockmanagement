package program;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import core.Stock;
import core.StockBuilder;
import core.Trade;
import file.FileNameGenerator;


public class TradeListingProgram extends AbstractProgram{

	public static void main(String args[]){
		TradeListingProgram cp = new TradeListingProgram();
		cp.startExecute(args);
	}
	
	private GregorianCalendar begin = new GregorianCalendar();
	private GregorianCalendar end  = new GregorianCalendar();
	
	protected void prepareCutOff() {
		resetCalendar(begin); resetCalendar(end);
		begin.set(Calendar.DATE, 1);end.set(Calendar.DATE, 1);
		end.add(Calendar.MONTH,1); end.add(Calendar.MILLISECOND, -1);
		System.out.println("Begin " + begin.getTime());
		System.out.println("End " + end.getTime());
	}
	
	@Override
	protected long getTimerInterval() {
		return -1;
	}

	private void setArgs(String args[]){
		int month = getIntegerValue(args, "month","-1");
		int year = getIntegerValue(args, "year","-1");
		if(month != -1){
			begin.set(Calendar.MONTH, month);
			end.set(Calendar.MONTH, month);
			System.out.println(begin.getTime() + "  " + end.getTime());
		}
		if(year != -1){
			begin.set(Calendar.YEAR, year);
			end.set(Calendar.YEAR, year);
			
		}
	}
	
	@Override
	public void execute(boolean force, String[] args) {
		List<Trade> allTrades = build(args);
		try {
			String outputFile = FileNameGenerator.getTmpDir() + "TradeListing.pdf";
			OutputStream file = new FileOutputStream(outputFile);
			Document document = new Document();
			PdfWriter.getInstance(document, file);
			document.open();
			appendToDocument(document,allTrades);
			document.close();
			file.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<Trade> build(String args[]){
		String filePath = getValue(args,"filepath","");
		setArgs(args);
		prepareCutOff();
		StockBuilder builder = new StockBuilder();
		builder.setInputFile(filePath);
		builder.setFullReport(true);
		builder.setWorkBook();
		Map<String, Stock> stocks = builder.read("None");
		Iterator<Stock> values = stocks.values().iterator();
		List<Trade> allTrades = new ArrayList<Trade>();
		while(values.hasNext()){
			Stock stock = values.next();
			allTrades.addAll(stock.getTradeList());
		}
		filter(allTrades);
		Collections.sort(allTrades);
		return allTrades;
	}
	
	private void filter(List<Trade> list) {
		Iterator<Trade> iterator = list.iterator();
		while(iterator.hasNext()){
			Trade pi = iterator.next();
			if(remove(pi)) iterator.remove();
		}
	}
	
	private boolean remove(Trade trade) {
		Calendar c = trade.getTransactionTime();
		return c.before(begin) || c.after(end);
	}

	protected void resetCalendar(Calendar c) {
		c.set(Calendar.DATE, 1); c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0);
	}
	
	
	public void appendToDocument(Document document,List<Trade> trades) throws DocumentException{
		addSectionHeader(document,"Trade");
	
		PdfPTable table = new PdfPTable(7);
		String headers[] = new String[]{"Date","Script","Type","Quantity","Net Rate","Profit","Ref"};
		addHeaders(table,headers);

		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
		double tp = 0d;
		for(Trade trade : trades){
			if(trade.getTradeType().equals(Trade.DELETED)){
				continue;
			}
			List<Object> row = new ArrayList<Object>();
			row.add(df.format(trade.getTransactionTime().getTime()));
			row.add(trade.getName());
			row.add(trade.getTradeType());
			row.add(trade.getQuantity());
			row.add(trade.getNetRate());
			if(trade.getTradeType().equals(Trade.SELL)){
				row.add(trade.getProfit());
			}else{
				row.add(" ");
			}
			row.add(trade.getReferenceRate());
			tp += trade.getProfit();
			addRow(row, table);
		}
		
		List<Object> totalRow = new ArrayList<Object>();
		totalRow.add("Total");totalRow.add("-");totalRow.add("-"); totalRow.add("-"); 
		totalRow.add("-");totalRow.add("-"); totalRow.add(tp);
		addRow(totalRow,table);
		
		document.add(table);
	}
}
