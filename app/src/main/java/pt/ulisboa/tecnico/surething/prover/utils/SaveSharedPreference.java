package pt.ulisboa.tecnico.surething.prover.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {

    static final String PREF_USER_NAME = "username";
    static final String PREF_TOKEN = null;

    static SharedPreferences getSharedPreferences(Context ctx){
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setPrefUser(Context ctx, String username, String token){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, username);
        editor.putString(PREF_TOKEN, token);
        editor.commit();
    }

    public static void setPrefToken(Context ctx, String token){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_TOKEN, token);
        editor.commit();
    }

    public static String getPrefUserName(Context ctx){
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

    public static String getPrefToken(Context ctx){
        return getSharedPreferences(ctx).getString(PREF_TOKEN, "");
    }
}
