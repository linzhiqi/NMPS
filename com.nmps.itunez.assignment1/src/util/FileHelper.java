package util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {

	private String base;

	public FileHelper(String dir) {
		this.base = dir;
	}
	

	public FileHelper() {

	}

	public void setBase(String dir) {
		this.base = dir;
	}

	public String getBase() {
		return this.base;
	}

	public ArrayList<String> getFiles() {
		// to do
		if (this.base == null) {
			return null;
		}
		List<String> fileList = new ArrayList<String>();
		File[] files = new File(this.base).listFiles();

		for (File file : files) {
			if(file.isFile()){
				fileList.add(file.getName());
			}			
		}
		return (ArrayList<String>) fileList;
	}
	
	public boolean hasFile(String fileName){
		ArrayList<String> files = getFiles();
		for(String file:files){
			if(file.equals(fileName)){
				return true;
			}
		}
		return false;
	}
	
	public boolean hasFile(String fileName, String dirName){
		ArrayList<String> files = getFiles(dirName);
		for(String file:files){
			if(file.equals(fileName)){
				return true;
			}
		}
		return false;
	}

	public ArrayList<String> getFiles(String obsolutePath) {
		// to do
		List<String> fileList = new ArrayList<String>();
		File[] files = new File(obsolutePath).listFiles();

		for (File file : files) {
			if (file.isFile()) {
				fileList.add(file.getName());
			}
		}
		return (ArrayList<String>) fileList;
	}

	public static final void main(String[] args) {
		FileHelper manager = new FileHelper("C:\\Users\\Vita\\Documents");
		for (String filename : manager.getFiles()) {
			System.out.println(filename);
		}
	}
}
