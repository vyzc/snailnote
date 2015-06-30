package me.naiyu.android.app.snailnote.adapter;

import java.util.List;

import me.naiyu.android.app.snailnote.R;
import me.naiyu.android.app.snailnote.model.SuperNote;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NoteAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private List<SuperNote> items;
	
	public NoteAdapter(Context context, List<SuperNote> datas) {
		items = datas;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public SuperNote getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_note, null);
			holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title_item);
			holder.tvNote = (TextView) convertView.findViewById(R.id.tv_note_item);
			holder.llItem = (LinearLayout) convertView.findViewById(R.id.ll_item);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		SuperNote superNote = getItem(position);
		holder.tvTitle.setText(superNote.getNote().getTitle());
		holder.tvNote.setText(superNote.getNote().getNote());
		if (superNote.isSelected()) {
			holder.llItem.setBackgroundResource(R.drawable.item_press);
		} else {
			holder.llItem.setBackgroundResource(R.color.transparent);
		}
		return convertView;
	}
	
	public void reflesh(List<SuperNote> notes) {
		items.clear();
		items.addAll(notes);
		notifyDataSetChanged();
	}
	
	public void clearData() {
		items.clear();
		notifyDataSetChanged();
	}
	
	static class ViewHolder {
		TextView tvTitle;
		TextView tvNote;
		LinearLayout llItem;
	}
	
	public void setSelected(int position, boolean isSelected) {
		SuperNote sn = items.get(position);
		sn.setSelected(isSelected);
		items.set(position, sn);
		notifyDataSetChanged();
	}

}
