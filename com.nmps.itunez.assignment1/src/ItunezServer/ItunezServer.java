package ItunezServer;

import rtsp.RTSPStack;
import util.ResourceManager;

public class ItunezServer {
	

	public final static void main(String[] args){
		
		ResourceManager rManager = new ResourceManager(ResourceManager.defaultBasePath);
		
		if((args.length>2)||(args.length<1)){
			System.out.println("Usage: ItunezServer port_for_playlist <BASE_PATH>");
			return;
		}else if(args.length==2){
			rManager = new ResourceManager(args[1]);
		}
				
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
