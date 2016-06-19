package util;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class InternetService {

	public static boolean up(){
		try{
			System.out.println("Going to check internet status");
			URL url = new URL("http://google.com");
			URLConnection connection = url.openConnection();
			connection.setDoOutput(true);
			InputStream in = connection.getInputStream();
			in.close();
		}catch(Throwable t){
			return false;
		}
		return true;
	}
	
	public static void main(String args[]){
		System.out.println("Internet Status " + up());
	}
	
}
