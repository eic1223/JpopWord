package com.dane.jpopword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
//import android.content.SharedPreferences;
import com.dane.jpopword.model.SharedPrefControl;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class YoutubeActivity extends AppCompatActivity implements SongsAdapter.OnHeartClickListener, LyricsFragment.OnFragmentInteractionListener, StudyFragment.OnFragmentInteractionListener, QuizFragment.OnFragmentInteractionListener {
    static Song songFromSongsAct; // 데이터 저장용
    static ArrayList<WordCard> wordsFromSongAct; // 데이터 저장용
    static ArrayList<Song> songsRecommend; // 데이터 저장용(?) 얘도 구성해줘야함

    static FirebaseAnalytics mFirebaseAnalytics;

    static int ACTIVITY_MODE = 0;
    int MODE_FEED = 1;
    int MODE_FAVORITE = 2;
    static int favoritesPos;

    String FILE_NAME_SHAREDPREF = "sFile";
    String FAVORITES = "favorites";

    YouTubePlayerView youTubePlayerView;

    TextView mTextViewLyrics;

    //Button mBtnChangeLyrics;
    String[] lyrics;
    ImageView imageViewHeart, imageViewWords, imageViewQuiz;//, imageViewPlayNext,imageViewPlayPrev;

    private boolean mIsHeartBtnClicked = false;

    Button mBtnGoNext, mBtnGoWords;
    int LYRICS_TYPE = 0;
    ScrollView scrollViewLyrics;

    // 파이어베이스 로그인 관련
    int RC_SIGN_IN = 9000;
    FirebaseAuth mAuth;// ...

    CallbackManager mCallbackManager; // 페이스북 추가 중

    // 프래그먼트 개발 중...
    Fragment lyricsFragment;
    Fragment studyFragment;
    Fragment quizFragment;
    static int CURRENT_TAB_NUM = 0;


    // 여기 resume에 들어있는 코드 원래 onCreate()에 있었는데 왓다갔다할 때 유튜브가 리프레시 안되는 경우가 있어서 resume으로 뺌.
    @Override
    protected void onResume(){
        super.onResume();
    }

    //프래그먼트와 프래그먼트끼리 직접접근을하지않는다. 프래그먼트와 엑티비티가 접근함
    public void onFragmentChange(int index){
        if(index == 0 ){
            getSupportFragmentManager().beginTransaction().replace(R.id.youtube_frameLayout, lyricsFragment).commit();
        }else if(index == 1){
            getSupportFragmentManager().beginTransaction().replace(R.id.youtube_frameLayout, studyFragment).commit();
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.youtube_frameLayout,quizFragment).commit();
        }
    }

    static void LogFirebaseAnalytics(String ITEM_ID, String ITEM_NAME, String CONTENT_TYPE){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, ITEM_ID);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, ITEM_NAME);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, CONTENT_TYPE);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_youtube);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this); // Firebase Analytics 추가 중

        mCallbackManager = CallbackManager.Factory.create(); // 페이스북 로그인용 콜백

        // [이전 액티비티로부터 데이터(intent) 받는 부분]
        // 이제 Feed->Youtube->Quiz->(Word) 순으로 진행할거임
        songFromSongsAct = getIntent().getParcelableExtra("selectedSong");
        wordsFromSongAct = getIntent().getParcelableArrayListExtra("words");
        songsRecommend = getIntent().getParcelableArrayListExtra("recommend");
        ACTIVITY_MODE = getIntent().getIntExtra("mode",1);
        favoritesPos = getIntent().getIntExtra("favoritesPos",0);
        Log.d("youtube가 받은 Song 체크", songFromSongsAct.getTitleKor());
        Log.d("youtube가 받은 Words 체크", wordsFromSongAct.get(0).wordJap);

        // 새로운 유튜브 플레이어 라이브러리
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(songFromSongsAct.getYoutubeId(), 0);
                Log.d("동인","ready");
            }
            // Called when an error occurs in the player.
            public void onError(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerError error){
                if(error.name().equals("INVALID_PARAMETER_IN_REQUEST")||error.name().equals("VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER")){
                    youTubePlayer.loadVideo(songFromSongsAct.getSubYoutubeId(), 0);
                }
                LogFirebaseAnalytics("Youtube error",""+error.name()+", video id: "+songFromSongsAct.getYoutubeId(), "Error");
                Log.d("Youtube error",""+error.name()+", video id: "+songFromSongsAct.getYoutubeId()+", re-load with subYoutubeId("+songFromSongsAct.getSubYoutubeId()+")");
            };
        });

        //0=가사/발음/해석, 1=가사/해석, 2=가사
        lyrics = manipulatedRawLyrics(songFromSongsAct.getLyricsJapKorPron());

        youTubePlayerView = findViewById(R.id.youtube_player_view);

        // 뷰 만들 때 좋아요 눌린 곡이라면 좋아요 눌린 이미지로 시작하게 만들어주는 부분
        /*SharedPreferences pref = getSharedPreferences("sFile", Context.MODE_PRIVATE);
        HashSet<String> dataForHeartCheck = new HashSet<String>(pref.getStringSet("favorites", new HashSet<String>())); // "favorite"이라는 키로 저장되어있던 데이터*/
        HashSet<String> dataForHeartCheck = SharedPrefControl.loadDataSetByKeyInSharedPref(this,FILE_NAME_SHAREDPREF, FAVORITES);

        imageViewHeart = (ImageView)findViewById(R.id.youtube_imageView_heart);
        if(dataForHeartCheck.contains(songFromSongsAct.songSaveFormat())){
            imageViewHeart.setImageResource(R.drawable.heart_red);
            mIsHeartBtnClicked = true;
        }else{
            imageViewHeart.setImageResource(R.drawable.heart_white);
            mIsHeartBtnClicked = false;
        }
        imageViewHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 기기에 저장되어있던 데이터 불러와서
                /*SharedPreferences pref = getSharedPreferences("sFile", Context.MODE_PRIVATE);
                HashSet<String> dataForSaveHeart = new HashSet<String>(pref.getStringSet("favorites", new HashSet<String>())); // "favorite"이라는 키로 저장되어있던 스트링셋 불러오고
                Log.d("Heart SAVE PROCESS","Heart Before-Saved Data: " + dataForSaveHeart);*/
                HashSet<String> dataForSaveHeart = SharedPrefControl.loadDataSetByKeyInSharedPref(YoutubeActivity.this,FILE_NAME_SHAREDPREF, FAVORITES);

                if(mIsHeartBtnClicked){
                    dataForSaveHeart.remove(songFromSongsAct.songSaveFormat()); // 좋아요 취소하면 SaveFormat을 HashSet에서 빼고
                    imageViewHeart.setImageResource(R.drawable.heart_white);
                    LogFirebaseAnalytics("YOUTUBE_HEART","YOUTUBE_HEART_"+songFromSongsAct.songSaveFormat(),"Remove");
                }else{
                    dataForSaveHeart.add(songFromSongsAct.songSaveFormat()); // 좋아요 하면 HashSet에 SaveFormat을 추가하고
                    imageViewHeart.setImageResource(R.drawable.heart_red);
                    LogFirebaseAnalytics("YOUTUBE_HEART","YOUTUBE_HEART_"+songFromSongsAct.songSaveFormat(),"Add");
                }
                mIsHeartBtnClicked = !mIsHeartBtnClicked;

                // 기기에 다시 저장해주는 과정
                /*SharedPreferences.Editor editor = pref.edit(); // 에디터를 열어서
                editor.putStringSet("favorites", (Set<String>) dataForSaveHeart);
                editor.apply(); // 변경 완료. .commit()을 써도 댐
                Log.d("Heart SAVE PROCESS","Heart After-Saved Data: " + pref.getStringSet("favorites", new HashSet<String>()));*/
                SharedPrefControl.saveDataSetByKeyInSharedPref(YoutubeActivity.this,FILE_NAME_SHAREDPREF,FAVORITES,dataForSaveHeart);
            }
        });

        // 모드에 따라서 다음곡/이전곡 재생 이미지가 보이거나 안보이게 해주는 코드.
        // 이걸 어디로 옮기지?
        if(ACTIVITY_MODE!=MODE_FEED){
        }

        lyricsFragment = new LyricsFragment();
        Bundle bundleLyrics = new Bundle(3); // 파라미터는 전달할 데이터 개수
        bundleLyrics.putString("lyricsJapKorPron", lyrics[0]);
        bundleLyrics.putString("lyricsJapKor", lyrics[1]);
        bundleLyrics.putString("lyricsJap", lyrics[2]);
        bundleLyrics.putString("updateDate", songFromSongsAct.getUpdateDate());
        lyricsFragment.setArguments(bundleLyrics);

        studyFragment = new StudyFragment();
        quizFragment = new QuizFragment();
        //

        // 새로운 탭바 관련 코드
        // 바꾸려는 탭바
        final BubbleNavigationLinearView bubbleNavigation = (BubbleNavigationLinearView) findViewById(R.id.top_navigation_linearView);
        bubbleNavigation.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                //navigation changed, do something
                CURRENT_TAB_NUM = position;
                Log.d("TAP", "CURRENT TAB NUM : "+CURRENT_TAB_NUM);

                // Voca, Favorites 탭의 경우 탭이 선택될 때마다 새롭게 추가/취소 된 리스트를 받아와서 업데이트를 시켜줘야 함
                switch (CURRENT_TAB_NUM){
                    case 0: // lyrics tab
                        if(QuizFragment.isQuizPlaying){

                            // Use the Builder class for convenient dialog construction
                            final AlertDialog.Builder builder = new AlertDialog.Builder(YoutubeActivity.this);
                            builder.setMessage("퀴즈를 그만하시겠어요?")
                                    .setPositiveButton("그만두기", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            QuizFragment.isQuizPlaying = false;
                                            getSupportFragmentManager().beginTransaction().replace(R.id.youtube_frameLayout,lyricsFragment).commit();
                                            LogFirebaseAnalytics("YOUTUBE_LYRICS_TAB","YOUTUBE_LYRICS_TAB"+songFromSongsAct.songSaveFormat(),"Click");
                                        }
                                    })
                                    .setNegativeButton("계속하기", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // User cancelled the dialog
                                            bubbleNavigation.setCurrentActiveItem(2);
                                        }
                                    });
                            // Create the AlertDialog object and return it
                            builder.create();
                            builder.show();
                        }else{
                            getSupportFragmentManager().beginTransaction().replace(R.id.youtube_frameLayout,lyricsFragment).commit();
                            LogFirebaseAnalytics("YOUTUBE_LYRICS_TAB","YOUTUBE_LYRICS_TAB"+songFromSongsAct.songSaveFormat(),"Click");
                        }
                        break;
                    case 1: // study tab (words)
                        if(QuizFragment.isQuizPlaying){

                            // Use the Builder class for convenient dialog construction
                            final AlertDialog.Builder builder = new AlertDialog.Builder(YoutubeActivity.this);
                            builder.setMessage("퀴즈를 그만하시겠어요?")
                                    .setPositiveButton("그만두기", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            QuizFragment.isQuizPlaying = false;
                                            getSupportFragmentManager().beginTransaction().replace(R.id.youtube_frameLayout,studyFragment).commit();
                                            LogFirebaseAnalytics("YOUTUBE_WORDS_TAB","YOUTUBE_WORDS_TAB"+songFromSongsAct.songSaveFormat(),"Click");
                                        }
                                    })
                                    .setNegativeButton("계속하기", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // User cancelled the dialog
                                            bubbleNavigation.setCurrentActiveItem(2);
                                        }
                                    });
                            // Create the AlertDialog object and return it
                            builder.create();
                            builder.show();
                        }else{
                            getSupportFragmentManager().beginTransaction().replace(R.id.youtube_frameLayout,studyFragment).commit();
                            LogFirebaseAnalytics("YOUTUBE_WORDS_TAB","YOUTUBE_WORDS_TAB"+songFromSongsAct.songSaveFormat(),"Click");
                        }
                        break;
                    case 2: // quiz tab
                        if(!QuizFragment.isQuizPlaying){
                            getSupportFragmentManager().beginTransaction().replace(R.id.youtube_frameLayout,quizFragment).commit();
                            LogFirebaseAnalytics("YOUTUBE_QUIZ_TAB","YOUTUBE_QUIZ_TAB"+songFromSongsAct.songSaveFormat(),"Click");
                        }
                        break;
                }
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.youtube_frameLayout,lyricsFragment).commit();

    } // end of onCreate()


    void changeCoverToOrigin(){
        // 이 노래가 커버곡이면 => 원곡으로 돌려
        Song songTemp;
        if(songFromSongsAct.getCover()!=0){ // 0이면 원곡, 0이 아니면 커버곡임
            //for (int i=0;i<SongsActivity.songsAll.size();i++){
            for (int i=0;i<SplashActivity.songsAll.size();i++){
                if(SplashActivity.songsAll.get(i).getSongId()==songFromSongsAct.getCover()){
                    songTemp = SplashActivity.songsAll.get(i);
                    songFromSongsAct = songTemp;
                }
            }
        }
    }

    public int convertPixelToDp(int input) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int)(input * scale + 0.5f);
    }

    @Override
    public void onBackPressed() {
        if(QuizFragment.isQuizPlaying){

            // Use the Builder class for convenient dialog construction
            final AlertDialog.Builder builder = new AlertDialog.Builder(YoutubeActivity.this);
            builder.setMessage("퀴즈를 그만하시겠어요?")
                    .setPositiveButton("그만두기", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            QuizFragment.isQuizPlaying = false;
                            LogFirebaseAnalytics("YOUTUBE_BACK","YOUTUBE_BACK"+songFromSongsAct.songSaveFormat(),"Click");
                            Log.d("Back Btn Override", "onBackPressed Called");
                            startActivity(new Intent(YoutubeActivity.this, SongsActivity.class));
                        }
                    })
                    .setNegativeButton("계속하기", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog

                        }
                    });
            // Create the AlertDialog object and return it
            builder.create();
            builder.show();
        }else{
            LogFirebaseAnalytics("YOUTUBE_BACK","YOUTUBE_BACK"+songFromSongsAct.songSaveFormat(),"Click");
            Log.d("Back Btn Override", "onBackPressed Called");
            startActivity(new Intent(this,SongsActivity.class));
        }
    }

    String[] manipulatedRawLyrics(String rawLyrics){
        Scanner scanner = new Scanner(rawLyrics);
        int lineNum = 0;

        StringBuilder stringBuilder1 = new StringBuilder(); // 원어+발음+해석
        StringBuilder stringBuilder2 = new StringBuilder(); // 원어+해석
        StringBuilder stringBuilder3 = new StringBuilder(); // 원어

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if(line.equals("***")){
                line = " \n";
                stringBuilder1.append(line);
                stringBuilder2.append(line);
                stringBuilder3.append(line);
            }else{
                line += "\n";
                if(lineNum%3==0){ //  첫 번째 줄 (원어)
                    stringBuilder1.append(line);
                    stringBuilder2.append(line);
                    stringBuilder3.append(line);
                }else if(lineNum%3==1){ // 두 번째 줄 (발음)
                    stringBuilder1.append(line);
                }else{ // 2 // 세 번째 줄 (해석)
                    stringBuilder1.append(line);
                    stringBuilder2.append(line);
                }
                lineNum++;
            }
        }
        scanner.close();

        //Log.d("Result", "Builder1 : "+stringBuilder1);
        //Log.d("Result", "Builder2 : "+stringBuilder2);
        //Log.d("Result", "Builder3 : "+stringBuilder3);

        String[] results = new String[3];

        results[0] = stringBuilder1.toString(); // 가사/발음/해석
        results[1] = stringBuilder2.toString(); // 가사/해석
        results[2] = stringBuilder3.toString(); // 가사


        // 가사-발음-해석이 모두 같은 경우(영어 같이) 한 줄만 적도록 하는 코드
        String[] newLines1 = results[0].split("\n");
        String[] newLines2 = results[1].split("\n");
        for(int i=1;i<newLines1.length;i++){
            if(!newLines1[i].equals("")){
                if((newLines1[i].equals(newLines1[i-1]))&&(newLines1[i].equals(newLines1[i-2]))){
                    newLines1[i] = "";
                    newLines1[i-1] = "";
                }
            }
        }
        for(int i=1;i<newLines2.length;i++){
            if(!newLines2[i].equals("")){
                if((newLines2[i].equals(newLines2[i-1]))&&(newLines2[i].equals(newLines2[i-2]))){
                    newLines2[i] = "";
                    newLines2[i-1] = "";
                }
            }
        }
        StringBuilder trimmedLyrics1 = new StringBuilder();
        StringBuilder trimmedLyrics2 = new StringBuilder();
        for(String i : newLines1){
            if(!i.equals("")){
                trimmedLyrics1.append(i+"\n");
            }
        }
        for(String i : newLines2){
            if(!i.equals("")){
                trimmedLyrics2.append(i+"\n");
            }
        }

        results[0] = trimmedLyrics1.toString(); // 가사/발음/해석
        results[1] = trimmedLyrics2.toString(); // 가사/
        //results[2] = stringBuilder3.toString(); // 가사


        return results;
    }

    @Override
    public void onHeartClick(View v, int pos, String titleKorSelected) {
        Log.d("Youtube Activity","Heart Image click");
    }


    // [프래그먼트 추가 중...]

    @Override
    public void onFragmentInteraction(Uri uri) {
        // 얘가 지금 2개 프래그먼트 전부 연결됨
    }


    //
} // end of YoutubeActivity Class
