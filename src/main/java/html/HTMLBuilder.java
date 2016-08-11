package html;

import java.text.NumberFormat;
import java.util.Map;

import core.Stock;
import util.MathUtils;


public class HTMLBuilder {
	
	public void buildContent(Map<String,Stock> stocks,StringBuffer buffer,StringBuffer subject){
		buffer.append("<html><body><table><tr><td>Stock</td><td>Avg/Mkt</td><td>Ivt/Mkt</td><td>Gain/Loss</td><td>Pft</td></tr>");
		double totalInvestedValue = 0, totalProfit = 0, totalMarketValue = 0;
		Object[] array = stocks.values().toArray();
		//Arrays.sort(array,new StockSorter());
		for(Object o : array){
			Stock stock = (Stock)o;
			String stockName = stock.getName();
			if(stockName.length() > 15){
				stockName = stockName.substring(0,15);
			}
			String color = stock.getAverage() > stock.getCurrentPrice() ? "red" : "green";
			if(stock.getTotalQuantity() == 0){
				color = "yellow";
			}
			buffer.append("<tr>" +
					"<td bgcolor= "+ color+ ">"+ stockName + "(" + stock.getTotalQuantity() + ")</td>" +
					"<td>"+ print(stock.getAverage()) + "/" + 
					print(stock.getCurrentPrice()) + "</td>" + 
					"<td>"+ print(stock.getAverage() * stock.getTotalQuantity()) + "/" +   
					 print(stock.getCurrentPrice() * stock.getTotalQuantity()) + "</td>" +
					"<td>"+ print((stock.getCurrentPrice()-stock.getAverage()) * stock.getTotalQuantity()) + "</td>" +
					"<td>"+ print(stock.getProfitRealised()) + "</td>" +
					"</tr>");
			totalInvestedValue += (stock.getAverage() * stock.getTotalQuantity());
			totalMarketValue += (stock.getCurrentPrice() * stock.getTotalQuantity());
			totalProfit += stock.getProfitRealised();
		}
		buffer.append("</table><br><br>");
		buffer.append(getSummary(totalInvestedValue,totalMarketValue,totalProfit));
		subject.append(print(totalMarketValue - totalInvestedValue));
		buffer.append("</body></html>");
		System.out.println("content is prepared");
	}
	
	private StringBuffer getSummary(double totalValue, double marketValue,double totalProfit){
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("<b> Total Value " + print(totalValue) 
				+ " <br><br> Total Market Value " + print(marketValue)
				+ " <br><br> Imaginary Loss/Gain " + print(marketValue - totalValue)
				+ " <br><br> Profit Realised " + print(totalProfit)
		);
		return buffer;
	}

	private String print(double d){
		d = MathUtils.Round(d,2);
		return NumberFormat.getInstance().format(d);
	}
}
