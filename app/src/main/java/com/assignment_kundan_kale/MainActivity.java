package com.assignment_kundan_kale;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.assignment_kundan_kale.adapters.Album;
import com.assignment_kundan_kale.adapters.AlbumsAdapter;
import com.assignment_kundan_kale.utils.Constants;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.assignment_kundan_kale.data.DbContract.DataEntry.COLUMN_ID;
import static com.assignment_kundan_kale.data.DbContract.DataEntry.COLUMN_IMAGEFOLDER_PATH;
import static com.assignment_kundan_kale.data.DbContract.DataEntry.COLUMN_ITEM_COST;
import static com.assignment_kundan_kale.data.DbContract.DataEntry.COLUMN_ITEM_DISCRIPTION;
import static com.assignment_kundan_kale.data.DbContract.DataEntry.COLUMN_ITEM_LOCATION;
import static com.assignment_kundan_kale.data.DbContract.DataEntry.COLUMN_ITEM_NAME;
import static com.assignment_kundan_kale.data.DbContract.DataEntry.CONTENT_URI_FILES;
import static com.assignment_kundan_kale.utils.Constants.PARENT_FOLDER_NAME;
import static com.assignment_kundan_kale.utils.Constants.Path;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Album>> {

    private RecyclerView recyclerView;
    private AlbumsAdapter adapter;
    private List<Album> fileInfoPojoList;
    ArrayList<Album> movieParcelableArrayList;


    private static final int FILES_LOADER_ID = 0;

    LoaderManager.LoaderCallbacks<ArrayList<Album>> callback;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initCollapsingToolbar();


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());








        checkDbValues();

        if (savedInstanceState == null || !savedInstanceState.containsKey(Constants.KEY_FILES_SAVE)) {

            Log.d("MainActivity", "onCreate: save instance not contains key");
            discoverFiles();


        } else {
            movieParcelableArrayList = savedInstanceState.getParcelableArrayList(Constants.KEY_FILES_SAVE);

            Log.d("MainActivity", "onCreate: save instance contains key");


            loadFileInfoData();

        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/


                Intent intent = new Intent(getApplicationContext(), FragmentContainerActivity.class);
                intent.putExtra("picker", "multi");
                startActivity(intent);





            }
        });




    }

    private void checkDbValues() {


        File file = new File(Path+"/"+PARENT_FOLDER_NAME);
        if(!file.exists()){
            file.mkdir();
        }



    }

    public void discoverFiles() {

        movieParcelableArrayList = new ArrayList<>();
        removeItemsFromList();
        int loaderId = FILES_LOADER_ID;
        callback = MainActivity.this;
        Bundle bundleForLoader = null;
        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);


    }

    private void removeItemsFromList() {

        if (movieParcelableArrayList != null) {
            movieParcelableArrayList.clear();
        }
    }




    @Override
    public Loader<ArrayList<Album>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<ArrayList<Album>>(this) {
            @Override
            protected void onStartLoading() {

                Log.d("MainActivity", "onStartLoading: ");



                forceLoad();

            }

            @Override
            public ArrayList<Album> loadInBackground() {


                try {


                    movieParcelableArrayList = loadDatafromDb();

                    return movieParcelableArrayList;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }


            public void deliverResult(ArrayList<Album> data) {

                movieParcelableArrayList = data;
                super.deliverResult(data);
            }
        };
    }

    private ArrayList<Album> loadDatafromDb() {

        Log.d("MainActivity", "loadDatafromDb: ");

        ArrayList<Album> fileInfoParcelables = new ArrayList<>();
        Cursor cursor = MainActivity.this.getContentResolver().query(CONTENT_URI_FILES, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {

            Log.d("MainActivity", "getfromDb: price " + cursor.getCount());

            if (cursor.moveToFirst()) {
                do {


                    int item_id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));

                    Log.d("MainActivity", "getfromDb: COLUMN_ID " + item_id);
                    String item_name = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_NAME));
                    Log.d("MainActivity", "getfromDb: COLUMN_ITEM_NAME " + item_name);


                    String item_discription = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_DISCRIPTION));

                    Log.d("MainActivity", "getfromDb: COLUMN_ITEM_DISCRIPTION " + item_discription);

                    String item_cost = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_COST));

                    Log.d("MainActivity", "getfromDb: COLUMN_ITEM_COST " + item_cost);

                    String item_location = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_LOCATION));

                    Log.d("MainActivity", "getfromDb: COLUMN_ITEM_LOCATION " + item_location);


                    String item_image_dir_path = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGEFOLDER_PATH));
                    Log.d("MainActivity", "getfromDb: COLUMN_IMAGEFOLDER_PATH " + item_image_dir_path);



                    if (checkFolderPresent(item_image_dir_path)) {

                        Log.d("MainActivity", "loadDatafromDb: folder present");

                        Album album = new Album();
                        album.setItem_id(item_id);
                        album.setName(item_name);
                        album.setDiscription(item_discription);
                        album.setLocation(item_location);
                        album.setCost(item_cost);
                        album.setImageFolderPath(item_image_dir_path);


                        fileInfoParcelables.add(album);
                    } else {

                        Log.d("MainActivity", "loadDatafromDb: folder not present");
                    }


                } while (cursor.moveToNext());
            }


        } else {
            Log.d("MainActivity", "getfromDb: cursor is null or price id 0");
        }


        cursor.close();

        return fileInfoParcelables;


    }

    private boolean checkFolderPresent(String folderpath) {



        File file = new File(folderpath);

        if (file.exists()) {
            return true;
        } else {
            return false;
        }


    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Album>> loader, ArrayList<Album> data) {



        Log.d("MainActivity", "onLoadFinished: ");
        if(data!=null) {
            if (data.size() > 0) {

                movieParcelableArrayList = data;

                loadFileInfoData();


            } else {

                // if no data available

                Log.d("MainActivity", "onLoadFinished: no data available");
            }
        }
    }

    public void updateOnDelete(){
        try {


            movieParcelableArrayList = loadDatafromDb();


            loadFileInfoData();


        } catch (Exception e) {
            e.printStackTrace();

        }

    }
    public void loadFileInfoData() {

        Log.d("@@####MainActivity", "loadFileInfoData: ");



        adapter = new AlbumsAdapter(this, movieParcelableArrayList);
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();





    }



    @Override
    public void onLoaderReset(Loader<ArrayList<Album>> loader) {

    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }



    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();




    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Constants.KEY_FILES_SAVE, movieParcelableArrayList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finishAffinity();
    }
}
