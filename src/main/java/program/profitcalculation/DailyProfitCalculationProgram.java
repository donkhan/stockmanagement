package program.profitcalculation;

import java.util.Calendar;
import java.util.Locale;

import profit.DailyProfitCalendar;
import profit.ProfitCalendarInterface;

public class DailyProfitCalculationProgram extends AbstractProfitCalculationProgram{
	
	public static void main(String args[]){
		DailyProfitCalculationProgram cp = new DailyProfitCalculationProgram();
		cp.startExecute(args);
	}
	
	public void prepareCutOff(String[] args) {
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

	public String getReadableDate(Calendar c){
		System.out.println(c.getTime() + "   " + c.get(Calendar.MONTH));
		return  "" + c.get(Calendar.DATE) + "-"
				+ (c.get(Calendar.MONTH)+1) + "-" + c.get(Calendar.YEAR);
	}
	
	


	
}
