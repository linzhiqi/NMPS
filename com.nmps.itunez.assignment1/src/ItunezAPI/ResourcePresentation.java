package ItunezAPI;

public class ResourcePresentation {
	private String title;
	private String rtspUrl;
	
	public ResourcePresentation(String title, String url){
		this.title = title;
		this.rtspUrl = url;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public String getUrl(){
		return this.rtspUrl;
	}
	
	public String print(){
		return "title="+this.title+"\n"+"url="+this.rtspUrl+"\n";
	}
}
