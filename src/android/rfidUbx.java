package com.justep.cordova.plugin;

import java.util.ArrayList;
import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.Tag_Data;
import com.speedata.libuhf.UHFManager;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

public class rfidUbx extends CordovaPlugin {
	private CallbackContext callbackContext;
	private final String LOG_TAG = "UHF";

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
	}

	private IUHFService uhfService;
	ArrayList<String> inventoryResult = new ArrayList<String>();
	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContextID) {
				this.callbackContext = callbackContextID;
		uhfService = UHFManager.getUHFService(cordova.getActivity());
		if (action.equals("openDev")) {
			int result = uhfService.OpenDev();
			if (result == 0) {
				this.callbackContext.success();
			} else {
				// 上電失敗
				this.callbackContext.error("power error");
			}
		} else if (action.equals("inventoryStart")) {
			// 盤點
			inventoryResult.clear();
			Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					ArrayList<Tag_Data> ks = (ArrayList<Tag_Data>) msg.obj;
                    for (int i = 0; i < ks.size(); i++) {
                        byte[] nq = ks.get(i).epc;
                        if (nq != null) {
                            inventoryResult.add(byteArrayToString(nq));
                        }
                    }
				}
			};
			
			try {
				uhfService.inventory_start(handler);
				int delay = args.getInt(0);
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						uhfService.inventory_stop();
						JSONObject obj;
						try {
							obj = createScanParms(inventoryResult);
							callback(PluginResult.Status.OK, true, obj);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, delay);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.callbackContext.error("参数不正确");
				return false;
			}
		}else if(action.equals("closeDev")){
			uhfService.CloseDev();
		}else if(action.equals("readArea")){
			int area;
			JSONObject obj;
			try {
				area = args.getInt(0);
				String addr = args.getString(1);
				String count = args.getString(2);
				String passwd = args.getString(3);
				String readResult=
				uhfService.read_area(area, addr, count, passwd);
				inventoryResult.clear();
				if(TextUtils.isEmpty(readResult)){
					this.callbackContext.error("读卡失败");
				}else{
					inventoryResult.add(readResult);
					obj = createScanParms(inventoryResult);
					callback(PluginResult.Status.OK, true, obj);
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.callbackContext.error("参数不正确");
				return false;
			}
			
		}else if(action.equals("writeArea")){
			try {
				int area = args.getInt(0);
				String addr = args.getString(1);
				String passwd = args.getString(2);
				String count = args.getString(3);
				String content = args.getString(4);
				int result=uhfService.write_area(area, addr,
						passwd, count, content);
				if(result==0){
					this.callbackContext.success();
				}else{
					this.callbackContext.error("写卡失败");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(action.equals("setAntennaPower")){
			try {
				int power = args.getInt(0);
				int result=uhfService.set_antenna_power(power);
				if(result==0){
					this.callbackContext.success();
				}else{
					this.callbackContext.error("设置失败");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(action.equals("getAntennaPower")){
			inventoryResult.clear();
			JSONObject obj = null;
			int result = uhfService.get_antenna_power();
			if (result != -1) {
				inventoryResult.add(result+"");
				try {
					obj = createScanParms(inventoryResult);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				callback(PluginResult.Status.OK, true, obj);
			} else {
				this.callbackContext.error("获取失败");
			}
		}else if(action.equals("setFreqRegion")){
			try {
				int region = args.getInt(0);
				int result=uhfService.set_freq_region(region);
				if(result==0){
					this.callbackContext.success();
				}else{
					this.callbackContext.error("设置失败");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(action.equals("getFreqRegion")){
			inventoryResult.clear();
			JSONObject obj = null;
			int result = uhfService.get_freq_region();
			if (result != -1) {
				inventoryResult.add(result+"");
				try {
					obj = createScanParms(inventoryResult);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				callback(PluginResult.Status.OK, true, obj);
			} else {
				this.callbackContext.error("获取失败");
			}
		}
		
	
		cordova.getActivity();
		PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
		result.setKeepCallback(true);
		this.callbackContext.sendPluginResult(result);
		// Log.d(LOG_TAG, "----setKeepCallback---");
		return true;

	}

	
	
	/**
     * byte[]->String {0x23,0x32,0x12}-->"233212" 比如从卡里解析出身份证
     *
     * @param src
     * @return
     */
    public static String byteArrayToString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

	
	
	private void callback(final PluginResult.Status state,
			final boolean isKeepCallback, final JSONObject data) {
		PluginResult result;
		if (data != null) {
			result = new PluginResult(state, data);
		} else {
			result = new PluginResult(state);
		}
		result.setKeepCallback(isKeepCallback);
		this.callbackContext.sendPluginResult(result);
		Log.d(LOG_TAG,
				"callbackContext.isFinished():" + callbackContext.isFinished());
	}

	private void callErrorback(String error) {
		PluginResult result;
		result = new PluginResult(PluginResult.Status.ERROR, error);
		result.setKeepCallback(false);
		this.callbackContext.sendPluginResult(result);
	}
	
	
	private static final String CANCELLED = "cancelled";
	// 构造参数 createScanParms
	private JSONObject createScanParms(List<String> tagList)
			throws JSONException {
		JSONObject obj = new JSONObject();
		JSONArray jsArray = new JSONArray(tagList);
		obj.put(CANCELLED, false);
		obj.put("tags", jsArray);
		return obj;
	}

}
