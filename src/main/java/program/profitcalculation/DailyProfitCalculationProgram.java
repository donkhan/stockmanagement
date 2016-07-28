package program.profitcalculation;

import jasper.ProfitReportGenerator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import profit.DailyProfitCalendar;
import profit.ProfitCalendar;

public class DailyProfitCalculationProgram extends AbstractProfitCalculationProgram{

	public static void main(String args[]){
		DailyProfitCalculationProgram cp = new DailyProfitCalculationProgram();
		cp.startExecute(args);
	}
	
	public void process(Map<Calendar,ProfitCalendar> map){
		List<ProfitCalendar> list = new ArrayList<ProfitCalendar>();
		Iterator<Calendar> keyIterator = map.keySet().iterator();
		while(keyIterator.hasNext()){
			Calendar key = keyIterator.next();
			list.add(map.get(key));
		}
		Collections.sort(list);
		prepareReport(list);
	}
	
	private void prepareReport(List<ProfitCalendar> profitCalendarList) {
		ProfitReportGenerator gen = new ProfitReportGenerator();
		gen.generate(profitCalendarList,"DailyProfitReport.jrxml");
	}

	@Override
	protected ProfitCalendar getProfitCalendar(Calendar c, Double d) {
		return new DailyProfitCalendar(c,d);
	}

	@Override
	protected void resetCalendar(Calendar c) {
		c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0);
	}
}
