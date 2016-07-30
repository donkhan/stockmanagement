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
	
	public JasperReport generate(String reportFile){
		try{
			InputStream inputStream = URLClassLoader.getSystemResourceAsStream(reportFile);
			JasperReport report = JasperCompileManager.compileReport(inputStream);
			return report;
		}catch(JRException jre){
			jre.printStackTrace();
		} 
		return null;
	}
	
	
	public JasperPrint fill(JasperReport jasperReport,Collection<Stock> stockCollection,
			TradeSummary summary,ExecutionSummary eSummary) {
		JRBeanCollectionDataSource dataSource =   new JRBeanCollectionDataSource(stockCollection);
		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("TotalProfit", new Double(summary.getTotalProfit()));
		parameters.put("tradeSummary", summary);
		parameters.put("executionSummary", eSummary);
		try {
			return JasperFillManager.fillReport(jasperReport, parameters, dataSource);
		}catch(JRException jre){

		}
		return null;
	}
	
	public void generate(JasperPrint jasperPrint,String fileName) {
		try {
			if(fileName.endsWith("html")) {
				JasperExportManager.exportReportToHtmlFile(jasperPrint, fileName);
			}
			if(fileName.endsWith("pdf")) {
				JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);
			}
		}catch(JRException e){

		}
	}

}
