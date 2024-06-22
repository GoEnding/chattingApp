package com.ksg.chattingapp.utility;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {

    public static void saveLoginState(Context context, boolean isLoggedIn) {
        SharedPreferences sharedPref = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.apply();
    }

    public static boolean getLoginState(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        return sharedPref.getBoolean("isLoggedIn", false);
    }
}