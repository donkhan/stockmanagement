package commissioncalculators;
import core.Stock;
import core.Trade;
import util.Global;
import util.MathUtils;


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
		if(trade.getTradeType().equals("I")){
			commission = getIntraDayCommission(trade);
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
		double stt = 14;
		
		extraCost += serviceTaxOnBrokerage;
		extraCost += stt;
		
		trade.setExtraCost(extraCost);
	}
	
	private double getIntraDayCommission(Trade trade) {
		double totalValue = trade.getIntraBuyRate() * trade.getQuantity();
		totalValue += (trade.getIntraSellRate() * trade.getQuantity());
		double c = totalValue * .01/100;
		if(c < 20){
			c = 20;
		}
		double d = c/(trade.getQuantity()*2);
		trade.setIntraBuyRate(MathUtils.Round(trade.getIntraBuyRate() + d, 2));
		trade.setIntraSellRate(MathUtils.Round(trade.getIntraSellRate()-d, 2));
		return d;
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
