package program;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import util.Global;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;


public abstract class AbstractProgram {

	protected abstract long getTimerInterval();
	protected abstract void execute(final boolean force,final String args[]);
	
	protected void main(final boolean force, final String[] args){
		if(force || getTimerInterval() == -1){
			execute(force,args);
			return;
		}
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				execute(force,args);
				System.out.println("Next Scheduled Execution is at " + 
						new Date(System.currentTimeMillis() + getTimerInterval()));
			}
		}, 0, getTimerInterval());
	}
	
	
	public void startExecute(String args[]){
		boolean force = getBooleanValue(args,"force");
		Global.debug = getBooleanValue(args,"debug");
		main(force,args);
	}
	
	protected boolean getBooleanProperty(String arg){
		StringTokenizer tokenizer = new StringTokenizer(arg,"=");
		tokenizer.nextToken();
		if(tokenizer.hasMoreTokens()){
			return Boolean.parseBoolean(tokenizer.nextToken());
		}
		return false;
	}

	protected String getStringProperty(String arg){
		StringTokenizer tokenizer = new StringTokenizer(arg,"=");
		tokenizer.nextToken();
		return tokenizer.nextToken();
	}
	
	protected String getValue(String args[],String arg,String defaultValue){
		for(int i = 0;i<args.length;i++){
			if(args[i].startsWith("--"+ arg)){
				return getStringProperty(args[i]);
			}
		}
		return defaultValue;
	}
	
	protected int getIntegerValue(String args[],String arg){
		String v = getValue(args,arg,"30");
		return Integer.parseInt(v);
	}
	
	protected boolean getBooleanValue(String args[],String arg){
		String v = getValue(args,arg,"false");
		return Boolean.parseBoolean(v);
	}
	
	protected void styleAsHeaderCell(PdfPCell cell){
	    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    cell.setPaddingTop(0f);
	    cell.setPaddingBottom(7f);
	    cell.setBackgroundColor(new BaseColor(0,121,182));
	    cell.setBorder(0);
	    cell.setBorderWidthBottom(2f);
	}
	
	protected void addHeaders(PdfPTable table,String[] headers){
		for(String header : headers){
			PdfPCell cell = new PdfPCell(new Paragraph(header));
			styleAsHeaderCell(cell);
			table.addCell(cell);
		}
	}
	
	protected void addRow(List<Object> row,PdfPTable table){
		for(Object z : row){
			PdfPCell cell = null;
			if(z instanceof java.lang.Double){
				String x = new DecimalFormat("#,###,###,##0.00").format(Double.parseDouble(z.toString()));
				cell = new PdfPCell(new Paragraph(x));
			}
			if(z instanceof String){
				cell = new PdfPCell(new Paragraph(z.toString()));
			}
			if(z instanceof Integer || z instanceof Long){
				cell = new PdfPCell(new Paragraph(z.toString()));
			}
			if(z instanceof java.lang.Number){
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			}
			table.addCell(cell);
		}
	}
	
}