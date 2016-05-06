package jasper;

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
import core.Stock;
import core.Trade;
import file.FileNameGenerator;

public class JasperReportGenerator {

	public static void main(String[] args) throws JRException{
		JasperReportGenerator gen = new JasperReportGenerator();
		Map<String,Stock> stocks = new HashMap<String,Stock>();
		Stock iocStock = new Stock();
		iocStock.setName("IOC"); 
		iocStock.setCurrentPrice(13);
		iocStock.setBroker("Geojit");
		Trade trade = new Trade();
		trade.setName("IOC"); trade.setQuantity(5); trade.setTradeType("B"); trade.setGrossrate(193);
		iocStock.addToTradeList(trade);
		stocks.put("IOC", iocStock);
		//gen.generate(stocks);
	}
	
	public String generate(List<Stock> stockCollection,double totalProfit){
		try{
			JasperReport report = JasperCompileManager.compileReport("c:\\Users\\kkhan\\jaspermetadata\\StockPeriodicReport.jrxml");
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
