package com.moon.android.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moon.android.broadcast.MsgBroadcastReceiver;
import com.moon.android.iptv.arb.film.Configs;
import com.moon.android.iptv.arb.film.MsgService;
import com.moon.android.iptv.arb.film.MyApplication;
import com.moon.android.model.AuthInfo;




import com.moon.android.model.Model_LeftMenu;
import com.moon.android.model.Model_ListContent;
import com.moon.android.model.Model_date;
import com.moon.android.model.TvList;
import com.moon.android.moonplayer.MainActivity;
import com.moon.android.moonplayer.VodPlayerActivity;
import com.moon.android.moonplayer.service.VodVideo;
import com.mooncloud.android.iptv.adapter.DateMenuAdapter;
import com.mooncloud.android.iptv.adapter.LeftMenuAdapter;
import com.mooncloud.android.iptv.adapter.ListContentAdapter;
import com.mooncloud.android.iptv.database.PasswordDAO;
import com.mooncloud.heart.beat.Beat;
import com.moonclound.android.iptv.util.ActivityUtils;
import com.moonclound.android.iptv.util.AjaxUtil;
import com.moonclound.android.iptv.util.AjaxUtil.PostCallback;
import com.moonclound.android.iptv.util.Logger;
import com.moonclound.android.iptv.util.MACUtils;
import com.moonclound.android.iptv.util.MyDecode;
import com.moonclound.android.iptv.util.StringUtil;
import com.moonclound.android.view.CustomToast;
import com.moonclound.android.view.PasswordDialog;
import com.moonlive.android.iptvback.R;


public class HomeActivity extends Activity {

	private Logger log = Logger.getInstance();

	// 以下是获取授权信息相关
	private AuthInfo mAuthInfo = MyApplication.authInfo;
	private MsgBroadcastReceiver mMsgReceiver;
	private Intent mMsgServiceIntent;
	private static final int TOAST_TEXT_SIZE = 24;

	private TextView mLoadSeconMenuFailedBtn;
	private TextView mLoadProgramFailedBtn;
    //首次加载标志
	private boolean isFirstLoad=true;
	//左侧菜单相关
	private Model_LeftMenu mLeftList;
	private ListView mListMenu;
	private LeftMenuAdapter mLeftMenuAdapter;
	//日期列表相关
	private List<Model_date> mDatelist;
	private GridView mGv_date;
	private DateMenuAdapter mGvDate_Adapter;
	
	//内容列表相关
	private ListView mListContent;
	private ListContentAdapter mListContent_Adapter;
	
	//当前选中相关
	private String now_rid=null;//当前选中左侧菜单ID
	private String now_date=null;//当前选中日期
	private int now_date_pos=-1;
	private List<Model_ListContent> now_listContent=null;//当前选中内容列表
	private HashMap<String,List<Model_ListContent>> mCache_ListContent=new HashMap<String,List<Model_ListContent>>();
	
	
	private String adjson=null;
	//add heart beat to check white list
//	private Beat mHeartBeat=Beat.getInstance(Configs.HeartBeat.URL, Configs.HeartBeat.MAC);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		initView();
		initLoadView();
		startUpdatAndGetMsgService();
		registerMyReceiver();
		GetLeftMenu();
		
		


	}
	 @Override  
	    public void onWindowFocusChanged(boolean hasFocus)  
	    {  
	        if (hasFocus)  
	        {   
	        	if(isFirstLoad){
//	        		GetLeftMenu();
	        	}
	        	isFirstLoad=false;
	        	
	        }  
	    } 
	
  
	public int getStatusBarHeight() {  
        int result = 0;  
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");  
        if (resourceId > 0) {  
            result = getResources().getDimensionPixelSize(resourceId);  
        }  
        return result;  
    }  
    private void GetAd(){
    	AjaxParams params=new AjaxParams();
    	params.put("mac", MACUtils.getMac());
    	params.put("appid", Configs.APPID);
    	new AjaxUtil().post(Configs.URL.GetAD(),params,new PostCallback() {
			
			@Override
			public void Success(String t) {
				// TODO Auto-generated method stub
				adjson=t;
			}
			
			@Override
			public void Failure() {
				// TODO Auto-generated method stub
				
			}
		});
    }
	private void GetLeftMenu() {
		// TODO Auto-generated method stub
		  showLoadWindow();
		  new AjaxUtil().post(Configs.URL.getLeftMenuApi(),new PostCallback() {
				
				@Override
				public void Success(String t) {
					// TODO Auto-generated method stub
					Log.d("LeftMenuRe",t);
					
					GetAd();
					try {
						String json=new MyDecode().getjson(t);
						Gson g=new Gson();
						mLeftList=g.fromJson(json,new TypeToken<Model_LeftMenu>(){}.getType());
						if(mLeftList.getTvlist().size()>0){
							//Log.d("listsuccess",mLeftList.getTvlist().size()+"");
							showLeftMenu();
							 
						} 
					} catch (Exception e) {
						// TODO: handle exception
						showPop_fail();
						//loadPopDismiss();
					}
				}
				
			

				@Override
				public void Failure() {
					// TODO Auto-generated method stub
					showPop_fail();
//					loadPopDismiss();
				}
			});
	}
	private void showLeftMenu() {
		// TODO Auto-generated method stub
		mLeftMenuAdapter=new LeftMenuAdapter(HomeActivity.this, mLeftList.getTvlist());
		mListMenu.setAdapter(mLeftMenuAdapter);
		getDataList();
	}
	private void showListContent() {
		// TODO Auto-generated method stub
		mGvDate_Adapter.clickChang(now_date_pos);
		mListContent_Adapter=new ListContentAdapter(HomeActivity.this, now_listContent);
		mListContent.setAdapter(mListContent_Adapter);
	}

	private void getDataList() {
		// TODO Auto-generated method stub
		new AjaxUtil().post(Configs.URL.getData(),new PostCallback() {
			
			@Override
			public void Success(String t) {
				// TODO Auto-generated method stub
//				Log.d("data",t);
				loadPopDismiss();
				try {
					Gson g=new Gson();
					mDatelist=g.fromJson(t,new TypeToken<List<Model_date>>(){}.getType());
//					Log.d("datasize",mDatelist.size()+"");
				    if(mDatelist.size()>0){
				    	mGv_date.setNumColumns(mDatelist.size());
				    	mGvDate_Adapter=new DateMenuAdapter(HomeActivity.this, mDatelist);
				    	mGv_date.setAdapter(mGvDate_Adapter);
				    	
				    	changeChannel(0);
				    }	
				} catch (Exception e) {
					// TODO: handle exception
					showPop_fail();
				}
			}
			
			@Override
			public void Failure() {
				showPop_fail();
				// TODO Auto-generated method stub
				//loadPopDismiss();
			}
		});
		
	}



	private void startUpdatAndGetMsgService() {
		mMsgServiceIntent = new Intent(this, MsgService.class);
		startService(mMsgServiceIntent);
	}

	private void registerMyReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Configs.BroadCast.APP_GET_MSG);
		intentFilter.addAction(Configs.BroadCast.UPDATE_MSG);
		mMsgReceiver = new MsgBroadcastReceiver(this);
		registerReceiver(mMsgReceiver, intentFilter);
	}

	/**
	 * 初始化获取各种列表的服务类
	 */





	private void initView() {
		mGv_date=(GridView) findViewById(R.id.main_date);
		mListMenu=(ListView) findViewById(R.id.main_leftmenu);
		mListContent=(ListView) findViewById(R.id.main_list_content);
		mListContent.setOnItemClickListener(mlistContentClick);
		mListMenu.setOnItemClickListener(mLeftMenuClick);
		mGv_date.setOnItemClickListener(mdataMenuClick);
		mListContent.setOnItemSelectedListener(mlistContentSelect);
		
	}
	OnItemClickListener mlistContentClick=new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			startPlay(arg2);
		}
		
	}; 
	OnItemSelectedListener mlistContentSelect=new OnItemSelectedListener(){

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			log.i("listselect:"+arg2);
			mListContent_Adapter.selectChang(arg2);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			log.i("listselect:"+"none");
			mListContent_Adapter.selectChang(-1);
		}
		
	}; 
	OnItemClickListener mdataMenuClick=new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			// TODO Auto-generated method stub
			GetListContent(now_rid,pos); 	
		}

	};
	OnItemClickListener mLeftMenuClick=new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			// TODO Auto-generated method stub
			changeChannel(pos);
			
		}};
	private void changeChannel(int pos){
		mLeftMenuAdapter.clickChang(pos);
		TvList item=mLeftList.getTvlist().get(pos);
		if(!item.getRid().equals(now_rid)){
			now_rid=item.getRid();
			GetListContent(now_rid,0);
		}
	}
	private void startPlay(int position) {
		List<VodVideo> listVOdVideo = new ArrayList<VodVideo>();
		for (Model_ListContent v : now_listContent) {
			VodVideo video = new VodVideo();
			String Url=v.getUrl();
			String[] UrlArr=Url.split("/");
			//force://lookback.itvpad.co:9906/56a4fd4b0003e1ae020eb5435a93120a
			video.setChannelId(UrlArr[3]);
		
			video.setLink("11");
	
			video.setName(v.getName());
		
			video.setStreamip(UrlArr[2]);
	
			video.setType("mp4");
			video.setUrl(v.getUrl());
	
			listVOdVideo.add(video);
		
		}

		ComponentName componetName = new ComponentName(this,
				VodPlayerActivity.class);
		try {
			Intent intent = new Intent();
			
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("programName", now_listContent.get(position).getName());
			intent.putExtra("videolist", (Serializable) listVOdVideo);
			intent.putExtra("index", position);
//			intent.putExtra("hasHistory", isPlayHistory);
			intent.putExtra("Adjson", adjson);
//			intent.putExtra("appmsg", MyApplication.appMsg + "+++"
//					+ Configs.APPID);
			
			intent.setComponent(componetName);
			startActivity(intent);
		} catch (Exception e) {
			
		}
	}
	
	private void GetListContent(String now_rid, int datepos) {
			// TODO Auto-generated method stub
		    now_date_pos=datepos;
		    now_date=mDatelist.get(datepos).getDate();
		    if(mCache_ListContent.get(getCacheKey())==null){
		    	log.i("getcontentFrom Net");
		    	getListContentFromNet(now_rid,datepos);
		    }else{
		    	now_listContent=mCache_ListContent.get(getCacheKey());
		    	showListContent();
		    	log.i("getcontentFrom CaChe");
		    }
			
	}
	private String getCacheKey() {
		// TODO Auto-generated method stub
		return now_rid+now_date;
	}
    private void SaveListCache(){
    	mCache_ListContent.put(now_rid+now_date, now_listContent);
    }


	private void getListContentFromNet(String now_rid2, final int datepos) {
		// TODO Auto-generated method stub
		
	        showLoadWindow();
	        
		    AjaxParams params=new AjaxParams();
		    params.put("rid", now_rid2);
		    params.put("date", mDatelist.get(datepos).getDate());
		    params.put("mac", MACUtils.getMac());
		    params.put("appid",Configs.APPID);
		    new AjaxUtil().post(Configs.URL.getListContent(),params,new PostCallback() {
				
				@Override
				public void Success(String t) {
					// TODO Auto-generated method stub
					 loadPopDismiss();
					Log.d("list",t);
					try {
						String json=new MyDecode().getjson(t);
						Gson g=new Gson();
						now_listContent=g.fromJson(json, new TypeToken<List<Model_ListContent>>(){}.getType());
//                        log.d(now_listContent.size()+"");
						if(now_listContent.size()>0){
							SaveListCache();
							showListContent();
						}else{
							now_listContent=new ArrayList<Model_ListContent>();
							showListContent();
						}
						
					} catch (Exception e) {
						// TODO: handle exception
						now_listContent=new ArrayList<Model_ListContent>();
						showListContent();
					}
				}
				
			

				@Override
				public void Failure() {
					// TODO Auto-generated method stub
					loadPopDismiss();
					now_listContent=new ArrayList<Model_ListContent>();
					showListContent();
				}
			});
	} 
    


	/**
	 * 修改View的显示状态
	 * 
	 * @param visible true--显示 false--不显示
	 */
	public void setViewVisible(View view, boolean visible) {
		if (visible == true) {
			view.setVisibility(View.VISIBLE);
		} else {
			view.setVisibility(View.GONE);
		}
	}
 
	@Override
	public void onBackPressed() {
		showExitWindow();
//		showLoadWindow();
	}

	private PopupWindow mExitPopupWindow;

	@SuppressLint("NewApi")
	private void showExitWindow() {
		LayoutInflater mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = mInflater.inflate(R.layout.p_exit_pop, null);
       
		Point point = new Point();
		Display display = getWindowManager().getDefaultDisplay();
		display.getSize(point);
		int width = point.x;
		int height = point.y;
		mExitPopupWindow = new PopupWindow(view, width, height, true);
		mExitPopupWindow.showAsDropDown(view, 0, 0);
		mExitPopupWindow.setOutsideTouchable(false);
		Button sure = (Button) view.findViewById(R.id.p_eixt_sure);
		Button cancel = (Button) view.findViewById(R.id.p_eixt_cancel);
		cancel.requestFocus();
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				exitPopDismiss();
			}
		});
		sure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				exitPopDismiss();
				clearCache();
				android.os.Process.killProcess(android.os.Process.myPid());  
			}

		});
	}

	private void clearCache() {

	}
	
	private void showPop_Pro(){
		mImg_loadpop.setVisibility(View.VISIBLE);
		mLine_loadbuttom.setVisibility(View.GONE);
	}
	private void showPop_fail(){
//		Log.d("loadfail","--------");
		mImg_loadpop.setVisibility(View.GONE);
		mLine_loadbuttom.setVisibility(View.VISIBLE);
		mbt_reload.requestFocus();
	}
	private PopupWindow LoadWindow;
//	private LinearLayout load_pop_fail;
//	private View loadviewpop;
	private ImageView mImg_loadpop;
	private LinearLayout mLine_loadbuttom;
	private Button mbt_reload,mbt_cancel;
	private View view;
	private void initLoadView() {
		LayoutInflater mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		view = mInflater.inflate(R.layout.p_load_pop, null);

		Point point = new Point();
		Display display = getWindowManager().getDefaultDisplay();
		display.getSize(point);
		int width = point.x;
		int height = point.y;
		 LoadWindow = new PopupWindow(view, width, height, true);
		 LoadWindow.setOutsideTouchable(false);
		 ImageView img=(ImageView) view.findViewById(R.id.load_pop_img);
			mImg_loadpop=(ImageView) view.findViewById(R.id.load_pop_img);
			mLine_loadbuttom=(LinearLayout) view.findViewById(R.id.load_pop_fail);
			mbt_reload=(Button) view.findViewById(R.id.load_reload);
			mbt_cancel=(Button) view.findViewById(R.id.load_cancel);
			mbt_reload.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
					GetLeftMenu();
					
				}
			});
			mbt_cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					loadPopDismiss();
				}
			});
			AnimationDrawable an=(AnimationDrawable) img.getBackground();
			an.setOneShot(false);
		
			an.start();
			showPop_Pro();
	}
	@SuppressLint("NewApi")
	private void showLoadWindow() {
		    if(LoadWindow.isShowing()){
		    	showPop_Pro();
		    }else{
		    	 view.post(new Runnable() {
					 	
						@Override
						public void run() {
							// TODO Auto-generated method stub
							LoadWindow.showAsDropDown(view, 0, 0);
						}
				 });
		    }
			
		
	   
	}
	
	
	private void exitPopDismiss() {
		if (null != mExitPopupWindow && mExitPopupWindow.isShowing())
			mExitPopupWindow.dismiss();
	}
	
	private void loadPopDismiss() {
		if (null != LoadWindow && LoadWindow.isShowing())
			LoadWindow.dismiss();
		
		initLoadView();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mMsgReceiver);
		stopService(mMsgServiceIntent);
		super.onDestroy();
	}


}
