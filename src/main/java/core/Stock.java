package core;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import commissioncalculators.CommissionCalculator;
import util.Global;


public class Stock {

	private String name;
	private String url;
	private double currentPrice;
	private double average;
	private int totalQuantity;
	private double profitRealised = 0;
	private String broker;
	private double imaginaryProfit;
	
	public String getBroker() {
		return broker;
	}

	public double getImaginaryProfit() {
		return imaginaryProfit;
	}

	public void setImaginaryProfit(double imaginaryProfit) {
		this.imaginaryProfit = imaginaryProfit;
	}

	public void setAverage(double average) {
		this.average = average;
	}

	public void setProfitRealised(double profitRealised) {
		this.profitRealised = profitRealised;
	}

	public void setTradeList(List<Trade> tradeList) {
		this.tradeList = tradeList;
	}

	public void setBroker(String broker) {
		this.broker = broker;
	}

	public void appendProfit(double profit){
		profitRealised += profit;
	}
	
	public double getProfitRealised(){
		return profitRealised;
	}
	
	public int getTotalQuantity() {
		return totalQuantity;
	}
	
	public void setTotalQuantity(int totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public double getAverage() {
		return average;
	}

	public List<Trade> getTradeList() {
		return tradeList;
	}
	

	private void printNetRate(Trade trade,double unitExtraCost){
		if(Global.debug){
            System.out.println("Extra Cost including Commission " + trade.getExtraCost() + " Per Unit " + unitExtraCost);
            System.out.println("Net Rate " + trade.getNetRate());
        }
	}
	
    private void handleBuy(Trade trade,TradeSummary summary){
        double unitExtraCost = trade.getExtraCost()/trade.getQuantity();
        if(trade.getTradeType().equals(Trade.RIGHTS)){
        	unitExtraCost = 0;
        }
    	double netRate = trade.getGrossRate() + unitExtraCost;
        trade.setNetRate(netRate);
        printNetRate(trade,unitExtraCost);
        double price = netRate * trade.getQuantity();
        average = ((totalQuantity * average) + price) / (totalQuantity + trade.getQuantity());
        totalQuantity += trade.getQuantity();
        summary.incrementBuyTrade();
        summary.incrementTurnOver(trade.getNetRate() * trade.getQuantity());
    }

    private void handleSell(Trade trade,boolean real,TradeSummary summary){
        double unitExtraCost = trade.getExtraCost()/trade.getQuantity();
        double netRate = trade.getGrossRate() - unitExtraCost;
        trade.setNetRate(netRate);
        printNetRate(trade,unitExtraCost);
        if(real) {
            totalQuantity -= trade.getQuantity();
        	addProfitAndAdjustAverage(trade);
            summary.incrementSellTrade();
            summary.incrementTurnOver(trade.getNetRate() * trade.getQuantity());
        }
    }
    

    
    private Trade getTrade(int tradeId){
    	List<Trade> trades = getTradeList();
    	for(Trade trade : trades){
    		if(trade.getId() == tradeId){
    			return trade;
    		}
    	}
    	return new Trade(12);
    }

    private void addProfitAndAdjustAverage(Trade trade){
        double profit = 0;
        List<Integer> buyTradeIds = trade.getBuyTradeIds();
        if(buyTradeIds != null){
        	for(int buyTradeId : buyTradeIds){
        		Trade buyTrade = getTrade(buyTradeId);
        		profit = (trade.getNetRate() - buyTrade.getNetRate()) * trade.getQuantity();
        		double tradeCost = buyTrade.getNetRate() * trade.getQuantity();
        		double totalCost = getAverage() * (trade.getQuantity() + totalQuantity);
        		totalCost -= tradeCost;
        		double newAverage = totalCost / totalQuantity;
        		setAverage(newAverage);
        	}
        }else{
            profit = (trade.getNetRate() - getAverage()) * trade.getQuantity();
        }
        if(Global.debug){
        	System.out.println("Trade Profit "  + profit);
        }
        trade.setProfit(profit);
        appendProfit(profit);
    }

	public void addToTradeList(Trade trade,TradeSummary summary) {
		tradeList.add(trade);
		if(trade.getTradeType().equals("B") || trade.getTradeType().equals("R") 
				|| trade.getTradeType().equals("BONUS")){
			handleBuy(trade,summary);
		}
		if(trade.getTradeType().equals("S")){
            handleSell(trade,true,summary);
		}
        
		if(Global.debug){
			System.out.println("Trade Value " + trade.getQuantity() * trade.getNetRate());
			print();
			Global.printLine();
		}
	}
	
	private List<Trade> tradeList = new ArrayList<Trade>();
	private List<Trade> positiveTrades = new ArrayList<Trade>();
	
	public void addToPositiveTrade(Trade trade){
		for(Trade t : positiveTrades){
			if(t.getId() == trade.getId()) return;
		}
		positiveTrades.add(trade);
	}
	
	public String getLookOutTrades(){
		String x = "";
		if(positiveTrades.size() == 0) {
			return "";
		}
		for(Trade trade : positiveTrades){
			x += trade.getId() + ",";
		}
		return x.substring(0,x.length()-1);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
    public void setUrl(String url) {
		this.url = url;
	}

	public String toString(){
		//return  name + "  " + tradeList + "\n";
		return name + " " + totalQuantity;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}
	
	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}
	
	public void print(){
		System.out.println(name + " Avg: " + average + " Qty: " + totalQuantity + " Total: " + average*totalQuantity + " Profit Realised " + profitRealised);
	}

	public void calculateImaginaryProfit(CommissionCalculator cc) {
		if(this.getTotalQuantity() == 0){
			return;
		}
		Trade imaginaryTrade = new Trade(12);
		imaginaryTrade.setQuantity(this.getTotalQuantity());
		imaginaryTrade.setTradeType("S");
		imaginaryTrade.setGrossRate(getCurrentPrice());
        imaginaryTrade.setBroker(getBroker());
        imaginaryTrade.setTransactionTime(new GregorianCalendar());
        imaginaryTrade.setName(getName());
		cc.calculateCommission(imaginaryTrade);
        handleSell(imaginaryTrade,false,null);
        imaginaryProfit = (imaginaryTrade.getNetRate() - getAverage()) * getTotalQuantity();

	}


}
