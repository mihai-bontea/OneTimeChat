package com.example.onetimechat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    // UPDATE STATUS
    public void updateTextView(String data) {
        TextView textView = (TextView) findViewById(R.id.textView9);
        textView.setText(data);
        if(data.equals("Success!")){
            textView.setTextColor(getResources().getColor(R.color.themeGreen));
        }
        else{
            textView.setTextColor(getResources().getColor(R.color.moba_read));
        }
    }

    int do_register(){

        // GET USERNAME FROM INPUT
        EditText editText1 = (EditText) findViewById(R.id.register_form_username);
        String new_acc_username = editText1.getText().toString();

        // GET PASSWORD FROM INPUT
        EditText editText2 = (EditText) findViewById(R.id.register_form_password);
        String new_acc_password = editText2.getText().toString();

        // GET EMAIL FROM INPUT
        EditText editText3 = (EditText) findViewById(R.id.register_form_email);
        String new_acc_email = editText3.getText().toString();

        // ESTABLISH HTTP CONNECTION
        OkHttpClient client = new OkHttpClient();
        String url = "http://167.71.55.68:8001/api.php";

        // PARAMETERS
        RequestBody registration_params = new FormBody.Builder()
                .add("q","UXM5qPUMHaaU5jN")
                .add("username",new_acc_username)
                .add("email",new_acc_email)
                .add("password",new_acc_password)
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
                    RegisterActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("[One Time Chat]", myResponse);
                            if(myResponse.equals("Failed to connect to MySQL: ") ||
                                myResponse.equals("Username already exists.") ||
                                myResponse.equals("Invalid Request.")){
                                updateTextView("ERROR!"); // UPDATE ACTION STATUS
                            }
                            else{
                                Log.d("[One Time Chat]","Registering Succeeded.");
                                updateTextView("Success!"); // UPDATE ACTION STATUS
                            }
                        }
                    });
                }
                else{
                    Log.d("[One Time Chat]","Registering Failed.");
                }
            }
        });

        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // NO ERRORS BY DEFAULT
        updateTextView("");

        // HIT REGISTER BUTTON
        Button submit_button = (Button)findViewById(R.id.registration_submit);
        submit_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Log.d("[One Time Chat]","Registering...");
                do_register();
            }

        });

    }
}