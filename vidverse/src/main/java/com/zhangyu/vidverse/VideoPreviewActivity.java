package com.zhangyu.vidverse;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;


public class VideoPreviewActivity extends ActionBarActivity {
  private String reversed_file_path = "";
  private String origin_file_path = "";
  private VideoView videoView;
  private ImageButton imageButtonReversingOrigin;
  private ImageButton imageButtonReversed;
  Resources res;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.fragment_video_preview);
    res = this.getResources();
    getSupportActionBar().hide();
    Intent intent = getIntent();
    if (intent != null) {
      reversed_file_path = intent.getStringExtra(Consts.VIDVERSE_REVERSED_FILEPATH);
      File file = new File(reversed_file_path);
      if (file.exists()) {
        videoView = (VideoView)findViewById(R.id.vv_video_preview);
        videoView.setVideoPath(reversed_file_path);
        videoView.setMediaController(new MediaController(this));
        videoView.start();
        imageButtonReversed = (ImageButton) findViewById(R.id.imgbtn_video_preview_reversed);
        imageButtonReversed.setImageBitmap(
          Utils.getVideoThumbnailBitmap(this, reversed_file_path));
        origin_file_path = Utils.getOriginFilePath(this, reversed_file_path);
        imageButtonReversingOrigin = (ImageButton) findViewById(R.id.imgbtn_video_preview_origin);
        if (origin_file_path == "") {
          imageButtonReversingOrigin.setImageDrawable(res.getDrawable(R.drawable.btn_choose_reverse));
        } else {
          imageButtonReversingOrigin.setImageBitmap(
            Utils.getVideoThumbnailBitmap(this, origin_file_path));
        }
      }
    }
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_video_preview, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
