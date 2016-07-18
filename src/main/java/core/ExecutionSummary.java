package core;

import java.util.Date;

public class ExecutionSummary {
	
	private Date executionTime;
	private Date nextExecutionTime;
	public Date getExecutionTime() {
		return executionTime;
	}
	public void setExecutionTime(Date executionTime) {
		this.executionTime = executionTime;
	}
	public long getTimeToExecute() {
		return timeToExecute;
	}
	public int getExecutionTimeInSeconds(){
		return (int)(timeToExecute/1000);
	}
	public void setTimeToExecute(long timeToExecute) {
		this.timeToExecute = timeToExecute;
	}
	public Date getNextExecutionTime() {
		return nextExecutionTime;
	}
	public void setNextExecutionTime(Date nextExecutionTime) {
		this.nextExecutionTime = nextExecutionTime;
	}
	
	private long timeToExecute;
}
