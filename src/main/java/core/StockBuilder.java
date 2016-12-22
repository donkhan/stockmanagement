package core;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import commissioncalculators.CommissionCalculator;
import commissioncalculators.GeojitCommissionCalculator;
import commissioncalculators.KotakCommissionCalculator;
import commissioncalculators.MoneyPalmCommissionCalculator;
import core.db.WorkBookManager;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import thread.StockThread;
import util.Global;

public class StockBuilder {
	
	private String inputFile;
	private boolean fullReport;
	private String mode;
	

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public boolean isFullReport() {
		return fullReport;
	}

	public void setFullReport(boolean fullReport) {
		this.fullReport = fullReport;
	}

	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}
	
	private TradeSummary tradeSummary = new TradeSummary();
	
	public TradeSummary getTradeSummary() {
		return tradeSummary;
	}

	public void setTradeSummary(TradeSummary tradeSummary) {
		this.tradeSummary = tradeSummary;
	}

	private void analyzeTrades(Map<String, Stock> stocks,String specificStock){
		System.out.println("Going to Analyze trades ");
		Global.printTaxDetails();
		Sheet sheet = workBook.getSheet(0);
		tradeSummary = new TradeSummary();
		int noOfTrades = sheet.getRows();
		tradeSummary.setNoOfTrades(noOfTrades);
		Global.printLine();
		for (int i = 1; i < noOfTrades; i++) {
			String stockName = sheet.getCell(0, i).getContents();
			if(stockName == null || stockName.equals("")){
				continue;
			}
			if(!specificStock.equals("None") && !stockName.contains(specificStock)){
				continue;
			}
			String brokerName = sheet.getCell(6, i).getContents();
			Stock stock = getStock(stockName,brokerName,stocks);
			Trade trade = new Trade(i+1);
			fill(trade,sheet,i);
			stock.addToTradeList(trade,tradeSummary);
		}
		System.out.println("Trades are analyzed");
	}
	
	public void updateStocks(Map<String, Stock> stocks,int maxRetries){
		System.out.println("Going to update stocks with current Price");
		String brokers [] = { "Kotak","Geojit","Money Palm"};
		Sheet sheet = workBook.getSheet(1);
		Iterator<Stock> stockIterator = stocks.values().iterator();
		List<StockThread> list = new ArrayList<StockThread>();

		while(stockIterator.hasNext()) {
			Stock stock = stockIterator.next();
			String stockName = stock.getName();

			if (stock.getTotalQuantity() == 0 && !fullReport) {
				continue;
			}
			String url = getURL(sheet, stockName);
			StockThread st = new StockThread(url,stockName,maxRetries,mode);
			st.start();
			list.add(st);
		}

		for(StockThread st : list){
			try {
				st.join();
			}catch(Exception e){
				e.printStackTrace();
			}
			double currentPrice = st.getStockPrice();
			for(int j = 0;j<brokers.length;j++){
				String uniqueName = st.getStockName() + "-" + brokers[j];
				Stock stock = stocks.get(uniqueName);
				if (stock != null) {
					stock.setCurrentPrice(currentPrice);
					stock.calculateImaginaryProfit(getCommissionCalculator(stock.getBroker()));
					List<Trade> trades = stock.getTradeList();
					for(Trade trade: trades){
						if(trade.getTradeType().equals(Trade.BUY) && !trade.isSoldOff()){
							if(stock.getCurrentPrice() > trade.getNetRate()){
								stock.addToPositiveTrade(trade);
							}
						}
					}
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
		analyzeTrades(stocks,specificStock);
		return stocks;
	}
	
	private Workbook workBook;
	
	public Map<String, Stock> readTrades(String specificStock) {
		Map<String, Stock> stocks = new LinkedHashMap<String, Stock>();
		analyzeTrades(stocks,specificStock);
		return stocks;
	}
	
	
	public Workbook getWorkBook() {
		return workBook;
	}


	public void setWorkBook() {
		workBook = new WorkBookManager().getWorkBook(inputFile);
	}

	private KotakCommissionCalculator kcc  = new KotakCommissionCalculator();
	private GeojitCommissionCalculator gcc  = new GeojitCommissionCalculator();
	private MoneyPalmCommissionCalculator mcc  = new MoneyPalmCommissionCalculator();
	
	private void fill(Trade trade,Sheet sheet,int index){
		Cell cell = sheet.getCell(1, index);
		handleTransactionDate(trade,cell);
		
		trade.setName(sheet.getCell(0,index).getContents());
		handleSettlementDate(trade,sheet.getCell(11, index));
		trade.setTradeType(sheet.getCell(2, index).getContents());
		trade.setQuantity(Long.parseLong(sheet.getCell(4, index).getContents()));
		trade.setGrossRate(Double.parseDouble(sheet.getCell(3, index).getContents()));
		String broker = sheet.getCell(6, index).getContents();
		trade.setBroker(broker);
		
		if(Global.debug){
            System.out.println("Name " + trade.getName() + " Quantity " + trade.getQuantity() + " "
                    + (trade.getTradeType().equals(Trade.SELL) ? "SELL" : "BUY") + "  "+ trade.getTransactionTime().getTime()  + " " + trade.getBroker());
            System.out.println("Gross Rate " + trade.getGrossRate());
        }
		
		calculateNetRate(trade,sheet,index);
		
		if(trade.getTradeType().equals(Trade.SELL)){
			int columns = sheet.getColumns();
			if(columns >= 9){
				cell = sheet.getCell(8,index);
				handleBuyRateCase(trade,cell);
			}
		}
		
		if(trade.getTradeType().equals(Trade.BUY)){
			handleSoldOffs(sheet,7,index,trade);
		}
	}
	
	private void calculateNetRate(Trade trade, Sheet sheet, int index) {
		Cell cell = sheet.getCell(10, index);
		if(cell != null){
			String content = cell.getContents();
			if(content != null && !content.equals("")){
				trade.setExtraCost(Double.parseDouble(content));
				return;
			}
		}
		CommissionCalculator cc = getCommissionCalculator(trade.getBroker());
		cc.calculateCommission(trade);
		
	}

	private void handleSoldOffs(Sheet sheet,int col,int index,Trade trade){
		Cell cell = sheet.getCell(col,index);
		if(cell != null){
			String content = cell.getContents();
			if (content != null && !content.equals("")){
				if(content.equals("Sold Off")){
					trade.setSoldOff(true);
					Cell unitsSoldCell = sheet.getCell(9,index);
					if(unitsSoldCell != null){
						String usContent = unitsSoldCell.getContents();
						if(usContent != null && !usContent.equals("")){
							int units = Integer.parseInt(usContent);
							trade.setUnsoldUnits((int)trade.getQuantity() - units);
							if(trade.getUnsoldUnits() != 0){
								trade.setSoldOff(false);
							}
						}
					}
				}
			}
		}
	}
	
	private void handleBuyRateCase(Trade trade,Cell cell){
		if(cell != null) {
			String content = cell.getContents();
			if (content != null && !content.equals("")){
				StringTokenizer tokenizer = new StringTokenizer(content,",");
				List<Integer> buyTradeIds = new ArrayList<Integer>();
				while(tokenizer.hasMoreTokens()){
					String token = tokenizer.nextToken();
					int buyTradeId = Integer.parseInt(token);
					buyTradeIds.add(buyTradeId);
				}
				trade.setBuyTradeIds(buyTradeIds);
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
	
	private void handleSettlementDate(Trade trade,Cell cell){
		if(cell == null) return;
		String content = cell.getContents();
		if("".equals(content)){
			return;
		}
		trade.setSettlementTime(getDate(content));
	}
	
	private void handleTransactionDate(Trade trade,Cell cell){
		trade.setTransactionTime(getDate(cell.getContents()));
	}
	
	private Calendar getDate(String content){
		Calendar calendar = new GregorianCalendar();
		if(content == null){
			return calendar;
		}
		StringTokenizer tokenizer = new StringTokenizer(content,"/");
		if(!tokenizer.hasMoreTokens()){
			return calendar;
		}
		calendar.set(Calendar.MONTH, Integer.parseInt(tokenizer.nextToken())-1);
		calendar.set(Calendar.DATE, Integer.parseInt(tokenizer.nextToken()));
		calendar.set(Calendar.YEAR, 2000+Integer.parseInt(tokenizer.nextToken()));
		return calendar;
	}
	
	
}
