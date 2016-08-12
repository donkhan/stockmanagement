package util;

import external.InternalStockService;
import external.MoneyControlStockService;
import services.StockService;

public class StockServiceFactory {

	public static StockService getStockService(){
		if(InternetService.up()){
			return new MoneyControlStockService();
		}
		
		return new InternalStockService();
	}
}
