package program;

import java.util.Map;

import core.Stock;
import core.StockBuilder;

public class SquareOffProgram extends AbstractProgram {

	@Override
	protected long getTimerInterval() {
		return 30 * 60 * 1000;
	}

	@Override
	protected void execute(boolean force, boolean sendmail, String specificStock,String filePath) {
		System.out.println("Execute Square Off Program for Stock " + specificStock);
		StockBuilder builder = new StockBuilder();
		builder.setInputFile("c://Users//kkhan//Trade.xls");
		
		Map<String, Stock> stocks = builder.read("GMR");
		Stock stock = (Stock) stocks.values().toArray()[0];
		double squarePrice = stock.getCurrentPrice() + .1;
		double totalPrice = stock.getTotalQuantity() * stock.getAverage();
		
		System.out.println("Square Price " + squarePrice + " Current Price " + stock.getCurrentPrice() + " Average Price " + stock.getAverage());
		
		double x = (totalPrice - (squarePrice * stock.getTotalQuantity()))/(squarePrice- stock.getCurrentPrice());
		System.out.println("Stocks Needs to be bought " + x + " Money to be spent " + x * stock.getCurrentPrice());
		
	}

	public static void main(String args[]){
		SquareOffProgram main = new SquareOffProgram();
		main.startExecute(args);
	}
}
