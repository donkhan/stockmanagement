package commissioncalculators;
import core.Trade;
import util.Global;


public class KotakCommissionCalculator extends SEBICommissionCalculator implements CommissionCalculator{

	private void setCommission(Trade trade,double transactionAmount,double stockPrice,double quantity){
		double commission = transactionAmount * .49/100;
		
		if(commission <= 21){
			commission = 21;
		}
		double _sebiMaxCommission = getMaxCommission(stockPrice,quantity);
		if(commission > _sebiMaxCommission){
			commission = _sebiMaxCommission;
		}
		
		if(trade.getTradeType().equals(Trade.INTRA_DAY)){
			commission = 42;
		}
		
		if(Global.debug){
			System.out.println("Brokerage " + commission);
		}
		if(trade.getTradeType().equals("R")){
			trade.setExtraCost(0);
			return;
		}
		
		double extraCost = commission;
		double serviceTaxOnBrokeragePercentage = getBrokerageCommission(trade) + getExtraCess(trade);
		if(Global.debug){
			System.out.println("Service Tax On Brokerage Percentage " + serviceTaxOnBrokeragePercentage);
		}
		double serviceTaxOnBrokerage = serviceTaxOnBrokeragePercentage *commission / 100;
		double stt = 14;
		
		if(Global.debug){
			System.out.println("Service Tax On Brokerage " + serviceTaxOnBrokerage);
		}
		
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
		double transactionAmount = trade.getQuantity() * trade.getGrossRate();
		setCommission(trade,transactionAmount,trade.getGrossRate(),trade.getQuantity());
	}


		
}
