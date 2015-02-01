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
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hunter on 1/18/15.
 */
public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                String sender = smsMessage.getDisplayOriginatingAddress();
                sender = sender.replaceAll("[^\\d.]", "");
                String messageBody = smsMessage.getMessageBody();
                if (sender.equals("13472692418")) {
                    registerWithNetwork(context, messageBody);
                }
            }
        }
    }

    private void registerWithNetwork(final Context context, String code) {
        String requestString = "/register?phone=" + SMS.getDeviceTelephoneNumber(context) + "&" + "pkey=" + Encryption.getPublicKeyString()
                + "&" + "code=" + code;
        VerifyRestClient.get(requestString, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String responseText = (String) response.get("0");
                    Intent i = new Intent(context, MyActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                } catch (JSONException e) {
                    Log.d("FYREBUG", "ERROR: SOMETHING WITH JSONObject");
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                Log.d("FYREBUG", "we got here JSONArray");
            }
        });
    }
}
