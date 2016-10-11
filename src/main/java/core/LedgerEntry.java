package core;

import java.util.Calendar;

public class LedgerEntry implements Comparable<LedgerEntry>{

	private String description = "";
	private Calendar time;
	private double amount;
	private String broker;
	
	public String getBroker() {
		return broker;
	}
	public void setBroker(String broker) {
		this.broker = broker;
	}
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
		time.set(Calendar.HOUR_OF_DAY, 17);
		time.set(Calendar.MINUTE, 0);
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);
		this.time = time;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public String toString(){
		return time.getTime()  + "  " + getAmount() + "  " + getDescription();
	}
	@Override
	public int compareTo(LedgerEntry le) {
		if(le.getTime().getTimeInMillis() == getTime().getTimeInMillis()){
			return 0;
		}
		return le.getTime().after(getTime()) ? -1 : 1;
	}
	
}
