package com.example.sainath.memorytest.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sainath on 10/17/2015.
 *
 * POJO class to denote the gridItem containing the url of the image, title and the state of the image.
 */
public class GridItem implements Parcelable {
    private String image;
    private String title;
    private boolean isShown;

    public GridItem() {
        super();
    }

    public GridItem(String img, String title, boolean shown) {
        this.image = img;
        this.title = title;
        this.isShown = shown;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isShown() {
        return isShown;
    }

    public void setIsShown(boolean isShown) {
        this.isShown = isShown;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);
        dest.writeString(title);
        dest.writeByte((byte) (isShown ? 0x01 : 0x00));
    }

    protected GridItem(Parcel in) {
        this.image = in.readString();
        this.title = in.readString();
        this.isShown = in.readByte() != 0x00;
    }

    public static final Parcelable.Creator<GridItem> CREATOR = new Parcelable.Creator<GridItem>() {
        public GridItem createFromParcel(Parcel source) {
            return new GridItem(source);
        }

        public GridItem[] newArray(int size) {
            return new GridItem[size];
        }
    };
}
