package core;

import java.util.Calendar;

public class Dividend implements Comparable<Dividend>{

	public String stock;
	private Calendar dividendPaidTime;
	private double value;
	public String getStock() {
		return stock;
	}
	public void setStock(String stock) {
		this.stock = stock;
	}
	public Calendar getDividendPaidTime() {
		return dividendPaidTime;
	}
	public void setDividendPaidTime(Calendar dividendPaidTime) {
		this.dividendPaidTime = dividendPaidTime;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public int compareTo(Dividend dividend) {
		return (int)(dividend.getDividendPaidTime().getTimeInMillis() - getDividendPaidTime().getTimeInMillis());
	}
	
	public String toString(){
		return " " + getStock() + " " + getValue();
	}
	
}
