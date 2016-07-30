package util;

import services.StockService;
import external.InternalStockService;

public class Global {

	public static boolean debug = false;
	public static void printLine(){
		System.out.println("-----------------------------------------------------------------------------");
	}
	
	public static void printTaxDetails(){
		if(debug){
			printLine();
			System.out.println("Service Tax Before June 1 2015 = 12.36%");
			System.out.println("Service Tax From June 1 2015 = 14%");
			System.out.println("Swatch Bharat Cess from Nov 1 2015 = 0.5%");
			System.out.println("Krish Kalyan Cess from June 1 2016= 0.5%");
			printLine();
		}
	}
	
	public static StockService getStockService(){
		//return new MoneyControlStockService();
		return new InternalStockService();
		
	}
}
