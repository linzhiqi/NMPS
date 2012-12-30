package RTSP;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import util.ResourceManager;

public class RTSPStack implements Runnable {

	public static int rtspport = 8554;
	private int listeningPort;
	private ServerSocket listeningSocket;
	Socket RTSPSocket;
	private final ExecutorService pool;
	private Hashtable<String, RTSPSession> sessions = new Hashtable<String, RTSPSession>();
	private ResourceManager resManager;

	public RTSPSession getSession(String sessionID) {
		return sessions.get(sessionID);
	}
	
	public void setResourceManager(ResourceManager manager){
		this.resManager = manager;
	}
	
	public ResourceManager getResourceManager(){
		return this.resManager;
	}

	public RTSPStack(int listeningPort) {
		super();
		this.listeningPort = listeningPort;
		try {
			this.listeningSocket = new ServerSocket(this.listeningPort);
		} catch (IOException e) {
			e.printStackTrace();
		}
		pool = Executors.newFixedThreadPool(5);
	}

	public void run() {
		while (true) {
			Socket serveSocket = null;
			try {
				serveSocket = this.listeningSocket.accept();
				//reconsider the timeout here
				serveSocket.setSoTimeout(100000);
			} catch (IOException e) {
				e.printStackTrace();
			}

			pool.execute( new ConnectionHandler(serveSocket));

		}
	}
	
	public void removeSession(String sessionId){
		this.sessions.remove(sessionId);
	}

	public RTSPSession createSession(Socket socket) {

		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString();

		RTSPSession newSession=null;
		try {
			newSession = new RTSPSession(randomUUIDString, socket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// associate the session with the stack
		newSession.setTheStack(this);

		// save the session in stack;
		sessions.put(newSession.getSessionID(), newSession);

		return newSession;
	}

	class ConnectionHandler implements Runnable {
		private Socket socket;
		ConnectionHandler(Socket socket){
			this.socket = socket;
		}
		
		public void run(){
			//newSession.setTheStack() possibly has incorrect argument
			RTSPSession session;
			
				session = createSession(this.socket);
			
			
				session.startprocess();
			
		}
	}
	
	public static final void main(String[] args){
		RTSPStack server = new RTSPStack(RTSPStack.rtspport);
		server.setResourceManager(new ResourceManager("C:\\Users\\Vita\\Documents\\study\\3152\\assignment"));
		Thread t1 = new Thread(server);
		t1.start();
	}

}
