package RTSP;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Sdp {

	final static String CRLF = "\r\n";
	public static int PCMU_TYPE = 0;
	public static int PCMA_TYPE = 8;
	
	public static String getSdp(String sessionId, String rtspUri, int range){
		StringBuffer buf = new StringBuffer();
		buf.append("v=0"+CRLF);
		String timestamp = String.valueOf(System.currentTimeMillis());
		String hostname;
		try {
			hostname = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			hostname = "Itunez-server";
		}
		buf.append("o=- "+timestamp + " "+timestamp+" "+ "IN IP4 " +hostname+CRLF);
		buf.append("s="+sessionId+CRLF);
		buf.append("i=N/A"+CRLF);
		buf.append("c=IN IP4 0.0.0.0"+CRLF);
		buf.append("t=0 0"+CRLF);
		buf.append("a=tool:Itunez"+CRLF);
		buf.append("a=range:npt=0-"+range+CRLF);
		buf.append("a=recvonly"+CRLF);
		buf.append("a=type:unicast"+CRLF);
		buf.append("a=charset:UTF-8"+CRLF);
		buf.append("a=control:"+rtspUri+CRLF);
		buf.append("m=audio 0 RTP/AVP "+String.valueOf(PCMA_TYPE)+CRLF);
		buf.append("b=AS:128"+CRLF);
		buf.append("a=rtpmap:"+String.valueOf(PCMA_TYPE)+" PCMA/8000/2"+CRLF);
		return buf.toString();
		
	}
}
