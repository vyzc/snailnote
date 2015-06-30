package me.naiyu.android.app.snailnote.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteSQLiteHelper extends SQLiteOpenHelper {
	
	private final static String DB_NAME = "snailnote.db";
	private final static int DB_vERSION = 1;
	
	public final static String _ID = "_id";
	public final static String TITLE = "title";
	public final static String NOTE = "note";
	public final static String TAG = "tag";
	public final static String CREATE_DATE = "create_date";
	public final static String MODIFY_DATE = "modify_date";
	public final static String TABLE_NAME = "notes";

	public NoteSQLiteHelper(Context context) {
		super(context, DB_NAME, null, DB_vERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table notes(_id integer primary key autoincrement, " +
				"title text not null," +
				"note text, " +
				"tag text not null," +
				"create_date text not null, " +
				"modify_date text);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
