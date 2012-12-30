package ItunezAPI;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;


public class PlayList {
	
	class ResourcePresentation{
		private String title;
		private String rtspUrl;
		
		public ResourcePresentation(String title, String url){
			this.title = title;
			this.rtspUrl = url;
		}
		
		public String print(){
			return "title="+this.title+"\n"+"url="+this.rtspUrl+"\n";
		}
	}
	
	private ArrayList<ResourcePresentation> playlist = new ArrayList<ResourcePresentation>();
	
	public void addResource(String title, String url){
		
		this.playlist.add(new ResourcePresentation(title,url));		
	}
	
	public String serialize(){
		StringBuffer buf = new StringBuffer();
		Iterator<ResourcePresentation> it = this.playlist.iterator();
		while(it.hasNext()){
			buf.append(it.next().print());
		}
		return buf.toString();
	}
	
	public static PlayList parse(String listText) throws IOException, InvalidePlayListException{
		BufferedReader reader = new BufferedReader( new InputStreamReader(new ByteArrayInputStream( listText.getBytes("UTF-8") )));
		PlayList list = new PlayList();
		String title = null;
		String url = null;
		String line = null;
		while((line = reader.readLine() )!= null){
			if(line.contains("title=")){
				title = line.substring(line.indexOf("title=")+6);
				if((line = reader.readLine())!=null){
					if(line.contains("url=")){
						url = line.substring(line.indexOf("url=")+4);
						list.addResource(title, url);
						continue;
					}
				}
			}
			
			throw new InvalidePlayListException();
		}
		return list;
	}
	

}
