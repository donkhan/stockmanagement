package program;



public class ProfitCalculationProgram extends AbstractProgram{
	
	public static void main(String args[]){
		ProfitCalculationProgram cp = new ProfitCalculationProgram();
		cp.startExecute(args);
	}
	

	@Override
	protected long getTimerInterval() {
		return 0;
	}

	@Override
	protected void execute(boolean force,String args[]) {
	}
	
	
	
}
