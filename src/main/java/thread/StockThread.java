package thread;

import external.MoneyControlStockService;

/**
 * Created by kkhan on 07/07/16.
 */
public class StockThread extends Thread implements Runnable {

    private String stockURL;
    private double stockPrice;
    private String stockName;

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public StockThread(String stockURL,String stockName){
        this.stockURL = stockURL;
        this.stockName = stockName;
    }


    public double getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(double stockPrice) {
        this.stockPrice = stockPrice;
    }

    public void run(){
        MoneyControlStockService mcs = new MoneyControlStockService();
        stockPrice = mcs.getCurrentPrice(stockURL);
        System.out.println("Price of " + stockName + " " + stockPrice);
    }
}
