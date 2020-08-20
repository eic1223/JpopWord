package com.dane.jpopword;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
//import android.content.SharedPreferences;
import com.dane.jpopword.model.SharedPrefControl;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.dane.jpopword.SplashActivity.categoryAll;
import static com.dane.jpopword.SplashActivity.wordsAll;


public class QuizFragment extends Fragment implements WordCardAdapter.OnStarClickListener, SongsAdapter.OnHeartClickListener  {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    int RC_SIGN_IN = 9000;
    private String FILE_NAME_SHAREDPREF = "sFile";
    private String BADGES = "badges";

    private String mParam1;
    private String mParam2;

    static boolean isQuizPlaying = false;
    private int currentQuizNum;
    private static final int totalQuizNum = 10; // 이건 내가 퀴즈를 몇 문제나 낼거냐에 따라 바꾸는 애
    private int score;

    private WordCard answerWord;
    private ArrayList<WordCard> wordsForPickQuiz; // 중복 안되게 뽑기위해 이용되는 녀석
    private ArrayList<WordCard> playerWrongWords; // 플레이어가 틀린 문항들만 순서대로 저장해 놓을 녀석
    private ArrayList<String> playerWrongAnswers; // 플레이어가 틀린 문제들 뭘 골랐었는지 순서대로 저장해 놓을 녀석


    // BeforeStart, OnGoing에 필요한 애들
    private TextView textViewQuestion, textViewQuestionHint, textViewQuestionNum;
    private Button btnOption1, btnOption2, btnOption3, btnStartQuiz;
    private Button[] btns;
    private ImageView imageViewResultIcon1, imageViewResultIcon2, imageViewResultIcon3, imageViewResultIcon4,
            imageViewResultIcon5, imageViewResultIcon6, imageViewResultIcon7, imageViewResultIcon8, imageViewResultIcon9, imageViewResultIcon10;
    private ImageView[] imageViewResultIcons;

    // Result에 필요한 애들
    private TextView textViewFinalScore;
    private Button btnGoSongs;

    RecyclerView recyclerViewWrongWords;
    WordCardAdapter quizResultAdapter;

    private RelativeLayout layoutQuizBeforeStart, layoutQuizOnGoing;
    private LinearLayout layoutQuizResult, layoutWrongWordsNone, layoutWrongWordsIs;

    private OnFragmentInteractionListener mListener;

    public QuizFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuizFragment.
     */
    public static QuizFragment newInstance(String param1, String param2) {
        QuizFragment fragment = new QuizFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        // Song, Words 데이터 가져오기(넘겨 받기)
        wordsForPickQuiz = new ArrayList<>();
        wordsForPickQuiz.addAll(YoutubeActivity.wordsFromSongAct);

        layoutQuizBeforeStart = (RelativeLayout)view.findViewById(R.id.quizFragment_layout_beforeStart);
        layoutQuizOnGoing = (RelativeLayout)view.findViewById(R.id.quizFragment_layout_onGoing);
        layoutQuizResult = (LinearLayout)view.findViewById(R.id.quizFragment_layout_result);

        textViewQuestion = (TextView)view.findViewById(R.id.quizFragment_textView_questionWord);
        textViewQuestionHint = (TextView)view.findViewById(R.id.quizFragment_textView_questionWordHint);
        textViewQuestionNum = (TextView)view.findViewById(R.id.quizFragment_textView_questionNum);
        btnOption1 = (Button)view.findViewById(R.id.quizFragment_btn_option1);
        btnOption2 = (Button)view.findViewById(R.id.quizFragment_btn_option2);
        btnOption3 = (Button)view.findViewById(R.id.quizFragment_btn_option3);
        btns = new Button[]{btnOption1, btnOption2, btnOption3};

        btnOption1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOptionBtn(btnOption1.getText().toString());
            }
        });
        btnOption2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOptionBtn(btnOption2.getText().toString());
            }
        });
        btnOption3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOptionBtn(btnOption3.getText().toString());
            }
        });

        recyclerViewWrongWords = view.findViewById(R.id.quizFragment_recyclerView_result_wrongWords);
        recyclerViewWrongWords.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        layoutWrongWordsNone = view.findViewById(R.id.quizFragment_layout_result_wrongWordsNone);
        layoutWrongWordsIs = view.findViewById(R.id.quizFragment_layout_result_wrongWordsIs);

        imageViewResultIcon1 = (ImageView)view.findViewById(R.id.quizFragment_imageView_resultIcon_1);
        imageViewResultIcon2 = (ImageView)view.findViewById(R.id.quizFragment_imageView_resultIcon_2);
        imageViewResultIcon3 = (ImageView)view.findViewById(R.id.quizFragment_imageView_resultIcon_3);
        imageViewResultIcon4 = (ImageView)view.findViewById(R.id.quizFragment_imageView_resultIcon_4);
        imageViewResultIcon5 = (ImageView)view.findViewById(R.id.quizFragment_imageView_resultIcon_5);
        imageViewResultIcon6 = (ImageView)view.findViewById(R.id.quizFragment_imageView_resultIcon_6);
        imageViewResultIcon7 = (ImageView)view.findViewById(R.id.quizFragment_imageView_resultIcon_7);
        imageViewResultIcon8 = (ImageView)view.findViewById(R.id.quizFragment_imageView_resultIcon_8);
        imageViewResultIcon9 = (ImageView)view.findViewById(R.id.quizFragment_imageView_resultIcon_9);
        imageViewResultIcon10 = (ImageView)view.findViewById(R.id.quizFragment_imageView_resultIcon_10);
        imageViewResultIcons = new ImageView[]{imageViewResultIcon1, imageViewResultIcon2,imageViewResultIcon3,imageViewResultIcon4,imageViewResultIcon5,
                imageViewResultIcon6,imageViewResultIcon7,imageViewResultIcon8,imageViewResultIcon9,imageViewResultIcon10};
        initResultIcons();
        textViewFinalScore = (TextView)view.findViewById(R.id.quizFragment_textView_result_finalScore);

        btnStartQuiz = (Button)view.findViewById(R.id.quizFragment_btn_startQuiz);
        btnStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoutubeActivity.LogFirebaseAnalytics("QUIZ_START","QUIZ_START","Click");

                // 로그인 먼저 요청
                // 파이어베이스 로그인 먼저 시켜야지
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

                if((FirebaseAuth.getInstance().getCurrentUser()!=null)||isLoggedIn){
                    // 퀴즈 시작할 때 초기화
                    isQuizPlaying = true;
                    score = 0;
                    currentQuizNum = 0;
                    wordsForPickQuiz = new ArrayList<>();
                    wordsForPickQuiz.addAll(YoutubeActivity.wordsFromSongAct);
                    Log.d("뭐냐", ""+wordsForPickQuiz.size());
                    playerWrongWords = new ArrayList<WordCard>();
                    playerWrongAnswers = new ArrayList<String>();

                    layoutQuizBeforeStart.setVisibility(View.GONE);
                    layoutQuizOnGoing.setVisibility(View.VISIBLE);
                    layoutQuizResult.setVisibility(View.GONE);

                    /*Intent intent = new Intent(WordsActivity.this, QuizActivity.class);
                    intent.putExtra("selectedSong", songFromSongsAct);
                    intent.putParcelableArrayListExtra("words", wordsFromPrevAct);
                    Log.d("Youtube Act.", "Words->Quiz로 보내는 단어 샘플: "+wordsFromPrevAct.get(0).wordJap+", "+wordsFromPrevAct.get(1).wordJap);
                    startActivity(intent);*/
                    //
                }else{
                    startActivityForResult(new Intent(getContext(), LoginActivity.class), RC_SIGN_IN);
                }
            }
        });
        isQuizPlaying = false;
        layoutQuizBeforeStart.setVisibility(View.VISIBLE);
        layoutQuizOnGoing.setVisibility(View.GONE);
        layoutQuizResult.setVisibility(View.GONE);

        makeQuizFromAnswerWord(); // 문제 만들고



        // 노래 추천
        SongsAdapter recommendSongsAdapter = new SongsAdapter(YoutubeActivity.songsRecommend, getContext(), this, 1,0);
        RecyclerView recyclerViewRecommend = (RecyclerView)view.findViewById(R.id.quizFragment_result_recyclerView_recommend);
        recyclerViewRecommend.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)) ;
        recyclerViewRecommend.setAdapter(recommendSongsAdapter);
        recommendSongsAdapter.setOnItemClickListener(recyclerViewListener);
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewRecommend);

        return view;
    }
    void initResultIcons(){
        for(int i=0;i<imageViewResultIcons.length;i++){
            imageViewResultIcons[i].setVisibility(View.INVISIBLE);
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    void addResultIcons(boolean isCorrect){
        if(isCorrect){
            imageViewResultIcons[currentQuizNum].setImageResource(R.drawable.ic_check_white_48dp);
            imageViewResultIcons[currentQuizNum].setColorFilter(Color.parseColor("#FF388E3C"));
        }else{
            imageViewResultIcons[currentQuizNum].setImageResource(R.drawable.ic_clear_black_24dp);
            imageViewResultIcons[currentQuizNum].setColorFilter(Color.parseColor("#FFC2185B"));
        }
        imageViewResultIcons[currentQuizNum].setVisibility(View.VISIBLE);
    }

    void clickOptionBtn(String optionText){
        // (지금 당장은 뭐에 쓰이는 건 아닌데 즉각적으로 animation을 띄운다던가 할 수도 있잖아)
        if(optionText.equals(answerWord.wordKor)){ // 정답인지 아닌지 판단하고
            score++;
            addResultIcons(true);
            YoutubeActivity.LogFirebaseAnalytics("QUIZ_ONGOING","QUIZ_ONGOING","Correct");
            Log.d("QUIZ","Correct! Next Quiz");
        }else{ // fail process
            addResultIcons(false);
            YoutubeActivity.LogFirebaseAnalytics("QUIZ_ONGOING","QUIZ_ONGOING","Wrong");
            Log.d("QUIZ","WRONG! Next Quiz");
            // 틀렸을 때의 문항(단어)과 플레이어가 선택한 답을 넣는다
            playerWrongWords.add(answerWord);
            playerWrongAnswers.add(optionText);
        }

        // 지금 퀴즈가 몇개나 더 남았는지 확인해서
        if(currentQuizNum+1<totalQuizNum){ // 아직 낼 퀴즈가 남았으면 => 다음 문항 보여주고
            Log.d("QUIZ","Go to Next Quiz");
            currentQuizNum++;
            makeQuizFromAnswerWord();
        }else{ // 퀴즈를 내려고 했던 수만큼 다 냈으면 => 카드 팝업 띄우고 => 최종 결과 보여주기
            isQuizPlaying = false;
            Log.d("QUIZ","Finish! Here is result");
            /*layoutQuizOnGoing.setVisibility(View.GONE);
            layoutQuizResult.setVisibility(View.VISIBLE);*/

            textViewFinalScore.setText("단어 "+(totalQuizNum-score)+"개만 다시 살펴볼까요?");

            // 퀴즈에서 뭘 틀렸는지도 보여줘야지
            if(playerWrongWords.size()!=0){ // 틀린 것들 주루룩 보여준다. 그냥 TextView에 붙이자. 리사이클러뷰는 귀찮다.
                quizResultAdapter = new WordCardAdapter(playerWrongWords,getContext(), this, 3);
                recyclerViewWrongWords.setAdapter(quizResultAdapter);
                LinearSnapHelper snapHelper3 = new LinearSnapHelper();
                snapHelper3.attachToRecyclerView(recyclerViewWrongWords);
                //quizResultAdapter.setHasStableIds(true);
                //quizResultAdapter.setOnItemClickListener(recyclerViewVocaListener);

                /*
                btnSaveToVoca.setEnabled(true); // 틀린걸 저장할 수 있어야하니까 버튼 활성화*/
                layoutWrongWordsNone.setVisibility(View.GONE);
                layoutWrongWordsIs.setVisibility(View.VISIBLE);
            }else{ // 틀린게 없어! 다 맞았어!
                layoutWrongWordsNone.setVisibility(View.VISIBLE);
                layoutWrongWordsIs.setVisibility(View.GONE);
            }
            //

            //
            if(getGrade()>2){
                saveBadgeList(YoutubeActivity.songFromSongsAct);
                Log.d("badges PROCESS","3점! 뱃지 획득!");
            }

            // 결과 카드 팝업
            songCard = new Dialog(getContext());
            showCard();
        }
    }

    // 클래스를 만들어서 Drawable을 포함한 자료 구조를 짜놓는다..?
    // or for문을 돌면서 singer에 따라 분기시킨다?
    Drawable pickChannelImage(Song currentSong){
        Drawable drawable = null;
        switch (currentSong.getSingerKor()){
            case "요네즈 켄시":
                drawable = getResources().getDrawable(R.drawable.ch_yonezukenshi);
                break;
            case "요루시카":
                drawable = getResources().getDrawable(R.drawable.ch_yorushika);
                break;
            case "아이묭":
                drawable = getResources().getDrawable(R.drawable.ch_aimyon);
                break;
            case "radwimps":
                drawable = getResources().getDrawable(R.drawable.ch_radwimps);
                break;
            case "호시노 겐":
                drawable = getResources().getDrawable(R.drawable.ch_hoshinogen);
                break;
            case "다즈비":
                drawable = getResources().getDrawable(R.drawable.ch_dazbee);
                break;
            default: drawable = getResources().getDrawable(R.drawable.ch_dalmabal);
        }
        return drawable;
    }

    // 퀴즈 완료 후, 성공 시 획득 카드 보여주는 함수.
    // 그런데 카드에 들어갈 정보를 얻으려면 Words 만 가지고 있으면 안되네. Song도 같이 가지고 있어야 할 듯
    Dialog songCard;
    public void showCard(){
        String youtubeId = YoutubeActivity.songFromSongsAct.getYoutubeId();
        Button btnConfirm;
        TextView textViewTitle, textViewSinger, textViewResult;
        ImageView imageViewThumb, imageViewChannel, imageViewStar1, imageViewStar2, imageViewStar3;

        songCard.setContentView(R.layout.quiz_card);
        imageViewThumb = (ImageView)songCard.findViewById(R.id.quiz_imagView_card_thumb);
        imageViewChannel = (ImageView)songCard.findViewById(R.id.quiz_imagView_card_channel);
        btnConfirm = (Button)songCard.findViewById(R.id.quiz_btn_comfirm_card_get);
        textViewTitle = (TextView)songCard.findViewById(R.id.quiz_textView_card_title);
        textViewSinger = (TextView)songCard.findViewById(R.id.quiz_textView_card_singer);
        textViewResult = (TextView)songCard.findViewById(R.id.quiz_textView_card_result);
        imageViewStar1 = (ImageView)songCard.findViewById(R.id.quiz_imageView_card_star1);
        imageViewStar2 = (ImageView)songCard.findViewById(R.id.quiz_imageView_card_star2);
        imageViewStar3 = (ImageView)songCard.findViewById(R.id.quiz_imageView_card_star3);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songCard.dismiss();
                layoutQuizBeforeStart.setVisibility(View.GONE);
                layoutQuizOnGoing.setVisibility(View.GONE);
                layoutQuizResult.setVisibility(View.VISIBLE);
                YoutubeActivity.LogFirebaseAnalytics("QUIZ_RESULT_CONFIRM","QUIZ_RESULT_CONFIRM","Click");
            }
        });

        // 별 갯수
        switch (getGrade()){
            case 0:
                imageViewStar1.setImageResource(R.drawable.ic_star_grey_48dp);
                imageViewStar2.setImageResource(R.drawable.ic_star_grey_48dp);
                imageViewStar3.setImageResource(R.drawable.ic_star_grey_48dp);
                break;
            case 1:
                imageViewStar1.setImageResource(R.drawable.ic_star_black_24dp);
                imageViewStar2.setImageResource(R.drawable.ic_star_grey_48dp);
                imageViewStar3.setImageResource(R.drawable.ic_star_grey_48dp);
                break;
            case 2:
                imageViewStar1.setImageResource(R.drawable.ic_star_black_24dp);
                imageViewStar2.setImageResource(R.drawable.ic_star_black_24dp);
                imageViewStar3.setImageResource(R.drawable.ic_star_grey_48dp);
                break;
            case 3:
                imageViewStar1.setImageResource(R.drawable.ic_star_black_24dp);
                imageViewStar2.setImageResource(R.drawable.ic_star_black_24dp);
                imageViewStar3.setImageResource(R.drawable.ic_star_black_24dp);
                break;
        }

        // 카드 윗부분 영상 썸네일 이미지
        Glide.with(this)
                .load("https://i.ytimg.com/vi/"+youtubeId+"/mqdefault.jpg")
                .override(320,180)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .transition(withCrossFade())
                .into(imageViewThumb);

        // 썸네일 아래 채널 프로필 이미지
        //.load(pickChannelImage(YoutubeActivity.songFromSongsAct))
        Glide.with(this)
                .load(YoutubeActivity.songFromSongsAct.getAlbumImageUrl())
                .override(120,120)
                .apply(new RequestOptions().transform(new RoundedCorners(20)).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                .into(imageViewChannel);

        // 텍스트 내용들
        textViewResult.setText(score+"/"+(totalQuizNum));
        textViewTitle.setText(YoutubeActivity.songFromSongsAct.getTitleKorAndJap());
        textViewSinger.setText(YoutubeActivity.songFromSongsAct.getSingerKorAndJap());

        songCard.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        songCard.setCancelable(false);
        songCard.setCanceledOnTouchOutside(false);
        songCard.show();


    }

    // 단어 퀴즈 총 몇 개 중에 몇 개 맞았고, 기준점을 통과해서 카드를 준다고. 정답률 50%면 별 하나, 70%면 별 두개, 90%면 별 3개 주자.
    int getGrade(){
        float scorePercentage = 0f;
        scorePercentage = (float)score/(float)totalQuizNum;
        if (scorePercentage<0.3f) {
            return 0; // 실패
        } else if(scorePercentage<0.6f){
            return 1; // 성공, 별 1개
        } else if(scorePercentage<0.9f){
            return 2; // 성공, 별 2개
        } else { // 90~100점
            return 3; // 성공. 별 3개
        }
    }


    // 뱃지 저장하는 함수 (sharedPref, Firebase 둘 다 저장)
    public void saveBadgeList(Song songToBadge){
        // [기기에 저장해 두는 과정]
        // 기기에 저장했던 데이터 불러와서
        /*SharedPreferences pref = getActivity().getSharedPreferences("sFile", Context.MODE_PRIVATE);
        HashSet<String> badgeData = new HashSet<String>(pref.getStringSet("badges", new HashSet<String>())); // "voca"라는 키로 저장되어있던 스트링셋 불러오고
        Log.d("badges PROCESS","badges Before-Saved Data: " + badgeData);*/
        HashSet<String> badgeData = SharedPrefControl.loadDataSetByKeyInSharedPref(getActivity(),FILE_NAME_SHAREDPREF,BADGES);

        String newBadgeToSave = songToBadge.songSaveFormat();

        // 혹시 이미 들어있는지 확인 한 번 하고
        if(!badgeData.contains(newBadgeToSave)){
            badgeData.add(newBadgeToSave); // HashSet에 saveFormat()을 추가한다
        }

        // 기기에 다시 저장해주는 과정
        /*SharedPreferences.Editor editor = pref.edit(); // 에디터를 열어서
        editor.putStringSet("badges", (Set<String>) badgeData);
        editor.apply();
        Log.d("badges PROCESS","badges After-Saved Data: " + pref.getStringSet("badges", new HashSet<String>()));*/
        SharedPrefControl.saveDataSetByKeyInSharedPref(getActivity(),FILE_NAME_SHAREDPREF, BADGES,badgeData);
        // 여기까지

        // [Firebase 서버에 저장해두는 함수]
        addBadgeInFirebase(songToBadge);
    }

    // [Firebase 서버에, 현재 로그인한 유저 아이디에 뱃지를 저장해두는 과정]
    public void addBadgeInFirebase(Song songToBadge){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Add Data
        Map<String, Object> newBadge = new HashMap<>();
        newBadge.put("titleKor", songToBadge.getTitleKor());
        newBadge.put("singerKor", songToBadge.getSingerKor());

        // Add a new document with a generated ID
        db.collection("user").document(user.getUid()).collection("badges")
                .add(newBadge)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error adding document", e);
                    }
                });
    }

    // 문제를 위한 word를 words에서 랜덤하게 뽑는 함수. 중복 출제되지 않게 제거하면서 뽑아야 함
    WordCard pickAnswerWord(){
        // wordsForPickQuiz에서 하나씩 뽑고, 뽑은 건 제거 할거야
        Log.d("QUIZ","단어 고르기 전, 후보 단어 개수: "+wordsForPickQuiz.size()+"개");
        //int pickWordNum = new Random().nextInt(wordsForPickQuiz.size());
        answerWord = wordsForPickQuiz.get(new Random().nextInt(wordsForPickQuiz.size()));
        wordsForPickQuiz.remove(answerWord);
        Log.d("QUIZ","단어 고른 후, 남은 후보 단어 개수: "+wordsForPickQuiz.size()+"개");
        return answerWord;
    }

    // 이 함수는 anwerWord를 고르고 [보기 옵션 + 문제 화면]을 구성하는 함수임
    void makeQuizFromAnswerWord(){
        answerWord = pickAnswerWord(); // answerWord 를 정하고 (얘가 중복 안되게 픽해줌)
        for(Button btn : btns){ // 버튼들 모두 ""로 초기화 시키고
            btn.setText("");
        }

        // wrong option 만들기 위해 리스트를 새로 만든다.
        ArrayList<WordCard> tempWords = wordsAll;

        // 보기 버튼에 들어갈 오답 옵션 고르기
        tempWords.remove(answerWord); // 단어 리스트에서 정답 단어 빼고
        WordCard wrongWord1 = tempWords.get(new Random().nextInt(tempWords.size()));

        tempWords.remove(wrongWord1); // 단어 리스트에서 오답1 단어도 (잠깐) 빼고
        WordCard wrongWord2 = tempWords.get(new Random().nextInt(tempWords.size()));

        int answerNum = new Random().nextInt(3); // 0,1,2 중에서 정답 번호로 쓸 번호 랜덤으로 하나 고르고
        btns[answerNum].setText(answerWord.wordKor); // 버튼 3개 중에서 정답 번호 버튼에 내용(퀴즈 문제) 넣고

        // 버튼 텍스트가 초기화 상태인 ""면 wrong word를 집어 넣자
        // 빈 칸인 옵션 버튼이 두 개니까 두 번 돈다 ㅋㅋ
        for(int i=0;i<3;i++){
            if(btns[i].getText().equals("")){
                btns[i].setText(wrongWord1.wordKor);
                break; // 한 번 넣었으면 그만한다. 그래야 두 번째 for문이 돌 수 있잖아
            }
        }
        for(int i=0;i<3;i++){
            if(btns[i].getText().equals("")){
                btns[i].setText(wrongWord2.wordKor);
            }
        }

        tempWords.add(wrongWord1); // 그리고 오답 단어는 다시 넣어줌. 뭐 한 두개 빼는거야 티도 안날거 같긴한데

        // 문제 화면도 만들고
        textViewQuestion.setText(answerWord.wordJap);
        textViewQuestionHint.setText(answerWord.wordJapPron);
        //textViewQuestionNum.setText((currentQuizNum+1)+"/"+totalQuizNum);
        textViewQuestionNum.setText(getString(R.string.quiz_fragment_questionNum, String.valueOf((currentQuizNum+1)), String.valueOf(totalQuizNum)));
    }

    @Override
    public void onStarClick(View v, int pos, String wordSelected) {
        //quizResultAdapter.removeItem(pos);
        //quizResultAdapter.notifyItemRemoved(pos);
        quizResultAdapter.notifyItemChanged(pos,"vocaIcon"); // 이거 이렇게 쓰는거 맞음?
        //refreshWholeStarBtns(wordSelected, true);
        YoutubeActivity.LogFirebaseAnalytics("QUIZ_RESULT_STAR","QUIZ_RESULT_STAR_"+wordSelected,"Click");
        Log.d("QUIZ ACTIVITY", "Quiz Result에서 클릭됨");
    }

    // 하트 버튼 눌렸을 때 동작하는 코드
    @Override
    public void onHeartClick(View v, int pos, String songToSaveFormat) {
        Log.d("LIKE CHECK", "Youtube(Lyrics)에서 눌림");
    }

    SongsAdapter.OnItemClickListener recyclerViewListener = new SongsAdapter.OnItemClickListener(){
        @Override
        public void onItemClick(View v, int position, int adapterNum) {
            Log.d("ITEM CHECK", "Youtube(Lyrics)에서 눌림");

            Toast.makeText(getContext(),
                    "Adapter# : "+ adapterNum +", Item# : "+position,Toast.LENGTH_SHORT).show();

            // Feed->Youtube->Words 순으로 진행됨
            Intent intent = new Intent(getContext(), YoutubeActivity.class);
            intent.putExtra("selectedSong", YoutubeActivity.songsRecommend.get(position));
            intent.putParcelableArrayListExtra("words", SongsActivity.songFIndBySongFormat(YoutubeActivity.songsRecommend.get(position).songSaveFormat()).getWords());

            // TODO : Songs->Youtube로 올 때 recommend와 Youtube->Youtube로 갈 때 recommend가 짜이는 로직이 다른데... 나중에 로직 더 다듬어야 할 듯
            //ArrayList<ArrayList<Song>> tempList = SongsActivity.categories;
            //tempList.remove(YoutubeActivity.songsRecommend);
            //intent.putParcelableArrayListExtra("recommend", tempList.get(new Random().nextInt(tempList.size())));
            intent.putParcelableArrayListExtra("recommend", categoryAll.get(new Random().nextInt(categoryAll.size())).getSongs());

            intent.putExtra("mode", 1);
            startActivity(intent);
            //
            getActivity().finish();
        }
    };
}
