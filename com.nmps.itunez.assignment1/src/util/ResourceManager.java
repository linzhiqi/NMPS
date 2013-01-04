package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class ResourceManager {
	private FileHelper filehelper;
	private String base;
	public final static String defaultBasePath = "C:\\Users\\Vita\\Documents\\study\\3152\\assignment\\mediahouse";

	public ResourceManager(String base) {

		this.filehelper = new FileHelper(base);
		this.base = base;
	}

	public boolean baseExists() {
		File file = new File(base);
		return file.exists();
	}

	public ArrayList<String> getFiles() {
		return this.filehelper.getFiles();
	}

	public FileHelper getFileHelper() {
		return this.filehelper;
	}

	public boolean hasResource(String fileName) {
		return this.filehelper.hasFile(fileName);
	}

	public File getResource(String fileName) {
		File file = new File(base + "\\" + fileName);
		System.out.println("get resource:" + base + "\\" + fileName);
		return file;
	}

	public FileInputStream getInStream(String fileName)
			throws FileNotFoundException {
		return new FileInputStream(getResource(fileName));
	}

	public long getResourceSize(String fileName) {
		return getResource(fileName).getTotalSpace();
	}
	
	public File createFile(String name) throws IOException{
		File file = new File(base+"\\"+name);
		file.createNewFile();
		return file;
	}

}
