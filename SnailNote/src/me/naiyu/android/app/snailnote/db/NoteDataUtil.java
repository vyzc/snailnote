package me.naiyu.android.app.snailnote.db;

import java.util.ArrayList;
import java.util.List;

import me.naiyu.android.app.snailnote.model.Note;
import me.naiyu.android.app.snailnote.model.SuperNote;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class NoteDataUtil {

	private NoteSQLiteHelper mHelper;

	private String[] mAllColumns = { "_id", "title", "note", "tag",
			"create_date", "modify_date" };

	public NoteDataUtil(Context context) {
		mHelper = new NoteSQLiteHelper(context);
	}

	public long newNote(String noteTitle, String noteBody, String noteTag) {
		String strTime = String.valueOf(System.currentTimeMillis());
		ContentValues values = new ContentValues();
		values.put(NoteSQLiteHelper.TITLE, noteTitle);
		values.put(NoteSQLiteHelper.NOTE, noteBody);
		values.put(NoteSQLiteHelper.TAG, noteTag);
		values.put(NoteSQLiteHelper.CREATE_DATE, strTime);
		
		SQLiteDatabase db = mHelper.getWritableDatabase();
		long newId = db
				.insert(NoteSQLiteHelper.TABLE_NAME, null, values);
		db.close();
		return newId;
		// 查询数据
	}

	public List<SuperNote> getAllNotes() {
		List<SuperNote> superNotes = new ArrayList<SuperNote>();
		
		SQLiteDatabase db = mHelper.getWritableDatabase();
		Cursor cursor = db.query(NoteSQLiteHelper.TABLE_NAME,
				mAllColumns, null, null, null, null, "create_date DESC");
		while (cursor.moveToNext()) {
			SuperNote superNote = new SuperNote();
			superNote.setNote(cursorToNote(cursor));
			superNote.setSelected(false);
			superNotes.add(superNote);
		}
		cursor.close();
		db.close();
		return superNotes;
	}

	private Note cursorToNote(Cursor cursor) {
		Note note = new Note();
		note.setId(cursor.getLong(0));
		note.setTitle(cursor.getString(1));
		note.setNote(cursor.getString(2));
		note.setTag(cursor.getString(3));
		note.setCreateDate(cursor.getString(4));
		note.setModifyDate(cursor.getString(5));
		return note;
	}

	public void modifyNote(long id, String noteTitle,
			String noteBody, String noteTag) {
		String strTime = String.valueOf(System.currentTimeMillis());
		ContentValues values = new ContentValues();
		values.put(NoteSQLiteHelper.TITLE, noteTitle);
		values.put(NoteSQLiteHelper.NOTE, noteBody);
		values.put(NoteSQLiteHelper.TAG, noteTag);
		values.put(NoteSQLiteHelper.MODIFY_DATE, strTime);
		
		SQLiteDatabase db = mHelper.getWritableDatabase();
		db.update("notes", values, "_id = " + id, null);
		db.close();
	}
	
	public void delNote(long id) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		db.delete("notes", "_id = " + id, null);
		db.close();
	}
	
	public List<SuperNote> searchNote(String key) {
		List<SuperNote> temp = new ArrayList<SuperNote>();
		String str = "select *from notes where title like '%" + key
				+ "%' order by " + "modify_date" + " DESC";
		System.out.println(str);
		SQLiteDatabase db = mHelper.getReadableDatabase();
		Cursor localCursor = db.rawQuery(str, null);
		System.out.println(localCursor.getCount());
		while (localCursor.moveToNext()) {
			SuperNote superNote = new SuperNote();
			superNote.setNote(cursorToNote(localCursor));
			superNote.setSelected(false);
			temp.add(superNote);
		}
		localCursor.close();
		db.close();
		return temp;
	}

}
