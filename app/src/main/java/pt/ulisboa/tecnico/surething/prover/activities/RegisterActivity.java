package pt.ulisboa.tecnico.surething.prover.activities;

import androidx.appcompat.app.AppCompatActivity;
import pt.ulisboa.tecnico.surething.prover.R;
import pt.ulisboa.tecnico.surething.prover.classes.UserSureThing;
import pt.ulisboa.tecnico.surething.prover.utils.Constants;
import pt.ulisboa.tecnico.surething.prover.utils.SaveSharedPreference;
import pt.ulisboa.tecnico.surething.prover.utils.api;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button btn_register = findViewById(R.id.btn_register);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText name = findViewById(R.id.register_editTextName);
                EditText password = findViewById(R.id.register_editTextPass);


                /*SureThingEntities.UserSureThingProto user = SureThingEntities.UserSureThingProto.newBuilder()
                        .setUsername(name.getText().toString())
                        .setPassword(password.getText().toString())
                        .build();

                RegisterTask task = new RegisterTask();
                task.execute(user);*/
            }
        });
    }


    /*public class RegisterTask extends AsyncTask<SureThingEntities.UserSureThingProto, Void, SureThingEntities.UserSureThingProto> {
        @Override
        protected SureThingEntities.UserSureThingProto doInBackground(SureThingEntities.UserSureThingProto... user) {
            byte[] bytes;
            try {
                Log.d("atoa", Constants.REGISTER);
                bytes = api.postRequest(Constants.REGISTER, user[0].toByteArray());
                SureThingEntities.UserSureThingProto userProto = SureThingEntities.UserSureThingProto.parseFrom(bytes);
                return userProto;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(SureThingEntities.UserSureThingProto user) {

            if(user.getUsername().equals("") && user.getPassword().equals("") && user.getId() == 0 && user.getToken().equals("")){
                TextView textView = findViewById(R.id.textView_errorRegister);
                textView.setText("Username already in use");
            }
            else{
                SaveSharedPreference.setPrefUser(RegisterActivity.this, user.getUsername(), user.getToken());
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }*/
}