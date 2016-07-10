package core;

import java.util.Date;

public class TradeSummary {

	private int noOfTrades;
	private int noOfBuyTrades;
	private int noOfSellTrades;
	private Date firstTradeDate;
	
	private double totalTurnOver;
	private double totalProfit;
	public int getNoOfTrades() {
		return noOfTrades;
	}
	public void setNoOfTrades(int noOfTrades) {
		this.noOfTrades = noOfTrades;
	}
	public int getNoOfBuyTrades() {
		return noOfBuyTrades;
	}
	public void setNoOfBuyTrades(int noOfBuyTrades) {
		this.noOfBuyTrades = noOfBuyTrades;
	}
	public int getNoOfSellTrades() {
		return noOfSellTrades;
	}
	public void setNoOfSellTrades(int sellTrades) {
		this.noOfSellTrades = sellTrades;
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
		noOfBuyTrades++;
	}
	
	public void incrementSellTrade(){
		noOfSellTrades++;
	}
	
	public void incrementTurnOver(double turnOver){
		totalTurnOver += turnOver;
	}
	
}
