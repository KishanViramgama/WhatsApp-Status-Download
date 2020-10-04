package com.app.status.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.status.Activity.ShowItem;
import com.app.status.Adapter.ViewAdapter;
import com.app.status.BuildConfig;
import com.app.status.Interface.OnClick;
import com.app.status.R;
import com.app.status.Util.Constant;
import com.app.status.Util.Method;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DownloadFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private String root;
    private File file;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ViewAdapter viewAdapter;
    private MaterialTextView textView_noData;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment, container, false);

        root = Environment.getExternalStorageDirectory() + BuildConfig.downloadUrl;
        file = new File(root);
        Constant.download.clear();
        Constant.download = getListFiles(file);

        int resId = R.anim.layout_animation_from_bottom;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);

        progressBar = view.findViewById(R.id.progressbar_fragment);
        recyclerView = view.findViewById(R.id.recyclerView_fragment);
        textView_noData = view.findViewById(R.id.textView_noData_fragment);
        textView_noData.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutAnimation(animation);

        onClick = new OnClick() {
            @Override
            public void position(int position, String type) {
                startActivity(new Intent(getActivity(), ShowItem.class)
                        .putExtra("position", position)
                        .putExtra("type", type));
            }
        };
        method = new Method(getActivity(), onClick);

        viewAdapter = new ViewAdapter(getActivity(), Constant.download, "all", method);

        if (Constant.download.size() == 0) {
            textView_noData.setVisibility(View.VISIBLE);
        } else {
            textView_noData.setVisibility(View.GONE);
            recyclerView.setAdapter(viewAdapter);
        }

        return view;

    }

    private List<File> getListFiles(File parentDir) {

        List<File> inFiles = new ArrayList<>();
        try {
            Queue<File> files = new LinkedList<>(Arrays.asList(parentDir.listFiles()));
            while (!files.isEmpty()) {
                File file = files.remove();
                if (file.isDirectory()) {
                    files.addAll(Arrays.asList(file.listFiles()));
                } else if (file.getName().endsWith(".jpg") || file.getName().endsWith(".gif") || file.getName().endsWith(".mp4")) {
                    inFiles.add(file);
                }
            }
        } catch (Exception e) {
            Log.d("error", e.toString());
        }
        return inFiles;
    }

    @Override
    public void onResume() {

        Constant.download.clear();
        Constant.download = getListFiles(file);
        if (viewAdapter != null && method != null) {
            if (Constant.download.size() == 0) {
                textView_noData.setVisibility(View.VISIBLE);
            } else {
                textView_noData.setVisibility(View.GONE);
                viewAdapter = new ViewAdapter(getActivity(), Constant.download, "all", method);
                recyclerView.setAdapter(viewAdapter);
            }
        }

        super.onResume();
    }
}
