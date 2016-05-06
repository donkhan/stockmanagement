package util;

import services.StockService;
import external.InternalStockService;
import external.MoneyControlStockService;

public class StockServiceFactory {

	public static StockService getStockService(){
		if(InternetService.up()){
			return new MoneyControlStockService();
		}
		
		return new InternalStockService();
	}
}
