package me.naiyu.android.app.snailnote.activities;

import me.naiyu.android.app.snailnote.R;
import me.naiyu.android.app.snailnote.db.NoteDataUtil;
import me.naiyu.android.app.snailnote.model.Note;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class DetailActivity extends ActionBarActivity {

	public final static String FROM_TYPE_TAG = "from_type_tag";
	public final static String FROM_TYPE_NEW = "new_note";
	public final static String FROM_TYPE_DETAIL = "detail_note";

	private String mTitle;
	private EditText mEtTitle;
	private EditText mEtNote;

	private boolean isNew;
	private long mNoteId;

	private NoteDataUtil mDbUtil;

	TextWatcher mTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			saveNote();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		mDbUtil = new NoteDataUtil(this);
		initViews();
		initTitle();
		initActionBar();
		setListener();
		
		mEtTitle.setFocusable(true);
		mEtTitle.setFocusableInTouchMode(true);
		mEtTitle.requestFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(mEtTitle, InputMethodManager.RESULT_SHOWN);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
				InputMethodManager.HIDE_IMPLICIT_ONLY);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initTitle() {
		Intent it = getIntent();
		String tempTag = it.getStringExtra(FROM_TYPE_TAG);
		if (tempTag.equals(FROM_TYPE_NEW)) {
			isNew = true;
			mTitle = getResources().getString(R.string.desc_new_note);
		} else {
			isNew = false;
			Note tempNote = (Note) it.getSerializableExtra("note");
			mTitle = getResources().getString(R.string.desc_modify_note);
			mNoteId = tempNote.getId();
			mEtTitle.setText(tempNote.getTitle());
			mEtNote.setText(tempNote.getNote());
			mEtTitle.setSelection(tempNote.getTitle().length());
		}

	}

	private void initViews() {
		mEtTitle = (EditText) findViewById(R.id.et_note_title_detail);
		mEtNote = (EditText) findViewById(R.id.et_note_body_detail);
	}

	private void setListener() {
		mEtTitle.addTextChangedListener(mTextWatcher);
		mEtNote.addTextChangedListener(mTextWatcher);
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		Drawable iconDrawable = getResources().getDrawable(R.drawable.abc_icon);
		actionBar.setIcon(iconDrawable);
		actionBar.setTitle(mTitle);
	}

	private void saveNote() {
		String noteTitle = mEtTitle.getText().toString().trim();
		String noteBody = mEtNote.getText().toString().trim();
		if (noteTitle.equals("")) {
			noteTitle = getResources().getString(R.string.no_title);
		}
		if (isNew) {
			mNoteId = mDbUtil.newNote(noteTitle, noteBody, "note");
			isNew = false;
		} else {
			mDbUtil.modifyNote(mNoteId, noteTitle, noteBody, "note");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.home:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
