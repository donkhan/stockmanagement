package jasper;

import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import core.ExecutionSummary;
import core.Stock;
import core.Trade;
import core.TradeSummary;
import file.FileNameGenerator;

public class JasperReportGenerator {

	public static void main(String[] args) throws JRException{
		JasperReportGenerator gen = new JasperReportGenerator();
		List<Stock> stocks = new ArrayList<Stock>();
		Stock iocStock = new Stock();
		iocStock.setName("IOC"); 
		iocStock.setCurrentPrice(13);
		iocStock.setBroker("Geojit");
		Trade trade = new Trade(12);
		trade.setName("IOC"); trade.setQuantity(5); trade.setTradeType("B"); trade.setGrossrate(193);
		iocStock.addToTradeList(trade,new TradeSummary());
		stocks.add(iocStock);
		gen.generate(stocks,new TradeSummary(),new ExecutionSummary());
	}
	
	public String generate(List<Stock> stockCollection,TradeSummary summary,ExecutionSummary eSummary){
		try{
			//InputStream inputStream = new FileInputStream(System.getProperty("user.dir") + "\\src\\main\\resources\\"+ "StockPeriodicReport.jrxml");
			InputStream inputStream = URLClassLoader.getSystemResourceAsStream("StockPeriodicReport.jrxml");
			JasperReport report = JasperCompileManager.compileReport(inputStream);
			JasperPrint print = fill(report,stockCollection,summary,eSummary);
			return pdf(print);
		}catch(JRException jre){
			jre.printStackTrace();
		} 
		return null;
	}
	
	
	public JasperPrint fill(JasperReport jasperReport,Collection<Stock> stockCollection,
			TradeSummary summary,ExecutionSummary eSummary) throws JRException{
		JRBeanCollectionDataSource dataSource =   new JRBeanCollectionDataSource(stockCollection);
		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("TotalProfit", new Double(summary.getTotalProfit()));
		parameters.put("tradeSummary", summary);
		parameters.put("executionSummary", eSummary);

		return JasperFillManager.fillReport(jasperReport, parameters, dataSource);
	}
	
	public String pdf(JasperPrint jasperPrint) throws JRException{
		String fileName = FileNameGenerator.getFileByTodaysDate("html");
		JasperExportManager.exportReportToHtmlFile(jasperPrint, fileName);
		return fileName;
	}

}
