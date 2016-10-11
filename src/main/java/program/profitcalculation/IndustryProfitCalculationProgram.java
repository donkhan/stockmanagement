package program.profitcalculation;

import java.text.DecimalFormat;
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
		return null;
	}

	@Override
	protected void resetCalendar(Calendar c) {
	}

	@Override
	protected void prepareCutOff(String[] args) {
	}

	@Override
	protected String getReadableDate(Calendar calendar) {
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
			}
			if(trade.getTradeType().equals(Trade.SELL)){
				sellTrades.add(trade);
			}
		}
		
		if(sellTrades.size() == 0){
			return;
		}
		System.out.println(stock + " " + stock.getBroker());
		DecimalFormat df = new DecimalFormat("#.##");
		
		int buyIndex = 0;
		double totalProfit = 0;
		for(Trade trade : sellTrades){
			long q = trade.getQuantity();
			double sellProfit = 0;
			while(q > 0){
				Trade bt = buyTrades.get(buyIndex);
				
				if(bt.getQuantity() > q){
					System.out.println("(" + df.format(trade.getNetRate()) +"-" + df.format(bt.getNetRate()) + ")*" + q);
					sellProfit += (trade.getNetRate() - bt.getNetRate()) * q;
					bt.setQuantity(bt.getQuantity() - q);
					q = 0;
				}else{
					System.out.println("(" + df.format(trade.getNetRate()) +"-" + df.format(bt.getNetRate()) + ")*" + bt.getQuantity());
					sellProfit += (trade.getNetRate() - bt.getNetRate()) * bt.getQuantity();
					q -= bt.getQuantity();
					bt.setQuantity(0);
				}
				if(bt.getQuantity() == 0) {
					buyIndex = buyIndex + 1;
				}
			}
			System.out.println("Sell Profit " + df.format(sellProfit));
			System.out.println("");
			totalProfit += sellProfit;
		}
		
		
		System.out.println("Total Profit " + df.format(totalProfit));
		
		
		double costBasis = 0;
		double remainingStocks = 0;
		for(int i= buyIndex;i<buyTrades.size();i++){
			Trade bt = buyTrades.get(i);
			costBasis += (bt.getQuantity() * bt.getNetRate());
			remainingStocks += bt.getQuantity();
		}
		if(remainingStocks > 0){
			System.out.println("Average " + costBasis/remainingStocks + " Stocks " + remainingStocks);
		}
		
		System.out.println("-----------------------------");
	}
	

	public static void main(String args[]){
		IndustryProfitCalculationProgram cp = new IndustryProfitCalculationProgram();
		cp.execute(false, args);
	}
	
}
