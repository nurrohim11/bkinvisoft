package co.id.gmedia.coremodul;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SessionManager {
    public static final String SP_APP = "InvisoftApp";

    public static final String SP_ID = "spId";
    public static final String SP_NAMA = "spNama";
    public static final String SP_NO_TELP = "spNotelp";
    public static final String SP_LEVEL = "spLevel";
    public static final String SP_USERNAME = "spUsername";

    public static final String SP_SUDAH_LOGIN = "spSudahLogin";

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    public SessionManager(Context context){
        sp = context.getSharedPreferences(SP_APP, Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public void saveSPString(String keySP, String value){
        spEditor.putString(keySP, value);
        spEditor.commit();
    }

    public void saveSPInt(String keySP, int value){
        spEditor.putInt(keySP, value);
        spEditor.commit();
    }

    public void saveSPBoolean(String keySP, boolean value){
        spEditor.putBoolean(keySP, value);
        spEditor.commit();
    }

    public String getSPNama(){
        return sp.getString(SP_NAMA, "");
    }

    public String getSpId(){
        return sp.getString(SP_ID, "");
    }

    public String getSpNoTelp(){
        return sp.getString(SP_NO_TELP, "");
    }

    public String getSpLevel(){
        return sp.getString(SP_LEVEL, "");
    }

    public String getSp_username(){
        return sp.getString(SP_USERNAME, "");
    }

    public Boolean getSPSudahLogin(){
        return sp.getBoolean(SP_SUDAH_LOGIN, false);
    }
}
