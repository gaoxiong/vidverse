package com.zhangyu.vidverse;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by gaoxiong on 15-7-11.
 */
public class MyReservedVideoAdapter extends SimpleAdapter {
  List<Map<String, Object>> contents;
  Context context;

  public MyReservedVideoAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
    super(context, data, resource, from, to);
    this.context = context;
    contents = (List<Map<String, Object>>)data;
  }

  @Override
  public void registerDataSetObserver(DataSetObserver dataSetObserver) {

  }

  @Override
  public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

  }

  @Override
  public int getCount() {
    return contents.size();
  }

  @Override
  public Object getItem(int i) {
    return contents.get(i);
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  @Override
  public View getView(int i, View view, ViewGroup viewGroup) {
    View v = super.getView(i, view, viewGroup);
    return v;
  }

  @Override
  public void setViewImage(ImageView v, String value) {
    v.setImageBitmap(Utils.getVideoThumbnailBitmap(context, value));
  }
}
