package com.fabianpalacios.superhero.Services.model;

import android.util.Log;


import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



import cafsoft.foundation.HTTPURLResponse;
import cafsoft.foundation.URLComponents;
import cafsoft.foundation.URLQueryItem;
import cafsoft.foundation.URLSession;

public class MarvelService {

    private  String publicAPIKey  ="";
    private String theTS ="";
    private String theHASH ="";

    public  MarvelService(String newPublicAPIKey, String newTS, String newHASH){
        publicAPIKey = newPublicAPIKey;
        theTS = newTS;
        theHASH = newHASH;
    }

    public void requestSuperHeroData(String name,OnDataResponse delegate){
        URLComponents components = new URLComponents();
        components.setScheme("https");
        components.setHost("gateway.marvel.com");
        components.setPath("/v1/public/characters");
        components.setQueryItems(new URLQueryItem[]{
                new URLQueryItem("ts", theTS),
                new URLQueryItem("apikey", publicAPIKey),
                new URLQueryItem("hash", theHASH),
                new URLQueryItem("name", name)
        });
        URLSession.getShared().dataTask(components.getURL(), (data, response, error) -> {
            HTTPURLResponse resp = (HTTPURLResponse) response;
            Root root = null;
            int statusCode = -1;

            if(error == null && resp.getStatusCode() == 200){
                String text = data.toText();
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
                Gson gson = gsonBuilder.create();
                root = gson.fromJson(text, Root.class);
                statusCode = resp.getStatusCode();
                Log.d("Demostracion", "OK");

            }else {
                Log.d("Demostracion", "ERROR NO ES NULL");
            }
            if(delegate != null){
                delegate.onChange(error != null, statusCode,root);
            }

        }).resume();
    }

    public interface OnDataResponse{
        public abstract void onChange(boolean isNetworkError, int statusCode, Root root);
    }



}
