package com.gethashlife.hashlife;

import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;

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
}