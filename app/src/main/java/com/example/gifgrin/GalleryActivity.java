package com.example.gifgrin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import android.Manifest;

public class GalleryActivity extends AppCompatActivity {

    private final Integer[] image_ids={
            R.drawable.meme1,
            R.drawable.meme2,
            R.drawable.meme3,
            R.drawable.meme4,
            R.drawable.meme5,
            R.drawable.meme6
    };

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        RecyclerView recyclerView=findViewById(R.id.gallery);

        if (ContextCompat.checkSelfPermission(GalleryActivity.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1000);
        }



        recyclerView.setHasFixedSize(true);


        RecyclerView.LayoutManager layoutManager=new GridLayoutManager(getApplicationContext(),3);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<GalleryCell> cells=prepareData();
        GalleryAdapter adapter=new GalleryAdapter(cells,GalleryActivity.this);
        recyclerView.setAdapter(adapter);
    }

    private ArrayList<GalleryCell> prepareData(){
        ArrayList<GalleryCell> theImage=new ArrayList<>();
        for (int i=0; i<image_ids.length; i++){
            GalleryCell cell=new GalleryCell();
            cell.setImage(image_ids[i]);
            theImage.add(cell);
        }
        return theImage;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1000:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    onBackPressed();
                }
                return;
        }
    }
}
