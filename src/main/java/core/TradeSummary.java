package core;

import java.util.Date;

public class TradeSummary {

	private int noOfTrades;
	private int buyTrades;
	private int sellTrades;
	private Date firstTradeDate;
	
	private double totalTurnOver;
	private double totalProfit;
	public int getNoOfTrades() {
		return noOfTrades;
	}
	public void setNoOfTrades(int noOfTrades) {
		this.noOfTrades = noOfTrades;
	}
	public int getBuyTrades() {
		return buyTrades;
	}
	public void setBuyTrades(int buyTrades) {
		this.buyTrades = buyTrades;
	}
	public int getSellTrades() {
		return sellTrades;
	}
	public void setSellTrades(int sellTrades) {
		this.sellTrades = sellTrades;
	}
	public Date getFirstTradeDate() {
		return firstTradeDate;
	}
	public void setFirstTradeDate(Date firstTradeDate) {
		this.firstTradeDate = firstTradeDate;
	}
	public double getTotalTurnOver() {
		return totalTurnOver;
	}
	public void setTotalTurnOver(double totalTurnOver) {
		this.totalTurnOver = totalTurnOver;
	}
	public double getTotalProfit() {
		return totalProfit;
	}
	public void setTotalProfit(double totalProfit) {
		this.totalProfit = totalProfit;
	}
	
	public void incrementBuyTrade(){
		buyTrades++;
	}
	
	public void incrementSellTrade(){
		sellTrades++;
	}
	
	public void incrementTurnOver(double turnOver){
		totalTurnOver += turnOver;
	}
	
}
