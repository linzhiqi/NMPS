package rtsp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import rtp.RTPpacket;
import rtp.SendRTPTask;
import rtp.RTPServer;
import util.ResourceManager;

public class RTSPSession {
	public final static String options = "DESCRIBE,SETUP,TEARDOWN,PLAY,PAUSE";

	public enum SessionState {
		initial, ready, playing, paused, teardown
	}

	public enum RequestMethod {
		option, describe, setup, play, pause, teardown
	}

	private String sessionId;
	private String streamSession;
	private RTSPStack stack;
	private Socket socket;
	private InetAddress clientIp;
	private SessionState state;
	private int Cseq;
	private String resource;
	private int[] clientport = new int[2];
	private int[] serverport = new int[2];
	private RTPServer rtpmanager = null;
	private String audioFileName;
	private BufferedReader RTSPBufferedReader;
	private BufferedWriter RTSPBufferedWriter;

	RTSPSession(String uuid, Socket socket) throws IOException {
		this.sessionId = uuid;
		this.socket = socket;
		this.state = SessionState.initial;
		this.clientIp = socket.getInetAddress();

		this.RTSPBufferedReader = new BufferedReader(new InputStreamReader(
				this.socket.getInputStream()));
		this.RTSPBufferedWriter = new BufferedWriter(new OutputStreamWriter(
				this.socket.getOutputStream()));
	}

	void setTheStack(RTSPStack stackInstance) {
		this.stack = stackInstance;
	}

	String getSessionID() {
		return sessionId;
	}

	void startprocess() {
		RTSPRequest request = null;
		RTSPResponse response = null;
		while (true) {
			if (this.state == SessionState.teardown) {
				break;
			}
			try {
				request = parseRequest(this.RTSPBufferedReader);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			response = new RTSPResponse();
			response.setHeader("Cseq", Integer.toString(request.getCseq()));

			System.out.println("method=" + request.getMethod());

			if (request.getMethod().equals(RTSPRequest.option)) {
				System.out.println("inside");
				response.setCode(true);
				response.setHeader("Content-length", "0");
				response.setHeader("Public", this.options);

			}

			if (request.getMethod().equals(RTSPRequest.describe)) {
				this.resource = parseUri(request.getUri());
				ResourceManager reManager = this.stack.getResourceManager();
				if (reManager.getFileHelper().hasFile(this.resource)) {
					String sdp = Sdp.getSdp(this.sessionId, request.getUri(),
							120);
					response.setCode(true);
					response.setHeader("Content-type", "application/sdp");
					response.setHeader("Content-base", request.getUri());
					response.setHeader("Content-length",
							String.valueOf(sdp.length()));
					response.setHeader("Cache-Control", "no-cache");
					response.setContent(sdp);
				} else {
					response.setCode(false);
					this.state = SessionState.teardown;
				}
			}

			if (this.state == SessionState.initial) {
				if (request.getMethod().equals(RTSPRequest.setup)) {
					// to do
					this.state = SessionState.ready;

					String transport = request.getHeader("Transport");
					int index = transport.indexOf("client_port=");
					String clientport = transport.substring(index + 12);
					String[] str = clientport.split("-");
					this.clientport[0] = Integer.parseInt(str[0]);
					this.clientport[1] = Integer.parseInt(str[1]);
					try {
						this.rtpmanager = new RTPServer(
								this.stack.getResourceManager());
					} catch (SocketException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					this.serverport = this.rtpmanager.getServerPort();
					this.rtpmanager.setClientIp(this.clientIp);
					this.rtpmanager.setclientport(this.clientport);
					this.rtpmanager.setResource(this.resource);
					response.setCode(true);
					response.setHeader(
							"Transport",
							"RTP/AVP/UDP;unicast;client_port="
									+ String.valueOf(this.clientport[0]) + "-"
									+ String.valueOf(this.clientport[1])
									+ ";server_port="
									+ String.valueOf(this.serverport[0]) + "-"
									+ String.valueOf(this.serverport[1])
									+ ";ssrc=123456;mode=play");
					response.setHeader("Session", this.sessionId
							+ ";timeout=60");
					response.setHeader("Content-length", "0");
					response.setHeader("Cache-Control", "no-cacher");
				}
			}

			if (this.state == SessionState.ready) {
				if (request.getMethod().equals(RTSPRequest.play)) {
					if (request.getHeader("Session").equals(this.sessionId)) {
						this.state = SessionState.playing;
						this.rtpmanager.play();
						/*
						 * try { Thread.sleep(600); } catch
						 * (InterruptedException e) { // TODO Auto-generated
						 * catch block e.printStackTrace(); }
						 */
						response.setCode(true);
						response.setHeader("Content-length", "0");
						response.setHeader("Session", this.sessionId
								+ ";timeout=60");
						response.setHeader("RTP-Info",
								"url=" + request.getUri() + ";seq="
										+ RTPServer.SeqIni + ";rtptime="
										+ RTPServer.timestampIni);
						response.setHeader("Range", "npt=0.000-");
						response.setHeader("Cache-Control", "no-cache");
					} else {
						response.setCode(false);
						this.state = SessionState.teardown;
					}

				}
			}

			if (this.state == SessionState.playing) {
				if (request.getMethod().equals(RTSPRequest.pause)) {
					// to do

					if (request.getHeader("Session").equals(this.sessionId)) {
						this.state = SessionState.paused;
						response.setCode(true);
						response.setHeader("Content-length", "0");
						response.setHeader("Session", this.sessionId);
						response.setHeader("Cache-Control", "no-cache");
						this.rtpmanager.pause();
					} else {
						response.setCode(false);
						this.state = SessionState.teardown;
					}
				}
			}

			if (this.state == SessionState.paused) {
				if (request.getMethod().equals(RTSPRequest.play)) {
					// to do

					if (request.getHeader("Session").equals(this.sessionId)) {
						this.state = SessionState.playing;
						response.setCode(true);
						response.setHeader("Content-length", "0");
						response.setHeader("Session", this.sessionId);
						if (request.hasHeader("Range")) {
							response.setHeader("Range",
									request.getHeader("Range"));
						}
						response.setHeader("Cache-Control", "no-cache");
						this.rtpmanager.resume();
					} else {
						response.setCode(false);
						this.state = SessionState.teardown;
					}
				}
			}

			if ((this.state == SessionState.playing)
					|| (this.state == SessionState.paused)) {
				if (request.getMethod().equals(RTSPRequest.teardown)) {
					// to do

					if (request.getHeader("Session").equals(this.sessionId)) {
						this.state = SessionState.teardown;
						response.setCode(true);
						response.setHeader("Content-length", "0");
						response.setHeader("Session", this.sessionId);
						response.setHeader("Cache-Control", "no-cache");
						this.rtpmanager.stop();
					} else {
						response.setCode(false);
						this.state = SessionState.teardown;
					}
				}
			}

			try {
				sendResponse(this.RTSPBufferedWriter, response.getMessage());
				System.out.println("response sent");
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}

		}
	}

	public RTSPRequest parseRequest(BufferedReader reader) throws IOException {
		RTSPRequest request = new RTSPRequest();
		String requestLine = null;
		requestLine = reader.readLine();
		System.out.println("requestline=" + requestLine);
		if (requestLine != null) {
			String[] parts = requestLine.split(" ");
			if (parts.length != 3) {
				return null;
			}
			request.setMethod(parts[0].trim());
			request.setUri(parts[1].trim());
		}
		while (true) {
			// maybe don't work
			String buf = null;
			if ((buf = (reader.readLine())).equals("")) {
				return request;
			}
			System.out.println("header=" + buf);

			if (buf.contains("CSeq:")) {
				String[] parts = buf.split(":");
				this.Cseq = Integer.parseInt(parts[1].trim());
				request.setCseq(this.Cseq);
				request.setHeader("Cseq", String.valueOf(this.Cseq));

			}

			if (buf.contains("Session:")) {
				String[] parts = buf.split(":");
				request.setHeader("Session", parts[1].trim());
			}

			if (buf.contains("Range:")) {
				String[] parts = buf.split(":");
				request.setHeader("Range", parts[1].trim());
			}

			if (buf.contains("Transport:")) {
				String[] parts = buf.split(":");
				String str = parts[1].trim();
				request.setHeader("Transport", str);
				int indexOfClientport = str.indexOf("client_port=");
				String[] portstrings = str.substring(indexOfClientport + 12)
						.split("-");

				this.clientport[0] = Integer.parseInt(portstrings[0]);
				this.clientport[1] = Integer.parseInt(portstrings[1]);
			}
		}
	}

	public static String parseUri(String uri) {
		int index = uri.lastIndexOf('/');
		return uri.substring(index + 1).trim();
	}

	public void sendResponse(BufferedWriter writer, String message)
			throws IOException {
		System.out.println("response message:\n" + message);
		writer.write(message);
		writer.flush();
		System.out.println("local port: " + this.socket.getLocalPort()
				+ "\nremote port: " + this.socket.getPort() + "\nremote ip: "
				+ this.socket.getRemoteSocketAddress());

	}

	public void teardown() {
		this.stack.removeSession(this.sessionId);
		try {
			if (this.RTSPBufferedReader != null) {
				this.RTSPBufferedReader.close();
			}

			if (this.RTSPBufferedWriter != null) {
				this.RTSPBufferedWriter.close();
			}

			if (this.socket != null) {
				this.socket.close();
			}
		} catch (IOException e) {

		}
	}
}
