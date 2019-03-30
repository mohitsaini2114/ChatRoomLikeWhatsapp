

package com.example.vaibhav.login_inclass6;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    AlertDialog.Builder builder = null;
    private FirebaseAuth mAuth;
    LoginDetails LOGIN_DETAILS;
    AlertDialog dialog = null;
    EditText email;
    EditText password;
    Button login;
    Button signup;
    String eml;
    String pass;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth =  FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null && !mAuth.getCurrentUser().getDisplayName().isEmpty())
        {
            String[] userNames = mAuth.getCurrentUser().getDisplayName().split(" ");
            LOGIN_DETAILS = new LoginDetails(userNames[0],userNames[1],mAuth.getCurrentUser().getEmail(),"");
            goToChats();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Chat Room");
        email = (EditText) findViewById(R.id.login_editText);
        password = (EditText) findViewById(R.id.password_editText);
        login = (Button) findViewById(R.id.login_button);

        builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setTitle("Logging........").setView(inflater.inflate(R.layout.activity_progress_loader, null)).setCancelable(false);
        dialog = builder.create();

        ((Button) findViewById(R.id.signup_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TestConnection.isConnected(getSystemService(Context.CONNECTIVITY_SERVICE))) {
                    email.setText("");
                    password.setText("");
                    Intent intent = new Intent(IntentKeyAndCodes.SIGNUP_ACTIVITY);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_LONG).show();
                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TestConnection.isConnected(getSystemService(Context.CONNECTIVITY_SERVICE))) {


                    boolean isValid = true;

                    dialog.show();

                    eml = email.getText().toString();
                    Log.d("Demo", "email" + eml);
                    pass = password.getText().toString();

                    email.setText("");
                    password.setText("");

                    if (eml == null || eml.equals("") || eml.trim().equals("")) {
                        email.setError("Enter Email");
                        isValid = false;
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(eml).matches()) {
                        email.setError("Enter Valid Email ");
                        isValid = false;
                    } else if (pass == null || pass.equals("") || pass.trim().equals("")) {
                        password.setError("Enter Password");
                        isValid = false;
                    } else if (pass == null || pass.equals("") || pass.trim().equals("")) {
                        password.setError("Enter Password");
                        isValid = false;
                    }
                    if (isValid) {

                        mAuth =  FirebaseAuth.getInstance();

                        mAuth.signInWithEmailAndPassword(eml,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(getApplicationContext(),"Logged In successfully",Toast.LENGTH_LONG).show();

                                String[] user = mAuth.getCurrentUser().getDisplayName().split(" ");
                                LOGIN_DETAILS = new LoginDetails(user[0],user[1],mAuth.getCurrentUser().getEmail(),"");
                                goToChats();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("error",e.getMessage());
                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_LONG).show();

                }
            }
        });


    }

    public void goToChats()
    {
        Intent intent = new Intent(MainActivity.this,MessageViewActivity.class);
        intent.putExtra(IntentKeyAndCodes.THREAD_TO_MESSAGR_KEY, LOGIN_DETAILS);
        startActivity(intent);
        finish();
    }
}
