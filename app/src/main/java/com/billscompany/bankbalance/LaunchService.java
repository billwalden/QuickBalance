package com.billscompany.bankbalance;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by walde_000 on 9/6/2015.
 */
public class LaunchService extends BroadcastReceiver {
    static final String bankNumber = "872265";
    static final String textMessage  = "BAL";
    public static final String ACTION_ALARM = "com.billscompany.bankbalance.ACTION_ALARM";

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println(intent.getAction());
        if(this.ACTION_ALARM.equals(intent.getAction())){
            System.out.println(intent.getAction());
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(bankNumber, null, textMessage, null, null);
            Intent serviceIntent = new Intent(context, BackGroundService.class);
            serviceIntent.putExtra("checked", true);
            context.startService(serviceIntent);
        }

        if("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {

            Bundle pdusBundle = intent.getExtras();
            Object[] pdus = (Object[]) pdusBundle.get("pdus");
            SmsMessage messages = SmsMessage.createFromPdu((byte[]) pdus[0]);
            if (messages.getMessageBody().contains("U.S. Bank Text Banking:")) {
                System.out.println(intent.getAction());
                String bal[] = messages.getMessageBody().split("\n");
                DisplayBalance.setSMS(bal[2]);
                String Date[] = bal[1].split("@");
                DisplayBalance.setDate(Date[1]);
                System.out.println(messages.getTimestampMillis());
                DisplayBalance.setSMSTime(messages.getTimestampMillis());
                if(DisplayBalance.isActive()){
                    DisplayBalance.setText();
                }
            }
        }

    }
}
