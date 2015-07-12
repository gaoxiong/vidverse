package com.zhangyu.vidverse;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;


public class DoReverseActivity extends ActionBarActivity {
  private int nativeInit = -1;

  private Context context;
  private String origin_file_path = "";
  private String reversed_file_path = "";
  private VideoView videoView;
  private ImageButton imageButtonTitle;
  private ImageButton imageButtonShare;
  private EditText editTextTitle;
  private RelativeLayout relativeLayoutStartReverse;
  private Button btnStartReverse;
  private RelativeLayout relativeLayoutReversing;
  private ImageView reversingAnim;
  private ImageButton imageButtonReversingOrigin;
  private ImageButton imageButtonReversingPlaceholder;
  private RelativeLayout relativeLayoutReversed;
  private REVERSING_STATUS status = REVERSING_STATUS.START;

  enum REVERSING_STATUS {
    START,
    REVERSING,
    REVERSED
  }

  static {
    System.loadLibrary("ffmpeg");
    System.loadLibrary("ffmpeg-jni");
  }
  private native int initNative();
  private native void deallocNative();
  public native int reverseNative(String file_src, String file_dest,
                                  long positionUsStart, long positionUsEnd,
                                  int videoStreamNo,
                                  int audioStreamNo, int subtitleStreamNo);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_do_reverse);
    context = this;
    getSupportActionBar().hide();
    Intent intent = getIntent();
    if (intent != null) {
      origin_file_path = intent.getStringExtra(Consts.VIDVERSE_PICKED_FILEPATH);
      File file = new File(origin_file_path);
      if (file.exists()) {
        videoView = (VideoView)findViewById(R.id.vv_video_preview);
        videoView.setVideoPath(origin_file_path);
        videoView.start();
      }
      nativeInit = initNative();
    }
    // init before reversing status
    relativeLayoutStartReverse = (RelativeLayout) findViewById(R.id.btn_start_reverse_parent);
    btnStartReverse = (Button)findViewById(R.id.btn_start_reverse);
    btnStartReverse.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        relativeLayoutStartReverse.setVisibility(View.GONE);
        relativeLayoutReversing.setVisibility(View.VISIBLE);
        status = REVERSING_STATUS.REVERSING;
        startReversing();
      }
    });
    // init reversing status
    relativeLayoutReversing = (RelativeLayout) findViewById(R.id.reversing_parent);
    reversingAnim = (ImageView) findViewById(R.id.imgview_center_image);
    imageButtonReversingOrigin = (ImageButton) findViewById(R.id.imgbtn_video_preview_origin);
    imageButtonReversingPlaceholder = (ImageButton) findViewById(R.id.imgbtn_video_preview_reversed);
  }

  private void startReversing() {
    // do reversing
    if (nativeInit == 0) {
      Toast.makeText(context,
        "Start reversing...", Toast.LENGTH_SHORT).show();
      String filename = Utils.getFileNameFromPath(origin_file_path);
      String reversedFileFolder = Consts.VIDVERSE_FOLDER + File.separator + Consts.VIDVERSE_REVERSED;
      if (!Utils.createFolder(reversedFileFolder)) {
        Log.d("reversing", "create folder error.");
        Toast.makeText(context,
          "Cannot create folder!", Toast.LENGTH_SHORT).show();
        return;
      }
      reversed_file_path = reversedFileFolder + File.separator + Consts.VIDVERSE_PREFIX + filename;
      new ReverseTask(this).execute(origin_file_path, reversed_file_path,
        Long.valueOf(0), Long.valueOf(0),
        Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(0));
    } else {
      Toast.makeText(context,
        "Native code not init!", Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  protected void finalize() throws Throwable {
    if (nativeInit == 0) {
      deallocNative();
    }
    super.finalize();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_do_reverse, menu);
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
