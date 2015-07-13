package com.zhangyu.vidverse;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;


public class VideoPreviewActivity extends ActionBarActivity {
  private String reversed_file_path = "";
  private String origin_file_path = "";
  private VideoView videoView;
  private ImageButton imageButtonReversingOrigin;
  private ImageButton imageButtonReversed;
  private ImageButton imageButtonTitleBack;
  private ImageButton imageButtonTitleShare;
  private RelativeLayout relativeLayoutImgBtnOriginParent;
  private RelativeLayout relativeLayoutImgBtnReversedParent;
  private Resources res;
  private Context context;

  enum VIDEO_SOURCE {
    ORIGIN,
    REVERSED
  }
  private VIDEO_SOURCE current_source;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.fragment_video_preview);
    res = this.getResources();
    context = this;
    getSupportActionBar().hide();
    Intent intent = getIntent();
    if (intent != null) {
      reversed_file_path = intent.getStringExtra(Consts.VIDVERSE_REVERSED_FILEPATH);
      File file = new File(reversed_file_path);
      if (file.exists()) {
        videoView = (VideoView)findViewById(R.id.vv_video_preview);
        videoView.setVideoPath(reversed_file_path);
        current_source = VIDEO_SOURCE.REVERSED;
        videoView.setMediaController(new MediaController(this));
        videoView.start();
        imageButtonReversed = (ImageButton) findViewById(R.id.imgbtn_video_preview_reversed);
        imageButtonReversed.setImageBitmap(
          Utils.getVideoThumbnailBitmap(this, reversed_file_path));

        origin_file_path = Utils.getOriginFilePath(this, reversed_file_path);
        imageButtonReversingOrigin = (ImageButton) findViewById(R.id.imgbtn_video_preview_origin);
        if (origin_file_path == "") {
//          imageButtonReversingOrigin.setImageDrawable(res.getDrawable(R.drawable.btn_choose_reverse));
        } else {
          imageButtonReversingOrigin.setImageBitmap(
            Utils.getVideoThumbnailBitmap(this, origin_file_path));
        }
        relativeLayoutImgBtnOriginParent = (RelativeLayout)findViewById(R.id.relativelayout_imgbtn_origin_parent);
        relativeLayoutImgBtnReversedParent = (RelativeLayout)findViewById(R.id.relativelayout_imgbtn_reversed_parent);

        imageButtonReversingOrigin.setOnClickListener(imageButtonClickListener);
        imageButtonReversed.setOnClickListener(imageButtonClickListener);
      }
    }
    imageButtonTitleBack = (ImageButton)findViewById(R.id.imgbtn_video_preview_title);
    imageButtonTitleBack.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v) {
        finish();
      }
    });
    imageButtonTitleShare = (ImageButton)findViewById(R.id.imgbtn_video_preview_share);
  }

  private View.OnClickListener imageButtonClickListener = new View.OnClickListener(){
    @Override
    public void onClick(View v) {
      if (v.getId() == R.id.imgbtn_video_preview_origin) {
        if (current_source == VIDEO_SOURCE.ORIGIN) {
          return;
        }
        if (origin_file_path == "") {
          Toast.makeText(context, R.string.cannot_find_video, Toast.LENGTH_SHORT).show();
          return;
        }
        current_source = VIDEO_SOURCE.ORIGIN;
        relativeLayoutImgBtnOriginParent.setBackgroundColor(res.getColor(R.color.bg_color_btn));
        relativeLayoutImgBtnReversedParent.setBackgroundColor(res.getColor(R.color.bg_color_bar));
        videoView.stopPlayback();
        videoView.setVideoPath(origin_file_path);
        videoView.start();
      } else if (v.getId() == R.id.imgbtn_video_preview_reversed) {
        if (current_source == VIDEO_SOURCE.REVERSED) {
          return;
        }
        if (reversed_file_path == "") {
          Toast.makeText(context, R.string.cannot_find_video, Toast.LENGTH_SHORT).show();
          return;
        }
        current_source = VIDEO_SOURCE.REVERSED;
        relativeLayoutImgBtnOriginParent.setBackgroundColor(res.getColor(R.color.bg_color_bar));
        relativeLayoutImgBtnReversedParent.setBackgroundColor(res.getColor(R.color.bg_color_btn));
        videoView.stopPlayback();
        videoView.setVideoPath(reversed_file_path);
        videoView.start();
      }
    }
  };
}
