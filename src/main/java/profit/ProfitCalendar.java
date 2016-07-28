package profit;

import java.util.Calendar;

public abstract class ProfitCalendar<T> implements Comparable<T>{
	
	private Calendar calendar;
	public ProfitCalendar(Calendar calendar, Double d) {
		this.calendar = calendar;
		this.profit = d;
		buyTrades = 0;
		sellTrades = 0;
	}
	
	private Double profit;
	private int buyTrades;
	private int sellTrades;
	private double totalTurnOver;
	private double totalBuyAmount;
	private double totalSellAmount;
	
	
	public double getTotalBuyAmount() {
		return totalBuyAmount;
	}
	public void setTotalBuyAmount(double totalBuyAmount) {
		this.totalBuyAmount = totalBuyAmount;
	}
	public double getTotalSellAmount() {
		return totalSellAmount;
	}
	public void setTotalSellAmount(double totalSellAmount) {
		this.totalSellAmount = totalSellAmount;
	}

	public double getTotalTurnOver() {
		return totalTurnOver;
	}
	public void setTotalTurnOver(double totalTurnOver) {
		this.totalTurnOver = totalTurnOver;
	}
	public int getSellTrades() {
		return sellTrades;
	}
	public void setSellTrades(int sellTrades) {
		this.sellTrades = sellTrades;
	}
	public Calendar getCalendar() {
		return calendar;
	}
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}
	public Double getProfit() {
		return profit;
	}
	public void setProfit(Double profit) {
		this.profit = profit;
	}
	public int getBuyTrades() {
		return buyTrades;
	}
	public void setBuyTrades(int buyTrades) {
		this.buyTrades = buyTrades;
	}
	
	public abstract String getPrefix();
}
