package com.github.williamli0707.mod;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlayerLog {
	List<ServerPlayer> players;
    Date date;

    public PlayerLog(List<ServerPlayer> players) {
        this.players = players;
        date = new Date();
    }

    public Document toDocument() {
        Document document = new Document();
        ArrayList<String> playerNames = new ArrayList<String>();
        for (ServerPlayer player : players) {
            playerNames.add(player.getGameProfile().getName());
        }
        document.append("date", date.getTime());
        document.append("players", playerNames);
        document.append("_id", 1);
        return document;
    }
}
