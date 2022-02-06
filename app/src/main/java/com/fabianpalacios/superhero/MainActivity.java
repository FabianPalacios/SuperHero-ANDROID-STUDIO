package com.fabianpalacios.superhero;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fabianpalacios.superhero.Services.model.MarvelService;
import com.fabianpalacios.superhero.Services.model.Root;

import java.net.MalformedURLException;
import java.net.URL;

import cafsoft.foundation.Data;
import cafsoft.foundation.HTTPURLResponse;
import cafsoft.foundation.URLRequest;
import cafsoft.foundation.URLSession;

public class MainActivity extends AppCompatActivity {

    private EditText txtSearch = null;

    private TextView lblName = null;
    private TextView lblDescribed = null;

    private ImageView imgSuperHero = null;

    private ProgressBar progress = null;


    private MarvelService service = null;

    final String PUBLIC_API_KEY = "502d6509cdb6d800b2374d6f4fb7698d";
    final String TS = "1";
    final String HASH = "b77a90841ab448f028fe4a42926c7197";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        service = new MarvelService(PUBLIC_API_KEY, TS, HASH);

    }

    public void initViews() {
        txtSearch = findViewById(R.id.txtSearch);
        lblName = findViewById(R.id.lblName);
        lblDescribed = findViewById(R.id.lblDescribed);
        imgSuperHero = findViewById(R.id.imgSuperHero);
        progress = findViewById(R.id.progress);
    }

    public void btnGetInfoOnClick(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        StringBuilder text = new StringBuilder();

        if (txtSearch.getText().toString().isEmpty()){
            text.append(getString(R.string.empty));
            alert.setMessage(text);
            alert.setPositiveButton(R.string.close,null);

            alert.show();
        }else {

            lblName.setVisibility(View.INVISIBLE);
            imgSuperHero.setVisibility(View.GONE);
            lblDescribed.setVisibility(View.GONE);
            getSuperHeroInfo(txtSearch.getText().toString());
            progress.setVisibility(View.VISIBLE);
        }
    }

    public void getSuperHeroInfo(String name) {

            service.requestSuperHeroData(name,(isNetworkError, statusCode, root) -> {
                if (!isNetworkError) {
                    if (statusCode == 200) {
                        //Log.d("Super Hero", String.valueOf(root.getData().getResults().isEmpty()));
                        showSuperHeroInfo(root);
                    } else {
                        Log.d("Super Hero", "Service error");
                    }
                } else {
                    Log.d("Super Hero", "Network error");
                }
            });
    }

    @SuppressLint("SetTextI18n")
    public  void showSuperHeroInfo(Root root) {
        runOnUiThread(() ->{
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            StringBuilder text = new StringBuilder();

            if(root.getData().getResults().isEmpty()){
                text.append(getString(R.string.no_exist));
                alert.setMessage(text);
                alert.setPositiveButton(R.string.close, null);

                alert.show();
            }else{
                lblName.setText(String.valueOf(root.getData().getResults().get(0).getName()));
                lblDescribed.setText(String.valueOf(root.getData().getResults().get(0).getDescription()));
            }
        });
        String urlImage ="";
        urlImage = root.getData().getResults().get(0).getThumbnail().getPath()+ "."+root.getData().getResults().get(0).getThumbnail().getExtension();
        getImage(urlImage);

    }

    public void getImage(String urlImage){
        URL url = null;
        urlImage = urlImage.replace("http", "https");
        try {
            url = new URL(urlImage);
            downloadImage(url);
        }catch (MalformedURLException e){
            Log.d("Error",e.toString());
        }
    }
    public void downloadImage(URL url){
        URLRequest request = new URLRequest(url);
        URLSession.getShared().dataTask(request, (data, response, error) -> {
            HTTPURLResponse resp = (HTTPURLResponse) response;
            if(error == null && resp.getStatusCode() == 200){
                showImage(dataToImage(data),imgSuperHero);
            }
        }).resume();
    }

    public void showImage(Bitmap image, ImageView imageView){
        runOnUiThread(() -> {
            progress.setVisibility(View.INVISIBLE);
            lblName.setVisibility(View.VISIBLE);
            imgSuperHero.setImageBitmap(image);
            imgSuperHero.setVisibility(View.VISIBLE);
            lblDescribed.setVisibility(View.VISIBLE);
        });
    }

    public  Bitmap dataToImage(Data data){
        Bitmap bitmap = BitmapFactory.decodeByteArray(data.toBytes(), 0,data.length());
        return bitmap;
    }

}

