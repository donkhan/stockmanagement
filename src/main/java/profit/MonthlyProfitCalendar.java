package profit;

import java.util.Calendar;
import java.util.Locale;

public class MonthlyProfitCalendar extends ProfitCalendar{
	
	public MonthlyProfitCalendar(Calendar calendar, Double d) {
		super(calendar,d);
	}
	
	public String getDisplayString(){
		Calendar calendar = getCalendar();
		return calendar.getDisplayName(Calendar.MONTH, Calendar.ALL_STYLES, Locale.getDefault()) 
				+ "-" + calendar.get(Calendar.YEAR);
	}
		
	public String getPrefix(){
		return "Monthly";
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
