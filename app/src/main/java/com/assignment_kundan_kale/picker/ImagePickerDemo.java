/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.assignment_kundan_kale.picker;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.assignment_kundan_kale.EditItemActivity;
import com.assignment_kundan_kale.R;
import com.bumptech.glide.Glide;

import com.google.android.cameraview.AspectRatio;
import com.google.android.cameraview.CameraView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import gun0912.tedbottompicker.GridSpacingItemDecoration;
import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.adapter.ImageGalleryAdapter;


/**
 * This demo app saves the taken picture to a constant file.
 * $ adb pull /sdcard/Android/data/com.google.android.cameraview.demo/files/Pictures/picture.jpg
 */
public class ImagePickerDemo extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback,
        AspectRatioFragment.Listener {

    private static final String TAG = "@@####ImagePickerDemo";

    private static final int REQUEST_CAMERA_PERMISSION = 1;

    private static final String FRAGMENT_DIALOG = "dialog";

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;


    private static final int[] FLASH_OPTIONS = {
            CameraView.FLASH_AUTO,
            CameraView.FLASH_OFF,
            CameraView.FLASH_ON,
    };

    private static final int[] FLASH_ICONS = {
            R.drawable.ic_flash_auto,
            R.drawable.ic_flash_off,
            R.drawable.ic_flash_on,
    };

    private static final int[] FLASH_TITLES = {
            R.string.flash_auto,
            R.string.flash_off,
            R.string.flash_on,
    };

    private int mCurrentFlash;

    private CameraView mCameraView;

    private Handler mBackgroundHandler;
    private RecyclerView galleryView;

    private ImageGalleryAdapter imageGalleryAdapter;

    private Uri cameraImageUri;


    ArrayList<Uri> selectedUriList;
    ArrayList<Uri> cameraClickedList = new ArrayList<>();
    ArrayList<Uri> tempUriList;

    static final int REQ_CODE_CAMERA = 1;
    static final int REQ_CODE_GALLERY = 2;
    static final String EXTRA_CAMERA_IMAGE_URI = "camera_image_uri";
    static final String EXTRA_CAMERA_SELECTED_IMAGE_URI = "camera_selected_image_uri";

    TedBottomPicker bottomSheetDialogFragment;

    private BottomSheetBehavior bottomSheetBehavior;
    private FrameLayout bottomSheetView;

    private String pickerType = "multi";
    Object []objects;
    String folderName="NA";
    File displayFolder;
    Uri selectedUri;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.take_picture:
                    if (mCameraView != null) {
                        mCameraView.takePicture();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_picker_demo);
        mCameraView = (CameraView) findViewById(R.id.camera);
        bottomSheetView = (FrameLayout) findViewById(R.id.container);

        // setRecyclerView();
        if (mCameraView != null) {
            mCameraView.addCallback(mCallback);
        }

        if (getIntent() != null) {

            objects =  (Object[])getIntent().getSerializableExtra("ITEM_DETAIL");
        }

        Log.d(TAG, "onCreate: name " + (String)objects[0]);
        Log.d(TAG, "onCreate: discription " + (String)objects[1]);
        Log.d(TAG, "onCreate: cost " + (String)objects[2]);
        Log.d(TAG, "onCreate: location " + (String)objects[3]);




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.take_picture);
        if (fab != null) {
            fab.setOnClickListener(mOnClickListener);
        }
       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermission()) {
            mCameraView.start();
            if (bottomSheetDialogFragment == null || !bottomSheetDialogFragment.isVisible()) {

                if (pickerType.equalsIgnoreCase("single"))
                    showBottomPicker();
                else if (pickerType.equalsIgnoreCase("multi")) {
                    showMultiBottomPicker();
                }


            }

        } else {
            requestPermission();
        }
    }

    private void showMultiBottomPicker() {
        bottomSheetDialogFragment = new TedBottomPicker.Builder(ImagePickerDemo.this)
                .setOnMultiImageSelectedListener(
                        new TedBottomPicker.OnMultiImageSelectedListener() {
                            @Override
                            public void onImagesSelected(ArrayList<Uri> uriList) {
                                Log.d(TAG, "showMultiBottomPicker: " + uriList.size());
                                selectedUriList = uriList;
                                // showUriList(uriList);
                            }
                        })
                .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
            @Override
            public void onImageSelected(Uri uri) {
                Log.d(TAG, "showMultiBottomPicker: onImageSelected " + uri);
               // imagePreview(uri);
            }
        })
                .setPeekHeight(getResources().getDisplayMetrics().heightPixels / 2)
                //.setPeekHeight(300)
                .showCameraTile(false)
                // .showGalleryTile(true)
                .setCompleteButtonText("Done")
                .setEmptySelectionText("No Select")
                .setSelectedUriList(selectedUriList)
                .create();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container, bottomSheetDialogFragment);
        ft.commitAllowingStateLoss();

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                    bottomSheetDialogFragment.setStateOpen(false);
                else
                    bottomSheetDialogFragment.setStateOpen(true);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void showBottomPicker() {
        bottomSheetDialogFragment = new TedBottomPicker.Builder(ImagePickerDemo.this)
                .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(Uri uri) {
                        imagePreview(uri);
                    }
                })
                .setPeekHeight(getResources().getDisplayMetrics().heightPixels / 2)
                //.setPeekHeight(300)
                .showCameraTile(false)
                // .showGalleryTile(true)
                .setCompleteButtonText(getString(R.string.done))
                .setEmptySelectionText(getString(R.string.no_select))
                .setSelectedUriList(selectedUriList)
                .create();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container, bottomSheetDialogFragment);
        ft.commitAllowingStateLoss();

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                    bottomSheetDialogFragment.setStateOpen(false);
                else
                    bottomSheetDialogFragment.setStateOpen(true);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        //
    }

    @Override
    protected void onPause() {
        if (mCameraView != null)
            mCameraView.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBackgroundHandler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBackgroundHandler.getLooper().quitSafely();
            } else {
                mBackgroundHandler.getLooper().quit();
            }
            mBackgroundHandler = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menupicker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.switch_camera:
                if (mCameraView != null) {
                    int facing = mCameraView.getFacing();
                    mCameraView.setFacing(facing == CameraView.FACING_FRONT ?
                            CameraView.FACING_BACK : CameraView.FACING_FRONT);
                }
                return true;
            case R.id.switch_save:

                Log.d(TAG, "onOptionsItemSelected: size " + bottomSheetDialogFragment.selectedUriList.size());

                new ImageTask().execute();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAspectRatioSelected(@NonNull AspectRatio ratio) {
        if (mCameraView != null) {
            Toast.makeText(this, ratio.toString(), Toast.LENGTH_SHORT).show();
            mCameraView.setAspectRatio(ratio);
        }
    }

    private Handler getBackgroundHandler() {
        if (mBackgroundHandler == null) {
            HandlerThread thread = new HandlerThread("background");
            thread.start();
            mBackgroundHandler = new Handler(thread.getLooper());
        }
        return mBackgroundHandler;
    }

    private CameraView.Callback mCallback
            = new CameraView.Callback() {

        @Override
        public void onCameraOpened(CameraView cameraView) {
            Log.d(TAG, "onCameraOpened");
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
            Log.d(TAG, "onCameraClosed");
        }

        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {
            Log.d(TAG, "onPictureTaken " + data.length);
            Toast.makeText(getApplicationContext(), getString(R.string.picture_taken),
                    Toast.LENGTH_SHORT)
                    .show();

            getBackgroundHandler().post(new Runnable() {
                @Override
                public void run() {
                    String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss",
                            Locale.getDefault()).format(
                            new Date());

                    folderName = (String)objects[0]+"_"+timeStamp;
                    //String imageFileName = "JPEG_" + timeStamp + "_";

                    String imageFileName = timeStamp+".jpg";

                   displayFolder = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                            folderName);
                    if(!displayFolder.exists()){
                        displayFolder.mkdir();
                    }

                    File filedisplay = new File(displayFolder+"/"+imageFileName);

                    OutputStream os = null;
                    Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                            getApplicationContext().getPackageName() + ".provider", filedisplay);

                    cameraClickedList.add(photoURI);

                    bottomSheetDialogFragment.onActivityResultCamera(photoURI);




                    try {
                        os = new FileOutputStream(filedisplay);
                        os.write(data);
                        os.close();
                    } catch (IOException e) {
                        Log.w(TAG, "Cannot write to " + filedisplay, e);
                    } finally {
                        if (os != null) {
                            try {
                                os.close();
                            } catch (IOException e) {
                                // Ignore
                            }
                        }
                    }


                }
            });
        }

    };

    public static class ConfirmationDialogFragment extends DialogFragment {

        private static final String ARG_MESSAGE = "message";
        private static final String ARG_PERMISSIONS = "permissions";
        private static final String ARG_REQUEST_CODE = "request_code";
        private static final String ARG_NOT_GRANTED_MESSAGE = "not_granted_message";

        public static ConfirmationDialogFragment newInstance(@StringRes int message,
                                                             String[] permissions, int requestCode, @StringRes int notGrantedMessage) {
            ConfirmationDialogFragment fragment = new ConfirmationDialogFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_MESSAGE, message);
            args.putStringArray(ARG_PERMISSIONS, permissions);
            args.putInt(ARG_REQUEST_CODE, requestCode);
            args.putInt(ARG_NOT_GRANTED_MESSAGE, notGrantedMessage);
            fragment.setArguments(args);
            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Bundle args = getArguments();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(args.getInt(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String[] permissions = args.getStringArray(ARG_PERMISSIONS);
                                    if (permissions == null) {
                                        throw new IllegalArgumentException();
                                    }
                                    ActivityCompat.requestPermissions(getActivity(),
                                            permissions, args.getInt(ARG_REQUEST_CODE));
                                }
                            })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getActivity(),
                                            args.getInt(ARG_NOT_GRANTED_MESSAGE),
                                            Toast.LENGTH_SHORT).show();
                                }
                            })
                    .create();
        }

    }

    private void setRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        galleryView.setLayoutManager(gridLayoutManager);
        galleryView.addItemDecoration(
                new GridSpacingItemDecoration(gridLayoutManager.getSpanCount(), 10, false));
    }


    private void requestPermission() {

        ActivityCompat.requestPermissions(ImagePickerDemo.this,
                new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {

            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:

                if (grantResults.length > 0) {
                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean StoragePermission =
                            grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (CameraPermission && StoragePermission) {
                        // Permission Granted
                        mCameraView.start();
                        showBottomPicker();

                        Toast.makeText(ImagePickerDemo.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ImagePickerDemo.this, "Permission Denied",
                                Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }

    public boolean checkPermission() {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED;
    }


    public void imagePreview(final Uri uri) {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.image_preview);

        ImageView previewImage = (ImageView) dialog.findViewById(R.id.img_preview_image);
        Button saveButton = (Button) dialog.findViewById(R.id.btn_image_preview_save);
        Button cancelButton = (Button) dialog.findViewById(R.id.btn_image_preview_canel);

        Glide.with(this).load(uri).into(previewImage);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public class ImageTask extends AsyncTask<Void,Void,Void>{

        File file;

        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(ImagePickerDemo.this);
            pd.setMessage("Please Wait..");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if(bottomSheetDialogFragment.selectedUriList!=null){

                if(bottomSheetDialogFragment.selectedUriList.size()>0){


                    for (int i=0;i<bottomSheetDialogFragment.selectedUriList.size();i++){

                        if(!cameraClickedList.contains(bottomSheetDialogFragment.selectedUriList.get(i))){

                          file =   copyGalleryImage(bottomSheetDialogFragment.selectedUriList.get(i));

                          if(file!=null) {
                              copyfileindir(file);
                          }


                        }


                    }


                }



            }





            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Log.d(TAG, "onPostExecute: ");
            if(pd.isShowing()){
                pd.dismiss();
            }
            objects[4] = displayFolder.getAbsolutePath();

            Intent intent = new Intent(ImagePickerDemo.this, EditItemActivity.class);
            intent.putExtra("ITEM_DETAIL",objects);
            startActivity(intent);
        }
    }

    private void copyfileindir(File file) {


        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss",
                Locale.getDefault()).format(
                new Date());

        if(folderName.equalsIgnoreCase("NA")){
            folderName = (String)objects[0]+"_"+timeStamp;
        }

        folderName = folderName.replaceAll(" " ,"_");
        //String imageFileName = "JPEG_" + timeStamp + "_";

        String imageFileName = timeStamp+".jpg";

         displayFolder = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                folderName);
        if(!displayFolder.exists()){
            displayFolder.mkdir();
        }

        File filedisplay = new File(displayFolder+"/"+imageFileName);

        FileInputStream instream = null;
        FileOutputStream outstream = null;




        try {
            instream = new FileInputStream(file);
            outstream = new FileOutputStream(filedisplay);

            byte[] buffer = new byte[1024];

            int length;
    	    /*copying the contents from input stream to
    	     * output stream using read and write methods
    	     */
            while ((length = instream.read(buffer)) > 0){
                outstream.write(buffer, 0, length);
            }

            //Closing the input/output file streams
            instream.close();
            outstream.close();

            Log.d(TAG, "copyfileindir: file copied");


        } catch (IOException e) {
            Log.w(TAG, "Cannot write to " + filedisplay, e);
        } finally {



        }
    }

    private File copyGalleryImage(Uri uri) {
        File file=null;
        try{
             file = new File(uri.getPath());
             if(!file.exists()){
                 file=null;
             }
        }catch(Exception e){
            e.printStackTrace();
        }



        return file;
    }
}
