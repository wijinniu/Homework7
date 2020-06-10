package com.bytedance.videoplayer;

import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.Formatter;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private Button button;
    private SeekBar mProgress;
    private TextView mText;
    private VideoView videoView;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("hdx","is created");
        button = findViewById(R.id.button);
        mProgress = findViewById(R.id.media_progress);
        mText=findViewById(R.id.text);
        videoView = findViewById(R.id.video_view);
        videoView.setVideoPath(getVideoPath(R.raw.bytedance));
        if (savedInstanceState != null) {
            int ss = savedInstanceState.getInt("a");
            Log.d("hdx","msg receied :"+String.valueOf(ss));
            videoView.seekTo(ss);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    button.setText("播放");
                    mHandler.removeMessages(1);
                } else {
                    button.setText("暂停");
                    videoView.start();
                    mHandler.sendEmptyMessage(1);
                }
            }
        });

        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == 1) {
                    int currentPosition = videoView.getCurrentPosition();
                    int duration = videoView.getDuration();
                    updateTime(currentPosition,duration);
                    mProgress.setMax(duration);
                    mProgress.setProgress(currentPosition);
                    mHandler.sendEmptyMessageDelayed(1, 500);
                    return true;
                }
                return false;
            }
        });


        mProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeMessages(1);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = mProgress.getProgress();
                videoView.seekTo(progress);
                mHandler.sendEmptyMessage(1);
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int a = videoView.getCurrentPosition();
        outState.putInt("a", videoView.getCurrentPosition());
        Log.d("hdx","msg sended :"+a);
        super.onSaveInstanceState(outState);
    }

    private String getVideoPath(int resId) {
        return "android.resource://" + this.getPackageName() + "/" + resId;
    }

    private void updateTime(int nowTime,int totalTime){
        String progress = stringForTime(nowTime) + "/" + stringForTime(totalTime);
        mText.setText(progress);
    }

    private String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder;
        mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }


}
