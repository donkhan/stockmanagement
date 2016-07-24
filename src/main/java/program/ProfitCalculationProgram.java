package program;

import jasper.ProfitReportGenerator;

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
		Map<Calendar,ProfitCalendar> map = new HashMap<Calendar,ProfitCalendar>();
		Map<String, Stock> stocks = builder.read("None");
		Iterator<Stock> values = stocks.values().iterator();
		while(values.hasNext()){
			Stock stock = values.next();
			List<Trade> trades = stock.getTradeList();
			for(Trade trade : trades){
				Calendar c = trade.getTransactionTime();
				c.set(Calendar.DATE, 1); c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0);
				ProfitCalendar pc = null;
				double tradeAmount = trade.getNetRate() * trade.getQuantity();
				if(map.containsKey(c)){
					pc = map.get(c);
					pc.setBuyTrades(pc.getBuyTrades() + 1);

				}else{
					pc = new ProfitCalendar(c,0d);
					map.put(c,pc);
				}
				if(trade.getTradeType().equals(Trade.SELL)){
					pc.setProfit(pc.getProfit() + trade.getProfit());
					pc.setSellTrades(pc.getSellTrades() + 1);
					pc.setTotalBuyAmount(pc.getTotalBuyAmount() + tradeAmount);
				}else{
					pc.setBuyTrades(pc.getBuyTrades() + 1);
					pc.setTotalSellAmount(pc.getTotalSellAmount() + tradeAmount);
				}
				pc.setTotalTurnOver(pc.getTotalTurnOver() + tradeAmount);
			}
		}
		List<ProfitCalendar> list = new ArrayList<ProfitCalendar>();
		
		Iterator<Calendar> keyIterator = map.keySet().iterator();
		while(keyIterator.hasNext()){
			Calendar key = keyIterator.next();
			list.add(map.get(key));
		}
		
		Collections.sort(list);
		prepareReport(list);
	}
	
	private void prepareReport(List<ProfitCalendar> profitCalendarList) {
		ProfitReportGenerator gen = new ProfitReportGenerator();
		gen.generate(profitCalendarList);
	}	
	
	
	
	
}
