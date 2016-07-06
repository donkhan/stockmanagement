package commissioncalculators;

import core.Stock;
import core.Trade;

public class MoneyPalmCommissionCalculator implements CommissionCalculator{


	public void calculateCommission(Trade trade) {
		double totalCommission = trade.getGrossrate() * .0014;
		trade.setCommission(totalCommission);
		return;
		
	}


	public void adjustCurrentPrice(Stock stock) {
		if(stock.getTotalQuantity() == 0){
			return;
		}
		double transactionAmount = stock.getTotalQuantity() * stock.getCurrentPrice();
		double commission =  transactionAmount * .0014;
		transactionAmount -= commission;
		stock.setCurrentPrice(transactionAmount/stock.getTotalQuantity());
	}
	
	public static void main(String args[]){
		MoneyPalmCommissionCalculator kc = new MoneyPalmCommissionCalculator();
		Trade trade = new Trade();
		trade.setName("Cadilla");
		trade.setQuantity(5);
		trade.setTradeType("B");
		trade.setGrossrate(1917.86);
		kc.calculateCommission(trade);
		System.out.println("Commission " + trade.getCommission());
	}

	
}
