package ItunezAPI;

public class ItunezRequest {
	public final static String RequestLine = "Getlist Itunez";
	public final static String CRLF = "\r\n";
	
	public String printRequest(){
		StringBuffer buf = new StringBuffer();
		buf.append(this.RequestLine+CRLF);
		buf.append("Content-length: 0"+CRLF);
		buf.append(CRLF);
		return buf.toString();
	}

}
