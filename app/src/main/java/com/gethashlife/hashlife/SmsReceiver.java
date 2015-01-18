package com.gethashlife.hashlife;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by hunter on 1/18/15.
 */
public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("FYREBUG", "RECEIVED");
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                String sender = smsMessage.getDisplayOriginatingAddress();
                sender = sender.replaceAll("[^\\d.]", "");
                String messageBody = smsMessage.getMessageBody();
                Log.d("FYREBUG", sender);
                if (sender.equals("13472692418")) {
                    registerWithNetwork(context, messageBody);
                }
            }
        }
    }

    private void registerWithNetwork(Context context, String code) {
        String requestString = "/register?phone=" + SMS.getDeviceTelephoneNumber(context) + "&" + "pkey=" + "123456"
                + "&" + "code=" + code;
        Log.d("FYREBUG", requestString);
        VerifyRestClient.get(requestString, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("FYREBUG", "we got here JSONObject");
                try {
                    String responseText = (String) response.get("0");
                    Log.d("FYREBUG", "RESPONSE FROM SERVER: " + responseText);
                } catch (Exception e) {
                    Log.d("FYREBUG", "ERROR: SOMETHING WITH JSONObject");
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                Log.d("FYREBUG", "we got here JSONArray");
            }

//            @Override
//            public void onFailure(int a, Header[] h, Throwable e, JSONObject o) {
////                Toast.makeText(context, (String) "Failed to register with server.",
////                        Toast.LENGTH_LONG).show();
//            }
        });
    }
}
