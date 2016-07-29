package program.profitcalculation;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import profit.DailyProfitCalendar;
import profit.ProfitCalendarInterface;

public class DailyProfitCalculationProgram extends AbstractProfitCalculationProgram{

	private GregorianCalendar begin = new GregorianCalendar();
	private GregorianCalendar end = new GregorianCalendar();
	
	public static void main(String args[]){
		DailyProfitCalculationProgram cp = new DailyProfitCalculationProgram();
		cp.prepareCutOff(args);
		cp.startExecute(args);
	}
	
	private void prepareCutOff(String[] args) {
		if(args.length == 0){
			end.set(Calendar.DATE,1);
			end.add(Calendar.MONTH, 1);
			resetCalendar(end);
			end.add(Calendar.MILLISECOND,-1);
		}

		if(args.length == 1){
			int month = this.getIntegerValue(args, "month");
			begin.set(Calendar.MONTH,month);
			end.set(Calendar.MONTH,month);
		}
		
		if(args.length == 2){
			int month = getIntegerValue(args, "month");
			int year = getIntegerValue(args, "year");
			begin.set(Calendar.MONTH,month);
			begin.set(Calendar.YEAR, year);
			end.set(Calendar.MONTH,month);
			end.set(Calendar.YEAR, year);
		}
		
		begin.set(Calendar.DATE,1);
		resetCalendar(begin);
		
		end.set(Calendar.DATE,1);
		end.add(Calendar.MONTH, 1);
		resetCalendar(end);
		end.add(Calendar.MILLISECOND,-1);
		
		System.out.println("Begin " + begin.getTime());
		System.out.println("End " + end.getTime());
	}

	@Override
	protected ProfitCalendarInterface getProfitCalendar(Calendar c, Double d) {
		return new DailyProfitCalendar(c,d);
	}

	@Override
	protected void resetCalendar(Calendar c) {
		c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0);
	}
	
	@Override
	protected String getReportFileName() {
		return "DailyProfitReport.jrxml";
	}


	@Override
	protected void filter(
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
}
