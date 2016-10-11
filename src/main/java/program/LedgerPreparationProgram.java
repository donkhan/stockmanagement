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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import core.LedgerEntry;
import core.Trade;
import file.FileNameGenerator;

public class LedgerPreparationProgram extends AbstractProgram{

	@Override
	protected long getTimerInterval() {
		return -1;
	}

	public static void main(String args[]){
		LedgerPreparationProgram main = new LedgerPreparationProgram();
		main.setArgs(args);
		main.startExecute(args);
	}
	
	private double balance = 0;
	private final Map<String,List<LedgerEntry>> ledgerMap = new HashMap<String,List<LedgerEntry>>();
	
	@Override
	protected void execute(boolean force, String[] args) {
		TradeListingProgram tp = new TradeListingProgram();
		Calendar begin = new GregorianCalendar();
		begin.set(Calendar.YEAR, 2012);
		tp.setBegin(begin);
		List<Trade> trades = tp.build(args);
		//Collections.sort(trades);
		
		
		PayInPayOutListingProgram cp = new PayInPayOutListingProgram();
		List<LedgerEntry> normalLedgerEntries = cp.build(args);
		
		merge(trades,ledgerMap);
		mergePayEntries(normalLedgerEntries,ledgerMap);
		prepareLedgers(ledgerMap);
	}
	
	
	private void prepareLedgers(Map<String,List<LedgerEntry>> ledgerMap){
		final Document document = new Document();
		final DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
		try {
			String outputFile = FileNameGenerator.getTmpDir() + "Ledger.pdf";
			OutputStream file = new FileOutputStream(outputFile);
			
			PdfWriter.getInstance(document, file);
			document.open();
			
			ledgerMap.forEach(new BiConsumer<String,List<LedgerEntry>>(){
				@Override
				public void accept(String broker, List<LedgerEntry> list) {
					if(list == null) return;
					System.out.println("Broker ...." + broker);
					//if(!broker.equals("Kotak")) return;
					Collections.sort(list);
					try {
						addSectionHeader(document,broker);
						final PdfPTable table = new PdfPTable(4);
						String headers[] = new String[]{"Date","Description","Amount","Balance"};
						addHeaders(table,headers);
						list.forEach(new Consumer<LedgerEntry>(){
							@Override
							public void accept(LedgerEntry le) {
								List<Object> row = new ArrayList<Object>();
								row.add(df.format(le.getTime().getTime()));
								row.add(le.getDescription());
								row.add(le.getAmount());
								balance += le.getAmount();
								row.add(balance);
								addRow(row, table);
							}
						});
						document.add(table);
						balance = 0;
					} catch (DocumentException e) {
						e.printStackTrace();
					}
					
					
				}
			});
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
	
	

	protected void merge(List<LedgerEntry> list, LedgerEntry le) {
		for(int i = 0;i<list.size();i++){
			LedgerEntry previousEntry = list.get(i);
			if(previousEntry.getTime().getTimeInMillis() == le.getTime().getTimeInMillis()){
				previousEntry.setAmount(previousEntry.getAmount() + le.getAmount());
				previousEntry.setDescription(previousEntry.getDescription() + ","+ le.getDescription());
				return;
			}
		}
		list.add(le);
	}
	
	private void merge(List<Trade> trades, final Map<String,List<LedgerEntry>> ledgerMap){
		trades.forEach(new Consumer<Trade>(){
			@Override
			public void accept(Trade t) {
				LedgerEntry le = new LedgerEntry();
				le.setAmount(t.getQuantity()* t.getNetRate());
				if(t.getSettlementTime() != null){
					le.setTime(t.getSettlementTime());
				}else{
					le.setTime(t.getTransactionTime());
				}
				if(t.getTradeType().equals(Trade.BUY)){
					le.setAmount(le.getAmount() * -1);
					le.setDescription("Bought " + t.getQuantity() + " " + t.getName());
				}
				if(t.getTradeType().equals(Trade.SELL)){
					le.setDescription("Sold " + t.getQuantity() + " " + t.getName());
				}
				List<LedgerEntry> list = ledgerMap.get(t.getBroker());
				
				if(list == null){
					list = new ArrayList<LedgerEntry>();
					ledgerMap.put(t.getBroker(),list);
				}
				merge(list,le);
			}
		});
	}
	
	private void mergePayEntries(final List<LedgerEntry> normalLedgerEntries, 
			final Map<String,List<LedgerEntry>> ledgerMap){
		normalLedgerEntries.forEach(new Consumer<LedgerEntry>(){
			@Override
			public void accept(LedgerEntry le) {
				List<LedgerEntry> list = ledgerMap.get(le.getBroker());
				//merge(list,le);
				list.add(le);
			}
		});
	}
}
