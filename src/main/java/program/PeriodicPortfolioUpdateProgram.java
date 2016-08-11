package program;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import core.ExecutionSummary;
import core.Stock;
import core.StockBuilder;
import core.StockSorter;
import core.TradeSummary;
import file.FileCleaner;
import file.FileNameGenerator;
import jasper.JasperReportGenerator;
import mail.Mailer;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

public class PeriodicPortfolioUpdateProgram extends AbstractProgram{

	private String[] args;
	
	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	public static void main(String[] args){
		PeriodicPortfolioUpdateProgram main = new PeriodicPortfolioUpdateProgram();
		main.setArgs(args);
		main.startExecute(args);
	}
	
	public long getTimerInterval(){
		return getIntegerValue(this.args,"interval") * 60 * 1000; 
	}
	
	
	public void execute(final boolean force,String args[]){
		GregorianCalendar currentTime = new GregorianCalendar();
		System.out.println("Pass started at " + currentTime.getTime());
		
		if(force){
			doWork(force,args);
			return;
		}
		/*
		if(!InternetService.up()){
			System.err.println("Internet is not Up.");
			return;
		}
		*/
		if(currentTime.get(Calendar.HOUR_OF_DAY) > 17 && currentTime.get(Calendar.HOUR_OF_DAY) < 20){
			doWork(force,args);
		}
		
		if(currentTime.get(Calendar.HOUR_OF_DAY) > 17  || currentTime.get(Calendar.HOUR_OF_DAY) < 2){
			System.out.println("Since time crossed 5 PM Markets would have been closed. Hence not running this time");
			//return;
		}
		if(currentTime.get(Calendar.DAY_OF_WEEK) == 7 ||  currentTime.get(Calendar.DAY_OF_WEEK) == 1){
			System.out.println("Won't Run on Sat or Sun");
			//return;
		}
		doWork(force,args);
	}
	
	private void doWork(final boolean force,String args[]){
		long start = System.currentTimeMillis();
		boolean fullReport = getBooleanValue(args,"fullreport");
		int maxRetries = getIntegerValue(args,"maxretry");
		String specificStock = getValue(args,"specificstock","None");
		StockBuilder builder = new StockBuilder();
		builder.setInputFile(getValue(args,"filepath",""));
		builder.setFullReport(fullReport);
		builder.setWorkBook();
		
		double totalProfit = 0;
		Map<String, Stock> stocks = builder.read(specificStock);
		builder.updateStocks(stocks,maxRetries);
		List<Stock> stockList = new ArrayList<Stock>();
		Iterator<Stock> values = stocks.values().iterator();
		while(values.hasNext()){
			Stock stock = values.next();
			if(stock.getTotalQuantity() > 0 || fullReport){
				stockList.add(stock);
			}
			totalProfit += stock.getProfitRealised();
		}
		
		TradeSummary tradeSummary = builder.getTradeSummary();
		tradeSummary.setTotalProfit(totalProfit);
		prepareReport(stockList,tradeSummary,prepareExecutionSummary(tradeSummary,start),getBooleanValue(args,"sendmail"));
	}

	private ExecutionSummary prepareExecutionSummary(TradeSummary tradeSummary, long start) {
		ExecutionSummary executionSummary = new ExecutionSummary();
		GregorianCalendar currentTime = new GregorianCalendar();
		executionSummary.setExecutionTime(currentTime.getTime());
		currentTime.add(Calendar.MINUTE, getIntegerValue(this.args,"interval"));
		executionSummary.setNextExecutionTime(currentTime.getTime());
		long elapsed = System.currentTimeMillis() - start;
		executionSummary.setTimeToExecute(elapsed);
		return executionSummary;
	}

	private void prepareReport(List<Stock> stockList,
			TradeSummary tradeSummary, ExecutionSummary executionSummary,boolean sendmail) {
		Collections.sort(stockList,new StockSorter());
		JasperReportGenerator gen = new JasperReportGenerator();
		String df = new SimpleDateFormat("yyyy-MM-dd-hh-mm").format(new Date());
		String generatedFileName = FileNameGenerator.getTmpDir() + "PeriodicReport-" + df + ".pdf";

		JasperReport report = gen.generate("StockPeriodicReport.jrxml");
		JasperPrint print = gen.fill(report,stockList,tradeSummary,executionSummary);
		gen.generate(print,generatedFileName);

		if(generatedFileName != null && sendmail){
			Mailer mailer = new Mailer(args);
			StringBuffer subject = new StringBuffer("");
			subject.append("Investment Bulletin @ " + DateFormat.getDateTimeInstance().format(new Date()));
			try{
				mailer.mail(generatedFileName,subject);
				FileCleaner fc = new FileCleaner();
				fc.clean(generatedFileName);
			}catch(Throwable t){
				System.err.println("Unable to send email");
			}
		}
		System.out.println(generatedFileName);
	}
	
	
}


