package com.zhangyu.vidverse;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyReversedVideosFragment extends Fragment {

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
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_my_reversed_videos, container, false);
    return view;
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
