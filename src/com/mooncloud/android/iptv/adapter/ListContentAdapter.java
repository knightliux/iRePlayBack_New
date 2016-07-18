package com.mooncloud.android.iptv.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
















import com.moon.android.model.Model_LeftMenu;
import com.moon.android.model.Model_ListContent;
import com.moon.android.model.TvList;
import com.moonclound.android.iptv.util.AjaxUtil.PostCallback;
import com.moonlive.android.iptvback.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


@SuppressLint("ResourceAsColor")
public class ListContentAdapter extends BaseAdapter {

	private Context mContext;
	private DisplayImageOptions mOptions;
	private List<Model_ListContent> mlist;
    private int selectPos=-1;
	public ListContentAdapter(Context context, List<Model_ListContent> list) {
		mlist = list;
		mContext=context;
		mOptions = new DisplayImageOptions.Builder()
//		.showImageOnLoading(R.drawable.pic_loading)
//		.showImageForEmptyUri(R.drawable.pic_loading)
//		.showImageOnFail(R.drawable.pic_loading).cacheInMemory(true)
		.cacheOnDisk(true).considerExifParams(true)
		// .displayer(new RoundedBitmapDisplayer(20))
		.build();

	}
    public void selectChang(int pos){
    	this.selectPos=pos;
    	notifyDataSetChanged();
    }
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mlist.size();
	}
   
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mlist.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		Holder holder = null;
		Model_ListContent item = mlist.get(position);
		if (null == convertView) {
			holder = new Holder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.list_content_item, null);
//			holder.image = (ImageView) convertView.findViewById(R.id.index_img);
			holder.name=(TextView) convertView.findViewById(R.id.list_content_name);
			holder.stime=(TextView) convertView.findViewById(R.id.list_content_stime);
//			holder.num=(TextView) convertView.findViewById(R.id.list_item_num);
			holder.ico=(ImageView) convertView.findViewById(R.id.list_content_ico);
			convertView.setTag(holder);

			//
		} else {
			holder = (Holder) convertView.getTag();
		}
		
	    holder.ico.setVisibility(selectPos==position?View.VISIBLE:View.GONE);
		
		holder.stime.setText(item.getStime()+"-"+item.getEtime());
		holder.name.setText(item.getName());


		return convertView;

	}

	class Holder {
	   
		TextView stime,name;
		ImageView ico;
	}



}
