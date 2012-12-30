package transport;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.Timer;

public class PcmURtpSender implements ActionListener {
	public static int PCMU_TYPE = 0;
	public static int ADPCM_32_TYPE = 5;
	public static int PCMA_TYPE = 8;
	String fileUrl;
	FileInputStream fis;
	int fileSize;
	DatagramSocket RTPsocket; // socket to be used to send and receive UDP
								// packets
	DatagramPacket senddp; // UDP packet containing the video frames
	InetAddress clientIpAddr; // Client IP address
	int RTP_dest_port; // destination port for RTP packets (given by the RTSP
						// Client)
	int chunkSize;
	int chunkNum;
	int chunkPeriod;
	int chunkSeq;
	PcmUStream audio;
	Timer timer;
	byte[] buf;

	public PcmURtpSender(FileInputStream fileInputStream, int fileSize,
			InetAddress clientIpAddr,
			int destPort, int chunkSize, int chunkPeriod) throws SocketException {
		this.fis = fileInputStream;
		this.fileSize = fileSize;
		this.chunkSize = chunkSize;
		this.chunkPeriod = chunkPeriod;
		this.chunkNum = fileSize / chunkSize;
		this.RTPsocket = new DatagramSocket();
		this.clientIpAddr = clientIpAddr;
		this.RTP_dest_port = destPort;
		this.audio = new PcmUStream(fileInputStream, chunkSize);

		timer = new Timer(chunkPeriod, this);
		timer.setInitialDelay(0);
		timer.setCoalesce(false);
		buf = new byte[chunkSize];
	}

	public void start() {
		System.out.println("started");
		
		this.timer.start();
		System.out.println("end");
	}

	public void stop() {
		timer.stop();
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (chunkSeq < chunkNum) {
			chunkSeq++;
			try {
				int length = audio.getnextframe(buf);
				RTPpacket rtp_packet = new RTPpacket(PCMA_TYPE, chunkSeq,
						chunkSize*chunkSeq, buf, length);
				int packet_length = rtp_packet.getlength();
				byte[] packet_bits = new byte[packet_length];
				rtp_packet.getpacket(packet_bits);
				senddp = new DatagramPacket(packet_bits, packet_length,
						clientIpAddr, RTP_dest_port);
				RTPsocket.send(senddp);
				rtp_packet.printheader();
				System.out.println("sent chunk #" + chunkSeq+"total num =" +chunkNum);
			} catch (Exception ex) {
				System.out.println("Exception caught: " + ex);
				System.exit(0);
			}
		} else {
			// if we have reached the end of the video file, stop the timer
			timer.stop();
			
		}
	}
	
	public static void main(String[] args) throws IOException{
		String fileUrl="C:\\Users\\Vita\\Documents\\study\\3152\\assignment\\blind.wav";
		File file = new File(fileUrl);
		long fileSize = file.length();
		System.out.println("file size: "+fileSize);
		FileInputStream fis= new FileInputStream(file);
		InetAddress client = InetAddress.getByName("127.0.0.1");
		
		PcmURtpSender sender = new PcmURtpSender(fis,(int)fileSize,client,6999,3200,400);
		sender.start();
		
	}

}
