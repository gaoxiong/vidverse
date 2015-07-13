package com.zhangyu.vidverse;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by gaoxiong on 2015/7/13.
 */
public class OriginReversedMapping {
  public static final String AUTHORITY  = "com.zhangyu.vidverse.VideoReverseContentProvider";

  public static final class OriginReverse implements BaseColumns {
    public static final Uri CONTENT_URI  = Uri.parse("content://com.zhangyu.vidverse.VideoReverseContentProvider");

    public static final String  ORIGIN  = "ORIGIN";
    public static final String  REVERSED  = "REVERSED";
  }
}
