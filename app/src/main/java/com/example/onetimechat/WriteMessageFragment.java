package com.example.onetimechat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.onetimechat.ui.main.Session;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WriteMessageFragment extends Fragment {

    private Session sess;
    private View resources;
    private Context accessible;

    // UPDATE STATUS
    public void updateTextView(String data) {
        TextView textView = (TextView) resources.findViewById(R.id.write_moba_status);
        textView.setText(data);
        if(data.equals("Success!")){
            textView.setTextColor(getResources().getColor(R.color.themeGreen));
        }
        else{
            textView.setTextColor(getResources().getColor(R.color.moba_read));
        }
    }

    int do_send_moba(){

        // ESTABLISH HTTP CONNECTION
        OkHttpClient client = new OkHttpClient();
        String url = "http://167.71.55.68:8001/api.php";

        // GET FRIEND USERNAME FROM INPUT
        EditText editText1 = (EditText) resources.findViewById(R.id.editTextTextPersonName4);
        String friend_username = editText1.getText().toString();

        // GET DATA FROM INPUT
        EditText editText2 = (EditText) resources.findViewById(R.id.editTextTextPersonName3);
        final String data = editText2.getText().toString();

        // PARAMETERS
        RequestBody registration_params = new FormBody.Builder()
                .add("q","KVY2ERbWMEGBgob")
                .add("token",sess.getToken())
                .add("friend_username",friend_username)
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("[One Time Chat]", myResponse);
                            if(myResponse.equals("Failed to connect to MySQL: ") ||
                                    myResponse.equals("Invalid Token.") ||
                                    myResponse.equals("Invalid Username.") ||
                                    myResponse.equals("Not a friend.") ||
                                    myResponse.equals("Invalid Request.")){
                                Log.d("[One Time Chat]","ERROR!");
                                updateTextView("Error!");
                            }
                            else{
                                Log.d("[One Time Chat]","Success!");

                                // NOW THAT I HAVE FRIEND TOKEN, I MUST LAUNCH SENDMOBA ACTIVITY
                                Intent intent_data = new Intent();
                                intent_data.putExtra("moba_display_content",data);
                                intent_data.putExtra("moba_display_package","com.example.onetimechat");
                                intent_data.putExtra("moba_display_class","com.example.onetimechat.ui.main.ShowMessage");

                                Intent i = new Intent(accessible, SendMessage.class);
                                i.putExtra("moba_user_token",sess.getToken());
                                i.putExtra("moba_friend_token",myResponse);
                                i.putExtra("moba_data",intent_data);
                                startActivity(i);

                                updateTextView("Success!");
                            }
                        }
                    });
                }
                else{
                    Log.d("[One Time Chat]","Seding Message Failed!");
                    updateTextView("Error!");
                }
            }
        });

        return 0;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // RESOLVE RESOURCES
        resources = inflater.inflate(R.layout.write_message_layout, container, false);
        accessible = this.getContext();

        // NO ERRORS BY DEFAULT
        updateTextView("");

        // HIT WRITE MOBA BUTTON
        Button submit_button = (Button)resources.findViewById(R.id.button);
        submit_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                do_send_moba();
            }

        });

        return resources;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("[One Time Chat]:","Writing Message...");

        // GET USER SESSION
        sess = Session.getInstance();
    }
}
