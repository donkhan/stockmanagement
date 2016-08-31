package external;

import services.StockService;

/**
 * This Service will be used in development process and won't consume any internet bandwidth
 * @author kkhan
 *
 */
public class InternalStockService implements StockService{
	
	public double getCurrentPrice(String singleURL) {
		return 0;
	}

	public void setMaxRetries(int maxRetries) {
		// TODO Auto-generated method stub
		
	}

	
}
