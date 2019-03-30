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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SignUp extends AppCompatActivity {

    FirebaseStorage storage;
    StorageReference storageReference;
    private FirebaseAuth mAuth;
    OkHttpClient client = new OkHttpClient();
    AlertDialog.Builder builder = null;
    AlertDialog dialog = null;
    LoginDetails loginDetails = null;
    String SIGN_UP_API = "http://ec2-18-234-222-229.compute-1.amazonaws.com/api/signup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setTitle("Creating Account").setView(inflater.inflate(R.layout.activity_progress_loader, null)).setCancelable(false);
        dialog = builder.create();

        try {
            if (TestConnection.isConnected(getSystemService(Context.CONNECTIVITY_SERVICE))) {
                ((Button) findViewById(R.id.cancel_button)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendToChatWindowAndExist(IntentKeyAndCodes.LOGIN_ACTIVITY, null);
                    }
                });
                ((Button) findViewById(R.id.sign_up_button)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            dialog.show();
                            final EditText firstName = ((EditText) findViewById(R.id.first_name_editText));
                            final EditText lastname = ((EditText) findViewById(R.id.last_name_editText));
                            final EditText email = ((EditText) findViewById(R.id.email_id_et));
                            final EditText password = ((EditText) findViewById(R.id.password_et));
                            EditText rePassword = ((EditText) findViewById(R.id.repassword_et));

                            if (v.getId() == R.id.sign_up_button) {
                                if (validateInputs(firstName, lastname, email, password, rePassword)) {
                                    loginDetails = new LoginDetails(firstName.getText().toString(), lastname.getText().toString(), email.getText().toString(), password.getText().toString());
                                    signUp();
                                } else {
                                    dialog.dismiss();
                                }
                            }
                        } catch (Exception e) {
                            dialog.dismiss();
                            Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(SignUp.this, "No internet", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Log.e("Error", ex.getMessage());
        }
    }

    private void GetAndSetJsonToClass(String json, boolean errorMsg) {
        JSONObject object = null;
        try {
            object = new JSONObject(json);
            if (errorMsg) {
                loginDetails.msg = object.getString("message");
                dialog.dismiss();
                Toast.makeText(SignUp.this, loginDetails.msg, Toast.LENGTH_LONG).show();
            } else {
                loginDetails.token = object.getString("token");
                loginDetails.user_role = object.getString("user_role");
                loginDetails.user_id = object.getString("user_id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void doPostrequest() throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .add("email", loginDetails.email)
                .add("password", loginDetails.password)
                .add("fname", loginDetails.firstName)
                .add("lname", loginDetails.lastName)
                .build();

        Request request = new Request.Builder()
                .url(SIGN_UP_API)
                .header("Content-Type", "x-www-form-urlencoded")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (!response.isSuccessful()) {
                    SignUp.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final ResponseBody responseBody = response.body();
                                GetAndSetJsonToClass(responseBody.string(), true);
                                dialog.dismiss();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                } else {
                    final ResponseBody responseBody = response.body();
                    SignUp.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                GetAndSetJsonToClass(responseBody.string(), false);
                                sendToChatWindowAndExist(IntentKeyAndCodes.THREAD_ACTIVITY, loginDetails);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                }
        }
        });

    }

    private void signUp()
    {
        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(loginDetails.email,loginDetails.password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("demo", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(loginDetails.firstName+" "+loginDetails.lastName)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("demo", "User profile updated.");

                                                Toast.makeText(SignUp.this, "Signed Up Successfully",
                                                        Toast.LENGTH_SHORT).show();
                                                sendToChatWindowAndExist(IntentKeyAndCodes.THREAD_ACTIVITY, loginDetails);

                                            }
                                            dialog.dismiss();
                                        }
                                    });
                        } else {

                            Log.e("error","createUserWithEmail:failure");
                            Toast.makeText(SignUp.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
    }

    private boolean validateInputs(EditText firstName, EditText lastname, EditText email, EditText password, EditText rePassword) {
        boolean isValid = true;
        if (password.getText() == null || password.getText().equals("") || password.getText().toString().trim().equals("")) {
            password.setError("Enter Password");
            isValid = false;
        } else if (rePassword.getText() == null || rePassword.getText().equals("") || rePassword.getText().toString().trim().equals("")) {
            rePassword.setError("Enter Repeat Password");
            isValid = false;
        } else if (!rePassword.getText().toString().equals(password.getText().toString())) {
            rePassword.setError("Not Matching Passwords");
            isValid = false;
        } else if (rePassword.getText().toString().trim().length() < 6) {

            rePassword.setError("Password need minimum 6 characters");
        } else if (password.getText().toString().trim().length() < 6) {
            password.setError("Password need minimum 6 characters");
        }
        if (firstName.getText() == null || firstName.getText().equals("") || firstName.getText().toString().trim().equals("")) {
            firstName.setError("Enter First Name");
            isValid = false;
        }
        if (lastname.getText() == null || lastname.getText().equals("") || lastname.getText().toString().trim().equals("")) {
            lastname.setError("Enter Last Name");
            isValid = false;
        }
        if (email.getText() == null || email.getText().equals("") || email.getText().toString().trim().equals("")) {
            email.setError("Enter email");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
            email.setError("Enter Valid Email ");
            isValid = false;
        }
        return isValid;
    }

    private void sendToChatWindowAndExist(String activity, LoginDetails loginDetails) {

        dialog.dismiss();
        if (loginDetails != null) {

            Intent intent = new Intent(SignUp.this,MessageViewActivity.class);
            intent.putExtra(IntentKeyAndCodes.THREAD_TO_MESSAGR_KEY, loginDetails);
            startActivity(intent);
            Toast.makeText(SignUp.this, "Signed Up Successfully", Toast.LENGTH_LONG).show();
        }
        else{
            Intent intent = new Intent(SignUp.this,MainActivity.class);
            startActivity(intent);
        }
        finish();
    }
}
