package external;

import services.StockService;

/**
 * This Service will be used in development process and won't consume any internet bandwidth
 * @author kkhan
 *
 */
public class InternalStockService implements StockService{
	
	public double getCurrentPrice(String singleURL) {
		if(singleURL.equals("http://www.moneycontrol.com/india/stockpricequote/steel-large/steelauthorityindia/SAI")){
			return 47.5d;
		}
		return 0;
	}

	public void setMaxRetries(int maxRetries) {
		// TODO Auto-generated method stub
		
	}

	
}
