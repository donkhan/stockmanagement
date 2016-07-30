package program;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import core.Stock;
import core.StockBuilder;
import core.Trade;
import file.FileNameGenerator;


public class PartnershipReportProgram extends AbstractProgram{

	public static void main(String args[]){
		PartnershipReportProgram cp = new PartnershipReportProgram();
		cp.startExecute(args);
	}

	@Override
	protected long getTimerInterval() {
		return -1;
	}

	@Override
	protected void execute(boolean force, String[] args) {
		
		StockListingProgram sp = new StockListingProgram();
		Double totalProfit = 0d;
		StockBuilder builder = new StockBuilder();
		List<Stock> stocks = sp.build(builder, totalProfit, false, 12, "None", "");
		
		System.out.println(stocks);
		
		TradeListingProgram tp = new TradeListingProgram();
		List<Trade> trades = tp.build(getValue(args,"filepath",""));
		
		System.out.println(trades);
		
		try {
			OutputStream file = new FileOutputStream(FileNameGenerator.getTmpDir() + "PartnerShipReport.pdf");
			
			Document document = new Document();
			PdfWriter.getInstance(document, file);

			document.open();
			Paragraph paragraph = new Paragraph("Stocks");
			paragraph.setSpacingAfter(10);
			document.add(paragraph);
			
			PdfPTable table = new PdfPTable(5);
			table.addCell("Script");
			table.addCell("Quantity");
			table.addCell("Average");
			table.addCell("Market Rate");
			table.addCell("UnRealized Profit/Loss");
			
			for(Stock stock : stocks){
				PdfPCell name = new PdfPCell(new Paragraph(stock.getName()));
				PdfPCell quantity = new PdfPCell(new Paragraph(""+stock.getTotalQuantity()));
				PdfPCell average = new PdfPCell(new Paragraph(new DecimalFormat("#,###,###,##0.00").format(stock.getAverage())));
				PdfPCell marketRate = new PdfPCell(new Paragraph(new DecimalFormat("#,###,###,##0.00").format(stock.getCurrentPrice())));
				PdfPCell unRealisedGainLoss = new PdfPCell(new Paragraph(new DecimalFormat("#,###,###,##0.00").format(stock.getImaginaryProfit())));
				table.addCell(name);
				table.addCell(quantity);
				table.addCell(average);
				table.addCell(marketRate);
				table.addCell(unRealisedGainLoss);
			}
			document.add(table);
			
			paragraph = new Paragraph("Trades");
			paragraph.setSpacingAfter(10);
			document.add(paragraph);
			
			table = new PdfPTable(6);
			table.addCell("Time");
			table.addCell("Script");
			table.addCell("Type");
			table.addCell("Quantity");
			table.addCell("Gross Rate");
			table.addCell("Net Rate");
			
			for(Trade trade : trades){
				PdfPCell time = new PdfPCell(new Paragraph(trade.getTransactionTime().getTime().toLocaleString()));
				PdfPCell name = new PdfPCell(new Paragraph(trade.getName()));
				PdfPCell type = new PdfPCell(new Paragraph(trade.getTradeType()));
				PdfPCell quantity = new PdfPCell(new Paragraph(""+trade.getQuantity()));
				PdfPCell grossRate = new PdfPCell(new Paragraph(new DecimalFormat("#,###,###,##0.00").format(trade.getGrossRate())));
				PdfPCell netRate = new PdfPCell(new Paragraph(new DecimalFormat("#,###,###,##0.00").format(trade.getNetRate())));
				
				table.addCell(time);
				table.addCell(name);
				table.addCell(type);
				table.addCell(quantity);
				table.addCell(grossRate);
				table.addCell(netRate);
			}
			document.add(table);
			
	            

			document.close();
			file.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
		
	}

}
