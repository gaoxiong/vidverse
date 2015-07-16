package com.zhangyu.vidverse;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by gaoxiong on 15-7-13.
 */
public class RecircledVideoView extends VideoView {
  private String path;
  private RecircledVideoView recircledVideoView;
  private int status = 0;

  public RecircledVideoView(Context context) {
    super(context);
    init();
  }

  public RecircledVideoView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public RecircledVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    recircledVideoView = this;
    recircledVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
      @Override
      public void onPrepared(MediaPlayer mp) {
        mp.start();
        mp.setLooping(true);
      }
    });

    this.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
      @Override
      public void onCompletion(MediaPlayer mp) {
        if (status >= 0) {
          recircledVideoView.setVideoPath(path);
          recircledVideoView.start();
        }
      }
    });

    this.setOnErrorListener(new MediaPlayer.OnErrorListener(){
      @Override
      public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        status = -1;
        return false;
      }
    });
  }

  @Override
  public void setVideoPath(String path) {
    super.setVideoPath(path);
    this.path = path;
  }
}
