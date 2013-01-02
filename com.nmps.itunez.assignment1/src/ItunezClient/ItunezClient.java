package ItunezClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import ItunezAPI.InvalidePlayListException;
import ItunezAPI.InvalidePlayListResponse;
import ItunezAPI.ItunezRequest;
import ItunezAPI.ItunezResponse;
import ItunezAPI.PlayList;

public class ItunezClient {

	private String serverIp;
	private int serverPort;
	private Socket socket;
	private BufferedReader inBuff;
    private PrintWriter outPrint;
	
	
	public ItunezClient(String ip, int port){
		this.serverIp = ip;
		this.serverPort = port;
		
	}

	public PlayList requestPlayList() throws InvalidePlayListException, InvalidePlayListResponse{
		
		try {
			this.socket = new Socket(this.serverIp,this.serverPort);
			this.socket.setSoTimeout(5000);
			this.outPrint = new PrintWriter(this.socket.getOutputStream());
			this.inBuff = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			sendRequest(this.outPrint);
			
			
			return getResponse(this.inBuff);
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return null;
	}
	
	private void sendRequest(PrintWriter out){
		out.write(new ItunezRequest().printRequest());
		out.flush();
	}
	
	private PlayList getResponse(BufferedReader in) throws IOException, InvalidePlayListException, InvalidePlayListResponse{
		int len = 0;
		String responseLine = in.readLine();
		char[] cbuf = null;
		
		if(responseLine.equals(ItunezResponse.BAD_REQUEST)){
			System.out.println("Response=Bad request");
		}else if(responseLine.equals(ItunezResponse.INTERNAL_ERROR)){
			System.out.println("Response=Internal error");
		}else if(responseLine.equals(ItunezResponse.OK)){
			String str = in.readLine();
			str = str.substring(16);
			len = Integer.parseInt(str);
			
			in.readLine();
			cbuf = new char[len];
			int n=in.read(cbuf, 0, len);
			//System.out.println("lenth="+len+"  "+"nubmer of char read ="+n+"  "+String.valueOf(cbuf));
			return PlayList.parse(String.valueOf(cbuf));
			
		}else{
			throw new InvalidePlayListResponse();
		}
		return null;
	}	
	
	public final static void main(String[] args){
		ItunezClient client = new ItunezClient(args[0],Integer.parseInt(args[1]));
		try {
			client.requestPlayList();
		} catch (InvalidePlayListException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidePlayListResponse e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
