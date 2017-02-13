package program;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import core.Stock;
import core.StockBuilder;
import core.Trade;
import file.FileNameGenerator;
import util.MathUtils;

public class StockAnalysisProgram extends AbstractProgram{

	@Override
	protected long getTimerInterval() {
		return -1;
	}

	@Override
	protected void execute(boolean force, String[] args) {
		String specificStock = getValue(args, "specificstock", "None");
		String specificBroker = getValue(args, "broker", "None");
		if(specificStock.equals("None") || specificBroker.equals("None")){
			System.err.println("Usage java -classpath stockmanagement.jar program.StockAnalysisProgram --specificstock=<stockname> --broker=<broker>");
			return;
		}
		StockBuilder builder = new StockBuilder();
		builder.setMode(getValue(args, "mode", "remote"));
		builder.setInputFile(getValue(args, "filepath", ""));
		builder.setWorkBook();

		Map<String, Stock> stocks = builder.read(specificStock);
		Stock stock = stocks.get(specificStock + "-" + specificBroker);
		builder.updateStocks(stocks, 1);
		try {
			String outputFile = FileNameGenerator.getTmpDir() + specificStock + "-" + specificBroker + ".pdf";
			OutputStream file = new FileOutputStream(outputFile);
			Document document = new Document();
			PdfWriter.getInstance(document, file);
			document.open();
			
			if(stock == null){
				System.err.println("Please check stock name and broker name. I do not find any entries here");
				return;
			}
			List<Trade> trades = stock.getTradeList();
			long q = 0;
			
			addSectionHeader(document,"Trades");
			PdfPTable table = new PdfPTable(8);
			String headers[] = new String[]{"Date","Quantity","Net","Gross","Net","Average","Profit","Reference"};
			addHeaders(table,headers);
			DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
			double tb = 0,ts = 0,tp = 0;
			int nb = 0,ns = 0;
			double ma = 0;
			for(Trade trade : trades){
				if(trade.getTradeType().equals(Trade.DELETED)){
					continue;
				}
				List<Object> row = new ArrayList<Object>();
				row.add(df.format(trade.getTransactionTime().getTime()));
				if(trade.getTradeType().equals(Trade.INTRA_DAY)){
					row.add("~" + trade.getQuantity());
					tp += trade.getProfit();
					ns++;nb++;
				}
				if(trade.getTradeType().equals(Trade.SELL)){
					q -= trade.getQuantity();
					ts += (trade.getQuantity() * trade.getNetRate());
					row.add(-1*trade.getQuantity());
					tp += trade.getProfit();
					ns++;
					if(q == 0){
						ma = 0;
					}
					
				}
				if(trade.getTradeType().equals(Trade.BUY) || trade.getTradeType().equals(Trade.RIGHTS)){
					ma = ((ma * q) + (trade.getQuantity() * trade.getNetRate())) / (q + trade.getQuantity());
					q += trade.getQuantity();
					row.add(trade.getQuantity());
					tb += (trade.getQuantity() * trade.getNetRate());
					nb++;
				
				}
				row.add(q);	
				row.add(trade.getGrossRate());
				row.add(trade.getNetRate());
				
				row.add(ma);
				
				row.add(trade.getProfit());
				
				if(trade.getBuyTradeIds() != null){
					row.add(trade.getReferenceRate());
				}else if(trade.getTradeType().equals(Trade.INTRA_DAY)){
					row.add(trade.getIntraBuyRate() + "-" + trade.getIntraSellRate());
					
				}else{
					row.add("");
				}
				addRow(row, table);
			}
			document.add(table);
			
			addSectionHeader(document,"Summary");
			document.add(new Paragraph("No of Buy : " + nb));
			document.add(new Paragraph("No of Sell : " + ns));
			document.add(new Paragraph("Total Buy Amount : " + MathUtils.Round(tb,2)));
			document.add(new Paragraph("Total Sell Amount : " + MathUtils.Round(ts,2)));
			document.add(new Paragraph("Total Profit : " + MathUtils.Round(tp,2)));
			document.add(new Paragraph("Current Price : " + stock.getCurrentPrice()));
			document.add(new Paragraph("Average : " + MathUtils.Round(stock.getAverage(),2)));

			double diff = MathUtils.Round((stock.getCurrentPrice() - stock.getAverage()) * stock.getTotalQuantity(), 2);
			document.add(new Paragraph(" UnRealized Loss/Gain : "	 + diff));
			
			
			
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
	
	
	
	public static void main(String args[]){
		StockAnalysisProgram main = new StockAnalysisProgram();
		main.setArgs(args);
		main.startExecute(args);
	}
}
