package com.example.pc.caseproject;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.cprButton)
    CPRButton cprButton;
    @BindView(R.id.aedButton)
    Button aedButton;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private ArrayList<Integer> missingPermissions;
    private String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.SEND_SMS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        requestAllPermissions();
    }

    //주변 AED 찾기 버튼 누르면 실행될 메서드 입니다.
    @OnClick(R.id.cprButton)
    public void onCPRButtonClicked(View v) {
        Intent intent = new Intent(this, HeartActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.aedButton)
    public void onAEDButtonClicked(View v) {
        AED_FIND_REQUEST myAedRequest = new AED_FIND_REQUEST();
        Location myLocation = findMyLocation();
        myAedRequest.setMyLatitude(myLocation.getLatitude());
        myAedRequest.setMyLongtitiude(myLocation.getLongitude());
        //이 메서드 안에서 자동으로 액티비티 넘어갑니다.
        AEDCallUtil.getAEDDataFromAPI(this, myLocation, myAedRequest, false, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void requestAllPermissions() {
        if (missingPermissions == null) {
            requestPermissions(permissions, 87);
        } else if (missingPermissions.size() > 0) {
            for (int i : missingPermissions) {
                requestPermissions(new String[]{permissions[missingPermissions.get(i)]}, 0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 87) {
            if (grantResults.length > 0) {
                missingPermissions = new ArrayList<>();
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        missingPermissions.add(i);
                    }
                }
            }
        }
    }

    //자신의 현재 위치를 파악하는 메서드 입니다.
    public Location findMyLocation() {
        //**gps 기능이 켜졌는지 확인하는 코드가 필요합니다,
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestAllPermissions();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            return myLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } else {
            Toast.makeText(getApplicationContext(), "먼저 위치 권한을 확인해주세요", Toast.LENGTH_LONG).show();
            return null;
        }
    }


}
