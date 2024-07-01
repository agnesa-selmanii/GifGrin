package com.example.gifgrin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {



    private static final int LOAD_IMAGE=1000;
    Button load, share;
    TextView text1, text2;

    EditText line1, line2;
    SquareImageView imageView;
    String currentImage="";
    BluetoothModeChangeReceiver receiver=new BluetoothModeChangeReceiver();

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter intentFilter=new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver,intentFilter);

        load=findViewById(R.id.b_load);
        share=findViewById(R.id.b_share);

        text1=findViewById(R.id.text1);
        text2=findViewById(R.id.text2);

        line1=findViewById(R.id.et_line1);
        line2=findViewById(R.id.et_line2);

        imageView=findViewById(R.id.imageView);

        share.setVisibility(View.INVISIBLE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    10);
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    10);
        }


        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), GalleryActivity.class);
                startActivityForResult(intent, LOAD_IMAGE);
            }
        });
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View content=findViewById(R.id.lay);
                    Bitmap bitmap=getScreenshoot(content);
                    long timeStamp= System.currentTimeMillis();
                    storeImage(bitmap, "meme"+timeStamp+".png"); //must save before share
                    shareImage("meme"+timeStamp+".png");

                    hideKeyboard();

                }
            });

            line1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    text1.setText(line1.getText().toString().toUpperCase());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

        line2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                text2.setText(line2.getText().toString().toUpperCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==LOAD_IMAGE&&resultCode==RESULT_OK&& data!=null){
            int image=Integer.parseInt(data.getStringExtra("image"));
            imageView.setImageResource(image);
            share.setVisibility(View.VISIBLE);
        }
    }

    private void shareImage(String fileName){
        File file=new File(getExternalFilesDir(null), fileName);
        if(file.exists())
        {
        Uri uri= FileProvider.getUriForFile(getApplicationContext(), "com.example.gifgrin", file);

        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(Intent.EXTRA_SUBJECT,"");
            intent.putExtra(Intent.EXTRA_TEXT,"");
            intent.putExtra(Intent.EXTRA_STREAM,uri);

            try{
               startActivity(intent.createChooser(intent,"Share via"));
            }catch(ActivityNotFoundException e){
                Toast.makeText(this, "No sharing app available", Toast.LENGTH_SHORT).show();
        }

        }else {
            Toast.makeText(this,"Error sharing!", Toast.LENGTH_SHORT).show();
        }
    }


    private void storeImage(Bitmap bm, String fileName)
    {
        File file=new File(getExternalFilesDir(null), fileName);
        try {

            ByteArrayOutputStream bos=new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 0,bos);
            byte[] bitmapData=bos.toByteArray();
            ByteArrayInputStream bs=new ByteArrayInputStream(bitmapData);

            OutputStream os=new FileOutputStream(file);
            byte[] data=new byte[bs.available()];
            bs.read(data);
            os.write(data);

            bs.close();
            os.close();

        }catch (IOException e){
            Toast.makeText(this, "Writing error!", Toast.LENGTH_SHORT).show();
        }
    }
    private static Bitmap getScreenshoot(View view){
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap=Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return bitmap;
    }


    //after typing hide Keyboard
    private void hideKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager !=null){
            inputMethodManager.hideSoftInputFromWindow(line1.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // Permission denied, show a message or take appropriate action
                Toast.makeText(this, "Location permission denied. Cannot access location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent=new Intent(MainActivity.this, GalleryActivity.class);

        if(item.getItemId()==R.id.memeGenerator){
            startActivity(intent);
        }
        if(item.getItemId()==R.id.imageSearch){
           // intent=new Intent(MainActivity.this,ImagesActivity.class);
            startActivity(intent);
        }
        return true;
    }
}