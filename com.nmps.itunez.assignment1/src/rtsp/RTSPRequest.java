package rtsp;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Hashtable;

public class RTSPRequest {
	
	public static final String option = "OPTIONS";
	public static final String describe = "DESCRIBE";
	public static final String setup = "SETUP";
	public static final String get_parameter = "GET_PARAMETER";
	public static final String play = "PLAY";
	public static final String pause ="PAUSE";
	public static final String teardown = "TEARDOWN";
	
	private String method;
	private int Cseq;
	private String uri;
	
	private Hashtable<String, String> headers = new Hashtable<String, String>();
	
	
	public void setMethod(String method){
		this.method = method;
	}
	
	public String getMethod(){
		return this.method;
	}
	
	public void setUri(String uri){
		this.uri = uri;
	}
	
	public String getUri(){
		return this.uri;
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
	
	public void setCseq(int Cseq){
		this.Cseq = Cseq;
	}
	
	public int getCseq(){
		return this.Cseq;
	}
	
}
