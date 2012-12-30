package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class ResourceManager {
	private FileHelper filehelper;
	private String base;
	
	public ResourceManager(String base){
		this.filehelper = new FileHelper(base);
		this.base = base;
	}
	
	public ArrayList<String> getFiles(){
		return this.filehelper.getFiles();
	}
	
	public FileHelper getFileHelper(){
		return this.filehelper;
	}
	
	public boolean hasResource(String fileName){
		return this.filehelper.hasFile(fileName);
	}
	
	public File getResource(String fileName){
		File file = new File(base+"\\"+fileName);
		System.out.println("get resource:"+base+"\\"+fileName);
		return file;
	}
	
	public FileInputStream getInStream(String fileName) throws FileNotFoundException{
		return new FileInputStream(getResource(fileName));
	}
	
	public long getResourceSize(String fileName){
		return getResource(fileName).getTotalSpace();
	}

}
