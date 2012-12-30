package rtp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Timer;

import util.ResourceManager;

public class RTPServer {
	public static int timestampIni = 225;
	public static int SeqIni = 1;
	
	int timestamp = timestampIni;
	int Seq = SeqIni;
	
	private InetAddress clientIp;
	private int[] serverport = new int[2];
	private int[] clientport = new int[2];
	private DatagramSocket[] serverSocket = new DatagramSocket[2];
	private String resourceName;
	private ResourceManager reManager;
	private Timer timer;
	private FileInputStream iStream;
	private SendRTPTask rtpTask;
	
	public RTPServer(ResourceManager remanager) throws SocketException{
		this.reManager = remanager;
		this.serverSocket[0] = new DatagramSocket();
		this.serverSocket[1] = new DatagramSocket();
		this.serverport[0] = this.serverSocket[0].getLocalPort();
		this.serverport[1] = this.serverSocket[1].getLocalPort();
	}
	
	public void setClientIp(InetAddress ip){
		this.clientIp = ip;
	}
	
	public InetAddress getClientIp(){
		return this.clientIp;
	}
	
	public void setclientport(int[] ports){
		this.clientport[0]=ports[0];
		this.clientport[1]=ports[1];
	}
	
	public int[] getServerPort(){
		return this.serverport;
	}
	
	public void setResource(String resource){
		this.resourceName = resource;
	}
	
	
	public void play(){
		//start rtp sending timer
		
		if(reManager.hasResource(this.resourceName)){
			try {
				this.iStream = reManager.getInStream(this.resourceName);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			return;
		}
		System.out.println("have resource: " + this.resourceName);
		this.rtpTask = new SendRTPTask(this,this.iStream,this.reManager.getResourceSize(this.resourceName),this.serverSocket[0],this.clientIp,this.clientport[0]);
		
		this.timer = new Timer();
		timer.schedule(rtpTask, 0, 40);
		System.out.println("play returning");
	}
	
	public void pause(){
		//pause rtp sending timer
		this.timer.cancel();
	}
	
	public void resume(){
		//resume rtp sending timer
		this.timer = new Timer();
		this.rtpTask = new SendRTPTask(this,this.iStream,this.reManager.getResourceSize(this.resourceName),this.serverSocket[0],this.clientIp,this.clientport[0]);
		this.timer.schedule(rtpTask, 0, 100);
	}
	
	public void stop(){
		//stop rtp sending
		this.timer.cancel();
		this.timer.purge();
	}
	
}
