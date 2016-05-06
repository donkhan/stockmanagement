package core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import services.StockService;
import util.StockServiceFactory;

public class StockList extends StockBuilder{

	public List<Stock> getCurrentStockPriceList(String[] stocks){
		List<Stock> list = new ArrayList<Stock>();
		StockService reader = StockServiceFactory.getStockService();
		try {
			Workbook w = getWorkBook();
			Sheet sheet = w.getSheet(1);
			for(String stockName : stocks){
				String URL = getURL(sheet,stockName);
				double currentPrice = reader.getCurrentPrice(URL);
				Stock stock = new Stock();
				stock.setName(stockName); stock.setCurrentPrice(currentPrice);
				list.add(stock);
			}
		} catch (BiffException e) {
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		}
		
		return list;
	}
}
