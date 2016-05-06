package commissioncalculators;

public abstract class SEBICommissionCalculator {

	protected final double getMaxCommission(double stockPrice,double quantity){
		return 2.5/100*stockPrice*quantity;
	}
	
}
