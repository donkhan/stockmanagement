package program;

import jasper.JasperReportGenerator;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import util.InternetService;

import mail.Mailer;
import core.Stock;
import core.StockBuilder;
import core.StockSorter;
import file.FileCleaner;

public class PeriodicPortfolioUpdateProgram extends AbstractProgram{

	public static void main(String[] args){
		PeriodicPortfolioUpdateProgram main = new PeriodicPortfolioUpdateProgram();
		main.startExecute(args);
	}
	
	public long getTimerInterval(){
		return 30 * 60 * 1000; 
	}
	
	
	public void execute(final boolean force,final boolean sendmail,final String specificStock){
		GregorianCalendar currentTime = new GregorianCalendar();
		System.out.println("Pass started at " + currentTime.getTime());
		
		if(!InternetService.up()){
			System.err.println("Internet is not Up.");
			return;
		}
		
		if(force){
			doWork(force,sendmail,specificStock,true);
			return;
		}
		
		if(currentTime.get(Calendar.HOUR_OF_DAY) > 17 && currentTime.get(Calendar.HOUR_OF_DAY) < 20){
			doWork(force,sendmail,specificStock,true);
		}
		
		if(currentTime.get(Calendar.HOUR_OF_DAY) > 17  || currentTime.get(Calendar.HOUR_OF_DAY) < 9){
			System.out.println("Since time crossed 5 PM Markets would have been closed. Hence not running this time");
			return;
		}
		if(currentTime.get(Calendar.DAY_OF_WEEK) == 7 ||  currentTime.get(Calendar.DAY_OF_WEEK) == 0){
			System.out.println("Won't Run on Sat or Sun");
			return;
		}
		doWork(force,sendmail,specificStock);
	}
	
	private void doWork(final boolean force,final boolean sendmail,final String specificStock){
		doWork(force,sendmail,specificStock,false);
	}
	
	private void doWork(final boolean force,final boolean sendmail,final String specificStock,boolean fullReport){
		StockBuilder builder = new StockBuilder();
		builder.setInputFile("c://Users//kkhan//Trade.xls");
		builder.setFullReport(fullReport);
		
		Map<String, Stock> stocks = builder.read(specificStock);
		List<Stock> stockList = new ArrayList<Stock>();
		Iterator<Stock> values = stocks.values().iterator();
		double totalProfit = 0;
		while(values.hasNext()){
			Stock stock = values.next();
			if(stock.getTotalQuantity() > 0 || fullReport){
				stockList.add(stock);
			}
			totalProfit += stock.getProfitRealised();
		}
		
		Collections.sort(stockList,new StockSorter());
		JasperReportGenerator gen = new JasperReportGenerator();
		String generatedFileName = gen.generate(stockList,totalProfit);
		
		if(generatedFileName != null && sendmail){
			Mailer mailer = new Mailer();
			StringBuffer subject = new StringBuffer("");
			subject.append("Investment Bulletin @ " + DateFormat.getDateTimeInstance().format(new Date()));
			mailer.mail(generatedFileName,subject);
			FileCleaner fc = new FileCleaner();
			fc.clean(generatedFileName);
		}
	}


}
