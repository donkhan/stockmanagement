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

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import core.Dividend;
import core.StockBuilder;
import file.FileNameGenerator;
import jxl.Sheet;


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

	@Override
	public void execute(boolean force, String[] args) {
		List<Dividend> allTrades = build(getValue(args,"filepath",""));
		try {
			String outputFile = FileNameGenerator.getTmpDir() + "DividendListing.pdf";
			OutputStream file = new FileOutputStream(outputFile);
			System.out.println("Output File " + outputFile);
			Document document = new Document();
			PdfWriter.getInstance(document, file);
			document.open();
			appendToDocument(document,allTrades);
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
	
	public List<Dividend> build(String filePath){
		prepareCutOff();
		StockBuilder builder = new StockBuilder();
		builder.setInputFile(filePath);
		builder.setFullReport(true);
		builder.setWorkBook();
		List<Dividend> allDividends = new ArrayList<Dividend>();
		Sheet sheet = builder.getWorkBook().getSheet(5);
		int rows = sheet.getRows();
		
		filter(allDividends);
		for(int index = 0;index<1;index++){
			Dividend d = new Dividend();
			GregorianCalendar gc = new GregorianCalendar();
			//gc.setTimeInMillis(dr.getDate().getTime());
			d.setDividendPaidTime(gc);
			//d.setValue(Double.parseDouble(sheet.getCell(3, index).getContents()));
			d.setStock(sheet.getCell(2, index).getContents());
			System.out.println(d);
		}
		Collections.sort(allDividends);
		return allDividends;
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
		return c.before(begin) || c.after(end);
	}

	protected void resetCalendar(Calendar c) {
		c.set(Calendar.DATE, 1); c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0);
	}
	
	
	public void appendToDocument(Document document,List<Dividend> trades) throws DocumentException{
		addSectionHeader(document,"Trade");
	
		PdfPTable table = new PdfPTable(7);
		String headers[] = new String[]{"Date","Script","Dividend"};
		addHeaders(table,headers);

		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
		double tp = 0d;
		for(Dividend trade : trades){
			List<Object> row = new ArrayList<Object>();
			row.add(df.format(trade.getDividendPaidTime().getTime()));
			row.add(trade.getStock());
			row.add(trade.getValue());
			tp += trade.getValue();
			addRow(row, table);
		}
		
		List<Object> totalRow = new ArrayList<Object>();
		totalRow.add("Total");totalRow.add("-");totalRow.add("-"); totalRow.add("-"); 
		totalRow.add("-");totalRow.add("-"); totalRow.add(tp);
		addRow(totalRow,table);
		
		document.add(table);
	}
}
