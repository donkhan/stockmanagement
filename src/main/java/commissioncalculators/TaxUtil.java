package commissioncalculators;

import java.util.Date;
import java.util.GregorianCalendar;

public class TaxUtil {
	
	public static boolean cutOff(Date d, GregorianCalendar c2){
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(d);
		//System.out.println(" " + c2.getTime() + "  " + cal.getTime() );
		return cal.before(c2);
	}
}
