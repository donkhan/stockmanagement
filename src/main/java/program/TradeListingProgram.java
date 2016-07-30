package program;

import jasper.TradeListingGenerator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import profit.ProfitCalendarInterface;
import core.Stock;
import core.StockBuilder;
import core.Trade;
import file.FileNameGenerator;


public class TradeListingProgram extends AbstractProgram{

	public static void main(String args[]){
		TradeListingProgram cp = new TradeListingProgram();
		cp.startExecute(args);
	}
	
	private GregorianCalendar begin = new GregorianCalendar();
	private GregorianCalendar end  = new GregorianCalendar();
	
	protected void prepareCutOff(String[] args) {
		resetCalendar(begin); resetCalendar(end);
		begin.set(Calendar.DATE, 1);end.set(Calendar.DATE, 1);
		end.add(Calendar.MONTH,1); end.add(Calendar.MILLISECOND, -1);
		System.out.println("Begin " + begin.getTime());
		System.out.println("End " + end.getTime());
	}
	
	@Override
	protected long getTimerInterval() {
		return -1;
	}

	@Override
	protected void execute(boolean force, String[] args) {
		prepareCutOff(args);
		StockBuilder builder = new StockBuilder();
		builder.setInputFile(getValue(args,"filepath",""));
		builder.setFullReport(true);
		builder.setWorkBook();
		Map<String, Stock> stocks = builder.read("None");
		Iterator<Stock> values = stocks.values().iterator();
		List<Trade> allTrades = new ArrayList<Trade>();
		while(values.hasNext()){
			Stock stock = values.next();
			allTrades.addAll(stock.getTradeList());
		}
		filter(allTrades);
		Collections.sort(allTrades);
		prepareReport(allTrades);
	}
	
	private void filter(List<Trade> list) {
		Iterator<Trade> iterator = list.iterator();
		while(iterator.hasNext()){
			Trade pi = iterator.next();
			if(remove(pi)) iterator.remove();
		}
	}
	
	private boolean remove(Trade trade) {
		Calendar c = trade.getTransactionTime();
		return c.before(begin) || c.after(end);
	}

	protected void resetCalendar(Calendar c) {
		c.set(Calendar.DATE, 1); c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0);
	}
	
	private void prepareReport(List<Trade> trades) {
		TradeListingGenerator gen = new TradeListingGenerator();
		gen.generate(trades,"TradeListing.jrxml",FileNameGenerator.getTmpDir() + "Trades.pdf");
	}
}
