package com.example.onetimechat;

import android.content.Context;
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

public class AddFriendFragment extends Fragment {

    private Session sess;
    private View resources;
    private Context accessible;

    // UPDATE STATUS
    public void updateTextView(String data) {
        TextView textView = (TextView) resources.findViewById(R.id.add_friend_status);
        textView.setText(data);
        if(data.equals("Success!")){
            textView.setTextColor(getResources().getColor(R.color.themeGreen));
        }
        else{
            textView.setTextColor(getResources().getColor(R.color.moba_read));
        }
    }

    int do_add_friend(){

        // ESTABLISH HTTP CONNECTION
        OkHttpClient client = new OkHttpClient();
        String url = "http://167.71.55.68:8001/api.php";

        // GET FRIEND USERNAME FROM INPUT
        EditText editText1 = (EditText) resources.findViewById(R.id.editTextTextPersonName4);
        final String friend_username = editText1.getText().toString();

        // PARAMETERS
        RequestBody registration_params = new FormBody.Builder()
                .add("q","j0y2vm32GH6cfiP")
                .add("token",sess.getToken())
                .add("username",friend_username)
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
                            if(friend_username.equals("Admin")){
                                updateTextView("Cannot add Admin!");
                                Log.d("[One Time Chat]", myResponse);
                            }
                            else if(myResponse.equals("Failed to connect to MySQL: ") ||
                                    myResponse.equals("Invalid Token.") ||
                                    myResponse.equals("Invalid Username.") ||
                                    myResponse.equals("Invalid Operation.") ||
                                    myResponse.equals("Friend Already Exists")){
                                Log.d("[One Time Chat]","ERROR!");
                                updateTextView("Error!");
                            }
                            else{
                                Log.d("[One Time Chat]","Friend Added with Success!");
                                updateTextView("Success!");
                            }
                        }
                    });
                }
                else{
                    Log.d("[One Time Chat]","Adding Friend Failed!");
                }
            }
        });

        return 0;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // RESOLVE RESOURCES
        resources = inflater.inflate(R.layout.add_friend_layout, container, false);
        accessible = this.getContext();

        // NO ERRORS BY DEFAULT
        updateTextView("");

        // HIT ADD FRIEND BUTTON
        Button submit_button = (Button)resources.findViewById(R.id.button2);
        submit_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Log.d("[App]","Adding Friend...");
                do_add_friend();
            }

        });

        return resources;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // GET USER SESSION
        sess = Session.getInstance();
    }


}
