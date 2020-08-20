package com.dane.jpopword;

import android.content.Context;
import android.content.Intent;
//import android.content.SharedPreferences;
import com.dane.jpopword.model.SharedPrefControl;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.dane.jpopword.SongsActivity.badgeList;
import static com.dane.jpopword.SplashActivity.songsAll;
import static com.dane.jpopword.YoutubeActivity.LogFirebaseAnalytics;

public class AboutFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private int RC_SIGN_IN = 9000;
    private String FILE_NAME_SHAREDPREF = "sFile";
    private String BADGES = "badges";

    private LinearLayout layoutGuestUser, layoutLoginUser;
    private Button btnLogin;
    private TextView btnLogout;
    public TextView textViewUserAccount;
    private TextView textViewBadgeCategory;

    private BadgeAdapter adapterBadges;
    private CategoryAdapter adapterCategory;

    private RecyclerView recyclerViewBadges;

    private ArrayList<String> tempSongsForBadges; // 뱃지 구성할 때 임시로 쓰는 애

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AboutFragment() {
        // Required empty public constructor
    }

    public static AboutFragment newInstance(String param1, String param2) {
        AboutFragment fragment = new AboutFragment();
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
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        recyclerViewBadges = (RecyclerView)view.findViewById(R.id.aboutFragment_recyclerView_badge);
        textViewBadgeCategory = (TextView)view.findViewById(R.id.aboutFragment_textView_badge_cat);
        textViewUserAccount = (TextView)view.findViewById(R.id.aboutFragment_textView_userId);
        layoutGuestUser = (LinearLayout)view.findViewById(R.id.aboutFragment_box_guestUser);
        layoutLoginUser = (LinearLayout)view.findViewById(R.id.aboutFragment_box_loginUser);
        btnLogin = (Button)view.findViewById(R.id.aboutFragment_btn_login);

        // 로그인 정보 보여주는 부분
        btnLogin.setOnClickListener(new View.OnClickListener() { // 로그인 버튼 클릭 리스너
            @Override
            public void onClick(View v) {
                //LogFirebaseAnalytics("ABOUT_LOGIN", "ABOUT_LOGIN", "Click");
                startActivityForResult(new Intent(getActivity(), LoginActivity.class), RC_SIGN_IN);
            }
        });
        btnLogout = (TextView)view.findViewById(R.id.aboutFragment_btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 로그아웃 버튼 클릭 리스너
//                    LogFirebaseAnalytics("ABOUT_LOGOUT", "ABOUT_LOGOUT", "Click");
                AuthUI.getInstance()
                        .signOut(getActivity().getApplicationContext())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // user is now signed out
                                refreshLoginState(); // 로그아웃 성공했으면 리프레시 해줘야지
                                Toast.makeText(getActivity().getApplicationContext(),"Logout!",Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(MyActivity.this, SignInActivity.class));
                                //finish();
                            }
                        });
            }
        });
        refreshLoginState();

        RecyclerView recyclerViewCategory = (RecyclerView)view.findViewById(R.id.aboutFragment_recyclerView_category);
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewCategory.setNestedScrollingEnabled(false);

        adapterCategory = new CategoryAdapter(getActivity(), SongsActivity.categoriesForFeed);
        adapterCategory.setOnItemClickListener(recyclerViewCategoryListener);
        recyclerViewCategory.setAdapter(adapterCategory);


        Button btnSendEmail = (Button)view.findViewById(R.id.aboutFragment_btn_send_email);
        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("mailto:"+getString(R.string.report_email));
                Intent mailSendIntent = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(mailSendIntent);
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    void refreshLoginState(){
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

            showBadges(); // +) 뱃지 나오는 부분 나오도록.
        } else {
            // not signed in
            textViewUserAccount.setText("게스트 모드입니다");

            // 뱃지 안보이게 숨기고. 로그인하고 더 많은 기능을 쓰라고 알려줘야지.
            layoutGuestUser.setVisibility(View.VISIBLE);
            layoutLoginUser.setVisibility(View.GONE);
        }
    }

    void showBadges(){
        // 뱃지 보여주는 함수
        // 1) sharedPref에서 먼저 불러와서 보여준다.
        // 2) Firebase에서 불러오는게 succeess 되면 sharedPref에서 불러온 거랑 비교해본다.
        // 3) 같으면 냅두고, 다르면 firebase 기준으로 덮어 씌운 후, adapter를 업데이트 한다.

        // (로그인/비로그인 공통) sharedPref 데이터 불러와서 -> 뱃지 리스트 만들기
        /*SharedPreferences prefBadge =getActivity().getSharedPreferences("sFile", Context.MODE_PRIVATE);
        final HashSet<String> badgeData = new HashSet<String>(prefBadge.getStringSet("badges", new HashSet<String>()));*/
        final HashSet<String> badgeData = SharedPrefControl.loadDataSetByKeyInSharedPref(getActivity(),FILE_NAME_SHAREDPREF,BADGES);

        badgeList = new ArrayList<Song>();
        for(int i = 0; i< songsAll.size(); i++){
            if(badgeData.contains(songsAll.get(i).songSaveFormat())){
                badgeList.add(songsAll.get(i));
            }
        }

        // 2) 어댑터에 넣어서 -> 리사이클러뷰에 붙여서 보여줘
        adapterBadges = new BadgeAdapter(badgeList,getActivity());

        recyclerViewBadges.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewBadges.setAdapter(adapterBadges);

        // 3) 뱃지가 총 몇 개인지도 보여주고

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
                                    /*SharedPreferences pref = getActivity().getSharedPreferences("sFile", Context.MODE_PRIVATE);
                                    HashSet<String> badgeData = new HashSet<String>(pref.getStringSet("badges", new HashSet<String>())); // "badges"라는 키로 저장되어있던 스트링셋 불러오고
                                    Log.d("badges PROCESS","badges Before-Saved Data: " + badgeData);*/
                                    HashSet<String> badgeData = SharedPrefControl.loadDataSetByKeyInSharedPref(getActivity(),FILE_NAME_SHAREDPREF,BADGES);

                                    badgeData = new HashSet<String>();
                                    for(int i=0;i<badgeList.size();i++){
                                        String newBadgeToSave = badgeList.get(i).songSaveFormat();
                                        badgeData.add(newBadgeToSave); // HashSet에 saveFormat()을 추가한다
                                    }

                                    // 기기에 다시 저장해주는 과정
                                    /*SharedPreferences.Editor editor = pref.edit(); // 에디터를 열어서
                                    editor.putStringSet("badges", (Set<String>) badgeData);
                                    editor.apply();
                                    Log.d("badges PROCESS","badges After-Saved Data: " + pref.getStringSet("badges", new HashSet<String>()));*/
                                    SharedPrefControl.saveDataSetByKeyInSharedPref(getActivity(), FILE_NAME_SHAREDPREF, BADGES,badgeData);
                                    // 여기까지

                                    // 그리고 어댑터(리사이클러뷰)에 데이터 바뀌는거 있다고 알려줘야지
                                    adapterBadges.notifyDataSetChanged();
                                    textViewBadgeCategory.setText(getString(R.string.songs_category_badgeCount, String.valueOf(badgeList.size())));
                                    //textViewBadgeCategory.setText("뱃지("+badgeList.size()+")");

                                    // 카테고리 퍼센티지에도 반영해줘야해
                                    adapterCategory.notifyDataSetChanged();


                                    Log.d("Badges Check", "BadgeList updated with Firebase");
                                }
                            /*
                            adapterBadges = new BadgeAdapter(badgeList,SongsActivity.this);
                            recyclerViewBadges.setAdapter(adapterBadges);
                            textViewBadgeCategory.setText("뱃지("+badgeList.size()+")");*/
                            } else {
                                Log.d("Badges Check", "Error - Just used sharedPref, not Firestore");
                            }
                        }
                    });
        }else{
            Log.d("Badges Check", "Not Login");
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    CategoryAdapter.OnItemClickListener recyclerViewCategoryListener = new CategoryAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            //Toast.makeText(getApplicationContext(),"Clicked Category in About Feed : "+position,Toast.LENGTH_SHORT).show();
            //LogFirebaseAnalytics("CATEGORY IN ABOUT","CATEGORY"+"_NUM_"+position,"Click");

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

    public void showLoginState(boolean isLogin){
        if(isLogin){
            layoutGuestUser.setVisibility(View.GONE);
            layoutLoginUser.setVisibility(View.VISIBLE);
        }else{
            layoutGuestUser.setVisibility(View.VISIBLE);
            layoutLoginUser.setVisibility(View.GONE);
        }
    }



}
