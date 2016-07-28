package profit;

import java.util.Calendar;

public class YearlyProfitCalendar extends ProfitCalendar<YearlyProfitCalendar>{

	public YearlyProfitCalendar(Calendar calendar, Double d) {
		super(calendar, d);
	}
	
	public int compareTo(YearlyProfitCalendar o) {
		Calendar c = o.getCalendar();
		Calendar myCalendar = getCalendar();
		return c.get(Calendar.YEAR) - myCalendar.get(Calendar.YEAR);
		
	}
	
	public String getDisplayString(){
		return "" +  getCalendar().get(Calendar.YEAR);
	}

	public String getPrefix(){
		return "Yearly";
	}
}
