package me.naiyu.android.app.snailnote.model;

import java.io.Serializable;

public class SuperNote implements Serializable {

	private static final long serialVersionUID = 8504628622681142488L;
	
	private Note note;
	
	private boolean isSelected;

	public Note getNote() {
		return note;
	}

	public void setNote(Note note) {
		this.note = note;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
