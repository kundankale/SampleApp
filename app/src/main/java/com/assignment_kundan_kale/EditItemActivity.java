package com.assignment_kundan_kale;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

import static com.assignment_kundan_kale.data.DbContract.DataEntry.COLUMN_ID;
import static com.assignment_kundan_kale.data.DbContract.DataEntry.COLUMN_IMAGEFOLDER_PATH;
import static com.assignment_kundan_kale.data.DbContract.DataEntry.COLUMN_ITEM_COST;
import static com.assignment_kundan_kale.data.DbContract.DataEntry.COLUMN_ITEM_DISCRIPTION;
import static com.assignment_kundan_kale.data.DbContract.DataEntry.COLUMN_ITEM_LOCATION;
import static com.assignment_kundan_kale.data.DbContract.DataEntry.COLUMN_ITEM_NAME;
import static com.assignment_kundan_kale.data.DbContract.DataEntry.CONTENT_URI_FILES;


public class EditItemActivity extends AppCompatActivity {

    EditText item_name_editText,item_discription_editText,item_cost_EditText,item_location_EditText;

    Button item_image_add_ButtonView;
    Object[] objects = new Object[5];
    String name,discription,cost,location,folderpath;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        if (getIntent() != null) {

            objects =  (Object[])getIntent().getSerializableExtra("ITEM_DETAIL");
        }
        item_name_editText = findViewById(R.id.editText_item_name);
        item_discription_editText = findViewById(R.id.editText_item_discription);
        item_cost_EditText = findViewById(R.id.editText_item_cost);
        item_location_EditText = findViewById(R.id.editText_item_location);

        item_image_add_ButtonView = findViewById(R.id.button_add_pics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initCollapsingToolbar();
        try {
            folderpath =(String)objects[4];

            name = (String) objects[0];
            discription = (String) objects[1];
            cost = (String) objects[2];
            location = (String) objects[3];
            id = (int)objects[5];




            Log.d("@@####", "onCreate:folderpath  " + folderpath);
            if(!folderpath.equalsIgnoreCase("NA")) {
                File file = new File(folderpath);
                File[] pictures = file.listFiles();

                Glide.with(this).load(pictures[0]).into((ImageView) findViewById(R.id.backdrop));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        item_name_editText.setText(name);
        item_discription_editText.setText(discription);
        item_cost_EditText.setText(cost);
        item_location_EditText.setText(location);

        if(id>0){
            item_image_add_ButtonView.setText("Update Item");
        }


        item_image_add_ButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                folderpath =(String)objects[4];

                name = item_name_editText.getText().toString().trim();
                discription = item_discription_editText.getText().toString().trim();
                cost = item_cost_EditText.getText().toString().trim();
                location =item_location_EditText.getText().toString().trim();
                id = (int)objects[5];



                if(id>0){
                    updateindb();

                }else{
                    saveindb();
                }


            }
        });

    }

    private void updateindb() {

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID, id);
        cv.put(COLUMN_ITEM_NAME, name);
        cv.put(COLUMN_ITEM_DISCRIPTION, discription);
        cv.put(COLUMN_ITEM_COST, cost);
        cv.put(COLUMN_ITEM_LOCATION, location);
        cv.put(COLUMN_IMAGEFOLDER_PATH, folderpath);

        int i = EditItemActivity.this.getContentResolver().update(CONTENT_URI_FILES, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});

        Log.d("SETTINNG", "updateDb: update result " + i);
        Intent intent =new Intent(EditItemActivity.this,MainActivity.class);
        startActivity(intent);
        finish();

    }

    private void saveindb() {


        int id = 1;
        Cursor cursor = EditItemActivity.this.getContentResolver().query(CONTENT_URI_FILES, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {

            id = cursor.getCount() + 1;

            cursor.close();
        }


        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID, id);
        cv.put(COLUMN_ITEM_NAME, name);
        cv.put(COLUMN_ITEM_DISCRIPTION, discription);
        cv.put(COLUMN_ITEM_COST, cost);
        cv.put(COLUMN_ITEM_LOCATION, location);
        cv.put(COLUMN_IMAGEFOLDER_PATH, folderpath);

        Uri uri = EditItemActivity.this.getContentResolver().insert(CONTENT_URI_FILES, cv);


        Log.d("@@####", "loadinDb: inserted uri " + uri);

        Intent intent =new Intent(EditItemActivity.this,MainActivity.class);
        startActivity(intent);
        finish();

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
}
