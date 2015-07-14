package com.zhangyu.vidverse;

import android.os.Environment;

import java.io.File;

/**
 * Created by gaoxiong on 15-7-12.
 */
public class Consts {
  public static final String VIDVERSE_FOLDER = Environment.getExternalStorageDirectory() + File.separator + "vidverse/";
  public static final String VIDVERSE_RECORDE = "record";
  public static final String VIDVERSE_REVERSED = "reversed";
  public static final String VIDVERSE_PREFIX = "r_";
  public static final String VIDVERSE_RECORD_FOLDER = VIDVERSE_FOLDER + VIDVERSE_RECORDE;
  public static final String VIDVERSE_REVERSED_FOLDER = VIDVERSE_FOLDER + VIDVERSE_REVERSED;

  /* For Intent */
  public static final String VIDVERSE_REVERSED_FILEPATH = "reversed_file_path";
  public static final String VIDVERSE_PICKED_FILEPATH = "picked_file_path";
  public static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
  public static final int CHOOSE_LOCAL_VIDEO_ACTIVITY_REQUEST_CODE = 201;

  /* handler message */
  public static final int UPDATE_REVERSE_PROGRESS = 100;
  public static final int REVERSE_PROGRESS_ERROR = 101;
  public static final int REVERSE_PROGRESS_DONE = 102;
}
