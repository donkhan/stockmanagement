package core;
import java.util.Date;


public class Trade {

	private int id;
	private long quantity;
	private double grossrate;
	private double netRate;

	private boolean soldOff = false;
	private int unsoldUnits = 0;

	public static int TRADE_ID = 1;

	public Trade(){
		id = ++TRADE_ID;
	}
	
	
	public int getId() {
		return id;
	}

	
	public int getUnsoldUnits() {
		return unsoldUnits;
	}

	public void setUnsoldUnits(int unsoldUnits) {
		this.unsoldUnits = unsoldUnits;
	}

	public boolean isSoldOff() {
		return soldOff;
	}

	public void setSoldOff(boolean soldOff) {
		this.soldOff = soldOff;
	}

	public double getNetRate() {
		return netRate;
	}

	public void setNetRate(double netRate) {
		this.netRate = netRate;
	}

	private double commission;
	private Date tradeTime;
	private String tradeType;

	private double extraCharges;

	public double getExtraCharges() {
		return extraCharges;
	}

	public void setExtraCharges(double extraCharges) {
		this.extraCharges = extraCharges;
	}

	private double cess;
	private String broker;

	private double buyRate;
	private double sellRate;
	
	public static String BUY = "B";
	public static String SELL = "S";
	
	public double getBuyRate() {
		return buyRate;
	}
	public void setBuyRate(double buyRate) {
		this.buyRate = buyRate;
	}
	public double getSellRate() {
		return sellRate;
	}
	public void setSellRate(double sellRate) {
		this.sellRate = sellRate;
	}
	public String getBroker() {
		return broker;
	}
	public void setBroker(String broker) {
		this.broker = broker;
	}
	public double getCess() {
		return cess;
	}
	public void setCess(double cess) {
		this.cess = cess;
	}
	public Date getTradeTime() {
		return tradeTime;
	}
	public void setTradeTime(Date tradeTime) {
		this.tradeTime = tradeTime;
	}
	public String getTradeType() {
		return tradeType;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	public long getQuantity() {
		return quantity;
	}
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	public double getGrossrate() {
		return grossrate;
	}
	public void setGrossrate(double grossrate) {
		this.grossrate = grossrate;
	}


	public double getCommission() {
		return commission;
	}
	public void setCommission(double commission) {
		this.commission = commission;
	}

	private Date transactionTime;
	public Date getTransactionTime() {
		return transactionTime;
	}
	public void setTransactionTime(Date time) {
		this.transactionTime = time;
	}
	private String name;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String getTradeTypeSymbol(){
		if(tradeType.equals("BONUS")){
			return "Got in BONUS";
		}
		return tradeType.equals("R") ? "Bought in Rights " : (tradeType.equals("S") ? "Sold" : "Bought") + " through " + broker;
	}
	

	
	public String toString(){
		double cost = getNetRate() * getQuantity();
		return id + ":" + getTradeTypeSymbol() + " " + quantity
				+ " " + name +  " " + cost;
	}
	
	public void print(){
		System.out.println(this);
	}

	private double transactionTax;

	public double getTransactionTax() {
		return transactionTax;
	}
	public void setTransactionTax(double transactionTax) {
		this.transactionTax = transactionTax;
	}
	
}
