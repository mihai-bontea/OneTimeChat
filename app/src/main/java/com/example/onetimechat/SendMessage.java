package com.example.onetimechat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Base64;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendMessage extends AppCompatActivity {

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

    int do_send_moba(String user_token, String friend_token, Intent intent_data){

        // ESTABLISH HTTP CONNECTION
        OkHttpClient client = new OkHttpClient();
        String url = "http://167.71.55.68:8001/api.php";
        Bundle data = intent_data.getExtras();
        display_bundle_extras(data);
        String moba_data = serialize_moba(data);

        // PARAMETERS
        RequestBody registration_params = new FormBody.Builder()
                .add("q","6xP0R1sioF5knfv")
                .add("my_token",user_token)
                .add("friend_token",friend_token)
                .add("data",moba_data)
                .build();

        // REQUEST STRUCTURE
        Request register_request = new Request.Builder()
                .url(url)
                .post(registration_params)
                .build();

        // PERFORM REQUEST
        client.newCall(register_request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { // REQUEST FAIL
                Log.d("One Time Chat","Error:"+e.toString());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException { // REQUEST SUCCEED
                if(response.isSuccessful()){
                    final String myResponse = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("[One Time Chat]", myResponse);
                            if(myResponse.equals("Failed to connect to MySQL: ") ||
                                    myResponse.equals("Invalid Token.") ||
                                    myResponse.equals("Invalid Username.") ||
                                    myResponse.equals("Not a friend.") ||
                                    myResponse.equals("Invalid Request.")){
                                Log.d("[One Time Chat]","ERROR!");
                            }
                            else{
                                Log.d("[One Time Chat]","Success!");
                            }
                        }
                    });
                }
                else{
                    Log.d("[One Time Chat]","Seding Message Failed!");
                }
            }
        });

        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.d("[One Time Chat]","Sending Message...");

        // GET INTENT
        Bundle b = getIntent().getExtras();
        String user_token = b.getString("moba_user_token");
        String friend_token = b.getString("moba_friend_token");
        Intent intent_data = (Intent)b.getParcelable("moba_data");

        do_send_moba(user_token, friend_token, intent_data);

        finish();
    }
}