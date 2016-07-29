package profit;

import java.util.Calendar;
import java.util.Locale;

public class DailyProfitCalendar extends ProfitCalendar{
	
	public DailyProfitCalendar(Calendar calendar, Double d) {
		super(calendar, d);
	}
	
	public int compareTo(ProfitCalendarInterface o) {
		Calendar c = o.getCalendar();
		Calendar myc = getCalendar();
		if(c.get(Calendar.YEAR) == myc.get(Calendar.YEAR)){
			if(c.get(Calendar.MONTH) == myc.get(Calendar.MONTH)){
				return c.get(Calendar.DATE) - myc.get(Calendar.DATE);
			}
			return c.get(Calendar.MONTH) - myc.get(Calendar.MONTH);
		}
		return c.get(Calendar.YEAR) - myc.get(Calendar.YEAR);
		
	}
	
	public String getDisplayString(){
		Calendar myc = getCalendar();
		return myc.get(Calendar.DATE) + "-" + myc.getDisplayName(Calendar.MONTH, Calendar.ALL_STYLES,Locale.getDefault()) + "-" + myc.get(Calendar.YEAR);
	}
}
