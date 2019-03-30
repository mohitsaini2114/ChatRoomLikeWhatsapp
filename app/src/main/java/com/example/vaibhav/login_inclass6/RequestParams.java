package com.example.vaibhav.login_inclass6;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.HashMap;

public class RequestParams {
    private HashMap<String,String> params;
    private StringBuilder stringBuilder;

    public RequestParams()
    {
        params = new HashMap<String, String>();
        stringBuilder = new StringBuilder();
    }

    public RequestParams addParams(String key,String Value)
    {
        try {
            params.put(key, URLEncoder.encode(Value,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return  this;

    }

    public String getEncodecParams()
    {
        stringBuilder = null;
        stringBuilder = new StringBuilder();
        for(String key:params.keySet())
        {
            if(stringBuilder.length()>0)
            {
                stringBuilder.append("&");
            }
            stringBuilder.append(key+"="+params.get(key));
        }
        return stringBuilder.toString();
    }

    public String getEncodedUrl(String url)
    {
        return  url+"?"+getEncodecParams();
    }

    public void encodePostParams(HttpURLConnection connection) throws IOException {
        connection.setDoOutput(true);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
        outputStreamWriter.write(getEncodecParams());
        outputStreamWriter.flush();
    }
}
