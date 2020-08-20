package com.dane.jpopword;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

@Keep
public class Song implements Parcelable {
    @SerializedName("catName")
    private String catName;
    @SerializedName("songId")
    private int songId;
    @SerializedName("cover")
    private int cover;
    @SerializedName("youtubeId")
    private String youtubeId;
    @SerializedName("subYoutubeId")
    private String subYoutubeId;
    @SerializedName("titleKor")
    private String titleKor;
    @SerializedName("titleJap")
    private String titleJap;
    @SerializedName("singerKor")
    private String singerKor;
    @SerializedName("singerJap")
    private String singerJap;
    @SerializedName("lyricsJapKorPron")
    private String lyricsJapKorPron;
    @SerializedName("words")
    private ArrayList<WordCard> words; // wordCards 아니고 words임 주의하셈.
    @SerializedName("albumImageUrl")
    private String albumImageUrl;
    @SerializedName("updateDate")
    private String updateDate; // YYYY-MM-DD


    public Song(String catName, int songId, int cover, String youtubeId, String subYoutubeId, String titleKor,
                String titleJap, String singerKor, String singerJap, String lyricsJapKorPron, String albumImageUrl, String updateDate) { // Word는 나중에 추가. 생성할 때는 필요 없음.
        this.catName = catName;
        this.songId = songId;
        this.youtubeId = youtubeId;
        this.subYoutubeId = subYoutubeId;
        this.cover = cover;
        this.titleKor = titleKor;
        this.titleJap = titleJap;
        this.singerKor = singerKor;
        this.singerJap = singerJap;
        this.lyricsJapKorPron = lyricsJapKorPron;
        this.words = new ArrayList<WordCard>();
        this.albumImageUrl = albumImageUrl;
        this.updateDate = updateDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public String getCatName() {
        return catName;
    }

    public int getSongId() {
        return songId;
    }

    public int getCover() {
        return cover;
    }

    public String getTitleKor() {
        return titleKor;
    }

    public String getTitleJap() {
        return titleJap;
    }

    public String getSingerKor() {
        return singerKor;
    }

    public String getSingerJap() {
        return singerJap;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public String getSubYoutubeId() {
        return subYoutubeId;
    }

    public String getLyricsJapKorPron() {
        return lyricsJapKorPron;
    }

    public ArrayList<WordCard> getWords() {
        return words;
    }

    public String getAlbumImageUrl() {
        return albumImageUrl;
    }

    public void setWords(ArrayList<WordCard> words) {
        this.words = words;
    }

    public void addWord(WordCard word){
        this.words.add(word);
    }

    public void addMembers(ArrayList<WordCard> words){
        this.words.addAll(words);
    }

    public int countMembers(){
        return words.size();
    }

    public WordCard getMember(int position){
        return words.get(position);
    }

    // ArrayList<Song>에 저장되는 포맷 : 이렇게 하자.
    public String songSaveFormat(){
        return titleKor+"/"+singerKor;
    }

    public String getTitleKorAndJap(){
        if(titleKor.equals(titleJap)){
            return titleKor;
        }else{
            return titleKor+"("+titleJap+")";
        }
    }

    public String getSingerKorAndJap(){
        if(singerKor.equals(singerJap)){
            return singerKor;
        }else{
            return singerKor+"("+singerJap+")";
        }
    }

    // 여기 아래부터는 Parcel 써서 데이터 넘기고 받는 코드
    protected Song(Parcel in){
        songId = in.readInt();
        cover = in.readInt();
        youtubeId = in.readString();
        subYoutubeId = in.readString();
        titleKor = in.readString();
        titleJap = in.readString();
        singerKor = in.readString();
        singerJap = in.readString();
        lyricsJapKorPron = in.readString();
        albumImageUrl = in.readString();
        updateDate = in.readString();
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(songId);
        parcel.writeInt(cover);
        parcel.writeString(youtubeId);
        parcel.writeString(subYoutubeId);
        parcel.writeString(titleKor);
        parcel.writeString(titleJap);
        parcel.writeString(singerKor);
        parcel.writeString(singerJap);
        parcel.writeString(lyricsJapKorPron);
        parcel.writeString(albumImageUrl);
        parcel.writeString(updateDate);
    }
    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>(){
        @Override
        public Song createFromParcel(Parcel parcel) {
            return new Song(parcel);
        }
        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }


    // Parcel 관련 코드 끝

}
