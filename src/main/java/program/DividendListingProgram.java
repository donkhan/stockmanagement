package program;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import core.Dividend;
import core.StockBuilder;
import file.FileNameGenerator;
import jxl.Cell;
import jxl.Sheet;
import util.Global;


public class DividendListingProgram extends AbstractProgram{

	public static void main(String args[]){
		DividendListingProgram cp = new DividendListingProgram();
		cp.startExecute(args);
	}
	
	private GregorianCalendar begin = new GregorianCalendar();
	private GregorianCalendar end  = new GregorianCalendar();
	
	protected void prepareCutOff() {
		resetCalendar(begin); resetCalendar(end);
		begin.set(Calendar.DATE, 1);end.set(Calendar.DATE, 1);
		end.add(Calendar.MONTH,1); end.add(Calendar.MILLISECOND, -1);
		System.out.println("Begin " + begin.getTime());
		System.out.println("End " + end.getTime());
	}
	
	@Override
	protected long getTimerInterval() {
		return -1;
	}
	
	public double getTotalDividend(	List<Dividend> allDividends){
		double total = 0d;
		for(Dividend d : allDividends){
			total += d.getValue();
		}
		return total;
	}

	@Override
	public void execute(boolean force, String[] args) {
		List<Dividend> allDividends = build(args);
		try {
			String outputFile = FileNameGenerator.getTmpDir() + "DividendListing.pdf";
			OutputStream file = new FileOutputStream(outputFile);
			System.out.println("Output File " + outputFile);
			Document document = new Document();
			PdfWriter.getInstance(document, file);
			document.open();
			appendToDocument(document,allDividends);
			document.close();
			file.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<Dividend> build(String[] args){
		setArgs(args);
		String filePath = getValue(args,"filepath","");
		prepareCutOff();
		StockBuilder builder = new StockBuilder();
		builder.setInputFile(filePath);
		builder.setFullReport(true);
		builder.setWorkBook();
		List<Dividend> allDividends = new ArrayList<Dividend>();
		Sheet sheet = builder.getWorkBook().getSheet(5);
		for(int index = 0;index< sheet.getRows();index++){
			Dividend d = new Dividend();
			handleDividendDate(d,sheet.getCell(0,index));
			d.setValue(Double.parseDouble(sheet.getCell(4, index).getContents()));
			d.setStock(sheet.getCell(2, index).getContents());
			if(Global.debug){
				System.out.println(d);
			}
			allDividends.add(d);
		}
		filter(allDividends);
		Collections.sort(allDividends);
		return allDividends;
	}
	
	private void handleDividendDate(Dividend dividend,Cell cell){
		String content = cell.getContents();
		content = content.replaceAll("\"", "");
		
		StringTokenizer tokenizer = new StringTokenizer(content,"/");
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.DATE, Integer.parseInt(tokenizer.nextToken()));
		calendar.set(Calendar.MONTH, Integer.parseInt(tokenizer.nextToken())-1);
		int year = Integer.parseInt(tokenizer.nextToken());
		if(year < 2000){
			calendar.set(Calendar.YEAR, 2000+year);
		}
		
		dividend.setDividendPaidTime(calendar);

	}
	
	private void filter(List<Dividend> list) {
		Iterator<Dividend> iterator = list.iterator();
		while(iterator.hasNext()){
			Dividend pi = iterator.next();
			if(remove(pi)) iterator.remove();
		}
	}
	
	private boolean remove(Dividend dividend) {
		Calendar c = dividend.getDividendPaidTime();
		if(Global.debug){
			System.out.println("BEGIN " + begin.getTime());
			System.out.println("DIVIDEND " + dividend.getDividendPaidTime().getTime() + " " + dividend.getStock());
			System.out.println("END " + end.getTime());
		}
		return c.before(begin) || c.after(end);
	}

	protected void resetCalendar(Calendar c) {
		c.set(Calendar.DATE, 1); c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0);
	}
	
	
	public void setArgs(String args[]){
		int month = getIntegerValue(args, "month","-1");
		int year = getIntegerValue(args, "year","-1");
		if(month != -1){
			begin.set(Calendar.MONTH, month);
			end.set(Calendar.MONTH, month);
			System.out.println(begin.getTime() + "  " + end.getTime());
		}
		if(year != -1){
			begin.set(Calendar.YEAR, year);
			end.set(Calendar.YEAR, year);
			
		}
	}
	
	public void appendToDocument(Document document,List<Dividend> dividends) throws DocumentException{
		addSectionHeader(document,"Dividend");
	
		PdfPTable table = new PdfPTable(3);
		String headers[] = new String[]{"Date","Script","Dividend"};
		addHeaders(table,headers);

		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
		double tp = 0d;
		for(Dividend dividend : dividends){
			List<Object> row = new ArrayList<Object>();
			row.add(df.format(dividend.getDividendPaidTime().getTime()));
			row.add(dividend.getStock());
			row.add(dividend.getValue());
			tp += dividend.getValue();
			addRow(row, table);
		}
		List<Object> totalRow = new ArrayList<Object>();
		totalRow.add("Total"); 
		totalRow.add("-");totalRow.add(tp);
		addRow(totalRow,table);
		
		document.add(table);
	}
}
