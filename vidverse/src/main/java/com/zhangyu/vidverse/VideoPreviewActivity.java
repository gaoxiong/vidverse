package com.zhangyu.vidverse;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.VideoView;

import java.io.File;


public class VideoPreviewActivity extends ActionBarActivity {
  private String reversed_file_path = "";
  private VideoView videoView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.fragment_video_preview);
    getSupportActionBar().hide();
    Intent intent = getIntent();
    if (intent != null) {
      reversed_file_path = intent.getStringExtra(Consts.VIDVERSE_REVERSED_FILEPATH);
      File file = new File(reversed_file_path);
      if (file.exists()) {
        videoView = (VideoView)findViewById(R.id.vv_video_preview);
        videoView.setVideoPath(reversed_file_path);
        videoView.start();
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
