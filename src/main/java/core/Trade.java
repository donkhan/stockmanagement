package core;
import java.util.Calendar;
import java.util.List;


public class Trade implements Comparable<Trade>{

	private int id;
	private long quantity;
	private double grossRate;
	private double netRate;

	private boolean soldOff = false;
	private int unsoldUnits = 0;

	
	public Trade(int id){
		this.id = id;
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

	private List<Integer> buyTradeIds;
	
	public List<Integer> getBuyTradeIds() {
		return buyTradeIds;
	}

	public void setBuyTradeIds(List<Integer> buyTradeIds) {
		this.buyTradeIds = buyTradeIds;
	}

	public static String BUY = "B";
	public static String SELL = "S";
	public static String DELETED = "D";
	public static String RIGHTS = "R";
	
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
	public double getGrossRate() {
		return grossRate;
	}
	public void setGrossRate(double grossrate) {
		this.grossRate = grossrate;
	}


	private Calendar transactionTime;
	public Calendar getTransactionTime() {
		return transactionTime;
	}
	public void setTransactionTime(Calendar time) {
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
				+ " " + name +  " " + cost + "  @" + getTransactionTime().getTime();
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

	private double profit;


	public double getProfit() {
		return profit;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	public int compareTo(Trade t) {
		return t.getTransactionTime().after(getTransactionTime()) ? 1 : -1;
	}
	
	private String referenceRate = "";


	public String getReferenceRate() {
		return referenceRate;
	}


	public void setReferenceRate(String referenceRate) {
		this.referenceRate = referenceRate;
	}


	
	
}
