package com.billscompany.bankbalance;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static com.billscompany.bankbalance.R.*;

public class DisplayBalance extends AppCompatActivity {
    static final String bankNumber = "872265";
    static final String textMessage  = "BAL";
    private static String SMS;
    private static String date;
    private static TextView  textView2;
    private static TextView textView1;
    private static CheckBox checkBox;
    private static boolean active;
    private static volatile long SMSTime;

    public static void setSMSTime(long SMSTime) {
        DisplayBalance.SMSTime = SMSTime;
    }

    public static boolean isActive() {
        return active;
    }

    public static void setDate(String date) {
        DisplayBalance.date = date;
    }
    public static void setSMS(String SMS) {
        DisplayBalance.SMS = SMS;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SMSTime = 0L;
        setContentView(layout.activity_display_balance);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView1 = (TextView) findViewById(R.id.textView);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        if(BackGroundService.getActive()==null)
            checkBox.setChecked(false);
        else if(BackGroundService.getActive().equals("active"))
            checkBox.setChecked(true);
        System.out.println(checkBox.isChecked() + "!!!!!!!!!!!!!!!!!!!!!!!");
        StringBuilder smsBuilder = new StringBuilder();
        final String SMS_URI_INBOX = "content://sms/inbox";
        final String SMS_URI_ALL = "content://sms/";

        try {
            Uri uri = Uri.parse(SMS_URI_INBOX);
            String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" };
            Cursor cur = getContentResolver().query(uri, projection, "address='872265'", null, "date desc");
            if (cur.moveToFirst()) {
                int index_Address = cur.getColumnIndex("address");
                int index_Person = cur.getColumnIndex("person");
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                int index_Type = cur.getColumnIndex("type");
                String strAddress = "";
                String strbody = "";
                boolean notEmpty = true;
                while (notEmpty && !strAddress.equals(bankNumber)&& !strbody.contains("$")) {
                    strAddress = cur.getString(index_Address);
                    int intPerson = cur.getInt(index_Person);
                    strbody = cur.getString(index_Body);
                    long longDate = cur.getLong(index_Date);
                    int int_Type = cur.getInt(index_Type);

                    smsBuilder.append(strbody + ", ");
                    smsBuilder.append(longDate + ", ");
                    notEmpty = cur.moveToNext();
                }

                if (!cur.isClosed()) {
                    cur.close();
                    cur = null;
                }
            } else {
                smsBuilder.append("no result!");
            } // end if
        }
        catch (SQLiteException ex) {
            Log.d("SQLiteException", ex.getMessage());
        }
        if(!smsBuilder.toString().equals("")) {
            String bal[] = smsBuilder.toString().split("\n");
            SMS = bal[2];
            String Date[] = bal[1].split("@");
            date = Date[1];
            textView1.setText(SMS);
            textView2.setText(date);
        }

    }
    @Override
    protected void onStart(){
        super.onStart();
        active = true;
    }
    @Override
    protected void onStop() {
        super.onStop();
        active = false;
    }

    public void sendText(View view){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(bankNumber, null, textMessage, null, null);
        long current = SMSTime;

    }
    public void boxChecked(View view){
        Intent serviceIntent = new Intent(this, BackGroundService.class);
        if(checkBox.isChecked()) {
            serviceIntent.putExtra("checked", true);
            startService(serviceIntent);
        }
        else {
            serviceIntent.putExtra("checked", false);
            startService(serviceIntent);
        }
    }
    public static void setText(){
        textView1.setText(SMS);
        textView2.setText(date);
    }
    public class loadingBalance implements Runnable {
        @Override
        public void run() {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_balance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
