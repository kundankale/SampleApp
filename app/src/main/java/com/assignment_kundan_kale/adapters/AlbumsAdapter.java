package com.assignment_kundan_kale.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.assignment_kundan_kale.EditItemActivity;
import com.assignment_kundan_kale.MainActivity;
import com.assignment_kundan_kale.R;
import com.assignment_kundan_kale.picker.ImagePickerDemo;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import static com.assignment_kundan_kale.data.DbContract.DataEntry.CONTENT_URI_FILES;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.MyViewHolder> {

    private Context mContext;
    private List<Album> albumList;

    int id;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, price;
        public ImageView thumbnail,overflow;
        CardView card_view;

        public MyViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.title);
            price = (TextView) view.findViewById(R.id.price);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            card_view = view.findViewById(R.id.card_view);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }


    public AlbumsAdapter(Context mContext, List<Album> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Album album = albumList.get(position);
        holder.title.setText(album.getName());

        holder.price.setText(album.getCost());
        String folderPath = album.getImageFolderPath();
        File file = new File(folderPath);
        File[] pictures = file.listFiles();


        Glide.with(mContext).load(pictures[0]).into(holder.thumbnail);

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Object []objects = new Object[6];
                objects[0] = album.getName();
                objects[1] = album.getDiscription();
                objects[2] = album.getCost();
                objects[3] = album.getLocation();
                objects[4] = album.getImageFolderPath();
                objects[5] = album.getItem_id();

                Intent intent = new Intent(mContext, EditItemActivity.class);
                intent.putExtra("ITEM_DETAIL",objects);
                mContext.startActivity(intent);



            }
        });
        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                id= album.getItem_id();
                showPopupMenu(holder.overflow);
            }
        });
       // Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);


    }


    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.action_delete:

                    Log.d("@@####adapter", "deleteEntry: id " + id);

                    int i = mContext.getContentResolver().delete(CONTENT_URI_FILES, null, new String[]{"" + id});


                    Log.d("adapter", "deleteEntry: result " + i);



                    ((MainActivity)mContext).updateOnDelete();

                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }
}
