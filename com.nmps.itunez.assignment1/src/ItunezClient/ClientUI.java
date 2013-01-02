package ItunezClient;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import ItunezAPI.InvalidePlayListException;
import ItunezAPI.InvalidePlayListResponse;
import ItunezAPI.PlayList;
import ItunezAPI.ResourcePresentation;

import org.apache.commons.cli.*;

import rtsp.RTSPStack;

public class ClientUI {

	public static enum UiState {
		ini, playing
	}
	private String vlcPath = "C://Program Files (x86)//VideoLAN//VLC//vlc.exe";
	private PlayList playlist;
	private ResourcePresentation currentResource;
	private int currentId;
	private UiState state = UiState.ini;


	public void setPlayList(PlayList list) {
		this.playlist = list;
		this.currentResource = this.playlist.getResourceList().get(0);
		this.currentId = 0;
	}

	public void printPlaylist() {
		if (this.playlist != null) {
			ArrayList<ResourcePresentation> list = this.playlist
					.getResourceList();
			Iterator<ResourcePresentation> it = list.iterator();
			int n = 0;
			while (it.hasNext()) {
				System.out.println(n + ". " + it.next().getTitle());
				n++;
			}
		}
	}

	public final static void main(String[] args) throws IOException {

		ClientUI ui = new ClientUI();
		
		Scanner sc = new Scanner(System.in);
		

		ItunezClient client = new ItunezClient(args[0],
				Integer.parseInt(args[1]));
		try {
			ui.setPlayList(client.requestPlayList());
		} catch (InvalidePlayListException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidePlayListResponse e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (true) {
			ui.printPlaylist();
			printHelp();
			ui.printCurrentResource();
			System.out.print(">");
			String input = sc.nextLine();
			if(input==null){
				System.out.println("null input");
			}
			if (input.equals("next")) {
				if (ui.state == UiState.ini) {
					ui.playNext();
					ui.state = UiState.playing;
				}else{
					System.out.println("invalide command");
				}

			} else if (input.equals("previous")) {
				if (ui.state == UiState.ini) {
					ui.playPrevious();
					ui.state = UiState.playing;
				}else{
					System.out.println("invalide command");
				}

			} else if (input.equals("id")) {
				if (ui.state == UiState.ini) {
					System.out.print("Type the id of resource to play\n>");
					input = sc.nextLine();
					if(input==null){
						System.out.println("null input");
					}
					System.out.println(input);
					int id = Integer.parseInt(input);
					ui.playResource(id);
					ui.state = UiState.playing;
				}else{
					System.out.println("invalide command");
				}
			} else if (input.equals("teardown")) {
				if (ui.state == UiState.playing) {
					ui.tearDown();
					ui.state = UiState.ini;
				}else{
					System.out.println("invalide command");
				}

			} else if (input.equals("newlist")) {
				if (ui.state == UiState.ini) {

					client = new ItunezClient(args[0],
							Integer.parseInt(args[1]));
					try {
						ui.setPlayList(client.requestPlayList());
					} catch (InvalidePlayListException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvalidePlayListResponse e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					System.out.println("invalide command");
				}

			}
		}

	}
	
	private void playPrevious() {
		// TODO Auto-generated method stub
		
	}

	private void tearDown() {
		// TODO Auto-generated method stub
		
	}

	public void playResource(int id){
		if((id<0)||(id>this.playlist.getResourceList().size())){
			this.state = UiState.ini;
			return;
		}
		
		this.currentId = id;
		this.currentResource = this.playlist.getResourceList().get(id);
		
		try {
            Runtime.getRuntime().exec(new String[]{this.vlcPath,"--play-and-exit",this.currentResource.getUrl()});
        } catch (IOException ioe) {
            System.err.println("IO exception: " + ioe.getMessage());
        }
		
	}
	

	
	public void playNext(){
		if((this.currentId+1<0)||(this.currentId+1>this.playlist.getResourceList().size())){
			this.state = UiState.ini;
			return;
		}
		
		this.currentId++;
		this.currentResource = this.playlist.getResourceList().get(this.currentId);
		
	}
	
	private void play(){
		try {
            Runtime.getRuntime().exec(new String[]{"C://Program Files (x86)//VideoLAN//VLC//vlc.exe",
            		"--play-and-exit",this.currentResource.getUrl()});
        } catch (IOException ioe) {
            System.err.println("IO exception: " + ioe.getMessage());
        }
	}

	public void printCurrentResource() {
		System.out.println("Current playing: "
				+ this.currentResource.getTitle());
	}

	public static void printHelp() {
		System.out
				.println("+--------------------------------------------------+");
		System.out
				.println("| Commands     | Description                       |");
		System.out
				.println("+--------------------------------------------------+");
		System.out
				.println("| next         | play next resource                |");
		System.out
				.println("| previouse    | play previous resource            |");
		System.out
				.println("| id           | play resource by id               |");
		System.out
				.println("| teardown     | teardown current stream           |");
		System.out
				.println("| newlist      | request new playlist from server  |");
		System.out
				.println("+--------------------------------------------------+");
	}
}
