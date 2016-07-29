package program.profitcalculation;

import java.util.Calendar;
import java.util.Locale;

import profit.MonthlyProfitCalendar;
import profit.ProfitCalendarInterface;

public class MonthlyProfitCalculationProgram extends AbstractProfitCalculationProgram{

	public static void main(String args[]){
		MonthlyProfitCalculationProgram cp = new MonthlyProfitCalculationProgram();
		cp.startExecute(args);
	}

	@Override
	protected ProfitCalendarInterface getProfitCalendar(Calendar c, Double d) {
		return new MonthlyProfitCalendar(c,d);
	}

	@Override
	protected void resetCalendar(Calendar c) {
		c.set(Calendar.DATE, 1); c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0);
	}
	
	@Override
	protected String getReportFileName() {
		return "MonthlyProfitReport.jrxml";
	}

	@Override
	protected void prepareCutOff(String[] args) {
		if(args.length == 1){
			int year = this.getIntegerValue(args, "year");
			begin.set(Calendar.YEAR,year);
			end.set(Calendar.MONTH,year);
		}		
		resetCalendar(begin);
		begin.set(Calendar.MONTH,0);
		resetCalendar(end);
		end.set(Calendar.MONTH,11);
		end.add(Calendar.MONTH,1);
		end.add(Calendar.MILLISECOND, -1);
		System.out.println("Begin " + begin.getTime());
		System.out.println("End " + end.getTime());

	}

	
	public String getReadableDate(Calendar c){
		return  ""  + c.getDisplayName(Calendar.MONTH, Calendar.ALL_STYLES, Locale.getDefault()) + "-" + c.get(Calendar.YEAR);
	}

}
