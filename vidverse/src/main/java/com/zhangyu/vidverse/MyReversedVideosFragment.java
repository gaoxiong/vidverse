package com.zhangyu.vidverse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyReversedVideosFragment extends Fragment {

  private Context context;
  private View rootview;
  private RelativeLayout relativeLayoutGridView;
  private RelativeLayout relativeLayoutEmptyVideos;
  private RelativeLayout relativeLayoutHideMenu;
  private Button btnStartCamera;
  private Button btnChooseLocal;
  RelativeLayout frameCamera;
  RelativeLayout frameChooseLocal;
  private ImageView imageViewFloat;
  private boolean floatBtnVisible = false;

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
    rootview = inflater.inflate(R.layout.fragment_my_reversed_videos, container, false);
    relativeLayoutGridView = (RelativeLayout) rootview.findViewById(R.id.gridview_parent);
    relativeLayoutEmptyVideos = (RelativeLayout) rootview.findViewById(R.id.empty_videos_parent);
    initGridView();
    return rootview;
  }

  private void initGridView() {
    List<Map<String, Object>> contents = initContentList();
    if (contents.size() == 0) {
      relativeLayoutGridView.setVisibility(View.GONE);
      relativeLayoutEmptyVideos.setVisibility(View.VISIBLE);
      btnStartCamera = (Button)rootview.findViewById(R.id.btn_start_camera);
      btnStartCamera.setOnClickListener(clickStartCameraListener);
      btnChooseLocal = (Button)rootview.findViewById(R.id.btn_choose_local);
      btnChooseLocal.setOnClickListener(clickChooseLocalListener);
    } else {
      relativeLayoutGridView.setVisibility(View.VISIBLE);
      relativeLayoutEmptyVideos.setVisibility(View.GONE);
      relativeLayoutHideMenu = (RelativeLayout)rootview.findViewById(R.id.relativelayout_hide_menu);
      GridView gridView = (GridView) rootview.findViewById(R.id.GridView);
      MyReservedVideoAdapter myReservedVideoAdapter =
        new MyReservedVideoAdapter(this.getActivity(),
          contents, R.layout.my_reserved_videos_items,
          new String[]{"FileThumbnail", "FilePath", "VideoLength"},
          new int[]{R.id.ItemImage, R.id.imgview_share, R.id.textview_video_length});
      gridView.setAdapter(myReservedVideoAdapter);
      gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
          HashMap<String, Object> item = (HashMap<String, Object>) adapterView.getItemAtPosition(i);
          String filepath = item.get("FilePath").toString();
          Intent intent = new Intent(context, VideoPreviewActivity.class);
          intent.putExtra(Consts.VIDVERSE_REVERSED_FILEPATH, filepath);
          context.startActivity(intent);
        }
      });

      frameCamera = (RelativeLayout) rootview.findViewById(R.id.frame_menu_camera);
      frameCamera.setOnClickListener(clickStartCameraListener);
      frameChooseLocal = (RelativeLayout) rootview.findViewById(R.id.frame_menu_choose_local);
      frameChooseLocal.setOnClickListener(clickChooseLocalListener);

      imageViewFloat = (ImageView)rootview.findViewById(R.id.btn_choose_reverse);
      imageViewFloat.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (floatBtnVisible) {
            floatBtnVisible = false;
            relativeLayoutHideMenu.setVisibility(View.GONE);
          } else {
            floatBtnVisible = true;
            relativeLayoutHideMenu.setVisibility(View.VISIBLE);
          }
        }
      });
    }
  }

  private View.OnClickListener clickStartCameraListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      // start camera
      Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
      intent.putExtra(MediaStore.EXTRA_OUTPUT, Utils.getOutputMediaFile());
      intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
      intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
      startActivityForResult(intent, Consts.CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
    }
  };

  private View.OnClickListener clickChooseLocalListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      // start choose local
      Intent intent = new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT
      intent.addCategory(Intent.CATEGORY_OPENABLE);
      intent.setType("video/*");
      startActivityForResult(intent, Consts.CHOOSE_LOCAL_VIDEO_ACTIVITY_REQUEST_CODE);
    }
  };

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    String pickedFilepath = "";
    if (data == null || resultCode == 0) {
      return;
    }
    if (requestCode == Consts.CHOOSE_LOCAL_VIDEO_ACTIVITY_REQUEST_CODE) {
      pickedFilepath = Utils.getRealPathFromURI(context, data.getData());
    } else if (requestCode == Consts.CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
      pickedFilepath = data.getData().toString();
    }
    if (pickedFilepath == null) {
      Toast.makeText(context, R.string.no_picked_video, Toast.LENGTH_SHORT).show();
      return;
    }
    pickedFilepath = pickedFilepath.replace("file:///", "/");
    Intent intent = new Intent(context, DoReverseActivity.class);
    intent.putExtra(Consts.VIDVERSE_PICKED_FILEPATH, pickedFilepath);
    context.startActivity(intent);
  }

  private List<Map<String, Object>> initContentList() {
    List<Map<String, Object>> contents = new ArrayList<>();
    List<String> reversedVideoList = new ArrayList<>();
    Utils.getReversedVideoPath(Consts.VIDVERSE_FOLDER, reversedVideoList);
    for (int i = 0; i < reversedVideoList.size(); i++) {
      String filePath = reversedVideoList.get(i);
      String filename = Utils.getFileNameFromPath(filePath);
      Map<String, Object> map = new HashMap<>();
      map.put("FileThumbnail", Utils.getVideoThumbnailBitmap(context, filePath,
        getResources().getDimension(R.dimen.thumbnail_width_large),
        getResources().getDimension(R.dimen.thumbnail_height_large)));
      map.put("FilePath", filePath);
      map.put("VideoLength", Utils.getVideoDurationFromPath(filePath));
      contents.add(map);
    }
    return contents;
  }

  @Override
  public void onResume() {
    super.onResume();
    initGridView();
  }

  @Override
  public void onDetach() {
    super.onDetach();
  }
}
