package com.github.williamli0707.mod;

import net.minecraftforge.event.world.NoteBlockEvent;
import org.bson.Document;

import java.util.Date;

public class PlayerEvent {
	public String type, playerName;
	Date date = new Date();
	public PlayerEvent(String type, String playerName) {
		this.type = type;
		this.playerName = playerName;
	}

	public String getType(){
		return type;
	}

	public void setType(String type){
		this.type = type;
	}

	public Document toDocument(){
		Document doc = new Document();
		doc.append("type", type);
		doc.append("playername", playerName);
		doc.append("date", date.getTime());
		return doc;
	}
}
