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
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TextView resultTextView = (TextView) findViewById(R.id.result);
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
                resultTextView.setText(Name + " : " + Number);
            } while (cursor.moveToNext());
            getPublicKey();
        }else if ((requestCode == FILE_PICKER_TO_ENCODE_RESULT) && (resultCode == RESULT_OK)) {
            Log.d("FYREBUG", "THIS IS IT BABY");
            String filePath = data.getData().getPath();
            Log.d("FYREBUG", filePath);
            File file = new File(filePath);
            Encryption.encryptTestFile(file);
            Log.d("FYREBUG", "inc");
        } else if ((requestCode == FILE_PICKER_TO_DECODE_RESULT) && (resultCode == RESULT_OK)) {
            Log.d("FYREBUG", "THIS IS IT BABY");
            String filePath = data.getData().getPath();
            Log.d("FYREBUG", filePath);
            File file = new File(filePath);
            Encryption.decryptTestFile(file);
            Log.d("FYREBUG", "dec");
        }
    }

    private void getPublicKey() {
        String requestString = "/getkey?phone=" + Number;
        Log.d("FYREBUG", requestString);
        VerifyRestClient.get(requestString, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String publicKeyResponse = (String) response.get("0");
                    publicKeyResponse = publicKeyResponse.replace(" ","+").trim();
                    Encryption.setOtherUserPublicKey(publicKeyResponse);
                    openFileExplorer();
                    Log.d("FYREBUG", "RESPONSE FROM SERVER: " + publicKeyResponse);
                    Log.d("FYREBUG", "Encrypted Response From Server: " + Encryption.stringToPublicKey(publicKeyResponse).getEncoded());
                } catch (JSONException e) {
                    Log.d("FYREBUG", "ERROR: SOMETHING WITH JSONObject");
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                Log.d("FYREBUG", "we got here JSONArray");
            }

            @Override
            public void onFailure(int a, Header[] h, Throwable e, JSONObject o) {
                Toast.makeText(getApplicationContext(), (String) "Failed to get the public key.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }


}
