package com.vidu.bassicapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity implements OnDataSendToActivity{
    SwitchCompat sw_denchum, sw_dowlight, sw_quattran;
    TextView txt_status, txt_chum, txt_dowlight, txt_quattran;
    BroadcastReceiver receiver;
    String url ="http://192.168.2.91/";
    Integer status = 0;
    Button btn_test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Kiểm tra kết nối wifi


        receiver = new BroadcastReceiver() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager manager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);

                NetworkInfo info = manager.getActiveNetworkInfo();
                if(info != null && info.isConnected()){
                    if(info.getType() == ConnectivityManager.TYPE_WIFI){
                       txt_status.setText("Kết nối wifi");
                       status = 1;
                    }
                    if (info.getType() != ConnectivityManager.TYPE_WIFI) {
                        txt_status.setText("Không có kết nối");
                        status = 0;
                    }
                }
            }
        };

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateStatus();
                handler.postDelayed(this,2000);
            }
        }, 5000);
        //hết kiểm tra kết nối wifi

        //Xử lý sự kiện swich bị thay đổi ;
        sw_denchum = findViewById(R.id.sw_chum);
        sw_dowlight = findViewById(R.id.sw_dowlight);
        sw_quattran = findViewById(R.id.sw_fan);
        txt_chum = findViewById(R.id.txt_chum);
        txt_dowlight = findViewById(R.id.txt_dowlight);
        txt_quattran = findViewById(R.id.txt_fan);
        txt_status = findViewById(R.id.txt_status);
        btn_test = findViewById(R.id.btn_test);

        btn_test.setOnClickListener(new View.OnClickListener() {
            String url_rl;
            @Override
            public void onClick(View view) {
                url_rl = url + "batdenchum";
                SelectTask task = new SelectTask(url_rl);
                task.execute();
                updateStatus();
            }
        });

        sw_denchum.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                txt_chum.setText("Đèn chùm đang " + (sw_denchum.isChecked() ? "Bật" : "Tắt"));
                String url_rl;
                if (sw_denchum.isChecked()) {
                    url_rl = url + "batdenchum";
                } else {
                    url_rl = url + "tatdenchum";
                }
                SelectTask task = new SelectTask(url_rl);
                task.execute();
                updateStatus();

            }
        });

        sw_dowlight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String url_rl;
                txt_dowlight.setText("Đèn dowlight đang " + (sw_dowlight.isChecked() ? "Bật": "Tắt"));
                if (sw_dowlight.isChecked()){
                    url_rl = url + "batdowlight";
                } else {
                    url_rl = url + "tatdowlight";
                }
                SelectTask task = new SelectTask(url_rl);
                task.execute();
                updateStatus();
            }
        });

        sw_quattran.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String url_rl;
                txt_quattran.setText("Quạt trần đang " + (sw_quattran.isChecked() ? "Bật" : "Tắt"));
                if (sw_quattran.isChecked()){
                    url_rl = url + "batquat";
                } else {
                    url_rl = url + "tatquat";
                }
                SelectTask task = new SelectTask(url_rl);
                task.execute();
                updateStatus();
            }
        });


    }
    //hết sự kiện

    //mở rộng kiểm tra kết nối
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void sendData(String str) {
        updateButtonStatus(str);
    }
    //hết kiểm tra kết nối

    //Xử lý đồng bộ trạng thái giữa app và esp8266
    private void updateStatus(){
        String url_rl = url + "status";
        StatusTask task = new StatusTask(url_rl, this);
        task.execute();
    }

    private void updateButtonStatus(String jsonStrings) {
        try {
            JSONObject json = new JSONObject(jsonStrings);
            String den_chum = json.getString("denchum");
            String dowlight = json.getString("dowlight");
            String quattran = json.getString("quattran");
            if(den_chum.equals("1")){
                sw_denchum.setChecked(true);
            } else {
                sw_denchum.setChecked(false);
            }
            if(dowlight.equals("1")){
                sw_dowlight.setChecked(true);
            } else {
                sw_dowlight.setChecked(false);
            }
            if(quattran.equals("1")){
                sw_quattran.setChecked(true);
            } else {
                sw_quattran.setChecked(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}