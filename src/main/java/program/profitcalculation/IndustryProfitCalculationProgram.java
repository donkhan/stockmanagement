package program.profitcalculation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import core.Stock;
import core.StockBuilder;
import core.Trade;
import profit.ProfitCalendarInterface;

public class IndustryProfitCalculationProgram extends AbstractProfitCalculationProgram{

	@Override
	protected ProfitCalendarInterface getProfitCalendar(Calendar c, Double d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void resetCalendar(Calendar c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void prepareCutOff(String[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getReadableDate(Calendar calendar) {
		// TODO Auto-generated method stub
		return null;
	}

	protected void execute(boolean force,String args[]) {
		StockBuilder builder = new StockBuilder();
		builder.setInputFile(getValue(args,"filepath",""));
		builder.setFullReport(true);
		builder.setWorkBook();
		String specificStock = getValue(args,"specificstock","None");
		Map<String, Stock> stocks = builder.read(specificStock);
		Iterator<String> iterator = stocks.keySet().iterator();
		while(iterator.hasNext()){
			try{
				determine(stocks.get(iterator.next()));
			}catch(Exception e){
				
			}
		}
		
	}
	
	private void determine(Stock stock){
		
		
		List<Trade> trades = stock.getTradeList();
		List<Trade> buyTrades = new ArrayList<Trade>();
		List<Trade> sellTrades = new ArrayList<Trade>();
		
		for(Trade trade : trades){
			if(trade.getTradeType().equals(Trade.BUY) 
					|| trade.getTradeType().equals(Trade.RIGHTS)) {
				buyTrades.add(trade);
			}else{
				sellTrades.add(trade);
			}
		}
		
		if(sellTrades.size() == 0){
			return;
		}
		System.out.println(stock + " " + stock.getBroker());
		
		int buyIndex = 0;
		double totalProfit = 0;
		for(Trade trade : sellTrades){
			long q = trade.getQuantity();
			double sellProfit = 0;
			while(q > 0){
				Trade bt = buyTrades.get(buyIndex);
				
				if(bt.getQuantity() > q){
					System.out.println("(" + trade.getNetRate() +"-" + bt.getNetRate() + ")*" + q);
					sellProfit += (trade.getNetRate() - bt.getNetRate()) * q;
					bt.setQuantity(bt.getQuantity() - q);
					q = 0;
				}else{
					System.out.println("(" + trade.getNetRate() +"-" + bt.getNetRate() + ")*" + bt.getQuantity());
					sellProfit += (trade.getNetRate() - bt.getNetRate()) * bt.getQuantity();
					q -= bt.getQuantity();
					bt.setQuantity(0);
				}
				if(bt.getQuantity() == 0) {
					buyIndex = buyIndex + 1;
				}
			}
			System.out.println("Sell Profit " + sellProfit);
			totalProfit += sellProfit;
		}
		
		System.out.println("Total Profit " + totalProfit);
		System.out.println("-----------------------------");
	}
	
	public static void main(String args[]){
		IndustryProfitCalculationProgram cp = new IndustryProfitCalculationProgram();
		cp.execute(false, args);
	}
	
}
