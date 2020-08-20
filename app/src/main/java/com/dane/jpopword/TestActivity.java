package com.dane.jpopword;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.dane.jpopword.model.JsonFileReader;
import com.dane.jpopword.model.newCategory;
import com.dane.jpopword.model.newSong;
import com.dane.jpopword.model.newWord;
import com.google.api.LogDescriptor;

import java.util.ArrayList;
import java.util.Scanner;


import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class TestActivity extends AppCompatActivity {

    Button btnTest;
    TextView textViewTest;

    public static ArrayList<newCategory> newCategoriesAll;
    public static ArrayList<newSong> newSongsAll;
    public static ArrayList<newWord> newWordsAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_test);

        newCategoriesAll = JsonFileReader.newAllCategories(TestActivity.this,"newCategories.json");
        newSongsAll = JsonFileReader.newAllSongs(TestActivity.this,"newSongs.json");
        newWordsAll = JsonFileReader.newAllWords(TestActivity.this,"newWords.json");

        btnTest = (Button)findViewById(R.id.button_test);
        textViewTest = (TextView)findViewById(R.id.textView_test);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("테스트",newCategoriesAll.get(0).toString());
                Log.d("테스트",newCategoriesAll.get(0).getName().toString());

                Log.d("테스트",newSongsAll.get(0).toString());
                Log.d("테스트",newSongsAll.get(0).getSingerJap().toString());

                Log.d("테스트",newWordsAll.get(0).toString());
                Log.d("테스트",newWordsAll.get(0).getWordJap().toString());


                /*ArrayList<Integer> temp = new ArrayList<Integer>();
                String raw = "1,3,5,7,9";
                String[] splitedRaw = raw.split(",");
                for (int i=0;i<splitedRaw.length;i++){
                    temp.add(Integer.valueOf(splitedRaw[i]));
                }
                for (int k=0;k<temp.size();k++){
                    Log.d("테스트",temp.get(k).toString());
                }*/
                // textViewTest.setText("");
            }
        });

    }

}
