package external;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import services.StockService;


public class MoneyControlStockService implements StockService{

	private int  MAX_RETRIES = 10;
	public static void main(String args[]){
		System.out.println(new MoneyControlStockService().getCurrentPrice("http://www.moneycontrol.com/india/stockpricequote/refineries/indianoilcorporation/IOC"));
	}
	
	public double getCurrentPrice(String singleURL){
		return getCurrentPrice(singleURL,0);
	}
	
	private double getCurrentPrice(String singleURL, int retry){
		if(retry == MAX_RETRIES){
			return 0;
		}
		double d = 0;
		try{
			URL url = new URL(singleURL);
			InputStream is = url.openStream();
			byte b[] = new byte[15000];
			while( is.read(b) != -1){
				String s = new String(b);
				int index = s.indexOf("Nse_Prc_tick");
				if(index != -1){
					int endIndex = s.indexOf(">");
					String content = s.substring(index);
					content = content.substring(content.indexOf("<strong>"));
					content = content.substring(content.indexOf(">")+1);
					
					endIndex = content.indexOf("</strong>");
					d = Double.parseDouble(content.substring(0,endIndex));
					is.close();
					break;
				}
			}
		}catch (MalformedURLException e) {
			System.out.println("Malformed URL " + singleURL);
			return getCurrentPrice(singleURL, retry+1);
		}catch (IOException e) {
			System.out.println("Failed to fetch current price for " + singleURL);
			return getCurrentPrice(singleURL, retry+1);
		}catch(NumberFormatException ne){
			return getCurrentPrice(singleURL,retry+1);
		}catch(Throwable t){
			return getCurrentPrice(singleURL,retry+1);
		}
		if(d == 0.0d){
			try{
				Thread.currentThread().sleep(2*1000);
			}catch(Exception e){
				
			}
			return getCurrentPrice(singleURL,retry+1);
		}
		//System.out.println(singleURL + " " + d);
		return d;
	}
	
}
