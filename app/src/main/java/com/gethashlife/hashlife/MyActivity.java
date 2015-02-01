package com.gethashlife.hashlife;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


public class MyActivity extends Activity {
    private static int CONTACT_PICKER_RESULT = 0;
    private static final int FILE_PICKER_TO_ENCODE_RESULT = 1;
    private static final int FILE_PICKER_TO_DECODE_RESULT = 2;
    private String Name, Number;
    private String publicKeyString, privateKeyString;
    private boolean isRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
    }

    public void pickContact(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        startActivityForResult(intent, CONTACT_PICKER_RESULT);
    }

    public void openFileExplorer(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivityForResult(intent,FILE_PICKER_TO_DECODE_RESULT);
    }

    private void openFileExplorer() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivityForResult(intent,FILE_PICKER_TO_ENCODE_RESULT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == CONTACT_PICKER_RESULT) && (resultCode == RESULT_OK)) {
            Uri uri = data.getData();
            ContentResolver cr = getContentResolver();
            String[] projection = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor cursor = cr.query(uri, projection, null, null, null);
            cursor.moveToFirst();
            do {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                number = number.replaceAll("[^\\d.]", "");
                Name = name;
                Number = number;
            } while (cursor.moveToNext());
            getPublicKey();
        }else if ((requestCode == FILE_PICKER_TO_ENCODE_RESULT) && (resultCode == RESULT_OK)) {
            String filePath = data.getData().getPath();
            File file = new File(filePath);
            Encryption.encryptTestFile(file);
        } else if ((requestCode == FILE_PICKER_TO_DECODE_RESULT) && (resultCode == RESULT_OK)) {
            String filePath = data.getData().getPath();
            File file = new File(filePath);
            Encryption.decryptTestFile(file);
            try {
                FileOpen.openFile(getApplicationContext(), file);
            } catch (Exception e) {

            }

        }
    }

    private void getPublicKey() {
        String requestString = "/getkey?phone=" + Number;
        VerifyRestClient.get(requestString, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String publicKeyResponse = (String) response.get("0");
                    publicKeyResponse = publicKeyResponse.replace(" ","+").trim();
                    Encryption.setOtherUserPublicKey(publicKeyResponse);
                    openFileExplorer();
                } catch (JSONException e) {
                    Log.d("FYREBUG", "ERROR: SOMETHING WITH JSONObject");
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                Log.d("FYREBUG", "we hit success");
            }

            @Override
            public void onFailure(int a, Header[] h, Throwable e, JSONObject o) {
                Toast.makeText(getApplicationContext(), (String) "Failed to get the public key.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }


}
