package jasper;

import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import file.FileNameGenerator;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import profit.ProfitCalendar;

public class ProfitReportGenerator {

	public String generate(List<ProfitCalendar> list){
		try{
			InputStream inputStream = URLClassLoader.getSystemResourceAsStream("ProfitReport.jrxml");
			JasperReport report = JasperCompileManager.compileReport(inputStream);
			JasperPrint print = fill(report,list);
			return pdf(print);
		}catch(JRException jre){
			jre.printStackTrace();
		} 
		return null;
	}
	
	
	public JasperPrint fill(JasperReport jasperReport,List<ProfitCalendar> list) throws JRException{
		JRBeanCollectionDataSource dataSource =   new JRBeanCollectionDataSource(list);
		Map<String,Object> parameters = new HashMap<String,Object>();
		return JasperFillManager.fillReport(jasperReport, parameters, dataSource);
	}
	
	public String pdf(JasperPrint jasperPrint) throws JRException{
		String fileName = FileNameGenerator.getProfitFile("html");
		JasperExportManager.exportReportToHtmlFile(jasperPrint, fileName);
		return fileName;
	}

}
