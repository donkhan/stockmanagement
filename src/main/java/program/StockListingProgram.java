package program;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
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
import file.FileNameGenerator;

public class StockListingProgram extends AbstractProgram{

	private String[] args;
	
	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	public static void main(String[] args){
		StockListingProgram main = new StockListingProgram();
		main.setArgs(args);
		main.startExecute(args);
	}
	
	public long getTimerInterval(){
		return getIntegerValue(this.args,"interval") * 60 * 1000; 
	}
	
	
	public void execute(final boolean force,String args[]){
		GregorianCalendar currentTime = new GregorianCalendar();
		System.out.println("Pass started at " + currentTime.getTime());
		doWork(force,args);
	}
	
	private void doWork(final boolean force,String args[]){
		boolean fullReport = getBooleanValue(args,"fullreport");
		int maxRetries = getIntegerValue(args,"maxretry");
		String specificStock = getValue(args,"specificstock","None");
		StockBuilder builder = new StockBuilder();
		Double totalProfit = 0d;
		List<Stock> stockList = build(builder,totalProfit,fullReport,maxRetries,specificStock,getValue(args,"filepath",""),
				getValue(args,"mode","real"));
		try {
			OutputStream file = new FileOutputStream(FileNameGenerator.getTmpDir() + "StockListing.pdf");
			Document document = new Document();
			PdfWriter.getInstance(document, file);
			document.open();
			appendToDocument(document,stockList);
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
	
	public List<Stock> build(StockBuilder builder,
			Double totalProfit,boolean fullReport, 
			int maxRetries, String specificStock,String inputFile,String mode){
		builder.setInputFile(inputFile);
		builder.setFullReport(fullReport);
		builder.setWorkBook();
		builder.setMode(mode);
		
		Map<String, Stock> stocks = builder.read(specificStock);
		builder.updateStocks(stocks,maxRetries);
		List<Stock> stockList = new ArrayList<Stock>();
		Iterator<Stock> values = stocks.values().iterator();
		while(values.hasNext()){
			Stock stock = values.next();
			if(stock.getTotalQuantity() > 0 || fullReport){
				stockList.add(stock);
			}
			totalProfit += stock.getProfitRealised();
		}
		return stockList;
	}



	public void appendToDocument(Document document,List<Stock> stocks) throws DocumentException{
		addSectionHeader(document,"Stocks");
		PdfPTable table = new PdfPTable(5);
		String headers[] = new String[]{"Script","Quantity","Average","Market Rate","Market Value"};
		addHeaders(table,headers);
		double totalValue = 0;
		for(Stock stock : stocks){
			List<Object> row = new ArrayList<Object>();
			row.add(stock.getName());
			row.add(stock.getTotalQuantity());
			row.add(stock.getAverage());
			row.add(stock.getCurrentPrice());
			double value = stock.getCurrentPrice() * stock.getTotalQuantity();
			totalValue += value;
			row.add(value);
			addRow(row,table);
		}
		
		List<Object> row = new ArrayList<Object>();
		row.add("Total"); row.add(""); row.add(""); row.add(""); row.add(totalValue);
		addRow(row,table);
		document.add(table);
	}
	
}
