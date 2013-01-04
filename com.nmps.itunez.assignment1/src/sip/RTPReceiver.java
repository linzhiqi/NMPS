package sip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import util.ResourceManager;

public class RTPReceiver implements Runnable{
	private SipSession session;
	private ResourceManager rmanager;
	private int localport;
	private DatagramSocket socket;
	
	public RTPReceiver(SipSession session, ResourceManager manager) throws SocketException{
		this.session = session;
		this.rmanager = manager;
		this.socket = new DatagramSocket();
		this.socket.setSoTimeout(20000);
		this.localport = this.socket.getLocalPort();
	}
	
	
	public int getLocalPort(){
		return this.localport;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		byte[] buf = new byte[1000];
		DatagramPacket p = new DatagramPacket(buf, 1000);
		File newFile = null;
		if(rmanager==null){
			System.out.println("mananger is null");
		}
		
		if(this.session.getAccount()==null){
			System.out.println("account is null");
		}
		try {
			newFile = this.rmanager.createFile(this.session.getAccount()+"_"+System.currentTimeMillis()+".wav");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(newFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while(true){
			byte[] data = null;
			try {
				this.socket.receive(p);
				data = renderRtpPacket(p.getData(),p.getLength());
				out.write(data, 0, data.length);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					socket.close();
					out.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return;
			}
		}
	}


	private byte[] renderRtpPacket(byte[] data, int length) {
		// TODO Auto-generated method stub
		byte[] buf = new byte[length-12];
		for(int i=0; i<length-12; i++){
			buf[i] = data[i+12];
		}
		return buf;
	}
	
	

}
