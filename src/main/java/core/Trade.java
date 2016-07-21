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

	
	private String tradeType;
	private String broker;

	private int buyTradeId;
	
	public int getBuyTradeId() {
		return buyTradeId;
	}

	public void setBuyTradeId(int buyTradeId) {
		this.buyTradeId = buyTradeId;
	}

	public static String BUY = "B";
	public static String SELL = "S";
	
	public String getBroker() {
		return broker;
	}
	public void setBroker(String broker) {
		this.broker = broker;
	}


	public String getTradeType() {
		return tradeType;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
		if(tradeType.equals(SELL)){
			setExtraCost(20);
		}
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


	private Date transactionTime;
	public Date getTransactionTime() {
		return transactionTime;
	}
	public void setTransactionTime(Date time) {
		if(time.getYear() < 2000)
			time.setYear(time.getYear() + 2000);
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

	private double stt;

	public double getStt() {
		return stt;
	}
	public void setStt(double stt) {
		this.stt = stt;
	}
	
	private double extraCost;

	public double getExtraCost() {
		return extraCost;
	}

	public void setExtraCost(double extraCost) {
		this.extraCost = extraCost;
	}
	
	public double getUnitAcquistionCost(){
		return extraCost/quantity;
	}
	
}
