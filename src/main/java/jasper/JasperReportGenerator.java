package jasper;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import core.Stock;
import core.Trade;
import file.FileNameGenerator;

public class JasperReportGenerator {

	public static void main(String[] args) throws JRException{
		JasperReportGenerator gen = new JasperReportGenerator();
		List<Stock> stocks = new ArrayList<Stock>();
		Stock iocStock = new Stock();
		iocStock.setName("IOC"); 
		iocStock.setCurrentPrice(13);
		iocStock.setBroker("Geojit");
		Trade trade = new Trade();
		trade.setName("IOC"); trade.setQuantity(5); trade.setTradeType("B"); trade.setGrossrate(193);
		iocStock.addToTradeList(trade);
		stocks.add(iocStock);
		gen.generate(stocks,12d);
	}
	
	public String generate(List<Stock> stockCollection,double totalProfit){
		try{
			//InputStream inputStream = new FileInputStream(System.getProperty("user.dir") + "\\src\\main\\resources\\"+ "StockPeriodicReport.jrxml");
			InputStream inputStream = URLClassLoader.getSystemResourceAsStream("StockPeriodicReport.jrxml");
			JasperReport report = JasperCompileManager.compileReport(inputStream);
			JasperPrint print = fill(report,stockCollection,totalProfit);
			return pdf(print);
		}catch(JRException jre){
			jre.printStackTrace();
		} 
		return null;
	}
	
	
	public JasperPrint fill(JasperReport jasperReport,Collection<Stock> stockCollection,double totalProfit) throws JRException{
		JRBeanCollectionDataSource dataSource =   new JRBeanCollectionDataSource(stockCollection);
		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("TotalProfit", new Double(totalProfit));
		return JasperFillManager.fillReport(jasperReport, parameters, dataSource);
	}
	
	public String pdf(JasperPrint jasperPrint) throws JRException{
		String fileName = FileNameGenerator.getFileByTodaysDate("html");
		JasperExportManager.exportReportToHtmlFile(jasperPrint, fileName);
		return fileName;
	}

}
