package com.company.mp3player;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class Play_Activity extends AppCompatActivity {
    private SeekBar volume, duration;
    private TextView movingTitle, timeStart, timeEnd;
    private Button previous, next, playPause;
    private MediaPlayer mediaPlayer;
    private Animation animation;

    String filePath, songTitle;
    int position;
    ArrayList<String> songList;

    Runnable runnable;
    Handler handler;
    int totalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        volume = findViewById(R.id.volumeControl);
        duration = findViewById(R.id.duration);
        movingTitle = findViewById(R.id.songTitle);
        timeStart = findViewById(R.id.timeStart);
        timeEnd = findViewById(R.id.timeEnd);
        previous = findViewById(R.id.previousSong);
        next = findViewById(R.id.nextSong);
        playPause = findViewById(R.id.pauseSong);
        animation = AnimationUtils.loadAnimation(Play_Activity.this, R.anim.translate_animation);

        songList = getIntent().getStringArrayListExtra("list");
        songTitle = getIntent().getStringExtra("songTitle");
        filePath = getIntent().getStringExtra("filePath");
        position = getIntent().getIntExtra("position", 0);

        movingTitle.setText(songTitle);
        movingTitle.setAnimation(animation);

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        previous.setOnClickListener(v -> {
            if (position == 0) {
                position = songList.size() - 1;
            }
            else {
                position--;
            }
            playNewAudio(songList, mediaPlayer, position, movingTitle);
        });

        next.setOnClickListener(v -> {
            if (position == songList.size() - 1) {
                position = 0;
            }
            else {
                position++;
            }
            playNewAudio(songList, mediaPlayer, position, movingTitle);

        });

        playPause.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playPause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
            }
            else{
                mediaPlayer.start();
                playPause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
            }
        });

        volume.setProgress(50);
        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    volume.setProgress(progress);
                    float volumeLevel = progress / 100f;
                    mediaPlayer.setVolume(volumeLevel, volumeLevel);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        duration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    mediaPlayer.seekTo(progress);
                    duration.setProgress(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        handler = new Handler();
        runnable = () -> {
            //gets total time of the audio file and set SeekBar to that length;
            totalTime = mediaPlayer.getDuration();
            duration.setMax(totalTime);

            //gets current length of audio and updates SeekBar position every second;
            int currentPosition = mediaPlayer.getCurrentPosition();
            duration.setProgress(currentPosition);
            handler.postDelayed(runnable, 1000);

            //Creates the time signatures for total length of the audio and time elapsed (00:00);
            String elapsedTime = createTimeLabel(currentPosition);
            timeStart.setText(elapsedTime);
            String printTotal = createTimeLabel(totalTime);
            timeEnd.setText(printTotal);

            //When audio is finished, plays the next audio automatically;
            mediaPlayer.setOnCompletionListener(mp -> {
                if (position == songList.size() - 1) {
                    position = 0;
                }
                else {
                    position++;
                }
                playNewAudio(songList, mp, position, movingTitle);
            });
        };
        handler.post(runnable);

//closing of onCreate;
    }

    public void playNewAudio(ArrayList<String> list, MediaPlayer media, int position, TextView title) {
        media.reset();
        String newFilePath = list.get(position);
        String newTitle = newFilePath.substring(newFilePath.lastIndexOf("/") + 1);
        title.setText(newTitle);
        try {
            media.setDataSource(newFilePath);
            media.prepare();
            media.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        playPause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
        title.clearAnimation();
        title.startAnimation(animation);
    }

    public String createTimeLabel(int currentPosition) {
        String timeLabel;
        int minutes, seconds;

        minutes = currentPosition / 1000 / 60;
        seconds = currentPosition / 1000 % 60;

        if(minutes < 10 && seconds < 10) {
            timeLabel = "0" + minutes + ":0" + seconds;
        }
        else if(minutes > 10 && seconds < 10) {
            timeLabel = minutes + ":0" + seconds;
        }
        else if(minutes < 10 && seconds > 10) {
            timeLabel = "0" + minutes + ":" + seconds;
        }
        else {
            timeLabel = minutes + ":" + seconds;
        }
        return timeLabel;
    }


}