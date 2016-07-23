package program;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import core.Stock;
import core.StockBuilder;




public class ProfitCalculationProgram extends AbstractProgram{
	
	public static void main(String args[]){
		ProfitCalculationProgram cp = new ProfitCalculationProgram();
		cp.startExecute(args);
	}
	

	@Override
	protected long getTimerInterval() {
		return 0;
	}

	@Override
	protected void execute(boolean force,String args[]) {
		StockBuilder builder = new StockBuilder();
		
		Map<String, Stock> stocks = builder.read("None");
		List<Stock> stockList = new ArrayList<Stock>();
		Iterator<Stock> values = stocks.values().iterator();
		while(values.hasNext()){
			Stock stock = values.next();
		}

		
		
		
	}
	
	
	
}
