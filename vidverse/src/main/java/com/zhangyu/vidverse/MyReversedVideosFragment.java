package com.zhangyu.vidverse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyReversedVideosFragment extends Fragment {

  private Context context;

  public static MyReversedVideosFragment newInstance() {
    MyReversedVideosFragment fragment = new MyReversedVideosFragment();
    return fragment;
  }

  public MyReversedVideosFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    this.context = getActivity();
    View view = inflater.inflate(R.layout.fragment_my_reversed_videos, container, false);
    GridView gridView = (GridView) view.findViewById(R.id.GridView);
    List<Map<String, Object>> contents = initContentList();
    MyReservedVideoAdapter myReservedVideoAdapter =
      new MyReservedVideoAdapter(this.getActivity(),
        contents, R.layout.my_reserved_videos_items,
        new String[] { "ItemImage", "ItemTitle", "ItemSize" },
        new int[] { R.id.ItemImage, R.id.ItemTitle, R.id.ItemSize });
    gridView.setAdapter(myReservedVideoAdapter);

    FrameLayout frameCamera = (FrameLayout)view.findViewById(R.id.frame_menu_camera);
    frameCamera.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // start camera
        Intent intent = new Intent(context, CameraActivity.class);
        context.startActivity(intent);
      }
    });
    FrameLayout frameChooseLocal = (FrameLayout)view.findViewById(R.id.frame_menu_choose_local);
    frameChooseLocal.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // start choose local
      }
    });
    return view;
  }

  private List<Map<String, Object>> initContentList() {
    List<Map<String, Object>> contents = new ArrayList<>();
    List<String> reversedVideoList = new ArrayList<>();
    Utils.getReversedVideoPath("/sdcard/vidverse", reversedVideoList);
    for (int i = 0; i < reversedVideoList.size(); i++) {
      String filePath = reversedVideoList.get(i);
      String filename = Utils.getFileNameFromPath(filePath);
      Map<String, Object> map = new HashMap<>();
      map.put("ItemImage", filePath);
      map.put("ItemTitle", filename);
      map.put("ItemSize", "20K");
      contents.add(map);
    }
    return contents;
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  public void onDetach() {
    super.onDetach();
  }
}
