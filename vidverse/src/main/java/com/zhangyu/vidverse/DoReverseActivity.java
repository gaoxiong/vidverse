package com.zhangyu.vidverse;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
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
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;


public class DoReverseActivity extends ActionBarActivity {
  private int nativeInit = -1;
  private int nativeTotalStep = 0;
  private int nativeCurrentStep = 0;
  private final int UPDATE_REVERSE_PROGRESS = 0;

  private Context context;
  private String origin_file_path = "";
  private String reversed_file_path = "";
  private VideoView videoView;
  private ImageButton imageButtonTitleBack;
  private ImageButton imageButtonTitleShare;
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
  private native int getReverseTotalStep();
  private native int getReverseCurrentStep();
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
        videoView.setMediaController(new MediaController(this));
        videoView.start();
        imageButtonReversingOrigin = (ImageButton) findViewById(R.id.imgbtn_video_preview_origin);
        imageButtonReversingOrigin.setImageBitmap(
          Utils.getVideoThumbnailBitmap(context, origin_file_path));
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
    // init title
    imageButtonTitleBack = (ImageButton)findViewById(R.id.imgbtn_video_preview_title);
    imageButtonTitleBack.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v) {
        finish();
      }
    });
    imageButtonTitleShare = (ImageButton)findViewById(R.id.imgbtn_video_preview_share);
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
      reverseProgressThread.start();
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

  private Handler mHandler = new Handler(){
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case UPDATE_REVERSE_PROGRESS: {
          Log.i("reversing", "cur step:" + String.valueOf(nativeCurrentStep) +
                ", total step:" + String.valueOf(nativeTotalStep));
          break;
        }
        default:
          break;
      }
    }
  };

  private Thread reverseProgressThread = new Thread(){
    @Override
    public void run() {
      while (true) {
        if (nativeTotalStep == 0) {
          nativeTotalStep = DoReverseActivity.this.getReverseTotalStep();
        }
        nativeCurrentStep = DoReverseActivity.this.getReverseCurrentStep();
        Message msg = Message.obtain();
        msg.what = UPDATE_REVERSE_PROGRESS;
        mHandler.sendMessage(msg);
        if (nativeCurrentStep == nativeTotalStep && nativeTotalStep > 0) {
          this.interrupt();
          break;
        }
        try {
          sleep(500);
        } catch (InterruptedException e) {
          Log.d("reversing", "thread error.");
          e.printStackTrace();
        }
      }
    }
  };

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
    if (id == R.id.action_share) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
