package com.zhangyu.vidverse;

import android.app.Application;
import android.content.Context;

/**
 * Created by gaoxiong on 2015/7/15.
 */
public class VidVerseApplication extends Application {
  private static Context context;

  @Override
  public void onCreate() {
    super.onCreate();
    context = this;
  }

  public static Context getAppContext() {
    return context;
  }
}
