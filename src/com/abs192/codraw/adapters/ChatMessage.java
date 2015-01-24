package com.abs192.codraw.adapters;

public class ChatMessage {

	private String message, uname;
	ChatType type;

	public enum ChatType {
		RECD, SENT, ERROR
	};

	public ChatMessage(String message, String uname, ChatType type) {
		this.message = message;
		this.uname = uname;
		this.type = type;
	}

	public ChatType getType() {
		return type;
	}

	public void setType(ChatType type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

}
