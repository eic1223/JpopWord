package com.dane.jpopword;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.ui.auth.IdpResponse;
import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.dane.jpopword.SplashActivity.categoryAll;
import static com.dane.jpopword.SplashActivity.songsAll;
import static com.dane.jpopword.SplashActivity.wordsAll;

public class SongsActivity extends AppCompatActivity implements FeedFragment.OnFragmentInteractionListener, FavoritesFragment.OnFragmentInteractionListener, VocaFragment.OnFragmentInteractionListener, AboutFragment.OnFragmentInteractionListener{//}, SongsAdapter.OnHeartClickListener, WordCardAdapter.OnStarClickListener{
    private FirebaseAnalytics mFirebaseAnalytics;

    static ArrayList<RecyclerView> songRecyclerViews;
    static ArrayList<SongsAdapter> songsAdapters;
    LinearLayout linearLayoutFeedTab;

    //ArrayList<String> catNames;
    //static ArrayList<ArrayList<Song>> categories;

    //static ArrayList<Song> songsAll;
    //static ArrayList<WordCard> wordsAll;
    //static ArrayList<Category> categoryAll;
    static ArrayList<Category> categoriesForFeed;
    static ArrayList<Category> categoriesCover;
    static ArrayList<WordCard> vocaList = new ArrayList<>();
    static ArrayList<Song> favoritesList = new ArrayList<>();
    static ArrayList<Song> badgeList = new ArrayList<>();

    //RecyclerView recyclerViewVoca, recyclerViewFavorites, recyclerViewBadges;
    //WordCardAdapter adapterVoca;
    //SongsAdapter adapterFavorites;
    //BadgeAdapter adapterBadges;
    //CategoryAdapter adapterCategory;

    //ScrollView scrollViewFeed, scrollViewVoca, scrollViewFavorites;//, scrollViewAbout;
    //NestedScrollView scrollViewAbout;
    static int CURRENT_TAB_NUM = 0;

    /*int VOCA_TAB_STATE = 0;
    int FAVORITES_TAB_STATE = 0;
    int ABOUT_TAB_STATE = 0;*/

    //LinearLayout layoutVocaNone, layoutVocaIs, layoutFavoritesNone, layoutFavoritesIs, layoutGuestUser, layoutLoginUser;
    //Button btnLogin;
    //TextView btnLogout;

    //TextView textViewUserAccount;
    int RC_SIGN_IN = 9000;
    //FirebaseAuth mAuth; //

    CallbackManager callbackManager; // 페이스북 로그인용

    //TextView textViewBadgeCategory;

    // 서비스 가이드 기능에 필요한 애들
    private ViewPager guideViewPager ;
    private SlideAdapter guideSlideAdapter ;
    Dialog guideSlides;
    ArrayList<Integer> slideXmls;

    //Dialog popupCard;

    //ArrayList<String> tempSongsForBadges; // 뱃지 구성할 때 임시로 쓰는 애

    // 프래그먼트 개발 중
    FrameLayout frameLayout;
    FeedFragment feedFragment;
    FavoritesFragment favoritesFragment;
    VocaFragment vocaFragment;
    AboutFragment aboutFragment;

    // 연관된 노래라 함은 비슷한 단어가 많이 들어가는 노래, 같은 가수의 다른 노래 등.
    // 비슷한 단어가 많이 들어가는 노래를 추천해주려면 결국 가사의 모든 단어 분석 해야겠네. 그래서 이 정도 단어로 70% 이상 알아들을 수 있는지.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_songs);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this); // Firebase Analytics 추가 중
        callbackManager = CallbackManager.Factory.create(); // 페이스북 로그인 추가

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {}
                    @Override
                    public void onCancel() {}
                    @Override
                    public void onError(FacebookException exception) {}
                });
        //

        frameLayout = findViewById(R.id.songs_frameLayout);
        feedFragment = new FeedFragment();
        favoritesFragment = new FavoritesFragment();
        vocaFragment = new VocaFragment();
        aboutFragment = new AboutFragment();

        // TODO : 앱 사용 가이드 xml 만들기
        //linearLayoutFeedTab = findViewById(R.id.sogns_feed_parent_layout);

        // 이 Json파일들을 읽는 과정을 Splash에서 하기로 한다. -> 문제가 생겨서 일단 여기서 한다
        // 모든 word가 담긴 Json 파일 읽어서 words 객체 리스트에 넣는다.
        /*wordsAll = JsonFileReader.makeWordsObjectFromJsonFile(this,"words.json");
        // 모든 songs가 닮긴 Json파일을 읽어서 카테고리 ArrayList에 분류해 담는다. words도 다 들어감.
        songsAll = JsonFileReader.makeObjectFromJsonFile(this,"songs.json", wordsAll);
        // 모든 categories가 닮긴 Json파일을 읽어서 카테고리 ArrayList에 분류해 담는다. songs도 다 들어감.
        categoryAll = JsonFileReader.makeCategoriesFromJsonFile(this, "categories.json", songsAll);*/

        // 카테고리 ~ 카테고리 소개 문구
        /*HashMap<String, String> catNameCopyMap= new HashMap<>();
        catNameCopyMap.put("RADWIMPS","「너의 이름은」ost 모음");
        catNameCopyMap.put("요네즈 켄시","앨범 내면 차트 올킬! 요네즈 켄시");
        catNameCopyMap.put("아이묭","매력 터지는 싱어송라이터, 아이묭");
        catNameCopyMap.put("호시노 겐","「니게하지」의 남주, 호시노 겐");
        catNameCopyMap.put("Rain Cover","에디터가 추천하는「언어의 정원」ost");
        catNameCopyMap.put("카타오모이 커버","Aimer의 '짝사랑' 커버");
        catNameCopyMap.put("Pretender","혜성같이 등장한 Pretender 커버");
        catNameCopyMap.put("요루시카","요루시카 노래 듣고 가세요");
        catNameCopyMap.put("Kpop","일본어로 듣는 케이팝");*/

        // 카테고리 생성. 카테고리의 순서를 (일단은) 랜덤하게라도 보여주기 위해 이렇게 한다
        /*int a= new Random().nextInt(3);
        switch (a){
            case 1:
                catNames = new ArrayList<String>(
                        Arrays.asList("Kpop","호시노 겐","RADWIMPS","Rain Cover","요루시카", "카타오모이 커버","요네즈 켄시","아이묭","Pretender"));
                break;
            case 2:
                catNames = new ArrayList<String>(
                        Arrays.asList("아이묭","요루시카","요네즈 켄시","호시노 겐","Rain Cover","Pretender","Kpop", "RADWIMPS", "카타오모이 커버"));
                break;
            default:
                catNames = new ArrayList<String>(
                        Arrays.asList("RADWIMPS","카타오모이 커버","Kpop","요네즈 켄시","아이묭","Rain Cover","호시노 겐","Pretender","요루시카"));
                break;
        }*/
        // * ㄴ카테고리 이름(catName)은 JSON 의 catName과 일치해야 함.

        // 모든 songs은 여기에 카테고리별로 분류되어 들어감
        //categories = new ArrayList<ArrayList<Song>>();
        //categories = JsonFileReader.getCategoryListFromJsonFile(this, songsAll, wordsAll, catNames);

        // 탭별 스크롤 설정
        //scrollViewFeed = (ScrollView)findViewById(R.id.songs_scrollView_feed);
        //scrollViewVoca = (ScrollView)findViewById(R.id.songs_scrollView_voca);
        //scrollViewFavorites = (ScrollView)findViewById(R.id.songs_scrollView_favorites);
        //scrollViewAbout = (NestedScrollView) findViewById(R.id.songs_scrollView_about);

        // category 개수에 맞게 리사이클러뷰 할당하고, LinearLayoutManager 객체를 지정한다.
        // TODO : type이 1(default)인 애들만 넣고, 2(cover)인 경우는 케이스를 따로 빼자. (밑에 작업 해야함)
        /*songRecyclerViews = new ArrayList<RecyclerView>();
        categoriesForFeed = new ArrayList<>();
        categoriesCover = new ArrayList<>();
        for (int i=0; i<categoryAll.size(); i++){
            if(categoryAll.get(i).getType()==1){
                categoriesForFeed.add(categoryAll.get(i));
            }else{ // type==2
                categoriesCover.add(categoryAll.get(i));
            }
        }
        for (int i=0; i<categoriesForFeed.size(); i++) {
                RecyclerView recyclerViewSong = new RecyclerView(this);
                recyclerViewSong.setId(i);
                recyclerViewSong.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)) ;
                // linearLayoutFeedTab.addView(recyclerViewSong,i*3+1); // 부모역할을 하는 xml에 붙인다. 두번째 param이 위치임.

                songRecyclerViews.add(recyclerViewSong); // 이건 recyclerView를 모아놓은 array list에 추가하는 거

                TextView textViewTitle = new TextView(this);
                textViewTitle.setId(i+100);
                //textViewTitle.setText(catNameCopyMap.get(catNames.get(i)));
                textViewTitle.setText(categoriesForFeed.get(i).getIntroCopy());
                textViewTitle.setTypeface(null, Typeface.BOLD);
                textViewTitle.setTextColor(Color.DKGRAY);

                textViewTitle.setTextSize(16);
                textViewTitle.setPadding(32,32,2,16);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                textViewTitle.setLayoutParams(params);

                // 모아서 한 번에 해줘야 순서가 안꼬여. 아마 생성되는 순간 인덱스가 바뀌는 듯
                linearLayoutFeedTab.addView(textViewTitle, i*3);
                linearLayoutFeedTab.addView(recyclerViewSong,i*3+1); // 부모역할을 하는 xml에 붙인다. 두번째 param이 위치임.
                addLineSeperator(i*3+2);

        }*/

        /*// 배너 추가(가이드)
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View banner = inflater.inflate(R.layout.songs_each_banner, null);
        TextView bannerTitle = (TextView)banner.findViewById(R.id.banner_textView_title);
        bannerTitle.setText("앱 사용 가이드");
        ImageView bannerImageView = (ImageView)banner.findViewById(R.id.banner_imageView_background);
        bannerImageView.setImageResource(R.drawable.cat_banner);
        banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 배너 클릭시 서비스 가이드 띄우기
                LogFirebaseAnalytics("BANNER_01", "BANNER_01", "Click");
                Toast.makeText(SongsActivity.this, "첫번째 배너 클릭", Toast.LENGTH_SHORT).show();
            }
        });
        linearLayoutFeedTab.addView(banner,9); // 첫 번째 배너는 3n 위치

        // 배너 추가(커버 모음)
        final Category coverSongs = categoriesCover.get(new Random().nextInt(categoriesCover.size())); // 커버 모음집 중 (일단은) 랜덤하게 하나 골라서
        View banner2 = inflater.inflate(R.layout.songs_each_banner, null);
        TextView bannerTitle2 = (TextView)banner2.findViewById(R.id.banner_textView_title);
        bannerTitle2.setText(coverSongs.getIntroCopy());
        ImageView bannerImageView2 = (ImageView)banner2.findViewById(R.id.banner_imageView_background);
        bannerImageView2.setImageResource(R.drawable.cat_banner);
        banner2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogFirebaseAnalytics("BANNER_02", "BANNER_02", "Click");
                Intent intent = new Intent(SongsActivity.this, CategoryActivity.class);

                intent.putExtra("categoryName",coverSongs.getName());
                intent.putExtra("type",2);
                startActivity(intent);

                Toast.makeText(SongsActivity.this, "커버 모음 배너 클릭", Toast.LENGTH_SHORT).show();
            }
        });
        linearLayoutFeedTab.addView(banner2,16); // 두 번째 배너는 3n+1 위치*/

        // 어댑터들도 categoriesForFeed 개수에 맞게 동적으로 만들어준다
        /*songsAdapters = new ArrayList<SongsAdapter>();
        for(int i=0;i<categoriesForFeed.size();i++){
            songsAdapters.add(new SongsAdapter(categoriesForFeed.get(i).getSongs(),this,this,1,i));
        }

        // 어댑터에 붙이는 리스너도, 리사이클러뷰에 붙이는 어댑터도 categoriesForFeed 개수에 맞게 동적으로 만들어줌
        for(int i=0;i<categoriesForFeed.size();i++){
            songsAdapters.get(i).setOnItemClickListener(recyclerViewListener);
            songRecyclerViews.get(i).setAdapter(songsAdapters.get(i));

            LinearSnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(songRecyclerViews.get(i));
        }*/

        // ============================================================================= //
        /*VOCA_TAB_STATE = 0;
        FAVORITES_TAB_STATE = 0;
        ABOUT_TAB_STATE = 0;*/

        Button btnSendEmail = findViewById(R.id.songs_about_btn_send_email);
        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("mailto:"+R.string.report_email);
                Intent mailSendIntent = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(mailSendIntent);
            }
        });

        // 새로 바꾸려는 탭바
        BubbleNavigationLinearView bubbleNavigation = (BubbleNavigationLinearView) findViewById(R.id.top_navigation_linearView);
        bubbleNavigation.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                //navigation changed, do something
                changeTab(position);
                CURRENT_TAB_NUM = position;
                Log.d("TAP", "CURRENT TAB NUM : "+CURRENT_TAB_NUM);

            }
        });
        changeTab(0); // 첫 시작 탭은 FEED

        // 환영 팝업 같은거 띄울 때 수정해서 쓰셈
        // songCard = new Dialog(this);
        // showCard();
    } // end of onCreated()

    void LogFirebaseAnalytics(String ITEM_ID, String ITEM_NAME, String CONTENT_TYPE){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, ITEM_ID);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, ITEM_NAME);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, CONTENT_TYPE);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    /**
     * 앱 사용 가이드 관련 코드
     */
    void showGuide(){
        guideSlides = new Dialog(this);
        slideXmls = new ArrayList<>();
        slideXmls.add(R.layout.guide_slide_01);
        slideXmls.add(R.layout.guide_slide_02);

        makeSlides(); // Dialog를 만들고 -> 만들어진 Dialog 안에 있는 ViewPager에 슬라이드를 붙이는 거임.
        guideViewPager = (ViewPager) guideSlides.findViewById(R.id.slides_viewPager);
        guideSlideAdapter = new SlideAdapter(this,slideXmls);
        guideViewPager.setAdapter(guideSlideAdapter);

        // 여기서부터 view pager indicator인데 뭐냐 빨간 글씨인데 진행 되네 ㅋㅋㅋ
        final PageIndicatorView pageIndicatorView = guideSlides.findViewById(R.id.pageIndicatorView);
        pageIndicatorView.setCount(slideXmls.size()); // specify total count of indicators
        pageIndicatorView.setSelection(0);

        guideViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
          @Override
          public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {/*empty*/}

          @Override
          public void onPageSelected(int position) {
            pageIndicatorView.setSelection(position);
          }

          @Override
          public void onPageScrollStateChanged(int state) {/*empty*/}
        });
        // 여기까지.
    }

    void makeSlides(){
        guideSlides.setContentView(R.layout.slides);
        Button btnConfirm = (Button)guideSlides.findViewById(R.id.slides_btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideSlides.dismiss();
            }
        });
        guideSlides.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        int dialogWindowWidth = (int) (displayWidth * 0.95f);
        int dialogWindowHeight = (int) (displayHeight * 0.95f);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(guideSlides.getWindow().getAttributes());
        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;
        guideSlides.getWindow().setAttributes(layoutParams);

        guideSlides.show();
    }


    /*void refreshAboutTab(){
        if(ABOUT_TAB_STATE==0){
            // 로그인 정보 보여주는 부분
            textViewUserAccount = (TextView)findViewById(R.id.songs_about_textView_userId);
            layoutGuestUser = (LinearLayout) findViewById(R.id.songs_about_box_guestUser);
            layoutLoginUser = (LinearLayout)findViewById(R.id.songs_about_box_loginUser);
            btnLogin = (Button)findViewById(R.id.songs_about_btn_login);
            btnLogin.setOnClickListener(new View.OnClickListener() { // 로그인 버튼 클릭 리스너
                @Override
                public void onClick(View v) {
                LogFirebaseAnalytics("ABOUT_LOGIN", "ABOUT_LOGIN", "Click");
                startActivityForResult(new Intent(SongsActivity.this, LoginActivity.class), RC_SIGN_IN);
                }
            });
            btnLogout = (TextView) findViewById(R.id.songs_about_btn_logout);
            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { // 로그아웃 버튼 클릭 리스너
                LogFirebaseAnalytics("ABOUT_LOGOUT", "ABOUT_LOGOUT", "Click");
                AuthUI.getInstance()
                    .signOut(getApplicationContext())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        refreshLoginState(); // 로그아웃 성공했으면 리프레시 해줘야지
                        Toast.makeText(getApplicationContext(),"Logout!",Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(MyActivity.this, SignInActivity.class));
                        //finish();
                        }
                    });
                }
            });
            refreshLoginState();

            RecyclerView recyclerViewCategory = (RecyclerView)findViewById(R.id.songs_about_recyclerView_category);
            recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewCategory.setNestedScrollingEnabled(false);

            adapterCategory = new CategoryAdapter(this, categoriesForFeed);
            adapterCategory.setOnItemClickListener(recyclerViewCategoryListener);
            recyclerViewCategory.setAdapter(adapterCategory);


            //ABOUT_TAB_STATE=1;
        }else{   // 초기화 된 이후, 리프레시 할 때만 돌아가는 코드
            refreshLoginState();    // 로그인 정보 보여주는 부분
        }
    }*/

    /*void resetData(){
        // 데이터 초기화 - 테스트할 동안에만 여기 둘게
        SharedPreferences pref = getSharedPreferences("sFile", Context.MODE_PRIVATE);
        HashSet<String> emptyData = new HashSet<String>();
        SharedPreferences.Editor editor = pref.edit(); // 에디터를 열어서
        editor.putStringSet("voca", (Set<String>) emptyData);
        editor.putStringSet("favorites", (Set<String>) emptyData);
        //editor.putStringSet("badges", (Set<String>) emptyData);
        //editor.putStringSet("0starSongs", (Set<String>) emptyData);
        //editor.putStringSet("1starSongs", (Set<String>) emptyData);
        //editor.putStringSet("2starSongs", (Set<String>) emptyData);
        //editor.putStringSet("3starSongs", (Set<String>) emptyData);
        editor.apply(); // 변경 완료. commit()을 써도 댐
        // 2) 다 지워진(최신) favorites 리스트를 업데이트하는 for문에 넣는다s
        favoritesList.clear();
        refreshWholeFeedRecyclerViews(favoritesList);
    }*/

    /*void showBadges(){
        // 뱃지 보여주는 함수
        // 1) sharedPref에서 먼저 불러와서 보여준다.
        // 2) Firebase에서 불러오는게 succeess 되면 sharedPref에서 불러온 거랑 비교해본다.
        // 3) 같으면 냅두고, 다르면 firebase 기준으로 덮어 씌운 후, adapter를 업데이트 한다.

        // (로그인/비로그인 공통) sharedPref 데이터 불러와서 -> 뱃지 리스트 만들기
        SharedPreferences prefBadge = getSharedPreferences("sFile", Context.MODE_PRIVATE);
        final HashSet<String> badgeData = new HashSet<String>(prefBadge.getStringSet("badges", new HashSet<String>()));

        badgeList = new ArrayList<Song>();
        for(int i = 0; i< songsAll.size(); i++){
            if(badgeData.contains(songsAll.get(i).songSaveFormat())){
                badgeList.add(songsAll.get(i));
            }
        }

        // 2) 어댑터에 넣어서 -> 리사이클러뷰에 붙여서 보여줘
        adapterBadges = new BadgeAdapter(badgeList,this);
        recyclerViewBadges = (RecyclerView)findViewById(R.id.songs_about_recyclerView_badge);
        recyclerViewBadges.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewBadges.setAdapter(adapterBadges);

        // 3) 뱃지가 총 몇 개인지도 보여주고
        textViewBadgeCategory = (TextView)findViewById(R.id.songs_about_textView_badge_cat);
        textViewBadgeCategory.setText(getString(R.string.songs_category_badgeCount, String.valueOf(badgeList.size())));
        //textViewBadgeCategory.setText("뱃지("+badgeList.size()+")");
        //

        // (로그인 유저의 경우) Firebase 데이터도 불러와서 이중 체크.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // 1-1) Firebase 데이터 불러와서 뱃지 리스트 만들고 -> sharedPref 뱃지 리스트와 비교
        if(user!=null){
            tempSongsForBadges = new ArrayList<>();
            FirebaseFirestore db = FirebaseFirestore.getInstance(); // Initialize Cloud Firestore
            db.collection("user").document(user.getUid()).collection("badges")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Badges Check", document.getId() + " => " + document.getData());
                                String songFormat = document.getData().get("titleKor").toString()+"/"+document.getData().get("singerKor").toString();
                                tempSongsForBadges.add(songFormat);
                            }
                            //for(String songFormat : tempSongsForBadges) {Log.d("들어감?", songFormat);}

                            ArrayList<Song> FirebaseBadgeList = new ArrayList<Song>();
                            for(int i = 0; i< songsAll.size(); i++){
                                if(tempSongsForBadges.contains(songsAll.get(i).songSaveFormat())){
                                    FirebaseBadgeList.add(songsAll.get(i));
                                }
                            }

                            if(!FirebaseBadgeList.equals(badgeList)){ // 만약 두 배열이 다르면 => Firebase 버전으로 덮어 씌운다.
                                badgeList.clear();
                                badgeList.addAll(FirebaseBadgeList);

                                // [sharedPref에 저장해 두는 과정]
                                // 기기에 저장했던 데이터 불러와서
                                SharedPreferences pref = getSharedPreferences("sFile", Context.MODE_PRIVATE);
                                HashSet<String> badgeData = new HashSet<String>(pref.getStringSet("badges", new HashSet<String>())); // "badges"라는 키로 저장되어있던 스트링셋 불러오고
                                Log.d("badges PROCESS","badges Before-Saved Data: " + badgeData);

                                badgeData = new HashSet<String>();
                                for(int i=0;i<badgeList.size();i++){
                                    String newBadgeToSave = badgeList.get(i).songSaveFormat();
                                    badgeData.add(newBadgeToSave); // HashSet에 saveFormat()을 추가한다
                                }

                                // 기기에 다시 저장해주는 과정
                                SharedPreferences.Editor editor = pref.edit(); // 에디터를 열어서
                                editor.putStringSet("badges", (Set<String>) badgeData);
                                editor.apply();
                                Log.d("badges PROCESS","badges After-Saved Data: " + pref.getStringSet("badges", new HashSet<String>()));
                                // 여기까지

                                // 그리고 어댑터(리사이클러뷰)에 데이터 바뀌는거 있다고 알려줘야지
                                adapterBadges.notifyDataSetChanged();
                                textViewBadgeCategory.setText(getString(R.string.songs_category_badgeCount, String.valueOf(badgeList.size())));
                                //textViewBadgeCategory.setText("뱃지("+badgeList.size()+")");

                                // 카테고리 퍼센티지에도 반영해줘야해
                                adapterCategory.notifyDataSetChanged();


                                Log.d("Badges Check", "BadgeList updated with Firebase");
                            }
                            *//*
                            adapterBadges = new BadgeAdapter(badgeList,SongsActivity.this);
                            recyclerViewBadges.setAdapter(adapterBadges);
                            textViewBadgeCategory.setText("뱃지("+badgeList.size()+")");*//*
                        } else {
                            Log.d("Badges Check", "Error - Just used sharedPref, not Firestore");
                        }
                    }
                });
        }else{
            Log.d("Badges Check", "Not Login");
        }

    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(this,"LOGIN : "+user.getEmail(),Toast.LENGTH_SHORT).show();

                } else {
                // Sign in failed.
                Toast.makeText(this,"LOGIN ERROR",Toast.LENGTH_SHORT).show();
                }
        }

        // 페이스북 추가 중
        callbackManager.onActivityResult(requestCode, resultCode, data);

        //refreshLoginState();
        refreshLoginStateInAboutFragment();
    }

    void refreshLoginStateInAboutFragment(){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // 페이스북 추가 중
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        //
        if ((auth.getCurrentUser() != null) || isLoggedIn) { // 페이스북 조건 추가
            // already signed in
            if(auth.getCurrentUser() != null){
                aboutFragment.textViewUserAccount.setText(auth.getCurrentUser().getEmail());
            }else{
                aboutFragment.textViewUserAccount.setText(accessToken.getUserId());
            }

            aboutFragment.showLoginState(true);

            aboutFragment.showBadges(); // +) 뱃지 나오는 부분 나오도록.
        } else {
            // not signed in
            aboutFragment.textViewUserAccount.setText("게스트 모드입니다");

            // 뱃지 안보이게 숨기고. 로그인하고 더 많은 기능을 쓰라고 알려줘야지..
            aboutFragment.showLoginState(false);
        }
    }

    /*void refreshLoginState(){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // 페이스북 추가 중
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        //
        if ((auth.getCurrentUser() != null) || isLoggedIn) { // 페이스북 조건 추가
            // already signed in
            if(auth.getCurrentUser() != null){
                textViewUserAccount.setText(auth.getCurrentUser().getEmail());
            }else{
                textViewUserAccount.setText(accessToken.getUserId());
            }

            layoutGuestUser.setVisibility(View.GONE);
            layoutLoginUser.setVisibility(View.VISIBLE);

            //showBadges(); // +) 뱃지 나오는 부분 나오도록.
            aboutFragment.showBadges();
        } else {
            // not signed in
            textViewUserAccount.setText("게스트 모드입니다");

            // 뱃지 안보이게 숨기고. 로그인하고 더 많은 기능을 쓰라고 알려줘야지.
            layoutGuestUser.setVisibility(View.VISIBLE);
            layoutLoginUser.setVisibility(View.GONE);
        }
    }*/


    @Override
    public void onBackPressed() {
        String currentTab="";
        switch(CURRENT_TAB_NUM){
            case 0:
                currentTab="FEED";
                break;
            case 1:
                currentTab="VOCA";
                break;
            case 2:
                currentTab="FAVORITES";
                break;
            case 3:
                currentTab="ABOUT";
                break;
        }
        LogFirebaseAnalytics("SONG_BACK","SONG_BACK"+currentTab,"Click");
        Log.d("Back Btn Override", "onBackPressed Called");
    }

    private void addLineSeperator(int index) {
        LinearLayout lineLayout = new LinearLayout(this);
        lineLayout.setBackgroundColor(Color.GRAY);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                2);
        params.setMargins(0, 10, 0, 10);
        lineLayout.setLayoutParams(params);
        linearLayoutFeedTab.addView(lineLayout, index);
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.dataReset:
                SharedPreferences pref = getSharedPreferences("sFile", Context.MODE_PRIVATE);
                HashSet<String> emptyData = new HashSet<String>();
                SharedPreferences.Editor editor = pref.edit(); // 에디터를 열어서
                editor.putStringSet("voca", (Set<String>) emptyData);
                editor.putStringSet("favorites", (Set<String>) emptyData);
                editor.putStringSet("badges", (Set<String>) emptyData);
                editor.putStringSet("0starSongs", (Set<String>) emptyData);
                editor.putStringSet("1starSongs", (Set<String>) emptyData);
                editor.putStringSet("2starSongs", (Set<String>) emptyData);
                editor.putStringSet("3starSongs", (Set<String>) emptyData);
                editor.apply(); // 변경 완료. commit()을 써도 댐
                //Toast.makeText(this, "Reset saved data", Toast.LENGTH_SHORT).show();

                // 2) 다 지워진(최신) favorites 리스트를 업데0이트하는 for문에 넣는다
                favoritesList.clear();
                refreshWholeFeedRecyclerViews(favoritesList);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    // 20200210 다른 액티비티 다녀올 때 돌게 만드는 코드
    @Override
    protected void onResume() {
        super.onResume();
        CURRENT_TAB_NUM=0;
        refreshFeedTab();
        /*if(CURRENT_TAB_NUM==0){ // 현재 feed 탭이면
            refreshFeedTab();
        } else if(CURRENT_TAB_NUM==1){ // 현재 voca 탭이면
            refreshVocaTab();
        }else if(CURRENT_TAB_NUM==2){ // 현재 favorites 탭이면
            refreshFavoritesTab();
        }*/
    }


    /*void refreshVocaTab(){
        // 1) 유저가 저장해 놓은 단어들 불러와서 ArrayList 만들고
        SharedPreferences prefVoca = getSharedPreferences("sFile", Context.MODE_PRIVATE);
        HashSet<String> data = new HashSet<String>(prefVoca.getStringSet("voca", new HashSet<String>()));
        Log.d("FUCK","저장된 voca: " + data);

        // 2) 초기화를 해야하는지(앱 실행 후 처음 탭 실행) or 초기화가 되어있는지(데이터 업데이트만 진행) 에 따라 다르게 동작한다.
        if(VOCA_TAB_STATE==0){
            layoutVocaNone = findViewById(R.id.songs_layout_voca_none);
            layoutVocaIs = findViewById(R.id.songs_layout_voca_is);

            vocaList = new ArrayList<>();
            int wordsSize = wordsAll.size();
            for(int k=0;k<wordsSize;k++){
                if(data.contains(wordsAll.get(k).wordSaveFormat())){
                    vocaList.add(wordsAll.get(k));
                }
            }

            // 중복 체크 후 추가 코드
            int v = vocaList.size();
            ArrayList<WordCard> tempList = new ArrayList<>();
            int index = 0;
            for ( int k=0; k < v; k++ ) {
                if ( index == 0 ) {
                    vocaList.add(vocaList.get(k));
                    index++;
                } else {
                    boolean isDuplication = false;
                    for ( int m=0; m < tempList.size(); m++ ) {
                        if ( tempList.get(m).wordSaveFormat().equals(vocaList.get(k).wordSaveFormat())) {
                            isDuplication = true;
                            Log.d("FUCK","중복이라서 제낀다 : " + vocaList.get(k).wordSaveFormat());
                            break;
                        }
                    }
                    if (isDuplication) {
                        //System.out.println("Duplication number : " + vocaListRefresh.get(k).getWordKor());
                    } else {
                        tempList.add(vocaList.get(k));
                        index++;
                    }
                }
            }
            vocaList.clear();
            vocaList.addAll(tempList);
            //


            // Adapter 만들어서 recylcerView에 붙여주고
            recyclerViewVoca = findViewById(R.id.songs_recyclerView_voca);
            recyclerViewVoca.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

            // voca 리사이클러뷰에 어댑터 붙일거야
            adapterVoca = new WordCardAdapter(vocaList, this, this, 2);
            //adapterVoca.setHasStableIds(true);
            adapterVoca.setOnItemClickListener(recyclerViewVocaListener);
            recyclerViewVoca.setAdapter(adapterVoca);

            LinearSnapHelper snapHelperVoca = new LinearSnapHelper();
            snapHelperVoca.attachToRecyclerView(recyclerViewVoca);

            //VOCA_TAB_STATE=1;
        }
        else{
            ArrayList<WordCard> vocaListRefresh = new ArrayList<>(); //
            int wordsSize = wordsAll.size();
            for(int i = 0; i< wordsSize; i++){
                if(data.contains(wordsAll.get(i).wordSaveFormat())){
                    vocaListRefresh.add(wordsAll.get(i));
                }
            }

            // 중복 체크 후 추가 코드
            int v = vocaListRefresh.size();
            ArrayList<WordCard> tempList = new ArrayList<>();
            int index = 0;
            for ( int k=0; k < v; k++ ) {
                if ( index == 0 ) {
                    tempList.add(vocaListRefresh.get(k));
                    index++;
                } else {
                    boolean isDuplication = false;
                    for ( int m=0; m < tempList.size(); m++ ) {
                        if ( tempList.get(m).wordSaveFormat().equals(vocaListRefresh.get(k).wordSaveFormat())) {
                            isDuplication = true;
                            Log.d("FUCK","중복이라서 제낀다 : " + vocaListRefresh.get(k).wordSaveFormat());
                            break;
                        }
                    }
                    if (isDuplication) {
                        //System.out.println("Duplication number : " + vocaListRefresh.get(k).getWordKor());
                    } else {
                        tempList.add(vocaListRefresh.get(k));
                        index++;
                    }
                }
            }
            //

            //Log.d("FUCK","원래 있던 voca: " + vocaList);
            //Log.d("FUCK","새로 담을 voca: " + vocaListRefresh);
            // 업데이트된 새 리스트를 recyclerView에 반영한다
            vocaList.clear();
            vocaList.addAll(tempList);
            //Log.d("FUCK","새로 담긴 voca: " + vocaList);
            adapterVoca.notifyDataSetChanged();
        }

        // 만약 저장해 놓은 단어가 하나도 없으면 없다고 멘트 보여주고
        showVocaIfListNone();
    }*/

    void refreshFeedTab(){
        // 좋아요 리스트가 없으면 좋아요를 눌러보라는 내용의 다른 뷰를 보여주는 녀석
        refreshWholeFeedRecyclerViews(favoritesList);
    }

    /*void refreshFavoritesTab(){
        // 1) favorites 리스트 데이터 불러오고(업데이트 하고)
        SharedPreferences pref = getSharedPreferences("sFile", Context.MODE_PRIVATE);
        HashSet<String> data = new HashSet<String>(pref.getStringSet("favorites", new HashSet<String>()));

        // 2) 초기화를 해야하는지(앱 실행 후 처음 탭 실행) or 초기화가 되어있는지(데이터 업데이트만 진행) 에 따라 다르게 동작한다.
        if(FAVORITES_TAB_STATE==0){
            // 0)
            layoutFavoritesNone = findViewById(R.id.songs_layout_favorites_none);
            layoutFavoritesIs = findViewById(R.id.songs_layout_favorites_is);
            recyclerViewFavorites = findViewById(R.id.songs_recyclerView_favorites);
            recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

            favoritesList = new ArrayList<>();
            for(int i = 0; i< songsAll.size(); i++){
                if(data.contains(songsAll.get(i).songSaveFormat())){
                    favoritesList.add(songsAll.get(i));
                }
            }

            // 리사이클러뷰에 어댑터 붙일거야
            adapterFavorites = new SongsAdapter(favoritesList, this, this,2,0); // 얘 0으로 하는게 맞나? ㅇㅇ 맞는 듯
            adapterFavorites.setHasStableIds(true);
            adapterFavorites.setOnItemClickListener(recyclerViewFavortesListener);
            recyclerViewFavorites.setAdapter(adapterFavorites);

            LinearSnapHelper snapHelperFavorites = new LinearSnapHelper();
            snapHelperFavorites.attachToRecyclerView(recyclerViewFavorites);

            //FAVORITES_TAB_STATE = 1;
        }
        else{
            ArrayList<Song> favoritesListRefresh = new ArrayList<>();
            for(int i = 0; i< songsAll.size(); i++){ // favorites 구성이 songsAll 기반으로 돌잖아.
                if(data.contains(songsAll.get(i).songSaveFormat())){
                    favoritesListRefresh.add(songsAll.get(i));
                }
            }

            // 새 데이터로 바꿔 넣어주는 하는 부분 (왜 굳이 이렇게 함?)
            favoritesList.clear();
            favoritesList.addAll(favoritesListRefresh);
            adapterFavorites.notifyDataSetChanged(); // [수정 필요] 여기에는 payload 적용 못하나? 여기는 열릴 때마다 깜빡여야 함?
        }

        // 만약 좋아요 눌러놓은 노래가 없으면 -> 눌러보라고 멘트가 나오게 하고
        showFavoritesIfListNone();
    }*/

  /*  void showFavoritesIfListNone(){
        if(favoritesList.size()==0){
            layoutFavoritesIs.setVisibility(View.GONE);
            layoutFavoritesNone.setVisibility(View.VISIBLE);
        } else{ // 눌러놓은게 하나라도 있으면 -> 업데이트한 리스트를 어댑터에 적용시킨다
            layoutFavoritesIs.setVisibility(View.VISIBLE);
            layoutFavoritesNone.setVisibility(View.GONE);
        }
    }

    void showVocaIfListNone(){
        if(vocaList.size()==0){
            layoutVocaIs.setVisibility(View.GONE);
            layoutVocaNone.setVisibility(View.VISIBLE);
        } else{ // 눌러놓은게 하나라도 있으면 -> 리사이클러뷰에 담아서 보여주고
            layoutVocaIs.setVisibility(View.VISIBLE);
            layoutVocaNone.setVisibility(View.GONE);
        }
    }*/

    /*// 좋아요(하트) 버튼 눌렸을 때 동작하는 코드
    @Override
    public void onHeartClick(View v, int pos, String songToSaveFormat) {
        // 좋아요(하트) 버튼 눌렸을 때 동작하는 코드
        // 지금 어느 탭이냐에 따라(feed or favorites) 다르게 동작한다.
        if(scrollViewFeed.getVisibility() == View.VISIBLE) {
            refreshWholeHeartBtns(songToSaveFormat, true);
            LogFirebaseAnalytics("FEED_HEART","FEED_HEART_"+songToSaveFormat,"Click");
            Log.d("LIKE CHECK", "Feed에서 눌림");
        }else if(scrollViewFavorites.getVisibility() == View.VISIBLE){
            adapterFavorites.removeItem(pos);
            adapterFavorites.notifyItemRemoved(pos);
            adapterFavorites.notifyDataSetChanged(); // 이걸 안넣어주면 맨 위 아이템을 클릭했을 때 Inconsistency detected 에러로 죽음
            // adapterFavorites.notifyItemRangeChanged(pos,adapterFavorites.getItemCount());
            refreshWholeHeartBtns(songToSaveFormat, false);
            LogFirebaseAnalytics("FAVORITES_HEART","FAVORITES_HEART"+songToSaveFormat,"Click");
            Log.d("LIKE CHECK", "Favorites에서 눌림. 삭제 콜백 먹음?");

            showFavoritesIfListNone();
        }
    }*/

    // 지금 어느 액티비티/탭이냐에 따라(voca(Songs.Act) or Words.Act) 다르게 동작한다.
    // 단어장은 favorite songs랑 다르게 같은 액티비티에 두 종류 recyclerView가 있는게 아니라
    // Songs(voca tab)과 Words 두 개의 액티비티에 존재한다. 그러니까 onStarClick 이거를 각각의 액티비티에서 오버라이드 해야함

    /*@Override
    public void onStarClick(View v, int pos, String wordSelected) {
        // 이건 songs의 voca에서 눌린거니까
        adapterVoca.removeItem(pos);
        //adapterVoca.notifyItemRemoved(pos);
        adapterVoca.notifyItemRangeChanged(pos,adapterVoca.getItemCount()); // 이거 이렇게 쓰는거 맞음?
        //refreshWholeStarBtns(wordSelected, false); // payload주고(true) 부분만 바꾸는게 아니라 바로 삭제
        LogFirebaseAnalytics("VOCA_STAR","VOCA_STAR"+wordSelected,"Click");
        Log.d("STAR CHECK", "voca tab에서 눌림");

        // 만약 단어를 전부 취소해서 사라지면?
        if(adapterVoca.getItemCount()<=0){
            layoutVocaIs.setVisibility(View.VISIBLE);
            layoutVocaNone.setVisibility(View.GONE);
        }
    }*/

    // @20200214 추가.
    /*void refreshWholeStarBtns(String wordKorSelected, boolean isPayloadNeeded){
        int vocaCount = adapterVoca.getItemCount();
        for (int i=0;i<vocaCount;i++){
            if(wordsAll.get(i).getWordKor().equals(wordKorSelected)){
                if(isPayloadNeeded){
                    adapterVoca.notifyItemChanged(i, "vocaIcon");
                }
                else{
                    adapterVoca.notifyItemChanged(i);
                }
            }
        }
    }*/

    void refreshWholeFeedRecyclerViews(ArrayList<Song> newestFavoritesList){
        int adaptersSize = songsAdapters.size();
        for(int p=0;p<adaptersSize;p++){ // 각각의 어댑터마다
            int listSize =favoritesList.size();
            for (int k=0;k<listSize;k++){ // 일단 favorites 리스트를 돌건데
                for (int i=0;i<songsAdapters.get(p).getItemCount();i++){ // 각각의 어댑터가 갖고 있는 아이템(Song) 수 만큼 돌거야
                    //if(categories.get(p).get(i).songSaveFormat().equals(newestFavoritesList.get(k).songSaveFormat())){ // 만약 어댑터가 가진 노래가 favorites안의 노래와 같다면
                    songsAdapters.get(p).notifyItemChanged(i, "favoriteIcon"); // refresh!
                    //}else{
                    //    songsAdapters.get(p).notifyItemChanged(i); // refresh!
                    //}
                }
            }
        }
    }

    // @20200210 songs 모든 어댑터 모든 곡 리프레시 시키는 코드.
    void refreshWholeHeartBtns(String songToSaveFormat, boolean isPayloadNeeded){
        int adaptersSize = songsAdapters.size();
        for(int a=0;a<adaptersSize;a++){ // 어댑터 개수 a만큼 돈다
            for (int i=0;i<songsAdapters.get(a).getItemCount();i++){ // 각 어댑터의 아이템 갯수 i를 구한다.
                //if(categories.get(a).get(i).songSaveFormat().equals(songToSaveFormat)){
                if(categoryAll.get(a).getSongs().get(i).songSaveFormat().equals(songToSaveFormat)){
                    // 카테고리 개수==어댑터 갯수일테니까. 카테고리의 어댑터 위치로 가서,
                    // 그 어댑터의 아이템위치로 가서, 이름/가수 포맷이 일치하는지 확인한다.
                    if(isPayloadNeeded){
                        songsAdapters.get(a).notifyItemChanged(i, "favoriteIcon");
                    }
                    else{
                        songsAdapters.get(a).notifyItemChanged(i);
                    }
                }
            }
        }
    }

    private void changeTab(int index) {
        frameLayout.setVisibility(View.VISIBLE) ;
        switch (index) {
            case 0 :
                getSupportFragmentManager().beginTransaction().replace(R.id.songs_frameLayout,feedFragment).commit();
                LogFirebaseAnalytics("FEED_TAB", "FEED_TAB", "Click");
                break ;
            case 1 :
                getSupportFragmentManager().beginTransaction().replace(R.id.songs_frameLayout,vocaFragment).commit();
                LogFirebaseAnalytics("VOCA_TAB", "VOCA_TAB", "Click");
                break ;
            case 2 :
                getSupportFragmentManager().beginTransaction().replace(R.id.songs_frameLayout,favoritesFragment).commit();
                LogFirebaseAnalytics("FAVORITES_TAB", "FAVORITES_TAB", "Click");
                break ;
            case 3:
                getSupportFragmentManager().beginTransaction().replace(R.id.songs_frameLayout,aboutFragment).commit();
                LogFirebaseAnalytics("ABOUT_TAB", "ABOUT_TAB", "Click");
                break;
        }
    }

    static Song songFIndBySongFormat(String songSaveFormat){
        Song tempSong = null;
        for(int i=0;i<songsAll.size();i++){
            if(songsAll.get(i).songSaveFormat().equals(songSaveFormat)){
                tempSong = songsAll.get(i);
            }
        }
        return tempSong;
    }


    // 추천 리스트 만드는 로직인데 더 보완하셈.
    // 1) Feed -> Youtube : 지금은 카테고리에서 현재곡 뺀 나머지를 리턴함
    // 2) Favorites -> Youtube : 플레이리스트 전체를 리턴
    static ArrayList<Song> getRecommendSongs(Song selectedSong, ArrayList<Song> selectedCatergory){
        ArrayList<Song> tempSongs = new ArrayList<Song>();
        if(CURRENT_TAB_NUM==0){ // 지금 feed 탭이면
            tempSongs.addAll(selectedCatergory);
            tempSongs.remove(selectedSong);
        }else{
            tempSongs = favoritesList;
        }
        return tempSongs;
    }


    SongsAdapter.OnItemClickListener recyclerViewListener = new SongsAdapter.OnItemClickListener(){
        @Override
        public void onItemClick(View v, int position, int adapterNum) { // 아이템 클릭 이벤트를 MainActivity에서 처리
            //Toast.makeText(getApplicationContext(),"Adapter# : "+ adapterNum +", Item# : "+position,Toast.LENGTH_SHORT).show();
            LogFirebaseAnalytics("FEED_SONG","FEED_SONG_"+"ADAPTER_"+adapterNum+"_POS_"+position,"Click");

            // Feed->Youtube
            Intent intent = new Intent(SongsActivity.this, YoutubeActivity.class);
            //intent.putExtra("selectedSong", categories.get(adapterNum).get(position));
            //intent.putParcelableArrayListExtra("words", categories.get(adapterNum).get(position).getWords());
            //intent.putParcelableArrayListExtra("recommend", getRecommendSongs(categories.get(adapterNum).get(position),categories.get(adapterNum)));
            intent.putExtra("selectedSong", categoryAll.get(adapterNum).getSongs().get(position));
            intent.putParcelableArrayListExtra("words", categoryAll.get(adapterNum).getSongs().get(position).getWords());
            intent.putParcelableArrayListExtra("recommend", getRecommendSongs(categoryAll.get(adapterNum).getSongs().get(position),categoryAll.get(adapterNum).getSongs()));
            intent.putExtra("mode", 1);
            startActivity(intent);
        }
    };

    // 위에랑 똑같이 youtube Act로 가는 애인데. 위에는 feed탭에서 가는애고 얘는 favortes 탭에서 가는 애임. 얘는 어댑터 내 아이템 pos를 같이 줘야해
    SongsAdapter.OnItemClickListener recyclerViewFavortesListener = new SongsAdapter.OnItemClickListener(){
        @Override
        public void onItemClick(View v, int position, int adapterNum) { // 아이템 클릭 이벤트를 MainActivity에서 처리
            //Toast.makeText(getApplicationContext(),"Adapter# : "+ adapterNum +", Item# : "+position,Toast.LENGTH_SHORT).show();
            LogFirebaseAnalytics("FAVORITES_SONG","FAVORITES_SONG"+"ADAPTER_"+adapterNum+"_POS_"+position,"Click");

            // 이제 Feed->Youtube->Words 순으로 진행할거야
            Intent intent = new Intent(SongsActivity.this, YoutubeActivity.class);
            intent.putExtra("selectedSong", favoritesList.get(position));
            intent.putParcelableArrayListExtra("words", favoritesList.get(position).getWords());
            //intent.putParcelableArrayListExtra("recommend", getRecommendSongs(categories.get(adapterNum).get(position),categories.get(adapterNum)));
            //intent.putParcelableArrayListExtra("recommend", getRecommendSongs(favoritesList.get(position),categories.get(adapterNum)));
            intent.putParcelableArrayListExtra("recommend", getRecommendSongs(favoritesList.get(position),categoryAll.get(adapterNum).getSongs()));
            intent.putExtra("mode", 2);
            intent.putExtra("favoritesPos", position);
            startActivity(intent);
        }
    };

    WordCardAdapter.OnItemClickListener recyclerViewVocaListener = new WordCardAdapter.OnItemClickListener(){
        @Override
        public void onItemClick(View v, int position, int layoutType) { // 아이템 클릭 이벤트를 MainActivity에서 처리
            //Toast.makeText(getApplicationContext(),"Clicked Voca Item Num : "+position,Toast.LENGTH_SHORT).show();
            LogFirebaseAnalytics("VOCA_WORDS","VOCA_WORDS"+"_POS_"+position,"Click");

            // 누르면 상세화면(Words Activity)로 넘어감
            Intent intent = new Intent(SongsActivity.this, WordsActivity.class);
            intent.putExtra("mode", 2); // mode=1이 feed->youtube->word 로 이어지는 경우, mode=2가 voca->word로 열리는 경우
            intent.putExtra("selectedVocaWordNum", position);
            startActivity(intent);
        }
    };

    CategoryAdapter.OnItemClickListener recyclerViewCategoryListener = new CategoryAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            //Toast.makeText(getApplicationContext(),"Clicked Category in About Feed : "+position,Toast.LENGTH_SHORT).show();
            LogFirebaseAnalytics("CATEGORY IN ABOUT","CATEGORY"+"_NUM_"+position,"Click");

            // 카테고리 상세 액티비티로 넘어감
            // TODO : 카테고리 상세페이지 만들어야함
            /*Intent intent = new Intent(SongsActivity.this, CategoryActivity.class);
            *//*intent.putExtra("categoryNum", position);
            intent.putExtra("category", categoryAll.get(position)); // ... 이거만 가져가면 되나?
            intent.putParcelableArrayListExtra("songs", categoryAll.get(position).getSongs());*//*
            intent.putExtra("categoryName", categoriesForFeed.get(position).getName());
            intent.putExtra("type", 1);
            startActivity(intent);*/
        }
    };


    @Override
    public void onFragmentInteraction(Uri uri) {
        // 얘가 지금 2개 프래그먼트 전부 연결됨
    }



} // end of activity

