 package com.example.MusicX;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.MusicX.Service.OnClearFromRecent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

 public class MusicPlayerActivity extends AppCompatActivity implements Player{

    TextView titleTv,currentTv,totalTv;
    SeekBar seekBar;
    ImageView pause,play,pause_play,music_Icon;
    NotificationManager notificationManager;
    List<Track> tracks;
    ArrayList<AudioModel> songsList;
    AudioModel currentSong;
    MediaPlayer mediaPlayer=MyMediaPlayer.getInstance();

    int position =0;
    boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        titleTv = findViewById(R.id.Songtitle);
        currentTv = findViewById(R.id.current_time);
        totalTv = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seek_bar);
        pause = findViewById(R.id.previous);
        play = findViewById(R.id.next);
        pause_play = findViewById(R.id.pause_play);
        music_Icon = findViewById(R.id.music_image);

        titleTv.setSelected(true);

        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");
        setResourceWithMusic();

        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTv.setText(convertToMMSS(mediaPlayer.getCurrentPosition() + ""));

                    if (mediaPlayer.isPlaying()) {
                        pause_play.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
                    } else {
                        pause_play.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
                    }
                }
                new Handler().postDelayed(this, 100);

            }
        });
        pause_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying){
                    onTrackPause();
                }else{
                    onTrackPLay();
                }
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean FromUser) {
                if (mediaPlayer != null && FromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        populateTracks();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
        registerReceiver(broadcastReceiver,new IntentFilter("TRACKS_TRACKS"));
        startService(new Intent(getBaseContext(), OnClearFromRecent.class));
        }

    }

     private void createChannel() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID,"MusicX",NotificationManager.IMPORTANCE_LOW);

            notificationManager = getSystemService(NotificationManager.class);
            if(notificationManager != null){
                notificationManager.createNotificationChannel(channel);
            }
        }
     }

     private void populateTracks(){
            tracks=new ArrayList<>();

            tracks.add(new Track("Track","Artist",R.drawable.musicicon));

        }


    void setResourceWithMusic(){
     currentSong = songsList.get(MyMediaPlayer.currentIndex);

     titleTv.setText(currentSong.getTitle());

     totalTv.setText(convertToMMSS(currentSong.getDuration()));

     pause_play.setOnClickListener(v-> pausePlay());
     play.setOnClickListener(v-> playNextSong());
     pause.setOnClickListener(v-> playPreviousSong());

     playMusic();

     }
     public void playMusic(){
        mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(currentSong.getPath());
                mediaPlayer.prepare();
                mediaPlayer.start();
                seekBar.setProgress(0);
                seekBar.setMax(mediaPlayer.getDuration());
            }catch (IOException e){
                e.printStackTrace();
            }


     }
     public void playNextSong(){
        if (MyMediaPlayer.currentIndex ==  songsList.size()-1)
            return;

        MyMediaPlayer.currentIndex +=1;
        mediaPlayer.reset();
        setResourceWithMusic();

     }
     public void playPreviousSong(){
        if (MyMediaPlayer.currentIndex==0)
            return;
        MyMediaPlayer.currentIndex -=1;
        mediaPlayer.reset();
        setResourceWithMusic();

     }
     public void pausePlay(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }else{
            mediaPlayer.start();
        }

     }

     public static String convertToMMSS(String Duration){
        Long millis=Long.parseLong(Duration);

       return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
     }
     BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
         @Override
         public void onReceive(Context context, Intent intent) {
             String action = intent.getExtras().getString("actionname");

             switch (action){
                 case CreateNotification.ACTION_PREVIOUS:
                     onTrackPrevious();
                     break;
                 case CreateNotification.ACTION_PLAY:
                     if(isPlaying){
                         onTrackPause();
                     }else {
                         onTrackPLay();
                     }
                     break;
                 case CreateNotification.ACTION_NEXT:
                     onTrackNext();
                     break;
             }
         }
     };

     @Override
     public void onTrackPrevious() {
         position--;
         CreateNotification.Create_Notification(MusicPlayerActivity.this,tracks.get(position),R.drawable.ic_baseline_pause_circle_filled_24,position,tracks.size()-1);
     titleTv.setText(tracks.get(position).getTitle());
     }

     @Override
     public void onTrackPLay() {
         CreateNotification.Create_Notification(MusicPlayerActivity.this,tracks.get(position),R.drawable.ic_baseline_pause_circle_filled_24,position,tracks.size() -1);
         play.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
         titleTv.setText(tracks.get(position).getTitle());
         isPlaying=true;

     }

     @Override
     public void onTrackPause() {
         CreateNotification.Create_Notification(MusicPlayerActivity.this,tracks.get(position),R.drawable.ic_baseline_play_circle_filled_24,position,tracks.size()-1);
         pause.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
         titleTv.setText(tracks.get(position).getTitle());
         isPlaying=false;

     }

     @Override
     public void onTrackNext() {
         position++;
         CreateNotification.Create_Notification(MusicPlayerActivity.this,tracks.get(position),R.drawable.ic_baseline_pause_circle_filled_24,position,tracks.size() -1);
         titleTv.setText(tracks.get(position).getTitle());
     }

     @Override
     protected void onDestroy() {
         super.onDestroy();
         if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
              notificationManager.cancelAll();
         }
         unregisterReceiver(broadcastReceiver);
     }
 }