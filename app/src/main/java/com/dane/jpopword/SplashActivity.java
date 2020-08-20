package com.dane.jpopword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.dane.jpopword.model.JsonFileReader;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    public static ArrayList<WordCard> wordsAll; //
    public static ArrayList<Song> songsAll; //
    public static ArrayList<Category> categoryAll ;//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        /*// 모든 word가 담긴 Json 파일 읽어서 words 객체 리스트에 넣는다.
        SongsActivity.wordsAll = JsonFileReader.makeWordsObjectFromJsonFile(this,"words.json");
        // 모든 songs가 닮긴 Json파일을 읽어서 카테고리 ArrayList에 분류해 담는다. words도 다 들어감.
        SongsActivity.songsAll = JsonFileReader.makeObjectFromJsonFile(this,"songs.json", SongsActivity.wordsAll);
        // 모든 categories가 닮긴 Json파일을 읽어서 카테고리 ArrayList에 분류해 담는다. songs도 다 들어감.
        SongsActivity.categoryAll = JsonFileReader.makeCategoriesFromJsonFile(this, "categories.json", SongsActivity.songsAll);*/

        // ===================================== //
        ArrayList<WordCard> words = JsonFileReader.makeWordsObjectFromJsonFile(this, "words.json");
        ArrayList<Song> songs = JsonFileReader.makeObjectFromJsonFile(this,"songs.json", words) ;
        ArrayList<Category> categories = JsonFileReader.makeCategoriesFromJsonFile(this,"categories.json", songs);

        final TextView textViewLine1 = (TextView)findViewById(R.id.splash_textViewLine1);
        final TextView textViewLine2 = (TextView)findViewById(R.id.splash_textViewLine2);
        final TextView textViewLine3 = (TextView)findViewById(R.id.splash_textViewLine3);

        textViewLine1.setVisibility(View.VISIBLE);
        textViewLine2.setVisibility(View.INVISIBLE);
        textViewLine3.setVisibility(View.INVISIBLE);

        /*new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                textViewLine1.setVisibility(View.VISIBLE);
            }
        }, 100); // 1*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                textViewLine2.setVisibility(View.VISIBLE);
            }
        }, 1000); // 1
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                textViewLine3.setVisibility(View.VISIBLE);
            }
        }, 2000); // 1

        /*SongsActivity.categoryAll = JsonFileReader.makeCategoriesFromJsonFile(
                this,
                "categories.json",
                JsonFileReader.makeObjectFromJsonFile(
                        this,"songs.json",
                        JsonFileReader.makeWordsObjectFromJsonFile(
                                this,
                                "words.json"
                        )
                )
        );*/

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, SongsActivity.class);
                //intent.putParcelableArrayListExtra("songs", );
                startActivity(intent);
            }
        }, 3000); // 스플래시 화면 3초 동안 보여준 후에 이동
        // 여기까지
    }
}
