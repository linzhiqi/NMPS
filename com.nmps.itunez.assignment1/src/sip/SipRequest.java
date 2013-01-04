package sip;

import java.util.Hashtable;
import java.util.Map.Entry;

public class SipRequest {
	
	public static final String OPTIONS = "OPTIONS";
	public static final String INVITE = "INVITE";
	public static final String BYE = "BYE";
	public static final String CRLF = "\r\n";
	
	private String method;
	private String uri;
	private String requestLine;
	private Hashtable<String, String> headers = new Hashtable<String, String>();
	private String body;
	
	public void setMethod(String method){
		this.method = method;
	}
	
	public String getMethod(){
		return method;
	}
	

	public void setUri(String uri){
		this.uri = uri;
	}
	
	public String getUri(){
		return this.uri;
	}
	
	public void setRequestLine(String line){
		this.requestLine = line;
	}
	
	public String getRequestLine(){
		return this.requestLine;
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
StringBuffer builder = new StringBuffer();
		
		builder.append(this.requestLine+CRLF);
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
