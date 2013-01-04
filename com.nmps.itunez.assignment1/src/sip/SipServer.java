package sip;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import util.ResourceManager;

public class SipServer implements Runnable {

	public int serverport = 5060;
	private ServerSocket listeningSocket;
	private final ExecutorService pool;
	private Hashtable<String, SipSession> sessions = new Hashtable<String, SipSession>();
	private ResourceManager rmanager;

	public SipSession getSession(String sessionID) {
		return sessions.get(sessionID);
	}

	public SipServer(ResourceManager manager) {
		super();
		this.rmanager = manager;
		try {
			this.listeningSocket = new ServerSocket(this.serverport);
			System.out.println("Sipserver starts listening on port:"
					+ serverport);
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
				// reconsider the timeout here
				serveSocket.setSoTimeout(100000);
				System.out.println("accept request");
			} catch (IOException e) {
				e.printStackTrace();
			}

			pool.execute(new ConnectionHandler(serveSocket));

		}
	}

	class ConnectionHandler implements Runnable {
		private Socket socket;

		ConnectionHandler(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			// newSession.setTheStack() possibly has incorrect argument
			SipSession session = null;

			try {
				session = createSession(this.socket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (session != null) {
				session.startprocess();
			}

		}
	}

	public SipSession createSession(Socket socket) throws IOException {
		// TODO Auto-generated method stub
		SipSession session = new SipSession(socket,this.rmanager);
		session.setSipServer(this);
		sessions.put(session.getSessionId(), session);

		return session;
	}

	public void removeSession(String sessionId) {
		this.sessions.remove(sessionId);
	}
	
	public final static void main(String[] args){
		
		ResourceManager rManager = new ResourceManager(ResourceManager.defaultBasePath);
		new Thread(new SipServer(rManager)).start();
	}
}
