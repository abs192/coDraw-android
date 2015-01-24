package com.abs192.codraw.adapters;

import com.abs192.codraw.CoDrawApplication;
import com.abs192.codraw.R;
import com.abs192.codraw.adapters.ChatMessage.ChatType;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChatListAdapter extends BaseAdapter {

	private Activity mContext;

	public ChatListAdapter(Activity context) {
		mContext = context;
	}

	@Override
	public int getCount() {
		return CoDrawApplication.chatList.size();
	}

	@Override
	public Object getItem(int i) {
		return CoDrawApplication.chatList.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public int getItemViewType(int position) {
		ChatType type = (CoDrawApplication.chatList.get(position)).getType();
		if (type == ChatType.RECD)
			return 0;
		else if (type == ChatType.SENT)
			return 1;
		else if (type == ChatType.ERROR)
			return 2;
		else
			return 2;
	}

	@SuppressLint({ "RtlHardcoded", "NewApi" })
	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		int type = getItemViewType(i);
		if (type == 0) {
			// recd
			if (view == null) {
				LayoutInflater factory = LayoutInflater.from(mContext);
				view = factory.inflate(R.layout.chat_item, null);
				TextView tvU = (TextView) view.findViewById(R.id.tvUName);
				TextView tvM = (TextView) view.findViewById(R.id.tvMsg);
				ChatMessage msg = (ChatMessage) getItem(i);
				if (msg != null) {
					tvU.setText(msg.getUname());
					tvM.setText(msg.getMessage());
				}
				if (android.os.Build.VERSION.SDK_INT > 14) {
					tvU.setGravity(Gravity.START);
					tvM.setGravity(Gravity.START);
				} else {
					if (android.os.Build.VERSION.SDK_INT > 14) {
						tvU.setGravity(Gravity.LEFT);
						tvM.setGravity(Gravity.LEFT);
					}
				}
				return view;
			}
		} else if (type == 1) {
			// sent
			LayoutInflater factory = LayoutInflater.from(mContext);
			view = factory.inflate(R.layout.chat_item, null);
			TextView tvU = (TextView) view.findViewById(R.id.tvUName);
			TextView tvM = (TextView) view.findViewById(R.id.tvMsg);
			ChatMessage msg = (ChatMessage) getItem(i);
			if (msg != null) {
				tvU.setText(msg.getUname());
				tvM.setText(msg.getMessage());
			}
			if (android.os.Build.VERSION.SDK_INT > 14) {
				tvU.setGravity(Gravity.END);
				tvM.setGravity(Gravity.END);
			} else {
				if (android.os.Build.VERSION.SDK_INT > 14) {
					tvU.setGravity(Gravity.RIGHT);
					tvM.setGravity(Gravity.RIGHT);
				}
			}
		} else if (type == 2) {
			// error
			System.out.println("ädasdas");
			LayoutInflater factory = LayoutInflater.from(mContext);
			view = factory.inflate(R.layout.chat_offline, null);
			return view;
		}
		
		return view;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}
}
