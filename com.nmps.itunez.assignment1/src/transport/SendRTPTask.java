package transport;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.TimerTask;

public class SendRTPTask extends TimerTask {

	private FileInputStream istream;
	private long size;
	private int chunksize = 800;
	private DatagramSocket RTPsocket;
	private byte[] buf = new byte[this.chunksize];
	private DatagramPacket senddp;
	private InetAddress desIp;
	private int desPort;
	private transport trans;
	
	public static int PCMA_TYPE = 8;
	public static int Dynamic_RTP = 96;
	
	public SendRTPTask(transport t, FileInputStream stream, long size, DatagramSocket socket, InetAddress ip, int port){
		this.trans = t;
		this.istream = stream;
		this.size = size;
		this.RTPsocket = socket;
		this.desIp = ip;
		this.desPort = port;
	}
	
	private int getNextFrame(byte[] frame, int size) throws IOException {
		// TODO Auto-generated method stub
		return this.istream.read(frame,0,size);
	}
	
	private void sendPacket(RTPpacket packet) throws IOException{
		int packet_length = packet.getlength();
		byte[] packet_bits = new byte[packet_length];
		packet.getpacket(packet_bits);
		senddp = new DatagramPacket(packet_bits, packet_length,
				this.desIp, this.desPort);
		RTPsocket.send(senddp);
		//packet.printheader();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int n=0;
		try {
			n=getNextFrame(this.buf,this.chunksize);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(n==0){
			return;
		}
		RTPpacket rtp_packet = new RTPpacket(PCMA_TYPE, trans.Seq,
				trans.timestamp, this.buf, n);
		trans.timestamp+=(n);
		System.out.println("timestamp ="+trans.timestamp);
		trans.Seq++;
		try {
			sendPacket(rtp_packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
