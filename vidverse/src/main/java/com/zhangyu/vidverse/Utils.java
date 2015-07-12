package com.zhangyu.vidverse;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.util.List;

/**
 * Created by gaoxiong on 15-7-11.
 */
public class Utils {
  private static final int MAX_SIDE_LEN = 240;

  public static void getReversedVideoPath(String path, List<String> fileList) {
    File[] files = new File(path).listFiles();
    if (files == null)
      return;
    for (int i = 0; i < files.length; i++) {
      File f = files[i];
      if (f.isFile()) {
        if (isReversedVideoFile(f.getPath())) {
          fileList.add(f.getPath());
        } else if (f.isDirectory() && f.getPath().indexOf("/.") == -1) {
          getReversedVideoPath(f.getPath(), fileList);
        }
      }
    }
  }

  public static String getFileNameFromPath(String path) {
    String[] splitString = path.split("/");
    int length = splitString.length - 1;
    if (length >= 0) {
      return splitString[length];
    } else {
      return "";
    }
  }

  public static Bitmap getVideoThumbnailBitmap(Context context, String path) {
    Bitmap thumbnail = getVideoThumbnailBitmap(context, path,
      MediaStore.Video.Thumbnails.FULL_SCREEN_KIND, null);
    if (thumbnail != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
        thumbnail = android.media.ThumbnailUtils.extractThumbnail(
          thumbnail, MAX_SIDE_LEN, MAX_SIDE_LEN);
      } else {
        thumbnail = scaleDown(thumbnail, MAX_SIDE_LEN, MAX_SIDE_LEN, true);
      }
    }
    return thumbnail;
  }

  public static Bitmap getVideoThumbnailBitmap(Context context, String path,
                                               int kind, BitmapFactory.Options options) {
    long id = -1;
    String escapedPath = path.replace("'", "''");
    Cursor cursor = context.getContentResolver().query(
      MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
      new String[] {MediaStore.Video.Media._ID},
      MediaStore.MediaColumns.DATA + "='" + escapedPath + "'",
      null, null);
    if (cursor != null) {
      if (cursor.moveToFirst()) {
        int idIndex = cursor.getColumnIndex(MediaStore.Video.Media._ID);
        id = cursor.getLong(idIndex);
      }
      cursor.close();
    }

    Bitmap thumbnail = null;
    if (path != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
        thumbnail = android.media.ThumbnailUtils.createVideoThumbnail(path, kind);
      } else {
        thumbnail = MediaStore.Video.Thumbnails.getThumbnail(
          context.getContentResolver(), id, kind, options);
      }
    }
    return thumbnail;
  }

  private static Bitmap scaleDown(Bitmap source, int width, int height, boolean recycle) {
    if (source == null) {
      return null;
    }
    float scale;
    if (source.getWidth() < source.getHeight()) {
      scale = width / (float) source.getWidth();
    } else {
      scale = height / (float) source.getHeight();
    }
    Matrix matrix = new Matrix();
    matrix.setScale(scale, scale);
    Bitmap miniThumbnail = transform(matrix, source, width, height, false);
    return miniThumbnail;
  }

  public static Bitmap transform(Matrix scaler, Bitmap source, int targetWidth, int targetHeight,
                                 boolean scaleUp) {
    int deltaX = source.getWidth() - targetWidth;
    int deltaY = source.getHeight() - targetHeight;
    if (!scaleUp && (deltaX < 0 || deltaY < 0)) {
      /*
       * In this case the bitmap is smaller, at least in one dimension,
       * than the target. Transform it by placing as much of the image as
       * possible into the target and leaving the top/bottom or left/right
       * (or both) black.
       */
      Bitmap b2 = creatBitmapSafty(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
      if (b2 == null) { // out of memory
        return source;
      }
      Canvas c = new Canvas(b2);

      int deltaXHalf = Math.max(0, deltaX / 2);
      int deltaYHalf = Math.max(0, deltaY / 2);
      Rect src =
        new Rect(deltaXHalf, deltaYHalf, deltaXHalf + Math.min(targetWidth, source.getWidth()),
          deltaYHalf
            + Math.min(targetHeight, source.getHeight()));
      int dstX = (targetWidth - src.width()) / 2;
      int dstY = (targetHeight - src.height()) / 2;
      Rect dst = new Rect(dstX, dstY, targetWidth - dstX, targetHeight - dstY);
      c.drawBitmap(source, src, dst, null);
      return b2;
    }
    float bitmapWidthF = source.getWidth();
    float bitmapHeightF = source.getHeight();

    float bitmapAspect = bitmapWidthF / bitmapHeightF;
    float viewAspect = (float) targetWidth / targetHeight;

    if (bitmapAspect > viewAspect) {
      float scale = targetHeight / bitmapHeightF;
      if (scale < .9F || scale > 1F) {
        scaler.setScale(scale, scale);
      } else {
        scaler = null;
      }
    } else {
      float scale = targetWidth / bitmapWidthF;
      if (scale < .9F || scale > 1F) {
        scaler.setScale(scale, scale);
      } else {
        scaler = null;
      }
    }

    Bitmap b1;
    if (scaler != null) {
      // this is used for minithumb and crop, so we want to filter here.
      b1 = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), scaler, true);
    } else {
      b1 = source;
    }

    int dx1 = Math.max(0, b1.getWidth() - targetWidth);
    int dy1 = Math.max(0, b1.getHeight() - targetHeight);

    Bitmap b2 = Bitmap.createBitmap(b1, dx1 / 2, dy1 / 2, targetWidth, targetHeight);
    return b2;
  }

  public static Bitmap creatBitmapSafty(int width, int height, Bitmap.Config config) {
    Bitmap output = null;
    try {
      output = Bitmap.createBitmap(width, height, config);
    } catch (Throwable e) {
      if (config == Bitmap.Config.ARGB_8888) {
        return creatBitmapSafty(width, height, Bitmap.Config.RGB_565);
      }
    }
    return output;
  }

  private static boolean isReversedVideoFile(String filePath) {
    String filename = getFileNameFromPath(filePath);
    if (filename.startsWith("r_") && filename.endsWith(".mp4")) {
      return true;
    } else {
      return false;
    }
  }

  public static String getRealPathFromURI(Context context, Uri contentUri) {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
      DocumentsContract.isDocumentUri(context, contentUri)){
      String wholeID = DocumentsContract.getDocumentId(contentUri);
      String id = wholeID.split(":")[1];
      String[] column = { MediaStore.Video.Media.DATA };
      String sel = MediaStore.Video.Media._ID + "=?";
      String filePath = "";
      Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        column, sel, new String[] { id }, null);
      int columnIndex = cursor.getColumnIndex(column[0]);
      if (cursor.moveToFirst()) {
        filePath = cursor.getString(columnIndex);
      }
      cursor.close();
      return filePath;
    } else {
      String[] proj = {MediaStore.Video.Media.DATA};
      Cursor cursor = ((Activity)context).managedQuery(contentUri, proj, null, null, null);
      int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
      cursor.moveToFirst();
      return cursor.getString(column_index);
    }
  }
}
