package program.profitcalculation;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import profit.ProfitCalendar;
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
		Map<Calendar,ProfitCalendar> map = new HashMap<Calendar,ProfitCalendar>();
		Map<String, Stock> stocks = builder.read("None");
		Iterator<Stock> values = stocks.values().iterator();
		while(values.hasNext()){
			Stock stock = values.next();
			List<Trade> trades = stock.getTradeList();
			for(Trade trade : trades){
				Calendar c = trade.getTransactionTime();
				resetCalendar(c);
				ProfitCalendar pc = null;
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
					pc.setTotalBuyAmount(pc.getTotalBuyAmount() + tradeAmount);
				}else{
					pc.setBuyTrades(pc.getBuyTrades() + 1);
					pc.setTotalSellAmount(pc.getTotalSellAmount() + tradeAmount);
				}
				pc.setTotalTurnOver(pc.getTotalTurnOver() + tradeAmount);
			}
		}
		process(map);
	}
	
	protected abstract void process(Map<Calendar,ProfitCalendar> map);
	protected abstract ProfitCalendar getProfitCalendar(Calendar c,Double d);
	protected abstract void resetCalendar(Calendar c);
}
