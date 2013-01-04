package sip;

import java.util.Hashtable;
import java.util.Map.Entry;

public class SipResponse {

	private final static String CRLF = "\r\n";
	private String statusLine;
	private Hashtable<String, String> headers = new Hashtable<String, String>();
	private String body;
	
	public void setStatusLine(String line){
		this.statusLine = line;
	}
	
	public String getStatusLine(){
		return this.statusLine;
	}
	
	public void setHeader(String name, String value){
		this.headers.put(name, value);
	}
	
	public boolean hasHeader(String name){
		return this.headers.containsKey(name);
	}
	
	public String getHeader(String name){
		return this.headers.get(name);
	}
	
	public void setBody(String valueOf) {
		// TODO Auto-generated method stub
		this.body = valueOf;
	}
	
	public String getBody(){
		return this.body;
	}
	
public String printMessage(){
		
		//refactory needed
		StringBuffer builder = new StringBuffer();
		
		builder.append(this.statusLine+CRLF);
		for(Entry<String, String> header : headers.entrySet()){
			builder.append(header.getKey()+": "+header.getValue()+CRLF);
		}
		builder.append(CRLF);
		if(this.body!=null){
			builder.append(this.body);
		}
		
		return builder.toString();
	}
}
