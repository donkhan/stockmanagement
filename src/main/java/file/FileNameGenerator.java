package file;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class FileNameGenerator {

	public static String getFileByTodaysDate(String extension){
		Calendar calendar = new GregorianCalendar();
		String file = calendar.get(Calendar.DATE) + "-" +
				(calendar.get(Calendar.MONTH)+1) + "-" + 
				calendar.get(Calendar.YEAR) + "." +extension;
		String dir = System.getProperty("java.io.tmpdir");
		file = dir + System.getProperty("file.separator") + file;
		System.out.println("Generated File Name " + file);
		return file;
	}
	
	public static String getProfitFile(String extension,String prefix){
		String file = prefix + "-profit-report" + "." +extension;
		String dir = System.getProperty("java.io.tmpdir");
		file = dir + System.getProperty("file.separator") + file;
		System.out.println("Generated File Name " + file);
		return file;
	}
}
