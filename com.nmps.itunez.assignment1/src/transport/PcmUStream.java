package transport;

import java.io.FileInputStream;

public class PcmUStream implements AudioStream {
	FileInputStream fis; // audio file
	int chunkSize;
	
	public PcmUStream(FileInputStream fis, int chunkSize){
		this.fis = fis;
		this.chunkSize = chunkSize;
	}
	@Override
	public int getnextframe(byte[] frame) throws Exception {
		// TODO Auto-generated method stub
		return fis.read(frame,0,this.chunkSize);
	}


}
