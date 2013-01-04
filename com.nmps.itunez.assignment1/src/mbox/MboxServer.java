package mbox;

import rtsp.RTSPStack;
import sip.SipServer;
import util.ResourceManager;
import ItunezServer.PlaylistServer;

public class MboxServer {

public final static void main(String[] args){
		
		ResourceManager rManager = new ResourceManager(ResourceManager.defaultBasePath);
		
		if((args.length>2)||(args.length<1)){
			System.out.println("Usage: MboxServer port_for_playlist <BASE_PATH>");
			return;
		}else if(args.length==2){
			rManager = new ResourceManager(args[1]);
		}
				
		if(!rManager.baseExists()){
			System.out.println("the base path does not exist");
			return;
		}
		new Thread(new SipServer(rManager)).start();
		
		PlaylistServer playlistserver = new PlaylistServer(Integer.parseInt(args[0]), rManager);
		RTSPStack rtspserver = new RTSPStack(RTSPStack.rtspport);
		rtspserver.setResourceManager(rManager);
		new Thread(playlistserver).start();
		new Thread(rtspserver).start();
	}
}
