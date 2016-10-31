package core;

public class StockTrack {

	String name;
	double lowerPrice;
	double upperPrice;
	String url;
	double currentPrice = 0;
	
	
	public double getCurrentPrice() {
		return currentPrice;
	}
	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public StockTrack(String name2, double lowerPrice2, double upperPrice2) {
		setName(name2);
		setLowerPrice(lowerPrice2);
		setUpperPrice(upperPrice2);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getLowerPrice() {
		return lowerPrice;
	}
	public void setLowerPrice(double lowerPrice) {
		this.lowerPrice = lowerPrice;
	}
	public double getUpperPrice() {
		return upperPrice;
	}
	public void setUpperPrice(double upperPrice) {
		this.upperPrice = upperPrice;
	}
	
	public String toString(){
		return name + " " + url + " " + lowerPrice + "/" + upperPrice;
	}
}
