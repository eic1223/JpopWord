package com.dane.jpopword;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Random;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.dane.jpopword.SplashActivity.categoryAll;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LyricsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LyricsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LyricsFragment extends Fragment implements SongsAdapter.OnHeartClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "lyricsJapKorPron";
    private static final String ARG_PARAM2 = "lyricsJapKor";
    private static final String ARG_PARAM3 = "lyricsJap";
    private static final String ARG_PARAM4 = "updateDate";

    static int LYRICS_TYPE = 0;
    TextView textViewLyrics;
    TextView textViewUpdateDate;
    TextView textViewTitle,textViewSinger;
    ScrollView scrollViewLyrics;

    private String lyricsJapKorPron;
    private String lyricsJapKor;
    private String lyricsJap;
    private String updateDate;

    private OnFragmentInteractionListener mListener;

    public LyricsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LyricsFragment.
     */
    public static LyricsFragment newInstance(String param1, String param2, String param3, String param4) {
        LyricsFragment fragment = new LyricsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lyricsJapKorPron = getArguments().getString(ARG_PARAM1);
            lyricsJapKor = getArguments().getString(ARG_PARAM2);
            lyricsJap = getArguments().getString(ARG_PARAM3);
            updateDate = getArguments().getString(ARG_PARAM4);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lyrics, container, false);
        scrollViewLyrics = (ScrollView)view.findViewById(R.id.youtube_lyricsFragment_scrollView);
        textViewLyrics = (TextView)view.findViewById(R.id.youtube_lyricsFragment_textViewLyrics);
        textViewUpdateDate = (TextView)view.findViewById(R.id.youtube_lyricsFragment_textView_updateDate);
        textViewTitle = (TextView)view.findViewById(R.id.youtube_lyricsFragment_textViewTitle);
        textViewSinger = (TextView)view.findViewById(R.id.youtube_lyricsFragment_textViewSinger);
        final ImageView imageViewAlbum = (ImageView)view.findViewById(R.id.youtube_lyricsFragment_imageView_album);
        FloatingActionButton fabChange = (FloatingActionButton)view.findViewById(R.id.youtube_lyricsFragment_fab_change);

        textViewLyrics.setText(lyricsJapKorPron);
        textViewTitle.setText(YoutubeActivity.songFromSongsAct.getTitleKorAndJap());
        textViewSinger.setText(YoutubeActivity.songFromSongsAct.getSingerKorAndJap());
        textViewUpdateDate.setText(updateDate);
        Glide.with(getContext())
                .load(YoutubeActivity.songFromSongsAct.getAlbumImageUrl())
                .error(
                    Glide.with(getContext())
                        .load(R.drawable.mqdefault))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        imageViewAlbum.setImageResource(R.drawable.album_default_icon);
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .transition(withCrossFade())
                .apply(new RequestOptions().transform(new RoundedCorners(20)))
                .into(imageViewAlbum);
        fabChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (LYRICS_TYPE){
                    case 0:
                        textViewLyrics.setText(lyricsJapKor);
                        LYRICS_TYPE = 1;
                        //mBtnChangeLyrics.setText("가사/해석");

                        int y = scrollViewLyrics.getScrollY();
                        scrollViewLyrics.setScrollY((int)(y*0.666));
                        break;
                    case 1:
                        textViewLyrics.setText(lyricsJap);
                        LYRICS_TYPE = 2;
                        //mBtnChangeLyrics.setText("가사");

                        int y2 = scrollViewLyrics.getScrollY();
                        scrollViewLyrics.setScrollY((int)(y2*0.5));
                        break;
                    case 2:
                        textViewLyrics.setText(lyricsJapKorPron);
                        LYRICS_TYPE = 0;
                        //mBtnChangeLyrics.setText("가사/발음/해석");

                        int y3 = scrollViewLyrics.getScrollY();
                        scrollViewLyrics.setScrollY((int)(y3*3));
                        break;
                }
                YoutubeActivity.LogFirebaseAnalytics("LYRICS_CHANGE","LYRICS_CHANGE","Click");
            }
        });

        // 추천 노래 리스트 띄우기
        SongsAdapter recommendSongsAdapter = new SongsAdapter(YoutubeActivity.songsRecommend, getContext(), this, 1,0);
        RecyclerView recyclerViewRecommend = (RecyclerView)view.findViewById(R.id.youtube_lyricsFragment_recyclerView_recommend);
        recyclerViewRecommend.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)) ;
        recyclerViewRecommend.setAdapter(recommendSongsAdapter);
        recommendSongsAdapter.setOnItemClickListener(recyclerViewListener);
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewRecommend);

        TextView textViewRecommend = (TextView)view.findViewById(R.id.youtube_lyricsFragment_textViewRecommend);
        if(YoutubeActivity.ACTIVITY_MODE==1){
            textViewRecommend.setText(R.string.lyricsFragment_recommend_category);
        }else{ // mode 2
            textViewRecommend.setText(R.string.lyricsFragment_recommend_favorites);
            recyclerViewRecommend.scrollToPosition(YoutubeActivity.favoritesPos);
        }

        return view;
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


    // 하트 버튼 눌렸을 때 동작하는 코드
    @Override
    public void onHeartClick(View v, int pos, String songToSaveFormat) {
        Log.d("LIKE CHECK", "Youtube(Lyrics)에서 눌림");
        YoutubeActivity.LogFirebaseAnalytics("LYRICS_HEART","LYRICS_HEART","Click");
    }

    SongsAdapter.OnItemClickListener recyclerViewListener = new SongsAdapter.OnItemClickListener(){
        @Override
        public void onItemClick(View v, int position, int adapterNum) {
            Log.d("ITEM CHECK", "Youtube(Lyrics)에서 눌림");

            //Toast.makeText(getContext(),"Adapter# : "+ adapterNum +", Item# : "+position,Toast.LENGTH_SHORT).show();

            // Feed->Youtube->Words 순으로 진행됨
            Intent intent = new Intent(getContext(), YoutubeActivity.class);
            intent.putExtra("selectedSong", YoutubeActivity.songsRecommend.get(position));
            intent.putParcelableArrayListExtra("words", SongsActivity.songFIndBySongFormat(YoutubeActivity.songsRecommend.get(position).songSaveFormat()).getWords());

            // Songs->Youtube로 올 때 recommend와 Youtube->Youtube로 갈 때 recommend가 짜이는 로직이 다른데... 나중에 로직 더 다듬어야 할 듯
            if(YoutubeActivity.ACTIVITY_MODE==1){
                //ArrayList<ArrayList<Song>> tempList = SongsActivity.categories;
                //tempList.remove(YoutubeActivity.songsRecommend);
                //intent.putParcelableArrayListExtra("recommend", tempList.get(new Random().nextInt(tempList.size())));
                intent.putParcelableArrayListExtra("recommend", categoryAll.get(new Random().nextInt(categoryAll.size())).getSongs());
                intent.putExtra("mode", 1);
                YoutubeActivity.LogFirebaseAnalytics("LYRICS_RECOMMEND_FEED","LYRICS_RECOMMEND_FEED","Click");
            }else{ // mode 2
                intent.putParcelableArrayListExtra("recommend", YoutubeActivity.songsRecommend);
                intent.putExtra("favoritesPos", position);
                intent.putExtra("mode", 2);
                YoutubeActivity.LogFirebaseAnalytics("LYRICS_RECOMMEND_FAVORITES","LYRICS_RECOMMEND_FAVORITES","Click");
            }

            startActivity(intent);
            //
            getActivity().finish();
        }
    };


} // end of script

