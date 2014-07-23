package com.example.locationdemo;

import java.util.Date;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

public class MainActivity extends Activity implements OnClickListener{

	private TextView mTextView;
	private Button gpsBtn, baiduBtn, amapBtn;
	
	//gps
	private LocationManager gpsManager;
	//baidu
	private LocationClient baduduManager;
	//amap
	private LocationManagerProxy aMapManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTextView = (TextView) findViewById(R.id.text);
		gpsBtn = (Button) findViewById(R.id.gps);
		baiduBtn = (Button) findViewById(R.id.baidu);
		amapBtn = (Button) findViewById(R.id.amap);
		
		gpsBtn.setOnClickListener(this);
		baiduBtn.setOnClickListener(this);
		amapBtn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gps:
			if (gpsBtn.getText().toString().equals("开启GPS定位")) {
				startGps();
				gpsBtn.setText("停止GPS定位");
			} else {
				stopGps();
				gpsBtn.setText("开启GPS定位");
			}
			break;
		case R.id.baidu:
			if (baiduBtn.getText().toString().equals("开启百度定位")) {
				startBaidu();
				baiduBtn.setText("停止百度定位");
			} else {
				stopBaidu();
				baiduBtn.setText("开启百度定位");
			}
			break;
		case R.id.amap:
			if (amapBtn.getText().toString().equals("开启高德定位")) {
				startAmap();
				amapBtn.setText("停止高德定位");
			} else {
				stopAmap();
				amapBtn.setText("开启高德定位");
			}
			break;

		default:
			break;
		}
	}

	private void startAmap() {
		aMapManager = LocationManagerProxy.getInstance(this);
		/*
		 * mAMapLocManager.setGpsEnable(false);
		 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
		 * API定位采用GPS和网络混合定位方式
		 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
		 */
		aMapManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 2000, 10, mAMapLocationListener);
	}

	private void stopAmap() {
		if (aMapManager != null) {
			aMapManager.removeUpdates(mAMapLocationListener);
			aMapManager.destory();
		}
		aMapManager = null;
	}
	
	private void startBaidu() {
		if (baduduManager == null) {
			baduduManager = new LocationClient(this);
			//定位的配置
			LocationClientOption option = new LocationClientOption();
			//定位模式选择，高精度、省电、仅设备
			option.setLocationMode(LocationMode.Hight_Accuracy); 
			//定位坐标系类型选取, gcj02、bd09ll、bd09
			option.setCoorType("gcj02"); 
			//定位时间间隔
			option.setScanSpan(1000);
			//选择定位到地址
			option.setIsNeedAddress(true);
			baduduManager.setLocOption(option);
			//注册定位的成功的回调
			baduduManager.registerLocationListener(mBdLocationListener);
		}
		baduduManager.start();
	}
	
	private void stopBaidu() {
		baduduManager.stop();
	}
	

	private void startGps() {
		// 获取到LocationManager对象
		gpsManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		//provider可为gps定位，也可为为基站和WIFI定位
		String provider = gpsManager.getProvider(LocationManager.GPS_PROVIDER).getName();
		
		//3000ms为定位的间隔时间，10m为距离变化阀值，gpsListener为回调接口
		gpsManager.requestLocationUpdates(provider, 3000, 10, gpsListener);
	}
	
	private void stopGps() {
		gpsManager.removeUpdates(gpsListener);
	}

	// 创建位置监听器
	private LocationListener gpsListener = new LocationListener() {
		
		// 位置发生改变时调用
		@Override
		public void onLocationChanged(Location location) {
			Log.e("Location", "onLocationChanged");
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			float speed = location.getSpeed();
			long time = location.getTime();
			String s = "latitude--->" + latitude
					+  "  longitude--->" + longitude
					+  "  speed--->" + speed 
					+  "  time--->" + new Date(time).toLocaleString();
			mTextView.setText("GPS定位\n" + s);
		}

		// provider失效时调用
		@Override
		public void onProviderDisabled(String provider) {
			Log.e("Location", "onProviderDisabled");
		}

		// provider启用时调用
		@Override
		public void onProviderEnabled(String provider) {
			Log.e("Location", "onProviderEnabled");
		}

		// 状态改变时调用
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.e("Location", "onStatusChanged");
		}
	};
	
	private BDLocationListener mBdLocationListener = new BDLocationListener() {
		
		@Override
		public void onReceiveLocation(BDLocation location) {
			//Receive Location 
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\ndirection : ");
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append(location.getDirection());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append("\noperationers : ");
				sb.append(location.getOperators());
			}
			mTextView.setText("百度定位\n" + sb.toString());
		}
	};
	
	private AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			
		}
		
		@Override
		public void onLocationChanged(Location location) {
			
		}
		
		@Override
		public void onLocationChanged(AMapLocation location) {
			if (location != null) {
				Double geoLat = location.getLatitude();
				Double geoLng = location.getLongitude();
				String cityCode = "";
				String desc = "";
				Bundle locBundle = location.getExtras();
				if (locBundle != null) {
					cityCode = locBundle.getString("citycode");
					desc = locBundle.getString("desc");
				}
				String str = ("定位成功:(" + geoLng + "," + geoLat + ")"
						+ "\n精    度    :" + location.getAccuracy() + "米"
						+ "\n定位方式:" + location.getProvider() + "\n定位时间:"
						+ new Date(location.getTime()).toLocaleString() + "\n城市编码:"
						+ cityCode + "\n位置描述:" + desc + "\n省:"
						+ location.getProvince() + "\n市:" + location.getCity()
						+ "\n区(县):" + location.getDistrict() + "\n区域编码:" + location
						.getAdCode());
				mTextView.setText("高德定位\n" + str);
			}
		}
	};

}
