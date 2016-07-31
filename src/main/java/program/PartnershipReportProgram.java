package program;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import profit.ProfitCalendarInterface;
import program.profitcalculation.DailyProfitCalculationProgram;

import com.itextpdf.text.Document;
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
		TradeListingProgram tp = new TradeListingProgram();
		List<Trade> trades = tp.build(getValue(args,"filepath",""));
		DailyProfitCalculationProgram dpp = new DailyProfitCalculationProgram();
		List<ProfitCalendarInterface> profitCalendarList = dpp.process(dpp.buildMap(new String[]{}));
		try {
			OutputStream file = new FileOutputStream(FileNameGenerator.getTmpDir() + "PartnerShipReport.pdf");
			Document document = new Document();
			PdfWriter.getInstance(document, file);
			document.open();
			sp.appendToDocument(document, stocks);
			tp.appendToDocument(document, trades);
			dpp.appendToDocument(document, profitCalendarList);
			document.close();
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
