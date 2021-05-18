package com.example.onetimechat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.onetimechat.ui.main.Session;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private Session sess;

    public void updateTextView(String data) {
        TextView textView = (TextView) findViewById(R.id.textView13);
        textView.setText(data);
    }

    int do_login(){

        // GET USERNAME FROM INPUT
        EditText editText1 = (EditText) findViewById(R.id.login_form_username);
        String acc_username = editText1.getText().toString();

        // GET PASSWORD FROM INPUT
        EditText editText2 = (EditText) findViewById(R.id.login_form_password);
        String acc_password = editText2.getText().toString();

        // ESTABLISH HTTP CONNECTION
        OkHttpClient client = new OkHttpClient();
        String url = "http://167.71.55.68:8001/api.php";

        // PARAMETERS
        RequestBody registration_params = new FormBody.Builder()
                .add("q","YOZ8AxBEUCgZEPG")
                .add("username",acc_username)
                .add("password",acc_password)
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
                Log.d("[One Time Chat]","Error:"+e.toString());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException { // REQUEST SUCCEED
                if(response.isSuccessful()){
                    final String myResponse = response.body().string();
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("[One Time Chat]", myResponse);
                            if(myResponse.equals("Failed to connect to MySQL: ") ||
                                    myResponse.equals("Login Failed") ||
                                    myResponse.equals("Invalid Credentials.")){
                                updateTextView("ERROR!");
                                Log.d("[One Time Chat]","Login Failed.");
                            }
                            else{
                                Log.d("[One Time Chat]","Login Succeeded.");
                                updateTextView("");

                                // SET USER TOKEN
                                sess.setToken(myResponse);

                                // REDIRECT TO HOME SCREEN
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
                else{
                    Log.d("[One Time Chat]","Login Failed.");
                }
            }
        });

        return 0;
    }

    // HANDLE CLICK ON "CREATE AN ACCOUNT"
    public void onClick(View v){
        Intent intent = null;
        switch(v.getId()){
            case R.id.textView14:
                intent = new Intent(this,RegisterActivity.class);
                break;
        }
        if (null!=intent) startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // NO ERRORS BY DEFAULT
        updateTextView("");

        // GET USER SESSION
        sess = Session.getInstance();

        // HIT LOGIN BUTTON
        Button submit_button = (Button)findViewById(R.id.login_submit);
        submit_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Log.d("[One Time Chat]","Logging in...");
                do_login();
            }

        });
    }
}