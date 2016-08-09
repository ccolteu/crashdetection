package com.randmcnally.crashdetection.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.randmcnally.crashdetection.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        //Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        //Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        //Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        //Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        //Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }

    public static Contact getGoogleDeviceContact(Context context, String name) {
        Contact contact = new Contact();
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like'%" + name + "%'";
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        };
        Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, selection, null, null);
        if (c != null && c.moveToFirst()) {
            contact.phone = c.getString(0);
            contact.name = c.getString(1);
            c.close();
        }
        if (contact.phone == null) {
            contact.phone = "Unknown";
        }
        if (contact.name == null) {
            contact.name = "Unknown";
        }
        Log.d("toto", contact.phone + " | " + contact.name);
        return contact;
    }

    public static ArrayList<Contact> getGoogleDeviceContacts(Context context) {
        ArrayList<Contact> ret = null;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        };
        Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, null, null, null);
        if (c != null) {
            ret = new ArrayList<>();
            while(c.moveToNext()) {
                Contact contact = new Contact();
                contact.phone = c.getString(0);
                contact.name = c.getString(1);
                ret.add(contact);
            }
            c.close();
        }
        return ret;
    }
}
