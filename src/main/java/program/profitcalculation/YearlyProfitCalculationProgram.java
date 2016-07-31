package program.profitcalculation;

import java.util.Calendar;

import profit.ProfitCalendarInterface;
import profit.YearlyProfitCalendar;

public class YearlyProfitCalculationProgram extends AbstractProfitCalculationProgram{

	public static void main(String args[]){
		YearlyProfitCalculationProgram dp = new YearlyProfitCalculationProgram();
		dp.startExecute(args);
	}
	
	@Override
	protected ProfitCalendarInterface getProfitCalendar(Calendar c, Double d) {
		return new YearlyProfitCalendar(c,d);
	}

	@Override
	protected void resetCalendar(Calendar c) {
		c.set(Calendar.DATE, 1); c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.MONTH, 0);
	}

	

	@Override
	protected void prepareCutOff(String[] args) {
		begin.set(Calendar.YEAR,2000);
	}
	
	public String getReadableDate(Calendar c){
		return  "" + c.get(Calendar.YEAR);
	}
	
}
