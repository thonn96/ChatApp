package com.example.appchat.base;

import android.app.Application;

import com.quickblox.auth.session.QBSettings;

import static com.quickblox.core.account.Consts.ACCOUNT_KEY;

public class BaseApplication extends Application {

    static final String APP_ID ="81656";
    static final String AUTH_KEY ="aBSyT5gqfuUVpcG";
    static final String AUTH_SECRET ="VuRzSts8De4W9n7";
    static final String ACCOUNT_KEY ="xGvq6AHMYnxb_XTuT_FS";



    @Override
    public void onCreate() {
        super.onCreate();
        initalizeFramwork();
    }

    private void initalizeFramwork() {
        QBSettings.getInstance().init(getApplicationContext(),APP_ID,AUTH_KEY,AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);

    }
}
