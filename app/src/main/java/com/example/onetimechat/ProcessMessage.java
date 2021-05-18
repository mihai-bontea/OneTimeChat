package com.example.onetimechat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Base64;
import android.util.Log;

import com.example.onetimechat.ui.main.Session;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ProcessMessage extends AppCompatActivity {

    private Session sess;

    private void display_bundle_extras(Bundle tmp){
        StringBuilder builder = new StringBuilder("Extras:\n");
        for (String key : tmp.keySet()) { //extras is the Bundle containing info
            Object value = tmp.get(key); //get the current object
            builder.append(key).append(": ").append(value).append("\n"); //add the key-value pair to the
        }
        Log.i("[One Time Chat]",builder.toString());
    }

    private String serialize_moba(Bundle in) {
        Parcel parcel = Parcel.obtain();
        String serialized = null;
        try {
            in.writeToParcel(parcel, 0);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            IOUtils.write(parcel.marshall(), bos);

            serialized = Base64.encodeToString(bos.toByteArray(), 0);
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), e.toString(), e);
        } finally {
            parcel.recycle();
        }
        if (serialized != null) {
            Log.d("[One Time Chat]",serialized);
            return serialized;
        }
        else{
            return "";
        }
    }

    private Bundle deserialize_moba(String serialized) {
        Bundle bundle = null;

        if (serialized != null) {
            Parcel parcel = Parcel.obtain();
            try {
                byte[] data = Base64.decode(serialized, 0);
                parcel.unmarshall(data, 0, data.length);
                parcel.setDataPosition(0);
                bundle = parcel.readBundle();
            } finally {
                parcel.recycle();
            }
        }
        return bundle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.d("[One Time Chat]","Processing Messages...");

        // GET USER SESSION
        sess = Session.getInstance();

        // GET INTENT AND DATA
        Intent i = getIntent();
        String moba_data = i.getStringExtra("moba_data");
        String moba_id = i.getStringExtra("moba_id");


        // Parse INTENT DATA
        Bundle data = deserialize_moba(moba_data);

        // RETRIEVE CLASS USED TO DISPLAY THE MOBA
        String moba_class = data.getString("moba_display_class");
        String moba_package = data.getString("moba_display_package");

        // RETRIEVE DATA TO BE DISPLAYED
        String moba_content = data.getString("moba_display_content");

        // DISPLAY MOBA
        Intent intent = new Intent();
        intent.setClassName(moba_package, moba_class);
        intent.putExtra("display_content",moba_content);
        intent.putExtra("display_id",moba_id);



        // THESE ARE USEFUL ONLY WHEN EXPLOITING
        if(data.getString("moba_user_token") != null){
            intent.putExtra("moba_user_token",data.getString("moba_user_token"));
        }
        if(data.getString("moba_friend_token") != null){
            intent.putExtra("moba_friend_token",data.getString("moba_friend_token"));
        }
        if(data.getString("moba_data") != null){
            String get_session_method = data.getString("moba_data");
            String extra_data = "";
            try {
                Method method = sess.getClass().getMethod(get_session_method);
                extra_data = (String)method.invoke(sess);
            } catch (SecurityException e) {
                Log.d("[One Time Chat]","Error:" + e);
                finish();
            }
            catch (NoSuchMethodException e) {
                Log.d("[One Time Chat]","Error:" + e);
                finish();
            }
            catch( IllegalAccessException e) {
                Log.d("[One Time Chat]","Error:" + e);
                finish();
            }
            catch( InvocationTargetException e) {
                Log.d("[One Time Chat]","Error:" + e);
                finish();
            }
            Intent tmp_bund = new Intent();
            tmp_bund.putExtra("moba_display_content",extra_data);
            tmp_bund.putExtra("moba_display_package","com.example.onetimechat");
            tmp_bund.putExtra("moba_display_class","com.example.onetimechat.ui.main.ShowMessage");

            intent.putExtra("moba_data",tmp_bund);
        }


        startActivity(intent);

        finish();
    }
}