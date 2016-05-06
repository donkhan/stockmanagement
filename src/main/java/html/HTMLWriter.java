package html;
import java.io.FileWriter;

import file.FileNameGenerator;


public class HTMLWriter {

	public String write(StringBuffer content){
		String stg = content.toString();
		
		String file = FileNameGenerator.getFileByTodaysDate("html"); 
		try{
			FileWriter fw = new FileWriter(file);
			fw.write(stg,0,stg.length());
			fw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("Wrote into file " + file);
		return file;
	}
	
	public static void main(String args[]){
		String dir = System.getProperty("java.io.tmpdir");
		System.out.println(dir );
	}
}
 