package thread;

import services.StockService;
import util.Global;

/**
 * Created by kkhan on 07/07/16.
 */
public class StockThread extends Thread implements Runnable {

    private String stockURL;
    private double stockPrice;
    private String stockName;
    private int maxRetries;

    public String getStockURL() {
		return stockURL;
	}

	public void setStockURL(String stockURL) {
		this.stockURL = stockURL;
	}

	public int getMaxRetries() {
		return maxRetries;
	}

	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}

	public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    private String mode;
    
    public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public StockThread(String stockURL,String stockName,int maxRetries,String mode){
        this.stockURL = stockURL;
        this.stockName = stockName;
        this.maxRetries = maxRetries;
        this.mode = mode;
    }


    public double getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(double stockPrice) {
        this.stockPrice = stockPrice;
    }

    public void run(){
        StockService mcs = Global.getStockService(mode);
        mcs.setMaxRetries(maxRetries);
        stockPrice = mcs.getCurrentPrice(stockURL);
        System.out.println("Price of " + stockName + " " + stockPrice);
    }
}
