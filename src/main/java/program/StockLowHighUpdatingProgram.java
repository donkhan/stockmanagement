package program;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;

import core.db.WorkBookManager;
import jxl.Sheet;
import jxl.Workbook;

public class StockLowHighUpdatingProgram extends AbstractProgram{

	@Override
	protected long getTimerInterval() {
		return -1;
	}

	@Override
	protected void execute(boolean force, String[] args){
		Workbook workBook = new WorkBookManager().getWorkBook();
		Sheet sheet = workBook.getSheet(1);
		int rows = sheet.getRows();
		for(int i = 0;i<rows;i++){
			System.out.println(sheet.getCell(0, i).getContents() 
					+ " " + sheet.getCell(1, i).getContents());
			updateFile(sheet.getCell(1, i).getContents(),sheet.getCell(0, i).getContents());
		}
		
		
	}
	
	
	private void updateFile(String singleURL,String name) {
		Calendar calendar = new GregorianCalendar();
		try {
			URL url = new URL(singleURL);
			//InputStream is = new FileInputStream("xyz.html");
			InputStream is = url.openStream();
			double low = 0;
			double high = 0;
			byte b[] = new byte[15000];
			while( is.read(b) != -1){
				String s = new String(b);
				if(s.contains("b_low_sh")){
					low = getValue(s,"b_low_sh");
				}
				if(s.contains("b_high_sh")){
					high = getValue(s,"b_high_sh");
				}
			}
			is.close();
			write(name,low,high,calendar);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void write(String name, double low, double high,Calendar calendar) {
		String x = calendar.get(Calendar.DATE) + "-" 
					+ (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.YEAR);
		String absolutePath = System.getProperty("user.dir") + File.separatorChar + "src" 
					+ File.separatorChar + "main" + File.separatorChar + "resources" 
					+ File.separatorChar + name + ".txt";
		try {
			
			File file =new File(absolutePath);
    		if(!file.exists()){
    			file.createNewFile();
    		}
    		String data = x + "  " + low + "   " + high;
    		FileWriter fileWriter = new FileWriter(absolutePath,true);
    		BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
    		bufferWriter.newLine();
    		bufferWriter.write(data);
    		bufferWriter.flush();
    		bufferWriter.close();
    		fileWriter.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private double getValue(String s,String indexString){
		int index = s.indexOf(indexString);
		String v = "";
		while(s.charAt(index) != '>'){
			index++;
		}
		index = index + 1;
		while(s.charAt(index) != '<'){
			v += s.charAt(index);
			index++;
		}
		return Double.parseDouble(v);
	}

	public static void main(String args[]){
		StockLowHighUpdatingProgram cp = new StockLowHighUpdatingProgram();
		cp.startExecute(args);
	}

	
}
