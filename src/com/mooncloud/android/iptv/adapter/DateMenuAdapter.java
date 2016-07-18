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
import com.moon.android.model.Model_date;
import com.moon.android.model.TvList;
import com.moonlive.android.iptvback.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


@SuppressLint("ResourceAsColor")
public class DateMenuAdapter extends BaseAdapter {

	private Context mContext;
	private DisplayImageOptions mOptions;
	private List<Model_date> mlist;
	 private int clickpos=-1;
	public DateMenuAdapter(Context context, List<Model_date> list) {
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
	public void clickChang(int pos){
    	this.clickpos=pos;
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
		Model_date item = mlist.get(position);
		if (null == convertView) {
			holder = new Holder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.date_menu_item, null);
			//holder.image = (ImageView) convertView.findViewById(R.id.index_img);
			holder.date=(TextView) convertView.findViewById(R.id.date_menu_date);
			holder.w=(TextView) convertView.findViewById(R.id.date_menu_w);
//			holder.num=(TextView) convertView.findViewById(R.id.list_item_num);
//			holder.logo=(ImageView) convertView.findViewById(R.id.left_menu_ico);
			convertView.setTag(holder);

			//
		} else {
			holder = (Holder) convertView.getTag();
		}
		if(position==clickpos){
//			holder.date.setTextColor(mContext.getResources().getColor(R.color.yellow_half));
//			holder.w.setTextColor(mContext.getResources().getColor(R.color.yellow_half));
			holder.date.setTextColor((ColorStateList) mContext.getResources().getColorStateList(R.drawable.left_color_click_selector) );
			 holder.w.setTextColor((ColorStateList) mContext.getResources().getColorStateList(R.drawable.left_color_click_selector) );
		}else{
//			holder.date.setTextColor(mContext.getResources().getColor(R.color.white));
//			holder.w.setTextColor(mContext.getResources().getColor(R.color.white));
			 holder.date.setTextColor((ColorStateList) mContext.getResources().getColorStateList(R.drawable.left_color_selector) );
			 holder.w.setTextColor((ColorStateList) mContext.getResources().getColorStateList(R.drawable.left_color_selector) );
		}
		holder.date.setText(item.getDate());
		holder.w.setText(item.getW());
		return convertView;

	}

	class Holder {
	   
		TextView date,w;
		ImageView logo;
	}



}
