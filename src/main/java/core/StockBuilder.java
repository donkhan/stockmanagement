package core;
import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import services.StockService;
import util.StockServiceFactory;

import commissioncalculators.CommissionCalculator;
import commissioncalculators.GeojitCommissionCalculator;
import commissioncalculators.KotakCommissionCalculator;
import commissioncalculators.MoneyPalmCommissionCalculator;

public class StockBuilder {
	
	private String inputFile;
	private boolean fullReport;
	

	public boolean isFullReport() {
		return fullReport;
	}

	public void setFullReport(boolean fullReport) {
		this.fullReport = fullReport;
	}

	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}
	
	private void analyzeTrades(Workbook w,Map<String, Stock> stocks,String specificStock){
		System.out.println("Going to Analyze trades");
		Sheet sheet = w.getSheet(0);
		int noOfTrades = sheet.getRows();
		System.out.println("No of Trades " + noOfTrades);
		for (int i = 1; i < noOfTrades; i++) {
			String stockName = sheet.getCell(0, i).getContents();
			if(!specificStock.equals("None") && !stockName.contains(specificStock)){
				continue;
			}
			String brokerName = sheet.getCell(6, i).getContents();
			Stock stock = getStock(stockName,brokerName,stocks);
			Trade trade = new Trade();
			fill(trade,sheet,i);
			stock.addToTradeList(trade);
		}
		System.out.println("Trades are analyzed");
	}
	
	private double getRate(String stockName,String url,StockService reader,Map<String,Double> rateMap){
		if(rateMap.containsKey(stockName)){
			return rateMap.get(stockName);
		}
		double currentPrice = reader.getCurrentPrice(url);
		rateMap.put(stockName,currentPrice);
		return currentPrice;
	}
	
	public void updateStocks(Workbook w,Map<String, Stock> stocks){
		System.out.println("Going to update stocks with current Price");
		StockService reader = StockServiceFactory.getStockService();
		String brokers [] = { "Kotak","Geojit","Money Palm"};
		Map<String,Double> rateMap = new HashMap<String,Double>();
		Sheet sheet = w.getSheet(1);
		Iterator<Stock> stockIterator = stocks.values().iterator();
		while(stockIterator.hasNext()){
			Stock stock = stockIterator.next();
			String stockName = stock.getName();

			if(stock.getTotalQuantity() == 0 && !fullReport){
				continue;
			}
			String url = getURL(sheet,stockName);
			double currentPrice = getRate(stockName,url,reader,rateMap);
			System.out.println("Current Price of " + stockName + " = "  + currentPrice);
			for(int j = 0;j<brokers.length;j++){
				String uniqueName = stockName + "-" + brokers[j];
				Stock s = stocks.get(uniqueName);
				if (s != null) {
					s.setCurrentPrice(currentPrice);
					s.calculateImaginaryProfit(getCommissionCalculator(s.getBroker()));
				}
			}
		}
		System.out.println("Stocks are updated");
	}
	
	protected String getURL(Sheet sheet,String stockName){
		int rows = sheet.getRows();
		for(int i  =0;i<rows;i++){
			if(sheet.getCell(0, i).getContents().equals(stockName)){
				return sheet.getCell(1,i).getContents();
			}
		}
		return "Not Found";
	}

	private Stock getStock(String stockName,String brokerName,Map<String, Stock> stocks){
		String uniqueName = stockName + "-" + brokerName;
		Stock stock = null;
		if (stocks.containsKey(uniqueName)) {
			stock = stocks.get(uniqueName);
		} else {
			stock = new Stock();
			stock.setName(stockName);
			stock.setBroker(brokerName);
			stocks.put(uniqueName, stock);
		}
		return stock;
	}
	
	public Map<String, Stock> read(String specificStock) {
		System.out.println("Going to read Work Sheet");
		Map<String, Stock> stocks = new LinkedHashMap<String, Stock>();
		try {
			Workbook w = getWorkBook();
			analyzeAndUpdate(w,stocks,specificStock);
		} catch (BiffException e) {
			e.printStackTrace();
		}  catch(IOException ioe){
			ioe.printStackTrace();
		}
		System.out.println("Sheet is read");
		return stocks;
	}
	
	public Map<String, Stock> readTrades(String specificStock) {
		Map<String, Stock> stocks = new LinkedHashMap<String, Stock>();
		try {
			Workbook w = getWorkBook();
			analyzeTrades(w,stocks,specificStock);
		} catch (BiffException e) {
			e.printStackTrace();
		}  catch(IOException ioe){
			ioe.printStackTrace();
		}
		return stocks;
	}
	
	private void analyzeAndUpdate(Workbook w,Map<String,Stock> stocks, String specificStock){
		analyzeTrades(w,stocks,specificStock);
		updateStocks(w,stocks);
	}

	protected Workbook getWorkBook() throws IOException,BiffException{
		Workbook w  = null;
		if(!inputFile.equals("")){
			File inputWorkbook = new File(inputFile);
			w = Workbook.getWorkbook(inputWorkbook);
		}else{
			w = Workbook.getWorkbook(URLClassLoader.getSystemResourceAsStream("Trade.xls"));
		}
		
		return w;
	}

	private KotakCommissionCalculator kcc  = new KotakCommissionCalculator();
	private GeojitCommissionCalculator gcc  = new GeojitCommissionCalculator();
	private MoneyPalmCommissionCalculator mcc  = new MoneyPalmCommissionCalculator();
	
	private void fill(Trade trade,Sheet sheet,int index){
		Cell cell = sheet.getCell(1, index);
		StringTokenizer tokenizer = new StringTokenizer(cell.getContents(),"/");
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.DATE, Integer.parseInt(tokenizer.nextToken()));
		calendar.set(Calendar.MONTH, Integer.parseInt(tokenizer.nextToken())-1);
		calendar.set(Calendar.YEAR, Integer.parseInt(tokenizer.nextToken()));
		trade.setTransactionTime(calendar.getTime());
		trade.setName(sheet.getCell(0,index).getContents());
		trade.setTradeType(sheet.getCell(2, index).getContents());
		trade.setQuantity(Long.parseLong(sheet.getCell(4, index).getContents()));
		trade.setGrossrate(Double.parseDouble(sheet.getCell(3, index).getContents()));
		String broker = sheet.getCell(6, index).getContents();
		trade.setBroker(broker);
		CommissionCalculator cc = getCommissionCalculator(broker);
		cc.calculateCommission(trade);
		
		if(trade.getTradeType().equals(Trade.SELL)){
			int columns = sheet.getColumns();
			if(columns == 9){
				cell = sheet.getCell(8,index);
				if(cell != null) {
					String content = cell.getContents();
					if (content != null && !content.equals("")){
						double buyRate = Double.parseDouble(content);
						trade.setBuyRate(buyRate);
					}
				}
			}
		}

	}
	
	private CommissionCalculator getCommissionCalculator(String broker){
		if(broker.equals("Kotak")){
			return kcc;
		}
		if(broker.equals("Geojit")){
			return gcc;
		}
		if(broker.equals("Money Palm")){
			return mcc;
		}
		return null;
	}
}
