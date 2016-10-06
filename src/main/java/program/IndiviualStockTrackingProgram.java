package program;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import core.Stock;
import core.StockBuilder;
import core.db.WorkBookManager;
import jxl.Sheet;
import jxl.Workbook;
import thread.StockThread;

public class IndiviualStockTrackingProgram extends AbstractProgram{

	@Override
	protected long getTimerInterval() {
		return 5 * 60 * 1000;
	}

	@Override
	protected void execute(boolean force, String[] args) {
		
		String passedStock = getValue(args,"specificstock","");
		Workbook workBook = new WorkBookManager().getWorkBook();
		Sheet sheet = workBook.getSheet(1);
		String stockURL = "";
		int rows = sheet.getRows();
		rows = 2;
		for(int i = 0;i<rows;i++){
			String stockName = sheet.getCell(0, i).getContents();
			stockURL = sheet.getCell(1, i).getContents();
			//System.out.println( stockName	+ " " + stockURL);
			if(stockName.equals(passedStock)){
				passedStock = stockName;
				break;
			}

		}
		StockBuilder builder = new StockBuilder();
		System.out.println("Stock Name " + passedStock);
		Calendar calendar = new GregorianCalendar();
		String d = calendar.get(Calendar.MONTH) +1 + "-" + calendar.get(Calendar.DATE);
		String fileName = System.getProperty("user.dir") + File.separatorChar + "src" 
				+ File.separatorChar + "main" + File.separatorChar + "resources" 
				+ File.separatorChar + passedStock +  "-" + d + ".txt";

		
		StockThread thread = new StockThread(stockURL,passedStock,3,"remote");
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		double price = thread.getStockPrice();
		
		DateFormat format = new SimpleDateFormat("HH:mm:ss");
		try {
			write(format.format(new Date()),price,fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}

	private void write(String time,double price,String fileName) throws FileNotFoundException{
		PrintWriter pw = new PrintWriter(new FileOutputStream(fileName));
		pw.println(time + "  " + price);
		pw.close();
	}

	public static void main(String args[]){
		IndiviualStockTrackingProgram cp = new IndiviualStockTrackingProgram();
		cp.startExecute(args);
	}
}
