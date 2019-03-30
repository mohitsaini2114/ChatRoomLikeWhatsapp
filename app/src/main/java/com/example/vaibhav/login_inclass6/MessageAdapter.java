package com.example.vaibhav.login_inclass6;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import android.os.Handler;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.ocpsoft.prettytime.PrettyTime;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
     LoginDetails USER = null;
     ArrayList<Message> MESSAGES = null;
    IMessageAdapter iMessageAdapter = null;
    Context PARRENT_CONTEXT;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    Bitmap BITMAP;
     public MessageAdapter(ArrayList<Message> messageModels,LoginDetails user)//,IMessageAdapter iMessageAdapter)
     {
         Log.d("demo","In MessageAdapter");
         this.MESSAGES = messageModels;
         this.USER = user;
        // this.iMessageAdapter = iMessageAdapter;
 }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("demo","In MessageAdapter onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_message_structure, parent, false);
        PARRENT_CONTEXT = parent.getContext();
        ViewHolder viewHolder = new ViewHolder(view);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            Log.d("demo","In MessageAdapter onBindViewHolder");
            Message messageModel = holder.selected = MESSAGES.get(position);
            holder.message.setText(messageModel.message);
            holder.timeDifference.setText(messageModel.getPrettyTime());
            holder.messanger.setText(messageModel.firstName);

            if(messageModel.imageName == null || messageModel.imageName.trim().isEmpty())
            {
                holder.messageImage.setVisibility(View.GONE);
            }
            else{

                StorageReference storageRef =  storage.getReferenceFromUrl(messageModel.imageUrl);
                try {


                    holder.progressDialog.setVisibility(View.VISIBLE);

                    final File localFile = File.createTempFile("images", "jpg");
                    storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            BITMAP = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            holder.messageImage.setImageBitmap(BITMAP);
                            holder.messageImage.setVisibility(View.VISIBLE);
                            holder.progressDialog.setVisibility(View.GONE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            holder.messageImage.setVisibility(View.GONE);
                            holder.progressDialog.setVisibility(View.GONE);
                        }
                    });
                } catch (IOException e ) {
                    Log.e("error",e.getMessage());
                }

            }
            if(USER.email.equals(messageModel.email))
            {
                holder.binImage.setVisibility(View.VISIBLE);
                holder.messanger.setText(messageModel.firstName);
            }
            else{
                holder.binImage.setVisibility(View.INVISIBLE);
            }

    }


    @Override
    public int getItemCount() {
        return MESSAGES.size();
    }

    public  class ViewHolder extends  RecyclerView.ViewHolder
    {
        TextView message;
        TextView messanger;
        TextView timeDifference;
        ImageView binImage;
        ImageView messageImage;
        ProgressBar progressDialog;
        Message selected =null;
        Handler handler = new Handler();
        public ViewHolder(final View itemView) {
            super(itemView);

            this.progressDialog = ((ProgressBar) itemView.findViewById(R.id.image_loader));
            this.progressDialog.setVisibility(View.GONE);
            final OkHttpClient CLIENT = new OkHttpClient();
            this.binImage = (ImageView)itemView.findViewById(R.id.bin);
            this.messanger = (TextView)itemView.findViewById(R.id.messanger_tv);
            this.message = (TextView)itemView.findViewById(R.id.messafe_tv);
            this.timeDifference = (TextView)itemView.findViewById(R.id.ago_timing_tv);
            this.messageImage = (ImageView) itemView.findViewById(R.id.message_image);

            
            binImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message message = MESSAGES.get(getPosition());
                    DatabaseReference r = FirebaseDatabase.getInstance().getReference().child("user").child(message.messageKey);
                    r.removeValue();



                    if(message.imageUrl != null && !message.imageUrl.trim().isEmpty()) {

                        StorageReference storageRef = storage.getReferenceFromUrl(message.imageUrl);

                        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                notifyDataSetChanged();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(itemView.getContext(),"Please try deleting again",Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                }
            });
        }
    }

}
