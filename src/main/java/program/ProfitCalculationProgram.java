package program;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import core.Stock;
import core.StockBuilder;
import core.Trade;

public class ProfitCalculationProgram extends AbstractProgram{
	
	public static void main(String args[]){
		ProfitCalculationProgram cp = new ProfitCalculationProgram();
		String arg[] = new String[]{"--force=true",args[0]};
		cp.startExecute(arg);
	}
	

	@Override
	protected long getTimerInterval() {
		return 10;
	}

	@Override
	protected void execute(boolean force,String args[]) {
		StockBuilder builder = new StockBuilder();
		builder.setInputFile(getValue(args,"filepath",""));
		builder.setFullReport(true);
		builder.setWorkBook();
		
		Map<String,Double> map = new HashMap<String,Double>();
		Map<String, Stock> stocks = builder.read("None");
		Iterator<Stock> values = stocks.values().iterator();
		while(values.hasNext()){
			Stock stock = values.next();
			List<Trade> trades = stock.getTradeList();
			for(Trade trade : trades){
				if(trade.getTradeType().equals(Trade.SELL)){
					//System.out.println(trade.getId() + "  " + trade.getProfit());
					String key = trade.getTransactionTime().getMonth() + "-" + trade.getTransactionTime().getYear();
					System.out.println(key);
					if(map.containsKey(key)){
						Double d= map.get(key);
						Double d1 = new Double(d.doubleValue() + trade.getProfit());
						map.put(key,d1);
					}else{
						Double d1 = new Double(trade.getProfit());
						map.put(key,d1);
					}
				}
			}
		}
		
		Iterator<String> keyIterator = map.keySet().iterator();
		while(keyIterator.hasNext()){
			String key = keyIterator.next();
			System.out.println(key + "  " + map.get(key));
		}
		System.out.println("Done");
		
		
	}
	
	
	
}
