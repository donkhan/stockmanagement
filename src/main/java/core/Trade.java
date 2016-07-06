package core;
import java.util.Date;


public class Trade {

	private long quantity;
	private double grossrate;
	private double commission;
	private Date tradeTime;
	private String tradeType;
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
		return tradeType.equals("R") ? "Bought in Rights " : tradeType.equals("S") ? "Sold" : "Bought" + " through " + broker;
	}
	
	private String getSymbol(){
		return tradeType.equals("B")  ? "+" : "-";
	}
	
	public String toString(){
		return getTradeTypeSymbol() + " " + quantity + " " + name + "@Price " + grossrate + getSymbol() + " with commission " + commission  + " transaction tax "  + transactionTax + " @" + transactionTime;
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
