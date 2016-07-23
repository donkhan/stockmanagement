package profit;

import java.util.Calendar;

public class ProfitCalendar implements Comparable<ProfitCalendar>{
	
	private Calendar calendar;
	public ProfitCalendar(Calendar calendar, Double d) {
		this.calendar = calendar;
		this.profit = d;
	}
	private Double profit;
	
	public int compareTo(ProfitCalendar o) {
		Calendar c = o.calendar;
		if(c.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)){
			return c.get(Calendar.MONTH) - calendar.get(Calendar.MONTH);
		}
		return c.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
		
	}

	private static String months[] = new String[]{"Jan", "Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
	public void print() {
		System.out.println(months[calendar.get(Calendar.MONTH)] + "-" + calendar.get(Calendar.YEAR) + "=" + profit);
		
	}
	
	
}
