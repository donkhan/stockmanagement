package commissioncalculators;
import core.Stock;
import core.Trade;


public class KotakCommissionCalculator extends SEBICommissionCalculator implements CommissionCalculator{

	private double getCommission(double transactionAmount,double stockPrice,double quantity){
		
		double commission = transactionAmount * .4 / 100;
		double _sebiMaxCommission = getMaxCommission(stockPrice,quantity);
		if(commission > _sebiMaxCommission){
			commission = _sebiMaxCommission;
		}
		return commission;
	}
	
	@Override
	public void calculateCommission(Trade trade) {
		if(!trade.getTradeType().equals("R")){
			double transactionAmount = trade.getQuantity() * trade.getGrossrate();
			trade.setCommission(getCommission(transactionAmount,trade.getGrossrate(),trade.getQuantity())/trade.getQuantity());
		}
	}

	@Override
	public void adjustCurrentPrice(Stock stock) {
		if(stock.getTotalQuantity() == 0){
			return;
		}
		double transactionAmount = stock.getTotalQuantity() * stock.getCurrentPrice();
		double commission = getCommission(transactionAmount,stock.getCurrentPrice(),stock.getTotalQuantity());
		transactionAmount -= commission;
		stock.setCurrentPrice(transactionAmount/stock.getTotalQuantity());
	}

	
	public static void main(String args[]){
		KotakCommissionCalculator kc = new KotakCommissionCalculator();
		System.out.println("Commission " + kc.getCommission(2659.91*4,2659.91 , 4));
	}
	
}
