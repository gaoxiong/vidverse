package com.zhangyu.vidverse;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FeedbackFragment extends Fragment {

  public static FeedbackFragment newInstance() {
    FeedbackFragment fragment = new FeedbackFragment();
    return fragment;
  }

  public FeedbackFragment() {
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
    View view = inflater.inflate(R.layout.fragment_feedback, container, false);
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
