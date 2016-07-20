package commissioncalculators;
import core.Stock;
import core.Trade;


public class KotakCommissionCalculator extends SEBICommissionCalculator implements CommissionCalculator{

	private void setCommission(Trade trade,double transactionAmount,double stockPrice,double quantity){
		double commission = transactionAmount * .4/100;
		if(commission <= 21){
			commission = 21;
		}
		double _sebiMaxCommission = getMaxCommission(stockPrice,quantity);
		if(commission > _sebiMaxCommission){
			commission = _sebiMaxCommission;
		}
		
		if(trade.getTradeType().equals("R")){
			trade.setExtraCost(0);
			return;
		}
		
		double extraCost = commission;
		double serviceTaxOnBrokerage = 14.5*commission / 100;
		double stt = 4;
		
		extraCost += serviceTaxOnBrokerage;
		extraCost += stt;
		
		trade.setExtraCost(extraCost);
	}


	public void calculateCommission(Trade trade) {
		double transactionAmount = trade.getQuantity() * trade.getGrossrate();
		setCommission(trade,transactionAmount,trade.getGrossrate(),trade.getQuantity());
	}


	public void adjustCurrentPrice(Stock stock) {
		if(stock.getTotalQuantity() == 0){
			return;
		}
		double transactionAmount = stock.getTotalQuantity() * stock.getCurrentPrice();
		double commission = 0;
		transactionAmount -= commission;
		stock.setCurrentPrice(transactionAmount/stock.getTotalQuantity());
	}
	
}
