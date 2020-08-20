package com.dane.jpopword;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
//import android.content.SharedPreferences;
import com.dane.jpopword.model.SharedPrefControl;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashSet;

import static com.dane.jpopword.SplashActivity.wordsAll;

public class WordsActivity extends AppCompatActivity implements WordCardAdapter.OnStarClickListener{
    Song songFromSongsAct; // 데이터 저장용
    ArrayList<WordCard> wordsFromPrevAct; // 데이터 저장용

    private FirebaseAnalytics mFirebaseAnalytics;

    static RecyclerView mRecyclerViewWord;
    static RecyclerView mRecyclerViewWordMode2;

    WordCardAdapter adapter;
    WordCardAdapter adapterMode2;

    public static int WORD_COUNT = 1;

    public int CURRENT_MODE = 0;
    public int SONG_MODE = 1;
    public int VOCA_MODE = 2;

    static TextView textViewWordNum;

    LinearLayout layoutNoneWords;

    int RC_SIGN_IN = 9000;
    private String FILE_NAME_SHAREDPREF = "sFile";
    private String VOCA = "voca";
    FirebaseAuth mAuth;// ...

    CallbackManager mCallbackManager; // 페이스북 추가 중

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_words);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this); // Firebase Analytics 추가 중

        WORD_COUNT = 0;
        textViewWordNum = (TextView)findViewById(R.id.textView_wordNum);
        layoutNoneWords = (LinearLayout)findViewById(R.id.words_layout_none_words);

        int startingVocaWordNum = getIntent().getIntExtra("selectedVocaWordNum",0);

        /*SharedPreferences prefVoca = getSharedPreferences("sFile", Context.MODE_PRIVATE);
        HashSet<String> vocaData = new HashSet<String>(prefVoca.getStringSet("voca", new HashSet<String>()));*/
        HashSet<String> vocaData = SharedPrefControl.loadDataSetByKeyInSharedPref(this,FILE_NAME_SHAREDPREF,VOCA);

        ArrayList<WordCard> vocaWords = new ArrayList<>();

        for(int k=0;k<wordsAll.size();k++){
            if(vocaData.contains(wordsAll.get(k).wordSaveFormat())){
                vocaWords.add(wordsAll.get(k));
            }
        }

        wordsFromPrevAct = vocaWords; // wordsFromPrevAct는 채워놔야함

        // 중복인지 체크한 후 추가하는 코드
        int v = vocaWords.size();
        ArrayList<WordCard> tempList = new ArrayList<>();
        int index = 0;
        for ( int k=0; k < v; k++ ) {
            if ( index == 0 ) {
                vocaWords.add(vocaWords.get(k));
                index++;
            } else {
                boolean isDuplication = false;
                for ( int m=0; m < tempList.size(); m++ ) {
                    if ( tempList.get(m).wordSaveFormat().equals(vocaWords.get(k).wordSaveFormat())) {
                        isDuplication = true;
                        Log.d("FUCK","중복이라서 제낀다 : " + vocaWords.get(k).wordSaveFormat());
                        break;
                    }
                }
                if (!isDuplication) {
                    tempList.add(vocaWords.get(k));
                    index++;
                }
            }
        }
        vocaWords.clear();
        vocaWords.addAll(tempList);
        //

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        mRecyclerViewWord = findViewById(R.id.recyclerViewWords);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewWord.setLayoutManager(layoutManager) ;

        // 어댑터 만들어서 리사이클러뷰에 붙여주고
        adapterMode2 = new WordCardAdapter(vocaWords, this, this, 1);
        mRecyclerViewWord.setAdapter(adapterMode2);

        mRecyclerViewWord.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    /*View centerView = snapHelper3.findSnapView(layoutManager);
                    int pos = layoutManager.getPosition(centerView);
                    textViewWordNum.setText((pos+1)+"/"+wordsFromPrevAct.size());
                    Log.e("Snapped Item Position:",""+pos);*/
                }
            }
        });

        // 스타팅 포인트로 이동시켜준다
        mRecyclerViewWord.scrollToPosition(startingVocaWordNum);
        WORD_COUNT = startingVocaWordNum + 1;

        layoutNoneWords.setVisibility(View.GONE);
    } // end of onCreate();

    void LogFirebaseAnalytics(String ITEM_ID, String ITEM_NAME, String CONTENT_TYPE){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, ITEM_ID);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, ITEM_NAME);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, CONTENT_TYPE);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    // 단어를 확인했다는 버튼. (Adapter에서 호출됨)
    /*public static void checkWord(boolean isNext, int wordsSize){
        if(isNext){
            if(WORD_COUNT<wordsSize) WORD_COUNT ++;
        }else{
            if(WORD_COUNT>1) WORD_COUNT --;
        }
        String textInBtn = "";
        textInBtn = ""+WORD_COUNT + "/" + wordsSize;
        textViewWordNum.setText(textInBtn);
    }*/

    // (Adapter에서 호출됨)
    public static void showWordCard(int position){
        mRecyclerViewWord.scrollToPosition(position);
    }


    // 단어추가(별) 이미지버튼 눌렸을 때 동작하는 코드
    // 단어장은 favorite songs랑 달리 Songs(voca tab)과 Words 두 개의 액티비티에 존재한다.
    // 그러니까 onStartClick 이거를 각각의 액티비티에서 오버라이드 해야함
    @Override
    public void onStarClick(View v, int pos, String wordSelected) {
        if(CURRENT_MODE==SONG_MODE){
            Log.d("WORD ACTIVITY", "SONG_MODE에서 클릭됨");
        }else{ // CURRENT_MODE ==  VOCA_MODE
            adapterMode2.removeItem(pos);
            adapterMode2.notifyItemRemoved(pos);
            //adapterMode2.notifyItemRangeChanged(pos,1); // 이거 이렇게 쓰는거 맞음?
            //refreshWholeStarBtns(wordSelected, true);
            Log.d("WORD ACTIVITY", "VOCA_MODE에서 클릭됨");

            // 만약 단어를 전부 취소해서 사라지면?
            if(adapterMode2.getItemCount()<=0){
                // 단어가 없다고 말해줘야지. 저장된 단어가 없습니다. 라는 멘트를 보여줘야지
                layoutNoneWords.setVisibility(View.VISIBLE);
            }else{
                layoutNoneWords.setVisibility(View.GONE);
            }
        }
    }

}
