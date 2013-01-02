package ItunezServer;

import rtsp.RTSPStack;
import util.ResourceManager;

public class ItunezServer {
	public static String basePath = "C:\\Users\\Vita\\Documents\\study\\3152\\assignment";
	
	public static void setBase(String path){
		basePath = path;
	}

	public final static void main(String[] args){
		if((args.length>2)||(args.length<1)){
			System.out.println("Usage: ItunezServer port_for_playlist <BASE_PATH>");
			return;
		}else if(args.length==2){
			ItunezServer.setBase(args[1]);
		}
		
		ResourceManager rManager = new ResourceManager(basePath);
		
		if(!rManager.baseExists()){
			System.out.println("the base path does not exist");
			return;
		}
		PlaylistServer playlistserver = new PlaylistServer(Integer.parseInt(args[0]), rManager);
		RTSPStack rtspserver = new RTSPStack(RTSPStack.rtspport);
		rtspserver.setResourceManager(rManager);
		new Thread(playlistserver).start();
		new Thread(rtspserver).start();
	}
}
