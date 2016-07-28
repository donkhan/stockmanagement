package profit;

import java.util.Calendar;

public class YearlyProfitCalendar extends ProfitCalendar{

	public YearlyProfitCalendar(Calendar calendar, Double d) {
		super(calendar, d);
	}
	
	public String getDisplayString(){
		return "" +  getCalendar().get(Calendar.YEAR);
	}

	public String getPrefix(){
		return "Yearly";
	}

	public int compareTo(ProfitCalendarInterface o) {
		Calendar c = o.getCalendar();
		Calendar myc = getCalendar();
		return c.get(Calendar.YEAR) - myc.get(Calendar.YEAR);
	}
}
