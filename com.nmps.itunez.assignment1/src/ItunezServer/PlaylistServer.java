package ItunezServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ItunezAPI.ItunezRequest;
import ItunezAPI.ItunezResponse;
import ItunezAPI.PlayList;
import RTSP.RTSPSession;
import RTSP.RTSPStack;

import util.ResourceManager;

public class PlaylistServer implements Runnable {

	private ResourceManager rManager;
	private int port;
	private ServerSocket listeningSocket;
	private final ExecutorService pool;
	private String localIp;

	PlaylistServer(int port, ResourceManager manager) {
		super();
		this.rManager = manager;
		this.port = port;
		try {
			this.listeningSocket = new ServerSocket(this.port);
		} catch (IOException e) {
			e.printStackTrace();
		}

		pool = Executors.newFixedThreadPool(5);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		while (true) {
			Socket serveSocket = null;
			try {
				serveSocket = this.listeningSocket.accept();
				// reconsider the timeout here
				serveSocket.setSoTimeout(100000);
			} catch (IOException e) {
				e.printStackTrace();
			}

			pool.execute(new ConnectionHandler(serveSocket, this));

		}

	}

	class ConnectionHandler implements Runnable {
		private Socket socket;
		private PlaylistServer server;

		ConnectionHandler(Socket socket, PlaylistServer server) {
			this.socket = socket;
			this.server = server;
			this.server.localIp = this.socket.getLocalAddress()
					.getHostAddress();
		}

		public void run() {
			// newSession.setTheStack() possibly has incorrect argument
			ItunezResponse response = new ItunezResponse();
			boolean flag = false;
			boolean ioexception = false;
			try {
				flag = renderRequest();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				ioexception = true;
				e.printStackTrace();

			}

			if (flag) {
				PlayList list = this.server.generateList();
				response.setResponseLine(ItunezResponse.OK);
				response.setContent(list.serialize());

			} else {
				if (ioexception) {
					response.setResponseLine(ItunezResponse.INTERNAL_ERROR);
				} else {
					response.setResponseLine(ItunezResponse.BAD_REQUEST);
				}
			}
			try {
				sendResponse(response.printResponse());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public boolean renderRequest() throws IOException {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					this.socket.getInputStream()));

			String line = reader.readLine();
			return line.equals(ItunezRequest.RequestLine);
		}

		public void sendResponse(String message) throws IOException {
			OutputStreamWriter writer = new OutputStreamWriter(
					this.socket.getOutputStream());
			
			writer.write(message);
		}
	}

	public PlayList generateList() {
		ArrayList<String> files = this.rManager.getFiles();
		PlayList list = new PlayList();
		Iterator<String> it = files.iterator();
		while (it.hasNext()) {
			String filename = it.next();
			list.addResource(filename, "rtsp://" + this.localIp + ":"
					+ RTSPStack.rtspport + "/" + filename);
		}
		return list;
	}
}