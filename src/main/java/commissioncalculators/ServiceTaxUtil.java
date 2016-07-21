package commissioncalculators;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import util.Global;

public class ServiceTaxUtil {
	
static GregorianCalendar juneone2015 = new GregorianCalendar();
	
	static{
		juneone2015.set(Calendar.HOUR_OF_DAY,0);juneone2015.set(Calendar.MINUTE,0);
		juneone2015.set(Calendar.SECOND,0); juneone2015.set(Calendar.MILLISECOND,0);
		juneone2015.set(Calendar.DATE,1);juneone2015.set(Calendar.MONTH,5);juneone2015.set(Calendar.YEAR,2015);
	}
	
	public static double getServiceTax(Date d) {
		return d.before(juneone2015.getTime()) ? 12.36d : 14d;
	}

}
