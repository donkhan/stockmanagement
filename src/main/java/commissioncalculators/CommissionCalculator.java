package commissioncalculators;
import core.Stock;
import core.Trade;


public interface CommissionCalculator {

	void calculateCommission(Trade trade);
	void adjustCurrentPrice(Stock stock);
}
