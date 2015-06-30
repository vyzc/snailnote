package me.naiyu.android.app.snailnote.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.naiyu.android.app.snailnote.R;
import me.naiyu.android.app.snailnote.adapter.NoteAdapter;
import me.naiyu.android.app.snailnote.db.NoteDataUtil;
import me.naiyu.android.app.snailnote.model.SuperNote;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements
		OnItemClickListener, OnItemLongClickListener {

	private ActionMode mSelectedActionMode;
	private boolean mIsSelectedModeShow = false;

	private long mFirstBackDownTime = 0;
	private static final long DOUBLE_BACK_DOWN_SPACING = 1500;

	private ListView mListView;
	private NoteAdapter mAdapter;
	private NoteDataUtil mDataUtil;

	private Map<Integer, Long> mSelected;

	private static final int MSG_TYPE_ALL = 1;
	private static final int MSG_TYPE_SEARCH = 2;
	private static final int MSG_TYPE_CLEAR = 3;

	private static boolean mIsSearching = false;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_TYPE_ALL:
				List<SuperNote> tempNote = (List<SuperNote>) msg.obj;
				SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
				boolean isFirst = preferences.getBoolean("isfirst", true);
				if (tempNote.size() == 0 && isFirst) {
					preferences.edit().putBoolean("isfirst", false).commit();
					mDataUtil.newNote(getStringById(R.string.welcome),
							getStringById(R.string.welcome_msg), "note");
					showData();
				} else {
					mAdapter.reflesh(tempNote);
				}
				break;
			case MSG_TYPE_SEARCH:
				List<SuperNote> searchResult = (List<SuperNote>) msg.obj;
				System.out.println("rs size : " + searchResult.size());
				mAdapter.reflesh(searchResult);
				break;
			case MSG_TYPE_CLEAR:
				mAdapter.clearData();
				break;
			default:
				break;
			}
		};
	};

	private String getStringById(int resId) {
		return getResources().getString(resId);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initActionBar();
		initVariable();
		initViews();
		setListener();
		setAdapter();
		showData();
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		Drawable iconDrawable = getResources().getDrawable(R.drawable.abc_icon);
		actionBar.setIcon(iconDrawable);
	}

	private void showData() {
		new Thread(new GetNotesRunnable()).start();
	}

	private void initVariable() {
		mDataUtil = new NoteDataUtil(this);
		List<SuperNote> datas = new ArrayList<SuperNote>();
		mAdapter = new NoteAdapter(this, datas);
		mSelected = new HashMap<Integer, Long>();
	}

	private void initViews() {
		mListView = (ListView) findViewById(R.id.lv_notes_main);
	}

	private void setAdapter() {
		mListView.setAdapter(mAdapter);
	}

	private void setListener() {
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
	}

	class GetNotesRunnable implements Runnable {

		@Override
		public void run() {
			List<SuperNote> temp = mDataUtil.getAllNotes();
			Message msg = new Message();
			msg.what = MSG_TYPE_ALL;
			msg.obj = temp;
			mHandler.sendMessage(msg);
		}

	}

	class QueryNote implements Runnable {

		private String key;

		public QueryNote(String paramKey) {
			key = paramKey;
		}

		@Override
		public void run() {
			if (!key.equals("")) {
				List<SuperNote> temp = mDataUtil.searchNote(key);
				Message msg = mHandler.obtainMessage(MSG_TYPE_SEARCH, temp);
				mHandler.sendMessage(msg);
			} else {
				Message msg1 = mHandler.obtainMessage(MSG_TYPE_CLEAR);
				mHandler.sendMessage(msg1);
			}

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_new_note:
			Intent intent = new Intent(this, DetailActivity.class);
			intent.putExtra(DetailActivity.FROM_TYPE_TAG,
					DetailActivity.FROM_TYPE_NEW);
			startActivityForResult(intent, 101);
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long currentTime = System.currentTimeMillis();
			if (currentTime - mFirstBackDownTime > DOUBLE_BACK_DOWN_SPACING) {
				mFirstBackDownTime = currentTime;
				Toast.makeText(this, R.string.desc_back, Toast.LENGTH_SHORT)
						.show();
			} else {
				finish();
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		if (mIsSelectedModeShow) {
			// 选择
		} else {
			mSelectedActionMode = startSupportActionMode(new SelActionModeCallBack());
			mSelected.put(position, mAdapter.getItem(position).getNote()
					.getId());
			mSelectedActionMode.setTitle(mSelected.size() + " 个被选中");
			mAdapter.setSelected(position, true);
		}
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		SuperNote superNote = mAdapter.getItem(position);
		if (!mIsSelectedModeShow) {
			Bundle bundle = new Bundle();
			bundle.putSerializable("note", superNote.getNote());
			Intent it = new Intent(this, DetailActivity.class);
			it.putExtras(bundle);
			it.putExtra(DetailActivity.FROM_TYPE_TAG,
					DetailActivity.FROM_TYPE_DETAIL);
			startActivityForResult(it, 100);
		} else {
			if (superNote.isSelected()) {
				mAdapter.setSelected(position, false);
				mSelected.remove(position);
			} else {
				mAdapter.setSelected(position, true);
				mSelected.put(position, mAdapter.getItem(position).getNote()
						.getId());
			}
			if (mSelected.size() == 0) {
				mSelectedActionMode.finish();
			}
			mSelectedActionMode.setTitle(mSelected.size() + " 个被选中");
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!mIsSearching) {
			showData();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	class SelActionModeCallBack implements Callback {

		@Override
		public boolean onActionItemClicked(ActionMode actionMode,
				MenuItem menuItem) {
			if (menuItem.getItemId() == R.id.action_del_note) {
				Iterator iterator = mSelected.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<Integer, Long> entry = (Map.Entry<Integer, Long>) iterator
							.next();
					long id = (long) entry.getValue();
					mDataUtil.delNote(id);
				}
				mSelectedActionMode.finish();
				mSelected.clear();
				showData();
			}
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
			actionMode.getMenuInflater().inflate(R.menu.selactionmode, menu);
			mIsSelectedModeShow = true;
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode actionMode) {
			mIsSelectedModeShow = false;
			clearSelected();
		}

		@Override
		public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
			return false;
		}

	}

	private void clearSelected() {
		Iterator iterator = mSelected.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Integer, Integer> entry = (Map.Entry<Integer, Integer>) iterator
					.next();
			int position = (Integer) entry.getKey();
			// 数据多的话，不建议使用这个方法
			mAdapter.setSelected(position, false);
		}
		mSelected.clear();
	}

	@Override
	public void setTitle(CharSequence title) {
		getSupportActionBar().setTitle(title);
	}

}
