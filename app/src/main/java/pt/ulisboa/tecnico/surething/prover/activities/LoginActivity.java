package pt.ulisboa.tecnico.surething.prover.activities;

import androidx.appcompat.app.AppCompatActivity;
import pt.ulisboa.tecnico.surething.prover.R;
import pt.ulisboa.tecnico.surething.prover.classes.UserSureThing;
import pt.ulisboa.tecnico.surething.prover.utils.Constants;
import pt.ulisboa.tecnico.surething.prover.utils.SSL;
import pt.ulisboa.tecnico.surething.prover.utils.SaveSharedPreference;
import pt.ulisboa.tecnico.surething.prover.utils.api;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            SSL.init(this);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "error initiating ssl", Toast.LENGTH_LONG).show();
        }

        Button button_registerHere = findViewById(R.id.btn_registerHere);

        button_registerHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        Button button_login = findViewById(R.id.btn_login);
        /*button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText name = findViewById(R.id.editTextNameLogin);
                EditText password = findViewById(R.id.editTextPasswordLogin);

                /*JSONObject user = new JSONObject();
                try {
                    user.put("id", null);
                    user.put("username", name.getText().toString());
                    user.put("password", name.getText().toString());
                    user.put("token", null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SureThingEntities.UserSureThingProto user = SureThingEntities.UserSureThingProto.newBuilder()
                        .setUsername(name.getText().toString())
                        .setPassword(password.getText().toString())
                        .build();
                LoginTask task = new LoginTask();
                task.execute(user);
            }
        });
    }

    public class LoginTask extends AsyncTask<SureThingEntities.UserSureThingProto, Void, SureThingEntities.UserSureThingProto> {
        @Override
        protected SureThingEntities.UserSureThingProto doInBackground(SureThingEntities.UserSureThingProto... user) {
            byte[] bytes;
            try {
                Log.d("atoa", Constants.LOGIN);
                bytes = api.postRequest(Constants.LOGIN, user[0].toByteArray());
                SureThingEntities.UserSureThingProto userProto = SureThingEntities.UserSureThingProto.parseFrom(bytes);
                return userProto;
            } catch (IOException e) {
                e.printStackTrace();
            }


            /*JSONObject userJson;
            try {
                userJson = new JSONObject(loginResponse);
                return new UserSureThing(((Number) userJson.get("id")).longValue(), (String) userJson.get("username"), (String) userJson.get("password"), (String) userJson.get("token"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
            //return null;
        }

        @Override
        protected void onPostExecute(SureThingEntities.UserSureThingProto user) {
            if(user.getUsername().equals("") && user.getPassword().equals("") && user.getId() == 0 && user.getToken().equals("")){
                TextView textView = findViewById(R.id.textView_errorLogin);
                textView.setText("Your credentials are incorrect");
            }
            else{
                SaveSharedPreference.setPrefUser(LoginActivity.this, user.getUsername(), user.getToken());
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }*/
    }
}