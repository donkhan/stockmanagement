package services;

public interface StockService {

	public abstract double getCurrentPrice(String singleURL);
	public abstract void setMaxRetries(int maxRetries);
}
