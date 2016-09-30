package core;

import java.util.ArrayList;
import java.util.List;

public class StakeHolder {
	
	private String name;
	private List<StakeHolderTransaction> transactionList = new ArrayList<StakeHolderTransaction>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<StakeHolderTransaction> getTransactionList() {
		return transactionList;
	}
	public void setTransactionList(List<StakeHolderTransaction> transactionList) {
		this.transactionList = transactionList;
	}
	
	public StakeHolder(String name){
		this.name = name;
	}
	
	public void add(StakeHolderTransaction sht){
		transactionList.add(sht);
	}
	
	public double getTotalInvestment() {
		double t = 0;
		for(StakeHolderTransaction sht : transactionList){
			t += sht.getAmount();
		}
		return t;
	}
	
	private double sharePercentage;
	public double getSharePercentage() {
		return sharePercentage;
	}
	public void setSharePercentage(double sharePercentage) {
		this.sharePercentage = sharePercentage;
	}
	
	public String toString(){
		return "Name " + name + " Investment " + getTotalInvestment();
	}
	
	
}
