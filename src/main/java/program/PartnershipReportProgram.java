package program;


public class PartnershipReportProgram extends AbstractProgram{

	public static void main(String args[]){
		PartnershipReportProgram cp = new PartnershipReportProgram();
		cp.startExecute(args);
	}

	@Override
	protected long getTimerInterval() {
		return -1;
	}

	@Override
	protected void execute(boolean force, String[] args) {
		
	}

}
