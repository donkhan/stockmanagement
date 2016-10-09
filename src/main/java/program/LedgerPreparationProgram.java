package program;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import core.LedgerEntry;
import core.Trade;

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
	
	@Override
	protected void execute(boolean force, String[] args) {
		TradeListingProgram tp = new TradeListingProgram();
		Calendar begin = new GregorianCalendar();
		begin.set(Calendar.YEAR, 2012);
		tp.setBegin(begin);
		List<Trade> trades = tp.build(args);
		Collections.sort(trades);
		Map<String,List<LedgerEntry>> ledgerMap = new HashMap<String,List<LedgerEntry>>();
		Map<String,Double> accountBalance= new HashMap<String,Double>();
		
		trades.forEach(new Consumer<Trade>(){
			@Override
			public void accept(Trade t) {
				//t.print();
				LedgerEntry le = new LedgerEntry();
				le.setAmount(t.getQuantity()* t.getNetRate());
				le.setTime(t.getTransactionTime());
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
				list.add(le);
			}
		});
		
		ledgerMap.forEach(new BiConsumer<String,List<LedgerEntry>>(){

			@Override
			public void accept(String broker, List<LedgerEntry> list) {
				if(list == null) return;
				if(!broker.equals("Kotak")) return;
				System.out.println("Broker ...." + broker);
				list.forEach(new Consumer<LedgerEntry>(){

					@Override
					public void accept(LedgerEntry le) {
						System.out.println(le);
					}
					
				});
			}
			
		});
		
		
	}

	
}
