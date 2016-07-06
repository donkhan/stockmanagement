package core;

import java.util.ArrayList;
import java.util.List;

import util.MathUtils;

import commissioncalculators.CommissionCalculator;


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
	
	private double findExtraCharges(double price){
		double extraCharges = (price * .4) / 100;

		return extraCharges;
	}

    private void handleBuy(Trade trade){
        double netRate = trade.getGrossrate() + (trade.getCommission() / trade.getQuantity());
        double price = netRate * trade.getQuantity();
        double extraCharges = findExtraCharges(price);
        double extraNetChargePerStock = extraCharges / trade.getQuantity();
        netRate += extraNetChargePerStock;
        trade.setNetRate(netRate);
        price += extraCharges;
        average = ((totalQuantity * average) + price) / (totalQuantity + trade.getQuantity());
        totalQuantity += trade.getQuantity();
        trade.print();
    }

    private void handleSell(Trade trade,boolean real){
        double netRate = trade.getGrossrate() - (trade.getCommission() / trade.getQuantity());
        double price = netRate * trade.getQuantity();
        double extraCharges = findExtraCharges(price);
        double extraNetChargePerStock = extraCharges / trade.getQuantity();
        netRate -= extraNetChargePerStock;
        trade.setNetRate(netRate);

        if(real) {
            price -= extraCharges;
            totalQuantity -= trade.getQuantity();
            addProfit(trade);
            trade.print();
        }
        
    }

    private void addProfit(Trade trade){
        double profit = 0;
        if(trade.getBuyRate() != 0.0d){
            profit = (trade.getNetRate() - trade.getBuyRate()) * trade.getQuantity();
            double newAverage = ((totalQuantity * getAverage()) + profit) / totalQuantity;
            setAverage(newAverage);
        }else{
            profit = (trade.getNetRate() - getAverage()) * trade.getQuantity();
        }
        appendProfit(profit);
    }

	public void addToTradeList(Trade trade) {
		tradeList.add(trade);
		if(trade.getTradeType().equals("B") || trade.getTradeType().equals("R") 
				|| trade.getTradeType().equals("BONUS")){
			handleBuy(trade);
		}
		if(trade.getTradeType().equals("S")){
            handleSell(trade,true);
		}
        print();
	}
	
	private List<Trade> tradeList = new ArrayList<Trade>();

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
		return  name + "  " + tradeList + "\n";
	}

	public double getCurrentPrice() {
		return currentPrice;
	}
	
	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}
	
	public void print(){
		System.out.println("Stock " + name + " Avg: " + average + " Qty: " + totalQuantity + " Total: " + average*totalQuantity + " Profit Realised " + profitRealised);
	}

	public void calculateImaginaryProfit(CommissionCalculator cc) {
		if(this.getTotalQuantity() == 0){
			return;
		}
		Trade imaginaryTrade = new Trade();
		imaginaryTrade.setQuantity(this.getTotalQuantity());
		imaginaryTrade.setTradeType("S");
		imaginaryTrade.setGrossrate(getCurrentPrice());
        imaginaryTrade.setBroker(getBroker());
        imaginaryTrade.setTransactionTime(new java.util.Date());
        imaginaryTrade.setName(getName());
		cc.calculateCommission(imaginaryTrade);
        handleSell(imaginaryTrade,false);
        imaginaryProfit = (imaginaryTrade.getNetRate() - getAverage()) * getTotalQuantity();

	}
}
