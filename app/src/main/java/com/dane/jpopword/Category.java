package com.dane.jpopword;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Keep;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

@Keep
public class Category implements Parcelable{
    @SerializedName("name")
    private String name;
    @SerializedName("description")
    private String description;
    @SerializedName("songs")
    private ArrayList<Song> songs = null;
    @SerializedName("introCopy")
    private String introCopy;
    @SerializedName("type")
    private int type; // 1(default), 2(cover)

    public Category(String name, String description, ArrayList<Song> songs, String introCopy, int type) {
        this.name = name;
        this.description = description;
        this.songs.addAll(songs); // ?
        this.introCopy = introCopy;
        this.type = type;
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public String getIntroCopy() {
        return introCopy;
    }

    public int getType() {
        return type;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public void addSong(Song song) {
        this.songs.add(song);
    }


    int getClearedBadgeCount(){
        int count = 0;
        for(int i=0;i< songs.size();i++){
            for(int z=0;z<SongsActivity.badgeList.size();z++){
                if(songs.get(i).songSaveFormat().equals(SongsActivity.badgeList.get(z).songSaveFormat())){
                    count++;
                }else{
                    //Log.d("동인",songs.get(i).songSaveFormat()+" != " +SongsActivity.badgeList.get(z).songSaveFormat());
                }
            }
        }
        return count;
    }

    float getPercent(){
        return (getClearedBadgeCount()/(float)songs.size());
    }


    // 여기 아래부터는 Parcel 써서 데이터 넘기고 받는 코드
    protected Category(Parcel in){
        name = in.readString();
        description = in.readString();
        introCopy = in.readString();
        type = in.readInt();
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeString(introCopy);
        parcel.writeInt(type);
    }
    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>(){
        @Override
        public Category createFromParcel(Parcel parcel) {
            return new Category(parcel);
        }
        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
    // Parcel 관련 코드 끝



} // end of class

