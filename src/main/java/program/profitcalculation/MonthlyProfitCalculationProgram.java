package program.profitcalculation;

import java.util.Calendar;
import java.util.List;

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
	protected void filter(
			List<ProfitCalendarInterface> list) {
	}
}
