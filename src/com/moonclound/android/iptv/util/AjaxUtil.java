package com.moonclound.android.iptv.util;

import android.util.Log;

import com.moon.android.iptv.arb.film.Configs;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

public class AjaxUtil {
//	 public FinalHttp fn;
	 public int HostStart=1;
	 public int HostEnd=3;
     public AjaxUtil(){
//    	 fn=new FinalHttp();
     }
     public interface PostCallback{
         void Success(String t);
         void Failure();
        
     }
     
     public void post(final String Url,final AjaxParams params,final PostCallback postcallback){
    	 String postUrl="";
    	 if(Url.indexOf("http")==0){
    		 postUrl=Url;
    	 }else{
    		 postUrl=Configs.URL.HOST+Url;
    	 }
    
    	 Log.d("ajaxUrl"+HostStart,postUrl);
    	 FinalHttp fn=new FinalHttp();
    	 fn.post(postUrl,params,new AjaxCallBack<Object>() {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				// TODO Auto-generated method stub
				super.onFailure(t, errorNo, strMsg);
				
				if(HostStart==1){
					Configs.URL.HOST=Configs.URL.HOST2;
				}
				if(HostStart==2){
					Configs.URL.HOST=Configs.URL.HOST3;
				}
				HostStart++;
				if(HostStart<=HostEnd){
					post(Url,params,postcallback);
				}
				if(postcallback!=null){
					postcallback.Failure();
				}
			}

			@Override
			public void onSuccess(Object t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);
				
				if(postcallback!=null){
					postcallback.Success(t.toString());
				}
			}
    		 
		});
     }
     public void post(String Url){
    	 this.post(Url, new AjaxParams(),null);
     }
     public void post(String Url,final PostCallback postcallback){
    	 this.post(Url, new AjaxParams(),postcallback);
     }
}
