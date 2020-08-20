package com.dane.jpopword;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static com.dane.jpopword.SongsActivity.categoriesCover;
import static com.dane.jpopword.SongsActivity.categoriesForFeed;
import static com.dane.jpopword.SongsActivity.getRecommendSongs;
import static com.dane.jpopword.SongsActivity.songRecyclerViews;
import static com.dane.jpopword.SongsActivity.songsAdapters;
import static com.dane.jpopword.SplashActivity.categoryAll;

public class FeedFragment extends Fragment implements SongsAdapter.OnHeartClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    LinearLayout linearLayoutFeedTab;


    public FeedFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FeedFragment newInstance(String param1, String param2) {
        FeedFragment fragment = new FeedFragment();
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
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        linearLayoutFeedTab = view.findViewById(R.id.feedFragment_feed_parent_layout);

        // category 개수에 맞게 리사이클러뷰 할당하고, LinearLayoutManager 객체를 지정한다.
        // TODO : type이 1(default)인 애들만 넣고, 2(cover)인 경우는 케이스를 따로 빼자. (밑에 작업 해야함)
        songRecyclerViews = new ArrayList<RecyclerView>();
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
            RecyclerView recyclerViewSong = new RecyclerView(getActivity());
            recyclerViewSong.setId(i);
            recyclerViewSong.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false)) ;
            // linearLayoutFeedTab.addView(recyclerViewSong,i*3+1); // 부모역할을 하는 xml에 붙인다. 두번째 param이 위치임.

            songRecyclerViews.add(recyclerViewSong); // 이건 recyclerView를 모아놓은 array list에 추가하는 거

            TextView textViewTitle = new TextView(getActivity());
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

        }

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
        songsAdapters = new ArrayList<SongsAdapter>();
        for(int i=0;i<categoriesForFeed.size();i++){
            songsAdapters.add(new SongsAdapter(categoriesForFeed.get(i).getSongs(),getActivity(),this,1,i));
        }

        // 어댑터에 붙이는 리스너도, 리사이클러뷰에 붙이는 어댑터도 categoriesForFeed 개수에 맞게 동적으로 만들어줌
        for(int i=0;i<categoriesForFeed.size();i++){
            songsAdapters.get(i).setOnItemClickListener(recyclerViewListener);
            songRecyclerViews.get(i).setAdapter(songsAdapters.get(i));

            LinearSnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(songRecyclerViews.get(i));
        }



        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void addLineSeperator(int index) {
        LinearLayout lineLayout = new LinearLayout(getActivity());
        lineLayout.setBackgroundColor(Color.GRAY);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                2);
        params.setMargins(0, 10, 0, 10);
        lineLayout.setLayoutParams(params);
        linearLayoutFeedTab.addView(lineLayout, index);
    }


    SongsAdapter.OnItemClickListener recyclerViewListener = new SongsAdapter.OnItemClickListener(){
        @Override
        public void onItemClick(View v, int position, int adapterNum) { // 아이템 클릭 이벤트를 MainActivity에서 처리
            //Toast.makeText(getApplicationContext(),"Adapter# : "+ adapterNum +", Item# : "+position,Toast.LENGTH_SHORT).show();

            // Feed->Youtube
            Intent intent = new Intent(getActivity(), YoutubeActivity.class);
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

    // 좋아요(하트) 버튼 눌렸을 때 동작하는 코드
    // 지금 어느 탭이냐에 따라(feed or favorites) 다르게 동작한다.
    @Override
    public void onHeartClick(View v, int pos, String songToSaveFormat) {
        refreshWholeHeartBtns(songToSaveFormat, true);
        Log.d("LIKE CHECK", "Feed에서 눌림");
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



}
