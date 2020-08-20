package com.dane.jpopword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.dane.jpopword.SplashActivity.categoryAll;

public class CategoryActivity extends AppCompatActivity {

    Category categoryFromPrevAct = null;
    int categoryNum = 0;


    Category findCategoryByName(String name){
        Category temp = null;
        for(int i=0;i<categoryAll.size();i++){
            if(categoryAll.get(i).getName().equals(name)){
                temp = categoryAll.get(i);
            }
        }
        return temp;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoryFromPrevAct = findCategoryByName(getIntent().getStringExtra("categoryName"));

        if(getIntent().getIntExtra("type",1)==1){

        }else{ // type=2

        }


        //categoryNum = getIntent().getIntExtra("categoryNum",0);
        //categoryFromSongsAct = getIntent().getParcelableExtra("category");
        // ArrayList<Song> songsFromSongAct = getIntent().getParcelableArrayListExtra("songs");
        // Songs 를 받지 말고, 어차피 SongsAll이 static으로 SongsActivity에 올라가 있으니까, Youtube로 넘길 때 이 static에서 꺼내쓰면 될 듯?

        RecyclerView recyclerViewSongs = (RecyclerView)findViewById(R.id.category_recyclerView_songs);
        recyclerViewSongs.setLayoutManager(new LinearLayoutManager(this));

        SongsInCategoryAdapter adapter = new SongsInCategoryAdapter(categoryFromPrevAct.getSongs(), this);
        recyclerViewSongs.setAdapter(adapter);

        TextView textViewTitle = (TextView)findViewById(R.id.category_textView_name);
        TextView textViewDescription = (TextView)findViewById(R.id.category_textView_description);

        //Log.d("동인",""+SongsActivity.categoryAll.get(categoryNum).getName());
        textViewTitle.setText(categoryFromPrevAct.getName());
        textViewDescription.setText(categoryFromPrevAct.getDescription());
    }

    SongsInCategoryAdapter.OnItemClickListener recyclerViewListener = new SongsInCategoryAdapter.OnItemClickListener(){
        @Override
        public void onItemClick(View v, int position) { // 아이템 클릭 이벤트를 MainActivity에서 처리
            //Toast.makeText(getApplicationContext(), "Item# : "+position,Toast.LENGTH_SHORT).show();
            //LogFirebaseAnalytics("FEED_SONG","FEED_SONG_"+"ADAPTER_"+adapterNum+"_POS_"+position,"Click"); TODO 나중에 살리셈

            // Feed->Youtube
            Intent intent = new Intent(CategoryActivity.this, YoutubeActivity.class);
            intent.putExtra("selectedSong", categoryFromPrevAct.getSongs().get(position));
            intent.putParcelableArrayListExtra("words", categoryFromPrevAct.getSongs().get(position).getWords());
            intent.putParcelableArrayListExtra("recommend", categoryFromPrevAct.getSongs());
            //intent.putExtra("mode", 1);
            startActivity(intent);
        }
    };
}
