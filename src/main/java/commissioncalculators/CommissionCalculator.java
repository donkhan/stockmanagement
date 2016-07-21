package commissioncalculators;
import core.Trade;


public interface CommissionCalculator {
	void calculateCommission(Trade trade);
}
