package com.zhangyu.vidverse;

import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Created by gaoxiong on 15-7-12.
 */
public class ReverseTask extends
  AsyncTask<Object, Void, Integer> {

  private final DoReverseActivity activity;

  public ReverseTask(DoReverseActivity activity) {
    this.activity = activity;
  }

  @Override
  protected Integer doInBackground(Object... params) {
    String file_src = (String) params[0];
    String file_dest = (String) params[1];

    long startTime = (Long) params[2];
    long endTime = (Long) params[3];

    Integer videoStream = (Integer) params[4];
    Integer audioStream = (Integer) params[5];
    Integer subtitleStream = (Integer) params[6];

    int videoStreamNo = videoStream == null ? -1 : videoStream.intValue();
    int audioStreamNo = audioStream == null ? -1 : audioStream.intValue();
    int subtitleStreamNo = subtitleStream == null ? -1 : subtitleStream.intValue();

    int result = activity.reverseNative(file_src, file_dest, startTime, endTime,
      videoStreamNo, audioStreamNo, subtitleStreamNo);

    return result;
  }

  @Override
  protected void onPostExecute(Integer result) {
    if (result >= 0) {
      Toast.makeText(activity.getApplicationContext(),
        "Reverse DONE!", Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(activity.getApplicationContext(),
        "Reverse ERROR!", Toast.LENGTH_SHORT).show();
    }
  }
}
