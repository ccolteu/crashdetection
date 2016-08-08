package com.randmcnally.crashdetection.apis;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.randmcnally.crashdetection.utils.Utils;

public class CNServiceApi {

    private static final String TAG = CNServiceApi.class.getSimpleName();

    private static Context mContext;
    private static Messenger messenger = null;
    private static boolean isBound;
    private ResponseHandler responseHandler = new ResponseHandler();
    private static CNServiceListener mCNServiceListener;

    public interface CNServiceListener {
        void onConnected();
        void onFailedToConnect();
        void onReceive(Bundle data);
    }

    /*
    Public methods
     */
    public boolean init(Context ctx, CNServiceListener listener) {
        mContext = ctx;
        mCNServiceListener = listener;
        return bindToRemoteService();
    }

    public void terminate() {
        if (mContext != null && serviceConnection != null && isBound) {
            mContext.unbindService(serviceConnection);
        }
        isBound = false;
    }

    public void startZendrive() {
        Bundle data = new Bundle();
        data.putString("command", "start_zendrive");
        send(data);
    }

    public void stopZendrive() {
        Bundle data = new Bundle();
        data.putString("command", "stop_zendrive");
        send(data);
    }

    public void mockAccident() {
        Bundle data = new Bundle();
        data.putString("command", "mock_accident");
        send(data);
    }

    public void send(Bundle data) {
        sendMessage(data);
    }

    /*
    Private methods
     */
    private static boolean bindToRemoteService() {
        Intent implicitIntent = new Intent("com.randmcnally.CNService");
        Intent explicitIntent = Utils.createExplicitFromImplicitIntent(mContext, implicitIntent);
        try {
            boolean ret = mContext.bindService(explicitIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            Log.d(TAG, "bindService succeeded: " + ret);
            return ret;
        } catch (Exception e) {
            Log.e(TAG, "bindService EXCEPTION: " + e.toString());
            e.printStackTrace();
            return false;
        }
    }

    private static ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            messenger = new Messenger(service);
            isBound = true;

            if (mCNServiceListener != null) {
                mCNServiceListener.onConnected();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            messenger = null;
            isBound = false;
            if (mCNServiceListener != null) {
                mCNServiceListener.onFailedToConnect();
            }
            Log.e(TAG, "remote service failed to bind");
        }
    };

    /*
    sends data via a Message to the remote service
     */
    private void sendMessage(Bundle data) {
        if (!isBound) {
            Log.e(TAG, "send message to remote service failed, service not bound");
            return;
        }

        Message msg = Message.obtain();

        // set the response handler
        msg.replyTo = new Messenger(responseHandler);

        // set data
        if (data != null) {
            msg.setData(data);
        }

        // send message to remote service
        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /*
    handles all responses from the remote service
     */
    private class ResponseHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            if (mCNServiceListener != null) {
                mCNServiceListener.onReceive(data);
            }
        }
    }

}
