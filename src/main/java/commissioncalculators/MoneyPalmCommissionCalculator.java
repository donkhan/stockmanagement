package commissioncalculators;

import core.Stock;
import core.Trade;

public class MoneyPalmCommissionCalculator implements CommissionCalculator{


	public void calculateCommission(Trade trade) {
		double totalCommission = trade.getGrossRate() * .0014;
		trade.setExtraCost(totalCommission);
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
}
