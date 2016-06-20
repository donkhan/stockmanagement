package program;

import java.util.List;

import mail.Mailer;
import core.Stock;
import core.StockList;


public class StockTrackingProgram extends AbstractProgram{

	private int iteration = 0;
	private static int INTERVAL_IN_MINUTES = 10;
	@Override
	protected long getTimerInterval() {
		return INTERVAL_IN_MINUTES*60*1000;
	}

	@Override
	protected void execute(boolean force,String[] args) {
		String specificStock = getValue(args,"specificstock","None");
		boolean sendmail  = getBooleanValue(args,"sendmail"); 
		String stocks[] = specificStock.split(",");
		StockList stockList = new StockList();		
		stockList.setInputFile("c://Users//kkhan//Trade.xls");
		List<Stock> list = stockList.getCurrentStockPriceList(stocks);
		Mailer mailer = new Mailer(args);
		StringBuffer subject = new StringBuffer("");
		subject.append("Alert. Stock Updates...");
		StringBuffer content = new StringBuffer();
		for(Stock stock: list){
			System.out.println(stock.getName() + "  " + stock.getCurrentPrice());
			content.append(stock.getName() + "  " + stock.getCurrentPrice() + "\n");
		}
		if(sendmail){
			mailer.mail(null,subject,content);
		}
		
		System.out.println("Iteration " +  ++iteration +" completed");
	}

	public static void main(String[] args){
		StockTrackingProgram main = new StockTrackingProgram();
		main.startExecute(args);
	}
}
