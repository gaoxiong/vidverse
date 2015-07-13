package com.zhangyu.vidverse;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by gaoxiong on 15-7-11.
 */
public class MyReservedVideoAdapter extends SimpleAdapter {
  private List<Map<String, Object>> contents;
  private Context context;
  private int resource;
  private String[] from;
  private int[] to;
  private LayoutInflater layoutInflater;

  public MyReservedVideoAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
    super(context, data, resource, from, to);
    this.context = context;
    contents = (List<Map<String, Object>>)data;
    this.resource = resource;
    this.from = from;
    this.to = to;
    layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    return createViewFromResource(i, view, viewGroup, resource);
  }

  private View createViewFromResource(int position, View view, ViewGroup parent, int resource) {
    View v;
    if (view == null) {
      v = layoutInflater.inflate(resource, parent, false);
    } else {
      v = view;
    }
    bindView(position, v);
    return v;
  }

  private void bindView(int position, View view) {
    Map<String, Object> dataSet = contents.get(position);
    if (dataSet == null) {
      return;
    }
    final ViewBinder binder = this.getViewBinder();
    final String[] from = this.from;
    final int[] to = this.to;
    final int count = to.length;
    for (int i = 0; i < count; i++) {
      final View v = view.findViewById(to[i]);
      if (v != null) {
        final Object data = dataSet.get(from[i]);
        String text = data == null ? "" : data.toString();
        if (text == null) {
          text = "";
        }

        boolean bound = false;
        if (binder != null) {
          bound = binder.setViewValue(v, data, text);
        }

        if (!bound) {
          if (v instanceof TextView) {
            setViewText((TextView) v, text);
          } else if (v.getId() == R.id.imgview_share) {
            v.setTag(data);
          } else if (v.getId() == R.id.ItemImage) {
            ((ImageView) v).setImageBitmap((Bitmap) data);
          } else {
            throw new IllegalStateException(v.getClass().getName() + " is not a " +
              " view that can be bounds by this SimpleAdapter");
          }
        }
      }
    }
  }
}
