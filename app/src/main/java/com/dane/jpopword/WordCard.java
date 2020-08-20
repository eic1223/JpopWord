package com.dane.jpopword;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class WordCard implements Parcelable{
    @SerializedName("songId")
    public
    int songId; // songs의 각각의 song들과 같음
    @SerializedName("wordId")
    int wordId; // 1~20
    @SerializedName("wordJap")
    String wordJap;
    @SerializedName("wordJapPron")
    String wordJapPron;
    @SerializedName("wordKor")
    String wordKor;
    @SerializedName("wordKorPron")
    String wordKorPron;
    @SerializedName("sentenceJap")
    String sentenceJap;
    @SerializedName("sentenceKor")
    String sentenceKor;
    @SerializedName("sentencePron")
    String sentencePron;

    public WordCard(int songId, int wordId, String wordJap, String wordJapPron, String wordKor, String wordKorPron,
                    String sentenceJap, String sentenceKor, String sentencePron) {
        this.wordJap = wordJap;
        this.wordJapPron = wordJapPron;
        this.wordKor = wordKor;
        this.wordKorPron = wordKorPron;
        this.sentenceJap = sentenceJap;
        this.sentenceKor = sentenceKor;
        this.sentencePron = sentencePron;
    }

    public int getSongId() {
        return songId;
    }

    public int getWordId() {
        return wordId;
    }

    public String getWordJap() {
        return wordJap;
    }

    public String getWordJapPron() {
        return wordJapPron;
    }

    public String getWordKor() {
        return wordKor;
    }

    public String getWordKorPron() {
        return wordKorPron;
    }

    public String getSentenceJap() {
        return sentenceJap;
    }

    public String getSentencePron() {
        return sentencePron;
    }

    public String getSentenceKor() {
        return sentenceKor;
    }

    // ArrayList<WordCArd>에 저장되는 포맷 : 이렇게 하자.
    public String wordSaveFormat(){
        return getWordJap()+"/"+getWordKorPron();
    }

    // 여기 아래부터는 Parcel 써서 데이터 넘기고 받는 코드
    protected WordCard(Parcel in){
        songId = in.readInt();
        wordId = in.readInt();
        wordJap = in.readString();
        wordJapPron = in.readString();
        wordKor = in.readString();
        wordKorPron = in.readString();
        sentenceJap = in.readString();
        sentenceKor = in.readString();
        sentencePron = in.readString();
    }
    public static final Creator<WordCard> CREATOR = new Creator<WordCard>(){
        @Override
        public WordCard createFromParcel(Parcel parcel) {
            return new WordCard(parcel);
        }

        @Override
        public WordCard[] newArray(int size) {
            return new WordCard[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(songId);
        parcel.writeInt(wordId);
        parcel.writeString(wordJap);
        parcel.writeString(wordJapPron);
        parcel.writeString(wordKor);
        parcel.writeString(wordKorPron);
        parcel.writeString(sentenceJap);
        parcel.writeString(sentenceKor);
        parcel.writeString(sentencePron);
    }
    // Parcel 관련 코드 끝

} // end of script
