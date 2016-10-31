package program;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import core.StockBuilder;
import core.StockTrack;
import jxl.Sheet;
import mail.Mailer;
import thread.StockThread;

public class StockTrackingProgram extends AbstractProgram{

	@Override
	protected long getTimerInterval() {
		return 1000 * 60;
	}

	@Override
	protected void execute(boolean force, String[] args) {
		String filePath = getValue(args,"filepath","");
		Calendar today = new GregorianCalendar();
		String sheetName = today.get(Calendar.DATE) + "-" + (today.get(Calendar.MONTH)+1) + "-" + today.get(Calendar.YEAR); 
		StockBuilder builder = new StockBuilder();
		builder.setInputFile(filePath);
		builder.setFullReport(true);
		builder.setWorkBook();
		Sheet sheet = builder.getWorkBook().getSheet(sheetName);
		Sheet companyInfoSheet = builder.getWorkBook().getSheet("Company Info");
		prepareStockTracksAndDotheWork(sheet,companyInfoSheet,args);
	}
	
	private void prepareStockTracksAndDotheWork(Sheet sheet,Sheet cSheet,String[] args){
		
		List<StockTrack> stockTracks = new ArrayList<StockTrack>();
		int rows = sheet.getRows();
		for(int i = 0;i<rows;i++){
			String name = sheet.getCell(0, i).getContents();
			double lowerPrice = Double.parseDouble(sheet.getCell(1, i).getContents());
			double upperPrice = Double.parseDouble(sheet.getCell(2, i).getContents());
			StockTrack stockTrack = new StockTrack(name,lowerPrice,upperPrice);
			stockTracks.add(stockTrack);
			int cRows = cSheet.getRows();
			for(int j = 0;j<cRows;j++){
				if(cSheet.getCell(0, j).getContents().equals(name)){
					stockTrack.setUrl(cSheet.getCell(1, j).getContents());
					break;
				}
			}
		}
		findCurrentPrice(stockTracks,args);
	}
	
	private void findCurrentPrice(List<StockTrack> stockTracks,String[] args){
		List<StockThread> list = new ArrayList<StockThread>();
		for(StockTrack stockTrack: stockTracks){
			StockThread st = new StockThread(stockTrack.getUrl(),stockTrack.getName(),3,"remote");
			st.start();
			list.add(st);
		}

		for(StockThread st : list){
			try {
				st.join();
			}catch(Exception e){
				e.printStackTrace();
			}
			double currentPrice = st.getStockPrice();
			String name = st.getStockName();
			for(StockTrack stockTrack: stockTracks){
				if(stockTrack.getName().equals(name)){
					stockTrack.setCurrentPrice(currentPrice);
				}
			}
		}
		analyse(stockTracks,args);
	}
	
	private void analyse(List<StockTrack> list,String[] args){
		String mailContent = "";
		boolean sendMail = false;
		for(StockTrack st: list){
			if(st.getCurrentPrice() <= st.getLowerPrice()){
				sendMail = true;
				mailContent += st.getName() + "'s Price is " + st.getCurrentPrice() 
					+ " and it violates lower value " + st.getLowerPrice(); 
			}
			if(st.getCurrentPrice() >= st.getUpperPrice()){
				sendMail = true;
				mailContent += st.getName() + "'s Price is " + st.getCurrentPrice() 
				+ " and it violates upper value " + st.getUpperPrice(); 
			}
			mailContent = mailContent + "\n";
		}
		System.out.println(mailContent);
		if(sendMail){
			String to = this.getValue(args, "to", "routetokamil@gmail.com");
			System.out.println(to);
			Mailer mailer = new Mailer(args);
			mailer.mail(null, new StringBuffer("Immediate Attention"),
					new StringBuffer(mailContent),to);
		}
		
	}
	

	public static void main(String args[]){
		StockTrackingProgram stp = new StockTrackingProgram();
		stp.startExecute(args);
	}
	
}
