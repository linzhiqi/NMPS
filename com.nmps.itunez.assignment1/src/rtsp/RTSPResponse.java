package rtsp;

import java.util.Hashtable;
import java.util.Map.Entry;

public class RTSPResponse {
	
	public static final String ok = "RTSP/1.0 200 OK";
	public static final String error = "RTSP/1.0 400 Bad Request";
	final static String CRLF = "\r\n";
	private int code;
	private String responseLine;
	
	private Hashtable<String, String> headers = new Hashtable<String, String>();
	
	private String content;
	
	public void setCode(boolean isOk){
		if(isOk){
			this.code = 200;
			this.responseLine = ok;
		}else{
			this.code = 400;
			this.responseLine = error;
		}
	}
	
	public int getCode(){
		return this.code;
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
	
	public void setContent(String content){
		this.content = content;
	}
	
	public String getContent(){
		return this.content;
	}
	
	
	public String getMessage(){
		
		//refactory needed
		StringBuffer builder = new StringBuffer();
		
		builder.append(this.responseLine+CRLF);
		for(Entry<String, String> header : headers.entrySet()){
			builder.append(header.getKey()+": "+header.getValue()+CRLF);
		}
		builder.append(CRLF);
		if(this.content!=null){
			builder.append(this.content);
		}
		
		return builder.toString();
	}
	

}
