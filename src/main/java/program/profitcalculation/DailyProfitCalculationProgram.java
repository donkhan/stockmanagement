package program.profitcalculation;

import java.util.Calendar;

import profit.DailyProfitCalendar;
import profit.ProfitCalendarInterface;

public class DailyProfitCalculationProgram extends AbstractProfitCalculationProgram{

	public static void main(String args[]){
		DailyProfitCalculationProgram cp = new DailyProfitCalculationProgram();
		cp.startExecute(args);
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
}
