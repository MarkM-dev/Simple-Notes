package com.markm.simplenotes;

public class Note {
	private int listPos;
	private int id;
	private String subject;
	private String contents;
	
	
	
	
	@Override
	public String toString () {
		return listPos + ". " + subject;
	}
	
	public Note() {
		super();
	}
	
	public Note(int id, String subject, String contents) {
		this.id = id;
		this.subject = subject;
		this.contents = contents;
	}

	public Note(String subject, String contents) {
		super();
		this.subject = subject;
		this.contents = contents;
	}
	
	
	public int getListPos() {
		return listPos;
	}

	public void setListPos(int listPos) {
		this.listPos = listPos;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
	
}
