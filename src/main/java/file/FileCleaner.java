package file;

import java.io.File;

import util.InternetService;

public class FileCleaner {

	public boolean clean(String fileName){
		if(InternetService.up()){
			System.out.println("Going to delete " + fileName);
			File file = new File(fileName);
			boolean deleteStatus = file.delete();
			System.out.println(fileName + (deleteStatus ? " Deleted " : " Not Deleted"));
			return deleteStatus;
		}
		return false;
	}
}
