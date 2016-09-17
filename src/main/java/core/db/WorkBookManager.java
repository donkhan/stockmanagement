package core.db;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;

import jxl.Workbook;
import jxl.read.biff.BiffException;

public class WorkBookManager {

	public Workbook getWorkBook(){
		return getWorkBook("");
	}
	
	public Workbook getWorkBook(String inputFile){
		Workbook workBook = null;
		try{
			if(!inputFile.equals("")){
				System.out.println("Reading File given as argument " + inputFile);
				File inputWorkbook = new File(inputFile);
				workBook = Workbook.getWorkbook(inputWorkbook);
			}else{
				String absolutePath = System.getProperty("user.dir") + File.separatorChar + "src" + File.separatorChar + "main" + File.separatorChar + "resources" + File.separatorChar + "Trade.xls";
				File file = new File(absolutePath);
				if(file.exists()){
					System.out.println("Reading File in the Path " + file.getAbsolutePath());
					workBook = Workbook.getWorkbook(file);
				}else{
					System.out.println("Reading File in the Bundle");
					workBook = Workbook.getWorkbook(URLClassLoader.getSystemResourceAsStream("Trade.xls"));
				}
			}
		}catch(IOException ioe){
			ioe.printStackTrace();
		}catch(BiffException bie){
			bie.printStackTrace();
		}
		return workBook;
	}
}
