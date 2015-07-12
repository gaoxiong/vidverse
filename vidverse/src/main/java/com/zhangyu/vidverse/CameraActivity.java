package com.zhangyu.vidverse;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends ActionBarActivity
  implements SurfaceHolder.Callback {
  private SurfaceView sv_camera;
  private MediaRecorder mediaRecorder;
  private Camera camera;
  private boolean isRecording = false;
  private boolean mPreviewRunning = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_camera);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.hide();
    }
    sv_camera = (SurfaceView)findViewById(R.id.surface_camera);
    sv_camera.getHolder().addCallback(this);
    sv_camera.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    findViewById(R.id.imgbtn_camera_title).setOnClickListener(click);
    findViewById(R.id.imgbtn_camera_record).setOnClickListener(click);
    findViewById(R.id.imgbtn_camera_reverse).setOnClickListener(click);
  }

  private View.OnClickListener click = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.imgbtn_camera_title:
          finish();
          break;
        case R.id.imgbtn_camera_record:
          startOrStopRecord();
          break;
        case R.id.imgbtn_camera_reverse:
          reverseCamera();
          break;
        default:
          break;
      }
    }
  };

  public void startOrStopRecord() {
    if (isRecording) {
      stopRecording();
    } else {
      startRecording();
    }
  }

  public void reverseCamera() {

  }

  private void stopRecording() {
    if (isRecording) {
      releaseMediaRecord();
      isRecording = false;
      Toast.makeText(CameraActivity.this, "停止录像，并保存文件", Toast.LENGTH_SHORT).show();
    }
  }

  private void startRecording() {
    if (!prepareMediaRecord()) {
      Log.e("camera", "prepare camera error.");
      releaseMediaRecord();
      return;
    }
    try {
      mediaRecorder.start();
      isRecording = true;
    } catch (Exception e) {
    }
  }

  private String generateStringFromTime() {
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    return dateFormat.format(new Date());
  }

  @Override
  public void surfaceCreated(SurfaceHolder surfaceHolder) {
    if (!checkCameraHadrware()) {
      return;
    }
    camera = Camera.open();
    try {
      camera.setPreviewDisplay(surfaceHolder);
    } catch (IOException e) {
      releaseCamera();
      e.printStackTrace();
    }
  }

  @Override
  public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
    if(mPreviewRunning){
      camera.stopPreview();
    }
    Camera.Parameters parameters = camera.getParameters();
    parameters.setPreviewSize(960, 720);
    camera.setParameters(parameters);
    try {
      camera.setPreviewDisplay(surfaceHolder);
    } catch (IOException e){
      e.printStackTrace();
    }
    camera.startPreview();
    mPreviewRunning = true;
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    if(null != camera)
    {
      camera.setPreviewCallback(null);
      camera.stopPreview();
      isRecording = false;
      camera.release();
      camera = null;
    }
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    releaseCamera();
  }

  private void releaseCamera() {
    if(camera != null)
    {
      camera.setPreviewCallback(null);
      camera.stopPreview();
      isRecording = false;
      camera.release();
      camera = null;
    }
  }

  private void releaseMediaRecord() {
    if (mediaRecorder != null) {
      mediaRecorder.reset();
      mediaRecorder.release();
      mediaRecorder = null;
      if (camera != null) {
        camera.lock();
      }
    }
  }

  private boolean prepareMediaRecord() {
    String filename  = Consts.VIDVERSE_FOLDER +
      Consts.VIDVERSE_RECORDE + "/VID-" + generateStringFromTime() + ".mp4";
    File file = new File(filename);
    if (file.exists()) {
      file.delete();
    }
    mediaRecorder = new MediaRecorder();
    mediaRecorder.reset();
    camera.unlock();
    mediaRecorder.setCamera(camera);
    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
    mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
    mediaRecorder.setVideoFrameRate(4);
    mediaRecorder.setOutputFile(file.getAbsolutePath());
    mediaRecorder.setPreviewDisplay(sv_camera.getHolder().getSurface());
    mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
      @Override
      public void onError(MediaRecorder mr, int what, int extra) {
        releaseMediaRecord();
        isRecording = false;
        Toast.makeText(CameraActivity.this, "录制出错", Toast.LENGTH_SHORT).show();
      }
    });
    try {
      mediaRecorder.prepare();
    } catch (IllegalStateException e) {
      return false;
    } catch (IOException e) {
      return false;
    }
    return true;
  }

  private boolean checkCameraHadrware() {
    if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
      return true;
    }
    return false;
  }
}
