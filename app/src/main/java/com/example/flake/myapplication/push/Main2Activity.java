package com.example.flake.myapplication.push;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.flake.myapplication.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class Main2Activity extends Activity {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        String json = getIntent().getStringExtra("greetjson");
        SharedPreferences prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);

        if (!checkPlayServices()) {
            Toast.makeText(
                    getApplicationContext(),
                    "Устройство не поддерживает Play сервисы, приложение не будет работать",
                    Toast.LENGTH_LONG).show();
            finish();
        }

        if (json != null) {
            try {
                JSONObject jsonObj = new JSONObject(json);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("greetimgurl", jsonObj.getString("greetImgURL"));
                editor.putString("greetmsg", jsonObj.getString("greetMsg"));
                editor.commit();

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Устройство не поддерживает Play сервисы, приложение не будет работать",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        } else {
            /*Toast.makeText(
                    getApplicationContext(),
                    "This device supports Play services, App will work normally",
                    Toast.LENGTH_LONG).show();*/
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }
}