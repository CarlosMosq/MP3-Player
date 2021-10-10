package com.company.mp3player;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    public final static Uri MEDIA_PATH = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private final ArrayList<String> songList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.songList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this
                    , new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}
                    , 1);
        }
        else {
            getAllAudioFiles();
        }
    }

    public void getAllAudioFiles() {
        ContentResolver contentResolver = getContentResolver();
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        Cursor songCursor = contentResolver.query(MEDIA_PATH, null, selection, null, null);
        if (songCursor != null) {
            while(songCursor.moveToNext()){
                String url = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                if (url.endsWith(".mp3")) {
                    songList.add(url);
                }
            }
            songCursor.close();
        }
        MusicAdapter adapter = new MusicAdapter(songList, MainActivity.this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getAllAudioFiles();
        }

    }
}