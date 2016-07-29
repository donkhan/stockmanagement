package program.profitcalculation;

import jasper.ProfitReportGenerator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import profit.ProfitCalendarInterface;
import program.AbstractProgram;
import core.Stock;
import core.StockBuilder;
import core.Trade;



public abstract class AbstractProfitCalculationProgram extends AbstractProgram{
	
	@Override
	protected long getTimerInterval() {
		return -1;
	}

	@Override
	protected void execute(boolean force,String args[]) {
		StockBuilder builder = new StockBuilder();
		builder.setInputFile(getValue(args,"filepath",""));
		builder.setFullReport(true);
		builder.setWorkBook();
		Map<Calendar,ProfitCalendarInterface> map = new HashMap<Calendar,ProfitCalendarInterface>();
		Map<String, Stock> stocks = builder.read("None");
		Iterator<Stock> values = stocks.values().iterator();
		while(values.hasNext()){
			Stock stock = values.next();
			List<Trade> trades = stock.getTradeList();
			for(Trade trade : trades){
				Calendar c = trade.getTransactionTime();
				resetCalendar(c);
				ProfitCalendarInterface pc = null;
				double tradeAmount = trade.getNetRate() * trade.getQuantity();
				if(map.containsKey(c)){
					pc = map.get(c);
					pc.setBuyTrades(pc.getBuyTrades() + 1);

				}else{
					pc = getProfitCalendar(c,0d);
					map.put(c,pc);
				}
				if(trade.getTradeType().equals(Trade.SELL)){
					pc.setProfit(pc.getProfit() + trade.getProfit());
					pc.setSellTrades(pc.getSellTrades() + 1);
					pc.setTotalSellAmount(pc.getTotalSellAmount() + tradeAmount);
					
				}else{
					pc.setBuyTrades(pc.getBuyTrades() + 1);
					pc.setTotalBuyAmount(pc.getTotalBuyAmount() + tradeAmount);
				}
				pc.setTotalTurnOver(pc.getTotalTurnOver() + tradeAmount);
			}
		}
		process(map);
	}
	
	public void process(Map<Calendar,ProfitCalendarInterface> map){
		List<ProfitCalendarInterface> list = new ArrayList<ProfitCalendarInterface>();
		Iterator<Calendar> keyIterator = map.keySet().iterator();
		while(keyIterator.hasNext()){
			Calendar key = keyIterator.next();
			list.add(map.get(key));
		}
		Collections.sort(list);
		filter(list);
		prepareReport(list);
	}
	
	
	private void prepareReport(List<ProfitCalendarInterface> profitCalendarList) {
		ProfitReportGenerator gen = new ProfitReportGenerator();
		gen.generate(profitCalendarList,getReportFileName());
	}
	
	
	
	protected abstract void filter(List<ProfitCalendarInterface> list);
	protected abstract String getReportFileName();
	protected abstract ProfitCalendarInterface getProfitCalendar(Calendar c,Double d);
	protected abstract void resetCalendar(Calendar c);
	
}
