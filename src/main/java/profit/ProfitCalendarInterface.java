package profit;

import java.util.Calendar;

public interface ProfitCalendarInterface extends Comparable<ProfitCalendarInterface>{
	public int getBuyTrades();
	public int getSellTrades();
	public void setSellTrades(int i);
	public void setBuyTrades(int i);	
	
	public Double getProfit();
	public void setProfit(Double d);
	
	public double getTotalBuyAmount();
	public void setTotalBuyAmount(double d);
	public double getTotalSellAmount();
	public void setTotalSellAmount(double d);
	public double getTotalTurnOver();
	public void setTotalTurnOver(double d);
	public String getPrefix();
	
	public Calendar getCalendar();
}
