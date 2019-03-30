package com.example.vaibhav.login_inclass6;

import android.content.Context;
import android.widget.Toast;

public class ToastMessage {

    public static void showToast(int id,Context appcontext,String...message) {

        if(message != null)
        {
            Toast.makeText(appcontext, message[0], Toast.LENGTH_LONG).show();
        }else {
            switch (id) {
                case 0:
                    Toast.makeText(appcontext, "No Internet Connection", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Toast.makeText(appcontext, "No Image Found", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(appcontext, "Invalid Operation,Please Check logs", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(appcontext, "No Internet or Connection Interrupted", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(appcontext, "One of the feed has invalid data, byupassing that feed", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    Toast.makeText(appcontext, "No Data Found", Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    Toast.makeText(appcontext, "No URL/Data Available", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
