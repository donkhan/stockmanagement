package commissioncalculators;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class TaxUtil {
	
	public static boolean cutOff(Calendar d, GregorianCalendar c2){
		return d.before(c2);
	}
}
