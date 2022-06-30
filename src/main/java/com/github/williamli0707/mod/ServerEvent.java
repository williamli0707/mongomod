package com.github.williamli0707.mod;

import org.bson.Document;

import java.util.Date;

public class ServerEvent {
    public String type;
	public Date date;
	public ServerEvent(String type){
		this.type = type;
		this.date = new Date();
	}

	public String getType(){
		return type;
	}

	public void setType(String type){
		this.type = type;
	}

	public Document toDocument(){
		Document document = new Document();
		document.append("type", type);
		document.append("date", date.getTime());
		return document;
	}

	public static ServerEvent fromDocument(Document document){
		return new ServerEvent(document.getString("type"));
	}
}
