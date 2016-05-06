package core;
import java.io.File;
import java.io.IOException;
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
		Sheet sheet = w.getSheet(0);
		for (int i = 0; i < sheet.getRows(); i++) {
			if(i == 0 || sheet.getCell(7,i).getContents().equals("Closed")){
				continue;
			}
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
	}
	
	private double getRate(String stockName,String url,StockService reader,Map<String,Double> rateMap){
		if(rateMap.containsKey(stockName)){
			System.out.println("Fetched Already");
			return rateMap.get(stockName);
		}
		System.out.println(stockName + ":" +  url);
		double currentPrice = reader.getCurrentPrice(url);
		System.out.println(stockName + ":" + currentPrice);
		rateMap.put(stockName,currentPrice);
		return currentPrice;
	}
	
	public void updateStocks(Workbook w,Map<String, Stock> stocks){
		StockService reader = StockServiceFactory.getStockService();
		String brokers [] = { "Kotak","Geojit","Money Palm"};
		Map<String,Double> rateMap = new HashMap<String,Double>();
		Sheet sheet = w.getSheet(1);
		Iterator<Stock> stockIterator = stocks.values().iterator();
		while(stockIterator.hasNext()){
			Stock stock = stockIterator.next();
			String stockName = stock.getName();
			if(stock.getTotalQuantity() == 0 && !fullReport){
				System.out.println("As Total Quantity of " + stockName + " is zero,it's price will not be queried");
				continue;
			}
			String url = getURL(sheet,stockName);
			double currentPrice = getRate(stockName,url,reader,rateMap);
			for(int j = 0;j<brokers.length;j++){
				String uniqueName = stockName + "-" + brokers[j];
				Stock s = stocks.get(uniqueName);
				if (s != null) {
					s.setCurrentPrice(currentPrice);
					s.calculateImaginaryProfit(getCommissionCalculator(s.getBroker()));
				}
			}
		}
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
		Map<String, Stock> stocks = new LinkedHashMap<String, Stock>();
		try {
			Workbook w = getWorkBook();
			analyzeAndUpdate(w,stocks,specificStock);
		} catch (BiffException e) {
			e.printStackTrace();
		}  catch(IOException ioe){
			ioe.printStackTrace();
		}
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
		File inputWorkbook = new File(inputFile);
		Workbook w = Workbook.getWorkbook(inputWorkbook);
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
		String commi = sheet.getCell(5, index).getContents();
		if(commi != null && !commi.equals("")){
			trade.setCommission(Double.parseDouble(commi));
		}
		trade.setGrossrate(Double.parseDouble(sheet.getCell(3, index).getContents()));
		String broker = sheet.getCell(6, index).getContents();
		trade.setBroker(broker);
		CommissionCalculator cc = getCommissionCalculator(broker);
		cc.calculateCommission(trade);
		trade.print();
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
