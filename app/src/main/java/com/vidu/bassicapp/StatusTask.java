package com.vidu.bassicapp;

import android.app.Activity;
import android.os.AsyncTask;

public class StatusTask extends AsyncTask<Void, Void, String> {
    private String mUrl;
    OnDataSendToActivity dataSendToActivity;

    public StatusTask (String url, Activity activity){
        dataSendToActivity = (OnDataSendToActivity) activity;
        mUrl = url;
    }

    @Override
    protected String doInBackground(Void... params) {
        String jsonString = JsonHttp.makeHttpRequest(mUrl);
        return jsonString;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        dataSendToActivity.sendData(s);
    }
}
