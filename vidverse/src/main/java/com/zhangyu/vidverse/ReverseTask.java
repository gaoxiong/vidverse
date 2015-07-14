package com.zhangyu.vidverse;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * Created by gaoxiong on 15-7-12.
 */
public class ReverseTask extends
  AsyncTask<Object, Void, Integer> {

  private final DoReverseActivity activity;
  private String origin_file_path = "";
  private String reversed_file_path = "";
  private Handler handler;

  public ReverseTask(DoReverseActivity activity, Handler handler) {
    this.activity = activity;
    this.handler = handler;
  }

  @Override
  protected Integer doInBackground(Object... params) {
    origin_file_path = (String) params[0];
    reversed_file_path = (String) params[1];

    long startTime = (Long) params[2];
    long endTime = (Long) params[3];

    Integer videoStream = (Integer) params[4];
    Integer audioStream = (Integer) params[5];
    Integer subtitleStream = (Integer) params[6];

    int videoStreamNo = videoStream == null ? -1 : videoStream.intValue();
    int audioStreamNo = audioStream == null ? -1 : audioStream.intValue();
    int subtitleStreamNo = subtitleStream == null ? -1 : subtitleStream.intValue();

    int result = activity.reverseNative(origin_file_path, reversed_file_path, startTime, endTime,
      videoStreamNo, audioStreamNo, subtitleStreamNo);

    return result;
  }

  @Override
  protected void onPostExecute(Integer result) {
    if (result >= 0) {
      Toast.makeText(activity.getApplicationContext(),
        "Reverse DONE!", Toast.LENGTH_SHORT).show();
      ContentValues values = new ContentValues();
      values.put(OriginReversedMapping.OriginReverse.ORIGIN,
        origin_file_path.replace("//", "/").toLowerCase());
      values.put(OriginReversedMapping.OriginReverse.REVERSED,
        reversed_file_path.replace("//", "/").toLowerCase());
      activity.getContentResolver().insert(OriginReversedMapping.OriginReverse.CONTENT_URI, values);

      Message msg = Message.obtain();
      msg.what = Consts.REVERSE_PROGRESS_DONE;
      handler.sendMessage(msg);
    } else {
      Toast.makeText(activity.getApplicationContext(),
        "Reverse ERROR!", Toast.LENGTH_SHORT).show();

      if (handler != null) {
        Message msg = Message.obtain();
        msg.what = Consts.REVERSE_PROGRESS_ERROR;
        handler.sendMessage(msg);
      }
    }
  }
}
