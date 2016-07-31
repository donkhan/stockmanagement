package core;

import java.util.Calendar;

public class StakeHolderTransaction {
	
	private Calendar transactionDate;
	private double amount;
	public Calendar getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(Calendar transactionDate) {
		this.transactionDate = transactionDate;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
}
