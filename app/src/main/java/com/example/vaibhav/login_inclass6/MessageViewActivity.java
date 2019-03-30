package com.example.vaibhav.login_inclass6;
//Assignment 6 :inclass class 9 : Group No 27
//Srishtee Marotkar, Mohit Saini
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MessageViewActivity extends AppCompatActivity implements IMessageAdapter {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private String IMAGE_NAME = null;
    LoginDetails RECEIVED_THREAD = null;
    OkHttpClient CLIENT = new OkHttpClient();
    AlertDialog.Builder builder = null;
    AlertDialog dialog = null;
    ArrayList<Message> MESSAGES = null;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference;
    ValueEventListener postListener;
    Message UPLOAD_MEEASAGE = null;
    int PICK_IMAGE_REQUEST = 101;
    Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_message_view);
            if (TestConnection.isConnected(getSystemService(CONNECTIVITY_SERVICE))) {
                builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = this.getLayoutInflater();
                builder.setTitle("Loading Chats...").setView(inflater.inflate(R.layout.activity_progress_loader, null)).setCancelable(false);
                dialog = builder.create();
                ImageView addimagebutton = ((ImageView) findViewById(R.id.addimagebutton));
                databaseReference = firebaseDatabase.getReference();
                recyclerView = (RecyclerView) findViewById(R.id.recycler_view_message);
                layoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(layoutManager);
                storageReference = firebaseStorage.getReference();
                if (getIntent() != null) {
                    if (getIntent().getSerializableExtra(IntentKeyAndCodes.THREAD_TO_MESSAGR_KEY) != null) {
                        RECEIVED_THREAD = (LoginDetails) getIntent().getSerializableExtra(IntentKeyAndCodes.THREAD_TO_MESSAGR_KEY);
                        ((TextView) findViewById(R.id.user_name)).setText(RECEIVED_THREAD.firstName +" "+ RECEIVED_THREAD.lastName);
                        dialog.show();

                    }
                }

                addimagebutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseImage();
                    }
                });


                postListener = new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        MESSAGES = new ArrayList<>();
                        MESSAGES.clear();


                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            Message task = postSnapshot.getValue(Message.class);
                            task.messageKey = postSnapshot.getKey();
                            MESSAGES.add(task);
                        }

                        adapter = new MessageAdapter(MESSAGES, RECEIVED_THREAD);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Error","Failing while fetching data");
                        Toast.makeText(getApplicationContext(),"Error adding Task",Toast.LENGTH_LONG).show();
                    }
                };


                databaseReference.child("user").addValueEventListener(postListener);


                ((ImageView) findViewById(R.id.logout_button)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intentForLoginPage = new Intent(MessageViewActivity.this,MainActivity.class);
                        startActivity(intentForLoginPage);
                        finish();
                    }
                });

                ((ImageView) findViewById(R.id.send_message)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TestConnection.isConnected(getSystemService(CONNECTIVITY_SERVICE))) {
                            EditText messageBox = ((EditText) findViewById(R.id.message_box));
                            ImageView im = ((ImageView) findViewById(R.id.addimagebutton));
                            if ( String.valueOf(im.getTag()) != ("addimage")  || !(messageBox == null || messageBox.getText().toString().equals("") || messageBox.getText().toString().trim().equals(""))) {
                                addMessageToChatBox();
                                messageBox.setText("");
                            }
                        } else {
                            ToastMessage.showToast(0, getApplicationContext(), null);
                        }
                    }
                });
            } else {
                ToastMessage.showToast(0, getApplicationContext());
            }
        } catch (Exception ex) {
            Log.e("error", ex.getMessage());
            showToast(0, "Technical Issue, Please check logs.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        UPLOAD_MEEASAGE =  new Message("", RECEIVED_THREAD.firstName,RECEIVED_THREAD.lastName, "",new Date().toString(),RECEIVED_THREAD.email);
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageViewUpload =((ImageView) findViewById(R.id.addimagebutton));
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            File file= new File(filePath.getPath());
            UPLOAD_MEEASAGE.imageName =  file.getName();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageViewUpload.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void addMessageToChatBox() {
        String messageContent = ((TextView)findViewById(R.id.message_box)).getText().toString();
        databaseReference = firebaseDatabase.getReference();
        if(UPLOAD_MEEASAGE == null)
            UPLOAD_MEEASAGE =  new Message(messageContent, RECEIVED_THREAD.firstName,RECEIVED_THREAD.lastName, null,new Date().toString(),RECEIVED_THREAD.email);
        else UPLOAD_MEEASAGE.message = messageContent;

        if(UPLOAD_MEEASAGE.imageName == null && UPLOAD_MEEASAGE.message != null && !UPLOAD_MEEASAGE.message.trim().isEmpty() ) {
            databaseReference.child("user").push().setValue(UPLOAD_MEEASAGE);
            GoToMessageAdapter();
        }else {
            uploadImage();
        }
        ((TextView) findViewById(R.id.message_box)).setText("");
        ((ImageView)findViewById(R.id.addimagebutton)).setImageResource(R.drawable.addimage);

    }

    public void GoToMessageAdapter() {
        if (TestConnection.isConnected(getSystemService(CONNECTIVITY_SERVICE))) {
            adapter = new MessageAdapter(MESSAGES, RECEIVED_THREAD);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        } else {
            showToast(0);
        }
    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

           // final StorageReference ref = storageReference.child("images/"+ UPLOAD_MEEASAGE.imageName);
            final StorageReference ref = storageReference.child("images/"+UUID.randomUUID()+".png");
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(MessageViewActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                             taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                 @Override
                                 public void onSuccess(Uri uri) {
                                     String uriImage = uri.toString();
                                     UPLOAD_MEEASAGE.imageUrl = uriImage;
                                     databaseReference.child("user").push().setValue(UPLOAD_MEEASAGE);
                                     GoToMessageAdapter();
                                     UPLOAD_MEEASAGE = null;
                                 }
                             });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MessageViewActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            GoToMessageAdapter();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }



    public void showToast(int id, String... message) {
        ToastMessage.showToast(id, getApplicationContext(), message);
    }

    public void setAlert(boolean set) {
        if (set)
            dialog.show();
        else
            dialog.dismiss();

    }
}
