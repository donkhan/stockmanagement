package program;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import core.Stock;
import core.StockBuilder;


public class ProfitCalculationProgram extends AbstractProgram{
	
	public static void main(String args[]){
		ProfitCalculationProgram cp = new ProfitCalculationProgram();
		cp.startExecute(args);
	}
	

	@Override
	protected long getTimerInterval() {
		return 0;
	}

	@Override
	protected void execute(boolean force, boolean sendmail, String specificStock) {
		StockBuilder builder= new StockBuilder();
		builder.setInputFile("c://Users//kkhan//Trade.xls");
		
		Iterator<Stock> iterator = builder.readTrades(specificStock).values().iterator();
		Map<String,Double> brokers = new HashMap<String,Double>(); 
		while(iterator.hasNext()){
			Stock stock = iterator.next();
			double profitRealised = stock.getProfitRealised();
			if(profitRealised > 0){
				System.out.println(stock.getName() + " (" +  stock.getBroker() + ")  "+ profitRealised);
			}
			String broker = stock.getBroker();
			if(brokers.containsKey(broker)){
				Double profit = brokers.get(broker);
				brokers.put(broker,profit + profitRealised);
			}else{
				brokers.put(broker,profitRealised);
			}
		}
		
		Iterator<String> iter = brokers.keySet().iterator();
		while(iter.hasNext()){
			String broker = iter.next();
			System.out.println(broker + " " + brokers.get(broker));
		}
		
	}
	
	
	
}
