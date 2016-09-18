package program;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
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
		rows = 2;
		for(int i = 0;i<rows;i++){
			String stockName = sheet.getCell(0, i).getContents();
			String stockURL = sheet.getCell(1, i).getContents();
			//System.out.println( stockName	+ " " + stockURL);
			//	if(stockName.equals("RICO Auto"))
			updateFile(stockURL,stockName);
		}
		
		
	}
	
	
	private void updateFile(String singleURL,String name) {
		Calendar calendar = new GregorianCalendar();
		try {
			URL url = new URL(singleURL);
			//InputStream is	 = new FileInputStream("src/main/resources/MoneyControlTestOutput.html");
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
		} catch(Throwable t){
			
		}
	}
	
	private void write(String name, double low, double high,Calendar calendar) throws IOException {
		String x = calendar.get(Calendar.DATE) + "-" 
					+ (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.YEAR);
		String filename = System.getProperty("user.dir") + File.separatorChar + "src" 
					+ File.separatorChar + "main" + File.separatorChar + "resources" 
					+ File.separatorChar + name + ".txt";
		String content = x + "  " + low + "   " + high +"\r\n";
		File file =new File(filename);
		if(!file.exists()){
			file.createNewFile();
    	}
		
		long offset = 0;
		RandomAccessFile r = new RandomAccessFile(new File(filename), "rw");
		RandomAccessFile rtemp = new RandomAccessFile(new File(filename + "~"), "rw");
		long fileSize = r.length();
		FileChannel sourceChannel = r.getChannel();
		FileChannel targetChannel = rtemp.getChannel();
		sourceChannel.transferTo(offset, (fileSize - offset), targetChannel);
		sourceChannel.truncate(offset);
		r.seek(offset);
		r.write(content.getBytes());
		long newOffset = r.getFilePointer();
		targetChannel.position(0L);
		sourceChannel.transferFrom(targetChannel, newOffset, fileSize);
		sourceChannel.close();
		targetChannel.close();
		
		new File(filename + "~").delete();
		
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
