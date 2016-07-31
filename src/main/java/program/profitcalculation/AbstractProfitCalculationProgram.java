package program.profitcalculation;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import profit.ProfitCalendarInterface;
import program.AbstractProgram;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import core.Stock;
import core.StockBuilder;
import core.Trade;
import file.FileNameGenerator;



public abstract class AbstractProfitCalculationProgram extends AbstractProgram{
	
	@Override
	protected long getTimerInterval() {
		return -1;
	}

	@Override
	protected void execute(boolean force,String args[]) {
		prepareReport(process(buildMap(args)));
	}
	
	public Map<Calendar,ProfitCalendarInterface> buildMap(String args[]){
		prepareCutOff(args);
		StockBuilder builder = new StockBuilder();
		builder.setInputFile(getValue(args,"filepath",""));
		builder.setFullReport(true);
		builder.setWorkBook();
		Map<Calendar,ProfitCalendarInterface> map = new HashMap<Calendar,ProfitCalendarInterface>();
		Map<String, Stock> stocks = builder.read("None");
		Iterator<Stock> values = stocks.values().iterator();
		while(values.hasNext()){
			Stock stock = values.next();
			List<Trade> trades = stock.getTradeList();
			for(Trade trade : trades){
				Calendar c = trade.getTransactionTime();
				resetCalendar(c);
				ProfitCalendarInterface pc = null;
				double tradeAmount = trade.getNetRate() * trade.getQuantity();
				if(map.containsKey(c)){
					pc = map.get(c);
					pc.setBuyTrades(pc.getBuyTrades() + 1);

				}else{
					pc = getProfitCalendar(c,0d);
					map.put(c,pc);
				}
				if(trade.getTradeType().equals(Trade.SELL)){
					pc.setProfit(pc.getProfit() + trade.getProfit());
					pc.setSellTrades(pc.getSellTrades() + 1);
					pc.setTotalSellAmount(pc.getTotalSellAmount() + tradeAmount);
					
				}else{
					pc.setBuyTrades(pc.getBuyTrades() + 1);
					pc.setTotalBuyAmount(pc.getTotalBuyAmount() + tradeAmount);
				}
				pc.setTotalTurnOver(pc.getTotalTurnOver() + tradeAmount);
			}
		}
		return map;
	}
	
	public List<ProfitCalendarInterface> process(Map<Calendar,ProfitCalendarInterface> map){
		List<ProfitCalendarInterface> list = new ArrayList<ProfitCalendarInterface>();
		Iterator<Calendar> keyIterator = map.keySet().iterator();
		while(keyIterator.hasNext()){
			Calendar key = keyIterator.next();
			list.add(map.get(key));
		}
		Collections.sort(list);
		filter(list);
		return list;
	}
	
	private void filter(
			List<ProfitCalendarInterface> list) {
		Iterator<ProfitCalendarInterface> iterator = list.iterator();
		while(iterator.hasNext()){
			ProfitCalendarInterface pi = iterator.next();
			if(remove(pi)) iterator.remove();
		}
	}
	
	private boolean remove(ProfitCalendarInterface pi) {
		Calendar c = pi.getCalendar();
		return c.before(begin) || c.after(end);
	}
	
	private void prepareReport(List<ProfitCalendarInterface> profitCalendarList) {
		try {
			OutputStream file = new FileOutputStream(getOutputFile());
			Document document = new Document();
			PdfWriter.getInstance(document, file);
			document.open();
			appendToDocument(document,profitCalendarList);
			document.close();
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void appendToDocument(Document document,List<ProfitCalendarInterface> profitCalendarList)  throws DocumentException{
		Paragraph paragraph = new Paragraph("Profit");
		paragraph.setSpacingAfter(10);
		document.add(paragraph);
	
		PdfPTable table = new PdfPTable(6);
		String headers[] = new String[]{"Date","Buy Trades","Sell Trades","Buy Amount","Sell Amount","Profit"};
		addHeaders(table,headers);
	
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
		
		for(ProfitCalendarInterface pi : profitCalendarList){
			List<Object> row = new ArrayList<Object>();
			row.add(df.format(pi.getCalendar().getTime()));
			row.add(pi.getBuyTrades());
			row.add(pi.getSellTrades());
			row.add(pi.getTotalBuyAmount());
			row.add(pi.getTotalSellAmount());
			row.add(pi.getProfit());
			addRow(row, table);
		}
		document.add(table);
		
	}



	protected GregorianCalendar begin = new GregorianCalendar();
	protected GregorianCalendar end = new GregorianCalendar(); 

	
	protected abstract ProfitCalendarInterface getProfitCalendar(Calendar c,Double d);
	protected abstract void resetCalendar(Calendar c);
	protected abstract void prepareCutOff(String[] args);
	protected abstract String getReadableDate(Calendar calendar);
	
	protected String getOutputFile() {
		String fileName = FileNameGenerator.getTmpDir() + 
				getReadableDate(begin) + "to" + getReadableDate(end) + "-profit-report.pdf";
		System.out.println(fileName);
		return fileName;
	}
	
}
