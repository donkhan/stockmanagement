package profit;

import java.util.Calendar;

public class ProfitCalendar implements Comparable<ProfitCalendar>{
	
	private Calendar calendar;
	public ProfitCalendar(Calendar calendar, Double d) {
		this.calendar = calendar;
		this.profit = d;
		buyTrades = 0;
		sellTrades = 0;
		displayString = months[calendar.get(Calendar.MONTH)] + "-" + calendar.get(Calendar.YEAR);
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
	public void setDisplayString(String displayString) {
		this.displayString = displayString;
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
	public int compareTo(ProfitCalendar o) {
		Calendar c = o.calendar;
		if(c.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)){
			return c.get(Calendar.MONTH) - calendar.get(Calendar.MONTH);
		}
		return c.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
		
	}

	private String displayString;
	private static String months[] = new String[]{"Jan", "Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
	public void print() {
		System.out.println(displayString + "=" + profit + "," + getBuyTrades() + "," + getSellTrades());
	}
	
	public String getDisplayString(){
		return months[calendar.get(Calendar.MONTH)] + "-" + calendar.get(Calendar.YEAR);
	}
		
}
