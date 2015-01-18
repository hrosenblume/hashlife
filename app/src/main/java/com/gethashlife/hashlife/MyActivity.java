package com.gethashlife.hashlife;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
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
import java.security.PrivateKey;
import java.security.PublicKey;


public class MyActivity extends Activity {
    private static int CONTACT_PICKER_RESULT = 0;
    private String Name, Number;
    private String publicKeyString, privateKeyString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "test.jpg");
        convertAndSetKeys();

        try {
            registerDevice();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void pickContact(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        startActivityForResult(intent, CONTACT_PICKER_RESULT);
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
                Name = name;
                Number = number;
                resultTextView.setText(Name + " : " + Number);
            } while (cursor.moveToNext());
        }
    }

    public void registerDevice() throws JSONException {
        Toast.makeText(getApplicationContext(), (String) "Currently Registering Device",
                Toast.LENGTH_LONG).show();
        SMS.sendMessage("571-212-5535", "This message will register my device.");

        String requestString = "/register?phone=" + getDeviceTelephoneNumber() + "&" + "pkey=" + "123456"
                + "&" + "code=" + "123456";
        VerifyRestClient.get(requestString, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("FYREBUG", "we got here JSONObject");
                try {
                    String responseText = (String) response.get("0");
                    Log.d("FYREBUG", responseText);
                } catch (Exception e) {
                    Log.d("FYREBUG", "ERROR: SOMETHING WITH JSONObject");
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                Log.d("FYREBUG", "we got here JSONArray");
            }

            @Override
            public void onFailure(int a, Header[] h, Throwable e, JSONObject o) {
                Toast.makeText(getApplicationContext(), (String) "Failed to register with server.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void convertAndSetKeys() {
        Encryption.generateKeys();

        PublicKey publicKeyObject = Encryption.getPublicKey();
        byte[] publicKeyArray = publicKeyObject.getEncoded();
        publicKeyString = new String(publicKeyArray);

        PrivateKey privateKeyObject = Encryption.getPrivateKey();
        byte[] privateKeyArray = privateKeyObject.getEncoded();
        privateKeyString = new String(privateKeyArray);
    }

    public String getDeviceTelephoneNumber() {
        TelephonyManager tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        mPhoneNumber = mPhoneNumber.replaceAll("[^\\d.]", "");
        return mPhoneNumber;
    }
}
