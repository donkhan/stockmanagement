package core;
import java.util.Comparator;

public class StockSorter implements Comparator<Stock>{

	public int compare(Stock s1, Stock s2) {
		
		if(s1.getTotalQuantity() == 0 && s2.getTotalQuantity() == 0){
			return 0;
		}
		
		if(s1.getTotalQuantity() == 0 && s2.getTotalQuantity() != 0){
			return 1;
		}
		
		if(s1.getTotalQuantity() != 0 && s2.getTotalQuantity() == 0){
			return -1;
		}
		
		
		double s1_gain = (s1.getAverage()  - s1.getCurrentPrice()) * s1.getTotalQuantity();
		double s2_gain = (s2.getAverage()  - s2.getCurrentPrice()) * s2.getTotalQuantity();
		
		return s1_gain > s2_gain ? 1 : -1;
	}
	
}
