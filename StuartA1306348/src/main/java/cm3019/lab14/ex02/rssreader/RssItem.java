package cm3019.lab14.ex02.rssreader;

import java.util.Date;

public class RssItem {
	// item title
	private String title;
	// item link
	private String link;
	// item date
	private String date;
	// item description
	private String desc;
	//item id
	private long id;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getDate(){
		return date;
	}
	public void setDate(String newDate){
		date = newDate;
	}
	public String getDesc(){
		return desc;
	}
	public void setDesc(String newDesc){
		desc = newDesc;
	}
	@Override
	public String toString() {
		return title;
	}
	public long getId(){
		return this.id;
	}
	public void setId(long newId){
		this.id = newId;
	}
}
