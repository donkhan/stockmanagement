package commissioncalculators;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CessUtil extends TaxUtil{
	
	static GregorianCalendar krishKalyanCess = new GregorianCalendar();
	static GregorianCalendar swatchBharatCess = new GregorianCalendar();
	
	static{
		krishKalyanCess.set(Calendar.HOUR_OF_DAY,0);krishKalyanCess.set(Calendar.MINUTE,0);
		krishKalyanCess.set(Calendar.SECOND,0); krishKalyanCess.set(Calendar.MILLISECOND,0);
		krishKalyanCess.set(Calendar.DATE,1);krishKalyanCess.set(Calendar.MONTH,5);krishKalyanCess.set(Calendar.YEAR,2016);
		
		
		swatchBharatCess.set(Calendar.HOUR_OF_DAY,0);swatchBharatCess.set(Calendar.MINUTE,0);
		swatchBharatCess.set(Calendar.SECOND,0); swatchBharatCess.set(Calendar.MILLISECOND,0);
		swatchBharatCess.set(Calendar.DATE,15);swatchBharatCess.set(Calendar.MONTH,10);swatchBharatCess.set(Calendar.YEAR,2015);
		
	}
	
	public static double getKrishKalyanCess(Date d) {
		return cutOff(d,krishKalyanCess) ? 0d : 0.5d;
	}
	
	public static double getSwatchBharatCess(Date d){
		return cutOff(d,swatchBharatCess) ? 0d : 0.5d;
	}
}
