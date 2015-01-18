package com.gethashlife.hashlife;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

/**
 * Created by hunter on 1/17/15.
 */
public class SMS {
    public Intent getMessageIntent(String recipient, String body) {
        recipient = recipient.replaceAll("[^\\d.]", "");
        Uri uri = Uri.parse("smsto:" + recipient);
        Intent sendMessageIntent = new Intent(Intent.ACTION_SENDTO, uri);
        sendMessageIntent.putExtra("sms_body", "THE SMS BODY");
        //remember that anything returned must be started with startActivity
        return sendMessageIntent;
    }

    public static void sendMessage(String recipient, String body) {
        recipient = recipient.replaceAll("[^\\d.]", "");
        SmsManager smsMgr = SmsManager.getDefault();
        smsMgr.sendTextMessage(recipient, null, body, null, null);
    }

    public static String getDeviceTelephoneNumber(Context context) {
        TelephonyManager tMgr =(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        mPhoneNumber = mPhoneNumber.replaceAll("[^\\d.]", "");
        return mPhoneNumber;
    }
}