package program;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import profit.ProfitCalendar;
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
		Map<Calendar,Double> map = new HashMap<Calendar,Double>();
		Map<String, Stock> stocks = builder.read("None");
		Iterator<Stock> values = stocks.values().iterator();
		while(values.hasNext()){
			Stock stock = values.next();
			List<Trade> trades = stock.getTradeList();
			for(Trade trade : trades){
				if(trade.getTradeType().equals(Trade.SELL)){
					Calendar c = trade.getTransactionTime();
					c.set(Calendar.DATE, 1); c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0);
					c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0);
					if(map.containsKey(c)){
						Double d= map.remove(c);
						Double d1 = new Double(d.doubleValue() + trade.getProfit());
						map.put(c,d1);
					}else{
						Double d1 = new Double(trade.getProfit());
						map.put(c,d1);
					}
				}
			}
		}
		List<ProfitCalendar> list = new ArrayList<ProfitCalendar>();
		
		Iterator<Calendar> keyIterator = map.keySet().iterator();
		while(keyIterator.hasNext()){
			Calendar key = keyIterator.next();
			list.add(new ProfitCalendar(key,map.get(key)));
		}
		
		Collections.sort(list);
	
		for(ProfitCalendar pc : list){
			pc.print();
		}
		
	}
	
	
	
	
}
