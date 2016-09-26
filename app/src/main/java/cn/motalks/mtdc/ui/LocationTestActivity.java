package cn.motalks.mtdc.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.motalks.mtdc.R;
import cn.motalks.mtdc.utils.EasyPermissionsEx;
import cn.motalks.mtdc.utils.LocationHelper;
import cn.motalks.mtdc.utils.LocationUtils;

/**
 * Created by MoLin on 16/9/25.
 */
public class LocationTestActivity extends Activity {

    private TextView mLatitudeView;
    private TextView mLongtitudeView;
    private TextView mWeatherView;
    private Button mButton;
    private String[] mNeedPermissionsList = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.location_text_activity);
        initView();
        super.onCreate(savedInstanceState);
    }

    private void initView() {
        mLatitudeView = (TextView) findViewById(R.id.location_latitude);
        mLongtitudeView = (TextView) findViewById(R.id.location_longtitude);
        mWeatherView = (TextView) findViewById(R.id.weather_info_tv);
        mButton = (Button) findViewById(R.id.location_button);

        mButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 使用了 EasyPermissionsEx 类来管理动态权限配置
                if (EasyPermissionsEx.hasPermissions(LocationTestActivity.this, mNeedPermissionsList)) {
                    initLocation();
                } else {
                    EasyPermissionsEx.requestPermissions(LocationTestActivity.this, "需要定位权限来获取当地天气信息", 1, mNeedPermissionsList);
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("MoLin", "已获取权限!");
                    initLocation();
                } else {
                    if (EasyPermissionsEx.somePermissionPermanentlyDenied(this, mNeedPermissionsList)) {
                        EasyPermissionsEx.goSettings2Permissions(this, "需要定位权限来获取当地天气信息,但是该权限被禁止,你可以到设置中更改"
                                , "去设置", 1);
                    }
                }
            }
            break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            Toast.makeText(this, "settings", Toast.LENGTH_LONG).show();
        }
    }

    private void initLocation() {
        LocationUtils.getInstance(LocationTestActivity.this).initLocation( new LocationHelper() {
            @Override
            public void UpdateLocation(Location location) {
                Log.e("MoLin", "location.getLatitude():" + location.getLatitude());
                mLatitudeView.setText(location.getLatitude() + "");
                mLongtitudeView.setText(location.getLongitude() + "");
            }

            @Override
            public void UpdateStatus(String provider, int status, Bundle extras) {
            }

            @Override
            public void UpdateGPSStatus(GpsStatus pGpsStatus) {

            }

            @Override
            public void UpdateLastLocation(Location location) {
                Log.e("MoLin", "UpdateLastLocation_location.getLatitude():" + location.getLatitude());
                mLatitudeView.setText(location.getLatitude() + "");
                mLongtitudeView.setText(location.getLongitude() + "");
            }
        });
    }

    @Override
    protected void onDestroy() {
        // 在页面销毁时取消定位监听
        LocationUtils.getInstance(LocationTestActivity.this).removeLocationUpdatesListener();
        super.onDestroy();
    }
}
