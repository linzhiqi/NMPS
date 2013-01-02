package ItunezClient;

import java.io.IOException;

import rtsp.RTSPStack;

public class VlcExecutor {

	public final static void main(String[] args){
		Process p1 = null;
		Process p2 = null;
		try {
            p1 = Runtime.getRuntime().exec(new String[]{"C://Program Files (x86)//VideoLAN//VLC//vlc.exe",
            		"--play-and-exit","rtsp://192.168.1.102:"+RTSPStack.rtspport+"/blind.wav"});
        } catch (IOException ioe) {
            System.err.println("IO exception: " + ioe.getMessage());
        }
		
		
		try {
			p1.waitFor();
			System.out.println("process terminated");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
