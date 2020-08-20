package com.dane.jpopword.model;

import java.util.ArrayList;

public class newSong {
    private int Id;
    private String youtubeId;
    private String subYoutubeId;
    private String titleJap;
    private String titleKor;
    private String singerJap;
    private String singerKor;
    private String lyricsJapKorPron;
    private String albumImageUrl;
    private String updateDate;
    private String words;

    public newSong(int songId, String youtubeId, String subYoutubeId, String titleJap, String titleKor, String singerJap, String singerKor, String lyricsJapKorPron, String albumImageUrl, String updateDate, String words) {
        this.Id = songId;
        this.youtubeId = youtubeId;
        this.subYoutubeId = subYoutubeId;
        this.titleJap = titleJap;
        this.titleKor = titleKor;
        this.singerJap = singerJap;
        this.singerKor = singerKor;
        this.lyricsJapKorPron = lyricsJapKorPron;
        this.albumImageUrl = albumImageUrl;
        this.updateDate = updateDate;
        this.words = words;
    }

    public int getSongId() {
        return Id;
    }

    public void setSongId(int songId) {
        this.Id = songId;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public String getSubYoutubeId() {
        return subYoutubeId;
    }

    public void setSubYoutubeId(String subYoutubeId) {
        this.subYoutubeId = subYoutubeId;
    }

    public String getTitleJap() {
        return titleJap;
    }

    public void setTitleJap(String titleJap) {
        this.titleJap = titleJap;
    }

    public String getTitleKor() {
        return titleKor;
    }

    public void setTitleKor(String titleKor) {
        this.titleKor = titleKor;
    }

    public String getSingerJap() {
        return singerJap;
    }

    public void setSingerJap(String singerJap) {
        this.singerJap = singerJap;
    }

    public String getSingerKor() {
        return singerKor;
    }

    public void setSingerKor(String singerKor) {
        this.singerKor = singerKor;
    }

    public String getLyricsJapKorPron() {
        return lyricsJapKorPron;
    }

    public void setLyricsJapKorPron(String lyricsJapKorPron) {
        this.lyricsJapKorPron = lyricsJapKorPron;
    }

    public String getAlbumImageUrl() {
        return albumImageUrl;
    }

    public void setAlbumImageUrl(String albumImageUrl) {
        this.albumImageUrl = albumImageUrl;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public ArrayList<Integer> getWords() {
        ArrayList<Integer> temp = null;
        //String raw = "1,3,5,7,9";
        String raw = words;
        String[] splitedRaw = raw.split(",");
        for (int i=0;i<splitedRaw.length;i++){
            temp.add(Integer.valueOf(splitedRaw[i]));
        }
        //"1,3,5,7,9" => 1, 3, 5, 7, 9,
        return temp;
    }

    public void setWords(String words) {
        this.words = words;
    }
}
