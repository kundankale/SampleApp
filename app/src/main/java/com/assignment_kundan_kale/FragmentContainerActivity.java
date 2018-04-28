package com.assignment_kundan_kale;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.assignment_kundan_kale.picker.ImagePickerDemo;


public class FragmentContainerActivity extends AppCompatActivity implements ParentFragment.OnFragmentInteractionListener {

    private static final String TAG_className = FragmentContainerActivity.class.getSimpleName();

    ParentFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if(getIntent()!=null){
            Intent i = getIntent();




        }




        fragment = new CreateItemFragment();
        Bundle bundle = new Bundle();

        fragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, fragment);
        ft.commit();





    }









    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public void addImages(Object object) {

        Log.d("ContainerClass", "addImages: " );
        Object[] objects = (Object[])object;
        Intent intent  = new Intent(FragmentContainerActivity.this, ImagePickerDemo.class);
        intent.putExtra("ITEM_DETAIL",objects);
        startActivity(intent);


    }
}
