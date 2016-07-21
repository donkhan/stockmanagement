package commissioncalculators;
import util.Global;
import core.Stock;
import core.Trade;


public class GeojitCommissionCalculator extends SEBICommissionCalculator implements CommissionCalculator{

	private void setCommission(Trade trade,double transactionAmount,double stockPrice,double quantity){
		double commission = transactionAmount * .3/100;
		if(commission <= 20){
			commission = 20;
		}
		double _sebiMaxCommission = getMaxCommission(stockPrice,quantity);
		if(commission > _sebiMaxCommission){
			commission = _sebiMaxCommission;
		}
		if(trade.getTradeType().equals("R")){
			trade.setExtraCost(0);
		}
		
		if(Global.debug){
			System.out.println("Brokerage per Unit " + commission/quantity);
		}
		double extraCost = commission;
		double serviceTaxOnBrokeragePercentage = getBrokerageCommission(trade) + getExtraCess(trade);
		if(Global.debug){
			System.out.println("Service Tax On Brokerage Percentage " + serviceTaxOnBrokeragePercentage);
		}
		double serviceTaxOnBrokerage = serviceTaxOnBrokeragePercentage *commission / 100;
		double stt = 4;
		
		extraCost += serviceTaxOnBrokerage;
		extraCost += stt;
		
		trade.setExtraCost(extraCost);
	}
	
	private double getBrokerageCommission(Trade trade) {
		return ServiceTaxUtil.getServiceTax(trade.getTransactionTime());
	}

	private double getExtraCess(Trade trade){
		return CessUtil.getKrishKalyanCess(trade.getTransactionTime()) + CessUtil.getSwatchBharatCess(trade.getTransactionTime());
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
