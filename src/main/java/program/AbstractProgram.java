package program;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import util.Global;


public abstract class AbstractProgram {

	protected abstract long getTimerInterval();
	protected abstract void execute(final boolean force,final String args[]);
	
	protected void main(final boolean force, final String[] args){
		if(force){
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
	
}