package cesi.com.tchatapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import cesi.com.tchatapp.helper.JsonParser;
import cesi.com.tchatapp.helper.NetworkHelper;
import cesi.com.tchatapp.model.HttpResult;
import cesi.com.tchatapp.utils.Constants;

/**
 * Created by sca on 02/06/15.
 */
public class SigninActivity extends Activity {

    EditText username;
    EditText pwd;
    ProgressBar pg;
    Button btn;
    View v ;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_signin);
        v = findViewById(R.id.layout);
        username = (EditText) findViewById(R.id.signin_username);
        pwd = (EditText) findViewById(R.id.signin_pwd);
        pg = (ProgressBar) findViewById(R.id.signin_pg);
        btn = (Button) findViewById(R.id.signin_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading(true);
                new SigninAsyncTask(v.getContext()).execute(username.getText().toString(), pwd.getText().toString());
            }
        });
        findViewById(R.id.signin_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), SignupActivity.class);
                startActivity(i);
            }
        });

    }

    private void loading(boolean loading) {
        if(loading){
            pg.setVisibility(View.VISIBLE);
            btn.setVisibility(View.INVISIBLE);
        } else {
            pg.setVisibility(View.INVISIBLE);
            btn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * AsyncTask for sign-in
     */
    protected class SigninAsyncTask extends AsyncTask<String, Void, String>{

        Context context;

        public SigninAsyncTask(final Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            if(!NetworkHelper.isInternetAvailable(context)){
                return null;
            }

            try {

                Map<String, String> p = new HashMap<>();
                p.put("username", params[0]);
                p.put("pwd", params[1]);

                HttpResult result = NetworkHelper.doPost(context.getString(R.string.url_signin), p, null);

                if(result.code == 200) {
                    // Convert the InputStream into a string
                    return JsonParser.getToken(result.json);
                }
                return null;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } catch (Exception e) {
                Log.e("NetworkHelper", e.getMessage());
                return null;
            }
        }

        @Override
        public void onPostExecute(final String token){
            loading(false);
            if(token != null){
                Intent i = new Intent(context, TchatActivity.class);
                i.putExtra(Constants.INTENT_TOKEN, token);
                startActivity(i);
            } else {
                Toast.makeText(SigninActivity.this, "Error login", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
