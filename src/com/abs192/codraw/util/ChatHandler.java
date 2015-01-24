package com.abs192.codraw.util;

import java.util.ArrayList;

import com.abs192.codraw.CoDrawApplication;
import com.abs192.codraw.MainActivity;
import com.abs192.codraw.adapters.ChatMessage;
import com.abs192.codraw.adapters.ChatMessage.ChatType;

public class ChatHandler {

	private MainActivity act;

	public ChatHandler(MainActivity act) {
		this.act = act;

		CoDrawApplication.chatList = new ArrayList<ChatMessage>();
		if (CoDrawApplication.isOffline()) {
			CoDrawApplication.chatList.add(new ChatMessage("", "",
					ChatType.ERROR));
		}
	}

	public void newMessage(ChatMessage msg) {

	}

}
