package com.app.status.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.app.status.Adapter.ShowAdapter;
import com.app.status.BuildConfig;
import com.app.status.R;
import com.app.status.Util.Constant;
import com.app.status.Util.Method;
import com.app.status.Util.ZoomOutTransformation;
import com.google.android.material.appbar.MaterialToolbar;
import com.theartofdev.edmodo.cropper.CropImage;

import org.apache.commons.io.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShowItem extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private ViewPager viewPager;
    private ShowAdapter showAdapter;
    private String type;
    private int selectedPosition = 0;
    private Bitmap mbitmap;
    private Uri resultUri, uri;
    private File file;
    private List<File> showArray;
    private Method method;
    private Animation myAnim;
    private ProgressDialog progressDialog;
    private ImageView imageView_one, imageView_two, imageView_three;
    private ImageView imageView_download, imageView_setWallpaper, imageView_profile, imageView_delete, imageView_share;
    private LinearLayout linearLayoutDownload, linearLayout_setAsWallpaper, linearLayout_profile, linearLayout_delete, linearLayout_share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_show);

        Intent in = getIntent();
        selectedPosition = in.getIntExtra("position", 0);
        type = in.getStringExtra("type");

        method = new Method(ShowItem.this);
        method.setStatusBarGradiant();
        progressDialog = new ProgressDialog(ShowItem.this);

        showArray = new ArrayList<>();

        myAnim = AnimationUtils.loadAnimation(ShowItem.this, R.anim.bounce);

        toolbar = findViewById(R.id.toolbar_imageShow);
        viewPager = findViewById(R.id.viewpager_imageShow);
        imageView_one = findViewById(R.id.imageView_line_one_imageShow);
        imageView_two = findViewById(R.id.imageView_line_two_imageShow);
        imageView_three = findViewById(R.id.imageView_line_three_imageShow);
        linearLayoutDownload = findViewById(R.id.linearLayout_download_imageShow);
        linearLayout_setAsWallpaper = findViewById(R.id.linearLayout_set_as_wallpaper_imageShow);
        linearLayout_profile = findViewById(R.id.linearLayout_profile_imageShow);
        linearLayout_delete = findViewById(R.id.linearLayout_delete_imageShow);
        linearLayout_share = findViewById(R.id.linearLayout_share_imageShow);
        imageView_download = findViewById(R.id.imageView_download_imageShow);
        imageView_setWallpaper = findViewById(R.id.imageView_set_as_wallpaper_imageShow);
        imageView_profile = findViewById(R.id.imageView_other_imageShow);
        imageView_delete = findViewById(R.id.imageView_delete_imageShow);
        imageView_share = findViewById(R.id.imageView_share_imageShow);
        linearLayout_delete.setVisibility(View.GONE);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        switch (type) {
            case "image":
                showArray = Constant.imageArray;
                break;
            case "video":
                showArray = Constant.videoArray;
                break;
            default:
                showArray = Constant.download;
                imageView_one.setVisibility(View.GONE);
                linearLayoutDownload.setVisibility(View.GONE);
                break;
        }

        ZoomOutTransformation zoomOutTransformation = new ZoomOutTransformation();
        viewPager.setPageTransformer(true, zoomOutTransformation);

        showAdapter = new ShowAdapter(ShowItem.this, showArray, type);
        viewPager.setAdapter(showAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition);

        checkImage(selectedPosition);

        linearLayoutDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_download.startAnimation(myAnim);
                new Download().execute();
            }
        });

        linearLayout_setAsWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_setWallpaper.startAnimation(myAnim);
                new DownloadImageTask().execute(showArray.get(viewPager.getCurrentItem()).toString());
            }
        });

        linearLayout_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_profile.startAnimation(myAnim);
                final Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
                switch (method.url_type()) {
                    case "wball":
                        break;
                    case "w":
                        intent.setPackage("com.whatsapp");
                        break;
                    case "wb":
                        intent.setPackage("com.whatsapp.w4b");
                        break;
                }
                intent.setDataAndType(Uri.fromFile(new File(showArray.get(viewPager.getCurrentItem()).toString())), "image/jpg");
                intent.putExtra("mimeType", "image/jpg");
                startActivityForResult(Intent.createChooser(intent, "Set image as"), 200);
            }
        });

        linearLayout_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_delete.startAnimation(myAnim);
                if (showArray.size() != 0) {
                    File files = new File(showArray.get(viewPager.getCurrentItem()).toString());
                    files.delete();
                    showArray.remove(viewPager.getCurrentItem());
                    showAdapter.notifyDataSetChanged();
                    if (showArray.size() == 0) {
                        onBackPressed();
                    }
                    Toast.makeText(ShowItem.this, getResources().getString(R.string.delete), Toast.LENGTH_SHORT).show();
                }
            }
        });

        linearLayout_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_share.startAnimation(myAnim);
                switch (type) {
                    case "image":
                        method.share(showArray.get(viewPager.getCurrentItem()).toString(), "image");
                        break;
                    case "video":
                        method.share(showArray.get(viewPager.getCurrentItem()).toString(), "video");
                        break;
                    default:
                        if (showArray.get(viewPager.getCurrentItem()).toString().contains(".mp4")) {
                            method.share(showArray.get(viewPager.getCurrentItem()).toString(), "video");
                        } else {
                            method.share(showArray.get(viewPager.getCurrentItem()).toString(), "image");
                        }
                        break;
                }
                Toast.makeText(ShowItem.this, getResources().getString(R.string.share), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
    }

    //	page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            checkImage(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

    };

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //-------------------------croup image-----------------------------------//


    @SuppressLint("StaticFieldLeak")
    public class DownloadImageTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String path = params[0];
            mbitmap = BitmapFactory.decodeFile(path);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            croup_storeImage(mbitmap);
            progressDialog.hide();

            super.onPostExecute(s);
        }
    }

    private void croup_storeImage(Bitmap imageData) {
        //get path to external storage (SD card)

        String filePath;
        String iconsStoragePath = getExternalCacheDir().getAbsolutePath();
        try {
            String fname = "Image";
            filePath = iconsStoragePath + "/" + fname + ".jpg";
            file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
            //choose another format if PNG doesn't suit you
            imageData.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return;
        }
        uri = Uri.fromFile(file);
        CropImage.activity(uri).start(ShowItem.this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                try {
                    Bitmap myBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(resultUri));
                    set_Wallpaper(myBitmap);
                } catch (IOException e) {
                    System.out.println(e);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void set_Wallpaper(Bitmap bitmap) {

        WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        try {
            myWallpaperManager.setBitmap(bitmap);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //-------------------------croup image-----------------------------------//

    public void checkImage(int selectedPosition) {

        switch (type) {
            case "video":
                imageView_one.setVisibility(View.GONE);
                imageView_two.setVisibility(View.GONE);
                imageView_three.setVisibility(View.GONE);
                linearLayout_setAsWallpaper.setVisibility(View.GONE);
                linearLayout_profile.setVisibility(View.GONE);
                break;
            case "image":
                imageView_three.setVisibility(View.GONE);
                break;
            case "all":
                linearLayout_delete.setVisibility(View.VISIBLE);
                if (showArray.get(selectedPosition).toString().contains(".mp4")) {
                    imageView_one.setVisibility(View.GONE);
                    imageView_two.setVisibility(View.GONE);
                    imageView_three.setVisibility(View.GONE);
                    linearLayout_profile.setVisibility(View.GONE);
                    linearLayout_setAsWallpaper.setVisibility(View.GONE);
                } else {
                    imageView_one.setVisibility(View.GONE);
                    imageView_two.setVisibility(View.VISIBLE);
                    imageView_three.setVisibility(View.VISIBLE);
                    linearLayout_profile.setVisibility(View.VISIBLE);
                    linearLayout_setAsWallpaper.setVisibility(View.VISIBLE);
                }
                break;
        }

    }

    //-------------------------download image-----------------------------------//

    @SuppressLint("StaticFieldLeak")
    public class Download extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                @SuppressLint("WrongThread") String file = showArray.get(viewPager.getCurrentItem()).toString();
                File source = new File(file);
                Random generator = new Random();
                int n = 10000;
                n = generator.nextInt(n);
                String fname, string = null;
                if (type.equals("image")) {
                    fname = "Image-" + n + ".jpg";
                    string = Environment.getExternalStorageDirectory().toString() + BuildConfig.downloadUrl + fname;
                } else if (type.equals("video")) {
                    fname = "Video-" + n + ".mp4";
                    string = Environment.getExternalStorageDirectory().toString() + BuildConfig.downloadUrl + fname;
                }
                File files = new File(string);
                FileUtils.copyFile(source, files);
                try {
                    MediaScannerConnection.scanFile(getApplicationContext(), new String[]{files.toString()},
                            null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {

                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(ShowItem.this, getResources().getString(R.string.download), Toast.LENGTH_SHORT).show();
            progressDialog.hide();
            super.onPostExecute(s);
        }
    }

}
