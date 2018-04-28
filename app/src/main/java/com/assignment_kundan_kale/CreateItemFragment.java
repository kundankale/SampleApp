package com.assignment_kundan_kale;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateItemFragment extends ParentFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText item_name_editText,item_discription_editText,item_cost_EditText,item_location_EditText;

    Button item_image_add_ButtonView;

    Object[] objects = new Object[6];
    public CreateItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateItemFragment newInstance(String param1, String param2) {
        CreateItemFragment fragment = new CreateItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_item,container,false);
        Bundle bundle = this.getArguments();

        item_name_editText = view.findViewById(R.id.editText_item_name);
        item_discription_editText = view.findViewById(R.id.editText_item_discription);
        item_cost_EditText = view.findViewById(R.id.editText_item_cost);
        item_location_EditText = view.findViewById(R.id.editText_item_location);

        item_image_add_ButtonView = view.findViewById(R.id.button_add_pics);


        return view;
    }
    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.trim().isEmpty())
            return false;
        return true;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        item_image_add_ButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isNullOrEmpty(item_name_editText.getText().toString().trim())||
                        isNullOrEmpty(item_discription_editText.getText().toString().trim())||
                        isNullOrEmpty(item_cost_EditText.getText().toString().trim())||
                        isNullOrEmpty(item_location_EditText.getText().toString().trim())){

                    Toast.makeText(getActivity(), "Please enter all the details", Toast.LENGTH_LONG).show();
                }else{

                    objects[0] = item_name_editText.getText().toString().trim();
                    objects[1] = item_discription_editText.getText().toString().trim();
                    objects[2] = item_cost_EditText.getText().toString().trim();
                    objects[3] = item_location_EditText.getText().toString().trim();
                    objects[4] = "NA";
                    objects[5] = -1;
                    if (mListener != null) {
                        mListener.addImages(objects);
                    }
                }
            }
        });

    }
}
