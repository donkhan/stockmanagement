package program;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;


public abstract class AbstractProgram {

	protected abstract long getTimerInterval();
	protected abstract void execute(final boolean force,final boolean sendmail,final String specificStock,
			final String filePath,final String args[]);
	
	protected void main(final boolean force, final boolean sendmail,final String specificStock,final String filePath,final String[] args){
		if(force){
			execute(force,sendmail,specificStock,filePath,args);
			return;
		}
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				execute(force,sendmail,specificStock,filePath,args);
				System.out.println("Next Scheduled Execution is at " + 
						new Date(System.currentTimeMillis() + getTimerInterval()));
			}
		}, 0, getTimerInterval());
	}
	
	
	public void startExecute(String args[]){
		boolean force = true, sendmail = true;
		String specificStock = "None";
		String filePath = "";
		for(int i = 0;i<args.length;i++){
			String arg = args[i];
			if(arg.startsWith("--force")){
				force = getBooleanProperty(arg);
			}
			if(arg.startsWith("--sendmail")){
				sendmail = getBooleanProperty(arg);
			}
			if(arg.startsWith("--specificstock")){
				specificStock = getStringProperty(arg);
			}
			if(arg.startsWith("--filepath")){
				filePath = getStringProperty(arg);
			}
		}
		main(force,sendmail,specificStock,filePath,args);
	}
	
	protected boolean getBooleanProperty(String arg){
		StringTokenizer tokenizer = new StringTokenizer(arg,"=");
		tokenizer.nextToken();
		return Boolean.parseBoolean(tokenizer.nextToken());
	}

	protected String getStringProperty(String arg){
		StringTokenizer tokenizer = new StringTokenizer(arg,"=");
		tokenizer.nextToken();
		return tokenizer.nextToken();
	}
	
	protected int getIntegerValue(String args[],String arg){
		for(int i = 0;i<args.length;i++){
			if(args[i].startsWith("--"+ arg)){
				return Integer.parseInt(getStringProperty(args[i]));
			}
		}
		return 30;
	}
	
}