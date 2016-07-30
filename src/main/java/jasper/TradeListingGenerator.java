package jasper;

import java.io.InputStream;
import java.net.URLClassLoader;
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
import core.Trade;

public class TradeListingGenerator {

	public String generate(List<Trade> list,String fileName,String outputFile){
		try{
			InputStream inputStream = URLClassLoader.getSystemResourceAsStream(fileName);
			JasperReport report = JasperCompileManager.compileReport(inputStream);
			JasperPrint print = fill(report,list);
			return pdf(print,list,outputFile);
		}catch(JRException jre){
			jre.printStackTrace();
		} 
		return null;
	}
	
	
	public JasperPrint fill(JasperReport jasperReport,List<Trade> list) throws JRException{
		JRBeanCollectionDataSource dataSource =   new JRBeanCollectionDataSource(list);
		Map<String,Object> parameters = new HashMap<String,Object>();
		return JasperFillManager.fillReport(jasperReport, parameters, dataSource);
	}
	
	public String pdf(JasperPrint jasperPrint,List<Trade> list,String outputFile) throws JRException{
		JasperExportManager.exportReportToPdfFile(jasperPrint, outputFile);
		return outputFile;
	}
}
