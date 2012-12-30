package ItunezAPI;

public class ItunezResponse {
	public final static String OK = "OK";
	public final static String BAD_REQUEST = "BAD REQUEST";
	public final static String INTERNAL_ERROR = "INTERNAL ERROR";
	public final static String CRLF = "\r\n";
	private String responseLine;
	private String content;
	private int contentLen;
	
	public void setResponseLine(String responseline){
		this.responseLine = responseline;
	}
	
	public int setContent(String content){
		this.content = content;
		this.contentLen = content.length();
		return this.contentLen;
	}
	
	public String printResponse(){
		StringBuffer buf = new StringBuffer();
		buf.append(this.responseLine+CRLF);
		buf.append("Content-length: "+this.contentLen+CRLF);
		buf.append(CRLF);
		buf.append(this.content);
		return buf.toString();
	}
	
}
