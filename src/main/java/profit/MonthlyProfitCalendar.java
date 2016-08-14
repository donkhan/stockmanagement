package profit;

import java.util.Calendar;

public class MonthlyProfitCalendar extends ProfitCalendar{
	
	public MonthlyProfitCalendar(Calendar calendar, Double d) {
		super(calendar,d);
	}
	
	public String getDisplayString(){
		Calendar calendar = getCalendar();
		return (calendar.get(Calendar.MONTH)+1)
				+ "-" + calendar.get(Calendar.YEAR);
	}
	
	public int compareTo(ProfitCalendarInterface o) {
		Calendar c = o.getCalendar();
		Calendar calendar = getCalendar();
		if(c.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)){
			return c.get(Calendar.MONTH) - calendar.get(Calendar.MONTH);
		}
		return c.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
	}

}
