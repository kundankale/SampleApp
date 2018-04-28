package com.assignment_kundan_kale.adapters;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lincoln on 18/05/16.
 */
public class Album implements Parcelable {
    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    int item_id;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCost() {
        return Cost;
    }

    public void setCost(String cost) {
        Cost = cost;
    }

    public String getImageFolderPath() {
        return imageFolderPath;
    }

    public void setImageFolderPath(String imageFolderPath) {
        this.imageFolderPath = imageFolderPath;
    }

    public static Creator<Album> getCREATOR() {
        return CREATOR;
    }

    private String name;
    private String discription;
    private String location;
    private String Cost;
    private String imageFolderPath;


    public Album() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.item_id);
        dest.writeString(this.name);
        dest.writeString(this.discription);
        dest.writeString(this.location);
        dest.writeString(this.Cost);
        dest.writeString(this.imageFolderPath);
    }

    protected Album(Parcel in) {
        this.item_id = in.readInt();
        this.name = in.readString();
        this.discription = in.readString();
        this.location = in.readString();
        this.Cost = in.readString();
        this.imageFolderPath = in.readString();
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel source) {
            return new Album(source);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[ size ];
        }
    };
}
