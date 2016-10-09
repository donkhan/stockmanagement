package core;

import java.util.Calendar;

public class LedgerEntry {

	private String description;
	private Calendar time;
	private double amount;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Calendar getTime() {
		return time;
	}
	public void setTime(Calendar time) {
		this.time = time;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public String toString(){
		return time.getTime() + "  " + getDescription() + "  " + getAmount();
	}
	
}
