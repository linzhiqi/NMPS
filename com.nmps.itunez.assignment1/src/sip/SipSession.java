package sip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import util.ResourceManager;

public class SipSession {

	private enum SessionState {
		initial, trying, ringing, receiving, bye
	}


	public final static int PCMA_TYPE = 8;
	private final static String allow = "INVITE, ACK, BYE, CANCEL, OPTIONS";
	private final static String CRLF = "\r\n";
	private String localip;
	private String sessionId;
	private Socket socket;
	private SipServer server;
	private SessionState state = SessionState.initial;
	private BufferedReader reader;
	private PrintWriter writer;
	private String account;
	private RTPReceiver rtpreceiver;
	//private int rtpRemotePort;
	private int rtpLocalPort;
	private ResourceManager rManager;

	public SipSession(Socket socket, ResourceManager manager)
			throws IOException {
		this.socket = socket;
		this.rManager = manager;
		this.sessionId = String.valueOf((int) (Math.random() * 1000000 + 100))
				+ "@" + socket.getLocalAddress().getHostAddress();
		this.localip = this.socket.getLocalAddress().getHostAddress();
		this.reader = new BufferedReader(new InputStreamReader(
				this.socket.getInputStream()));
		this.writer = new PrintWriter(this.socket.getOutputStream());
	}

	public void setSipServer(SipServer sipServer) {
		// TODO Auto-generated method stub
		this.server = sipServer;
	}

	public String getSessionId() {
		// TODO Auto-generated method stub
		return this.sessionId;
	}

	public String getAccount() {
		return this.account;
	}

	public void startprocess() {
		SipRequest request = null;
		SipResponse response = null;
		int tag = (int) (Math.random() * 10000);

		while (true) {
			if (this.state == SessionState.bye) {
				try {
					this.socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.server.removeSession(sessionId);
				break;
			}
			try {
				request = parseRequest(this.reader);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println(request.printMessage());
			response = new SipResponse();

			if (request.getMethod().equals(SipRequest.OPTIONS)) {
				response.setStatusLine("SIP/2.0 200 OK");
				String via = request.getHeader("Via");
				String[] parts = via.split("rport;");
				via = parts[0] + "rport=" + this.socket.getLocalPort() + ";"
						+ parts[1];
				response.setHeader("Via", via);
				response.setHeader("From", request.getHeader("From"));
				response.setHeader("To", request.getHeader("To") + ";tag="
						+ tag);
				response.setHeader("Call-ID", request.getHeader("Call-ID"));
				response.setHeader("Allow", SipSession.allow);
				response.setHeader("Cseq", request.getHeader("CSeq"));
				response.setHeader("ACCEPT", "application/SDP");
				response.setHeader("Content-length", "0");

				try {
					System.out.println("local port: "
							+ this.socket.getLocalPort() + "\nremote port: "
							+ this.socket.getPort() + "\nremote ip: "
							+ this.socket.getRemoteSocketAddress());
					System.out.println(response.printMessage());
					sendResponse(this.writer, response.printMessage());
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}

			if (request.getMethod().equals(SipRequest.INVITE)) {
				if (this.state == SessionState.initial) {
					this.account = request.getUri().split("@")[0].split("sip:")[1];
					response.setStatusLine("SIP/2.0 100 Trying");
					String via = request.getHeader("Via");
					String[] parts = via.split("rport;");
					via = parts[0] + "rport=" + this.socket.getLocalPort()
							+ ";" + parts[1];
					response.setHeader("Via", via);
					response.setHeader("From", request.getHeader("From"));
					response.setHeader("To", request.getHeader("To"));
					response.setHeader("Call-ID", request.getHeader("Call-ID"));
					response.setHeader("Cseq", request.getHeader("CSeq"));
					response.setHeader("Content-length", "0");

					try {
						System.out.println("local port: "
								+ this.socket.getLocalPort()
								+ "\nremote port: " + this.socket.getPort()
								+ "\nremote ip: "
								+ this.socket.getRemoteSocketAddress());
						System.out.println(response.printMessage());
						sendResponse(this.writer, response.printMessage());
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}

					this.state = SessionState.trying;
				}

				if (this.state == SessionState.trying) {

					try {
						this.rtpreceiver = new RTPReceiver(
								this, this.rManager);
					} catch (SocketException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					this.rtpLocalPort = this.rtpreceiver.getLocalPort();
					new Thread(this.rtpreceiver).start();

					response = new SipResponse();
					response.setStatusLine("SIP/2.0 180 Ringing");
					String via = request.getHeader("Via");
					String[] parts = via.split("rport;");
					via = parts[0] + "rport=" + this.socket.getLocalPort()
							+ ";" + parts[1];
					response.setHeader("Via", via);
					response.setHeader("From", request.getHeader("From"));
					response.setHeader("To", request.getHeader("To") + ";tag="
							+ tag);
					response.setHeader("Contact", "<sip:" + this.account + "@"
							+ this.localip + ";transport=tcp>");
					response.setHeader("Call-ID", request.getHeader("Call-ID"));
					response.setHeader("Cseq", request.getHeader("CSeq"));
					response.setHeader("Content-length", "0");

					try {
						System.out.println(response.printMessage());
						sendResponse(this.writer, response.printMessage());
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}
					this.state = SessionState.ringing;
				}

				if (this.state == SessionState.ringing) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					response = new SipResponse();
					response.setBody(createSdp());
					response.setStatusLine("SIP/2.0 200 OK");
					String via = request.getHeader("Via");
					String[] parts = via.split("rport;");
					via = parts[0] + "rport=" + this.socket.getLocalPort()
							+ ";" + parts[1];
					response.setHeader("Via", via);
					response.setHeader("From", request.getHeader("From"));
					response.setHeader("To", request.getHeader("To") + ";tag="
							+ tag);
					response.setHeader("Contact", "<sip:" + this.account + "@"
							+ this.localip + ";transport=tcp>");
					response.setHeader("Call-ID", request.getHeader("Call-ID"));
					response.setHeader("Cseq", request.getHeader("CSeq"));
					response.setHeader("Content-Type", "application/sdp");
					response.setHeader("Content-length",
							String.valueOf(response.getBody().length()));

					try {
						System.out.println(response.printMessage());
						sendResponse(this.writer, response.printMessage());
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}
					this.state = SessionState.receiving;
				}
			}

			if (request.getMethod().equals(SipRequest.BYE)) {
				if (this.state == SessionState.receiving) {
					this.state = SessionState.bye;
				}
			}

		}
	}

	private String createSdp() {
		// TODO Auto-generated method stub
		StringBuffer buf = new StringBuffer();
		buf.append("v=0" + CRLF);
		String timestamp = String.valueOf(System.currentTimeMillis());
		buf.append("o=" + this.account + " " + timestamp + " " + timestamp
				+ " " + "IN IP4 " + this.localip + CRLF);
		buf.append("s=Talk" + CRLF);
		buf.append("c=IN IP4 " + this.localip + CRLF);
		buf.append("t=0 0" + CRLF);
		buf.append("m=audio " + this.rtpLocalPort + " RTP/AVP "
				+ String.valueOf(PCMA_TYPE) + CRLF);
		buf.append("a=rtpmap:" + String.valueOf(PCMA_TYPE) + " PCMA/8000/2"
				+ CRLF);
		return buf.toString();
	}

	private SipRequest parseRequest(BufferedReader reader) throws IOException {
		// TODO Auto-generated method stub
		SipRequest request = new SipRequest();
		String requestLine = null;
		requestLine = reader.readLine();
		if (requestLine != null) {
			request.setRequestLine(requestLine);
			String[] parts = requestLine.split(" ");
			if (parts.length != 3) {
				return null;
			}
			request.setMethod(parts[0].trim());
			request.setUri(parts[1].trim());
		}

		while (true) {
			String buf = null;
			if ((buf = (reader.readLine())).equals("")) {
				getRequestBody(reader, request);
				return request;
			}

			if (buf.contains("Via:")) {
				request.setHeader("Via", buf.split("Via:")[1].trim());
			}

			if (buf.contains("From:")) {
				request.setHeader("From", buf.split("From:")[1].trim());
			}

			if (buf.contains("To:")) {
				request.setHeader("To", buf.split("To:")[1].trim());
			}

			if (buf.contains("Call-ID:")) {
				request.setHeader("Call-ID", buf.split("Call-ID:")[1].trim());
			}

			if (buf.contains("CSeq:")) {
				request.setHeader("CSeq", buf.split("CSeq:")[1].trim());
			}

			if (buf.contains("Content-Length:")) {
				request.setHeader("Content-Length",
						buf.split("Content-Length:")[1].trim());
			}
		}
	}

	private void getRequestBody(BufferedReader reader, SipRequest request)
			throws IOException {
		// TODO Auto-generated method stub
		int len = 0;
		char[] cbuf = null;
		if (request.hasHeader("Content-Length")) {
			len = Integer.valueOf(request.getHeader("Content-Length"));
		}

		if (len >= 0) {
			cbuf = new char[len];
			reader.read(cbuf, 0, len);
		}

		request.setBody(String.valueOf(cbuf));
	}

	public void sendResponse(PrintWriter writer, String message)
			throws IOException {
		writer.write(message);
		writer.flush();

	}

}
