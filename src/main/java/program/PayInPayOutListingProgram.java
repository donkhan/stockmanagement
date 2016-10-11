package program;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import core.LedgerEntry;
import core.StockBuilder;
import file.FileNameGenerator;
import jxl.Sheet;


public class PayInPayOutListingProgram extends AbstractProgram{

	public static void main(String args[]){
		PayInPayOutListingProgram cp = new PayInPayOutListingProgram();
		cp.startExecute(args);
	}
	
	
	@Override
	protected long getTimerInterval() {
		return -1;
	}
	
	@Override
	public void execute(boolean force, String[] args) {
		List<LedgerEntry> allTransactions = build(args);
		try {
			String outputFile = FileNameGenerator.getTmpDir() + "PayInOut.pdf";
			OutputStream file = new FileOutputStream(outputFile);
			Document document = new Document();
			PdfWriter.getInstance(document, file);
			document.open();
			appendToDocument(document,allTransactions);
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
	
	public List<LedgerEntry> build(String args[]){
		return fetchTransactions(args);
	}
	
	
	public List<LedgerEntry> fetchTransactions(String args[]){
		String filePath = getValue(args,"filepath","");
		StockBuilder builder = new StockBuilder();
		builder.setInputFile(filePath);
		builder.setFullReport(true);
		builder.setWorkBook();
		List<LedgerEntry> allTransactions = new ArrayList<LedgerEntry>();
		Sheet sheet = builder.getWorkBook().getSheet("Payments");
		int rows = sheet.getRows();
		
		for(int i = 0;i<rows;i++){
			LedgerEntry le = new LedgerEntry();
			String dc = sheet.getCell(0, i).getContents();
			dc = dc.replaceAll("\"", "");
			StringTokenizer d = new StringTokenizer(dc,"/");
			Calendar c = new GregorianCalendar();
			c.set(Calendar.DATE, Integer.parseInt(d.nextToken()));
			c.set(Calendar.MONTH, Integer.parseInt(d.nextToken())-1);
			c.set(Calendar.YEAR, Integer.parseInt(d.nextToken()));
			
			le.setTime(c);
			le.setAmount(Double.parseDouble(sheet.getCell(1, i).getContents()));
			le.setBroker(sheet.getCell(2, i).getContents());
			if(le.getAmount() > 0) le.setDescription("Pay In");
			else le.setDescription("Pay Out");
			allTransactions.add(le);
		}
		return allTransactions;
	}
	
	
	public void appendToDocument(Document document,List<LedgerEntry> entries) throws DocumentException{
		addSectionHeader(document,"Ledger Entry");
	
		PdfPTable table = new PdfPTable(3);
		String headers[] = new String[]{"Date","Description","Amount"};
		addHeaders(table,headers);

		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
		for(LedgerEntry entry : entries){
			List<Object> row = new ArrayList<Object>();
			row.add(df.format(entry.getTime().getTime()));
			row.add(entry.getDescription());
			row.add(entry.getAmount());
			addRow(row, table);
		}
		document.add(table);
	}
}
