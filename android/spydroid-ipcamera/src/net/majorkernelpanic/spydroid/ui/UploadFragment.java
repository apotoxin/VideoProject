/*
 * Copyright (C) 2011-2013 GUIGUI Simon, fyhertz@gmail.com
 * 
 * This file is part of Spydroid (http://code.google.com/p/spydroid-ipcamera/)
 * 
 * Spydroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package net.majorkernelpanic.spydroid.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.majorkernelpanic.spydroid.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.TextView;

;

public class UploadFragment extends Fragment implements
		AdapterView.OnItemClickListener {

	private ListView lv;

	static class FileToken {
		String path;
		String name;
	}

	List<File> names;

	private Context context;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		this.context = this.getActivity();

		View rootView = inflater.inflate(R.layout.upload, container, false);

		lv = (ListView) rootView.findViewById(R.id.lv);
		lv.setOnItemClickListener(this);
		names = new ArrayList<File>();
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File path = Environment.getExternalStorageDirectory();// 获得SD卡路径
			File sdPath = new File(path.getAbsolutePath() + File.separator
					+ "VideoRec");
			File[] files = sdPath.listFiles();// 读取
			getFileName(files);
		}

		ArrayAdapter<File> mAdapter = new ArrayAdapter<File>(context,
				android.R.layout.simple_expandable_list_item_1, names) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				LayoutInflater _LayoutInflater = LayoutInflater.from(context);

				convertView = _LayoutInflater.inflate(R.layout.listitem, null);

				if (convertView != null)

				{

					TextView name = (TextView) convertView
							.findViewById(R.id.textView1);

					TextView path = (TextView) convertView
							.findViewById(R.id.textView2);

					name.setText(names.get(position).getName());

					path.setText(names.get(position).getAbsolutePath());

					convertView.setTag(names.get(position));

				}

				return convertView;

			}

		};

		lv.setAdapter(mAdapter);

		return rootView;

	}

	private void getFileName(File[] files) {
		if (files != null) {// 先判断目录是否为空，否则会报空指针
			for (File file : files) {
				if (file.isDirectory()) {
					Log.i("zeng", "若是文件目录。继续读1" + file.getName().toString()
							+ file.getPath().toString());

					getFileName(file.listFiles());
					Log.i("zeng", "若是文件目录。继续读2" + file.getName().toString()
							+ file.getPath().toString());
				} else {
					String fileName = file.getName();
					if (fileName.endsWith(".mp4")) {
						names.add(file);
					}
				}
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long id) {
		// TODO Auto-generated method stub

		final File file = (File) view.getTag();

		AlertDialog alertDialog = new AlertDialog.Builder(context)
				.setTitle("上传到服务器？").setMessage("上传该视频至服务器")
				.setIcon(R.drawable.icon).create();
		alertDialog.setButton(Dialog.BUTTON_POSITIVE, "确定",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (which == Dialog.BUTTON_POSITIVE) {
							UploadManager.getInstance().upload(file);
						} else {
							dialog.dismiss();
						}
					}
				});
		alertDialog.show();

	}

}
