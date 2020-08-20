package com.dane.jpopword;

import android.content.Context;
import android.content.Intent;
//import android.content.SharedPreferences;
import com.dane.jpopword.model.SharedPrefControl;
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

import java.util.ArrayList;
import java.util.HashSet;

import static com.dane.jpopword.SongsActivity.favoritesList;
import static com.dane.jpopword.SongsActivity.songsAdapters;
import static com.dane.jpopword.SplashActivity.categoryAll;
import static com.dane.jpopword.SplashActivity.songsAll;

public class FavoritesFragment extends Fragment implements SongsAdapter.OnHeartClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    LinearLayout layoutFavoritesNone, layoutFavoritesIs;
    RecyclerView recyclerViewFavorites;
    SongsAdapter adapterFavorites;

    private String FILE_NAME_SHAREDPREF = "sFile";
    private String FAVORITES = "favorites";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FavoritesFragment newInstance(String param1, String param2) {
        FavoritesFragment fragment = new FavoritesFragment();
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
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        // 1) favorites 리스트 데이터 불러오고(업데이트 하고)
        /*SharedPreferences pref = getActivity().getSharedPreferences("sFile", Context.MODE_PRIVATE);
        HashSet<String> data = new HashSet<String>(pref.getStringSet("favorites", new HashSet<String>()));*/
        HashSet<String> data = SharedPrefControl.loadDataSetByKeyInSharedPref(getActivity(),FILE_NAME_SHAREDPREF, FAVORITES);

        layoutFavoritesNone = view.findViewById(R.id.favoritesFragment_layout_favorites_none);
        layoutFavoritesIs = view.findViewById(R.id.favoritesFragment_layout_favorites_is);
        recyclerViewFavorites = view.findViewById(R.id.favoritesFragment_recyclerView_favorites);
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        favoritesList = new ArrayList<>();
        for(int i = 0; i< songsAll.size(); i++){
            if(data.contains(songsAll.get(i).songSaveFormat())){
                favoritesList.add(songsAll.get(i));
            }
        }

        // 리사이클러뷰에 어댑터 붙일거야
        adapterFavorites = new SongsAdapter(favoritesList, getActivity(), this,2,0); // 얘 0으로 하는게 맞나? ㅇㅇ 맞는 듯
        adapterFavorites.setHasStableIds(true);
        adapterFavorites.setOnItemClickListener(recyclerViewFavortesListener);
        recyclerViewFavorites.setAdapter(adapterFavorites);

        LinearSnapHelper snapHelperFavorites = new LinearSnapHelper();
        snapHelperFavorites.attachToRecyclerView(recyclerViewFavorites);

        // 만약 좋아요 눌러놓은 노래가 없으면 -> 눌러보라고 멘트가 나오게 하고
        showFavoritesIfListNone();

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


    // youtube Act로 가는 애인데. 위에는 feed탭에서 가는애고 얘는 favortes 탭에서 가는 애임. 얘는 어댑터 내 아이템 pos를 같이 줘야해
    SongsAdapter.OnItemClickListener recyclerViewFavortesListener = new SongsAdapter.OnItemClickListener(){
        @Override
        public void onItemClick(View v, int position, int adapterNum) { // 아이템 클릭 이벤트를 MainActivity에서 처리
            //Toast.makeText(getApplicationContext(),"Adapter# : "+ adapterNum +", Item# : "+position,Toast.LENGTH_SHORT).show();
            //LogFirebaseAnalytics("FAVORITES_SONG","FAVORITES_SONG"+"ADAPTER_"+adapterNum+"_POS_"+position,"Click");

            // 이제 Feed->Youtube->Words 순으로 진행할거야
            Intent intent = new Intent(getActivity(), YoutubeActivity.class);
            intent.putExtra("selectedSong", favoritesList.get(position));
            intent.putParcelableArrayListExtra("words", favoritesList.get(position).getWords());
            //intent.putParcelableArrayListExtra("recommend", getRecommendSongs(categories.get(adapterNum).get(position),categories.get(adapterNum)));
            //intent.putParcelableArrayListExtra("recommend", getRecommendSongs(favoritesList.get(position),categories.get(adapterNum)));
            intent.putParcelableArrayListExtra("recommend", SongsActivity.getRecommendSongs(favoritesList.get(position),categoryAll.get(adapterNum).getSongs()));
            intent.putExtra("mode", 2);
            intent.putExtra("favoritesPos", position);
            startActivity(intent);
        }
    };

    // 좋아요(하트) 버튼 눌렸을 때 동작하는 코드
    @Override
    public void onHeartClick(View v, int pos, String songToSaveFormat) {
        adapterFavorites.removeItem(pos);
        adapterFavorites.notifyItemRemoved(pos);
        adapterFavorites.notifyDataSetChanged(); // 이걸 안넣어주면 맨 위 아이템을 클릭했을 때 Inconsistency detected 에러로 죽음
        // adapterFavorites.notifyItemRangeChanged(pos,adapterFavorites.getItemCount());
        refreshWholeHeartBtns(songToSaveFormat, false);
        //LogFirebaseAnalytics("FAVORITES_HEART","FAVORITES_HEART"+songToSaveFormat,"Click");
        Log.d("LIKE CHECK", "Favorites에서 눌림. 삭제 콜백 먹음?");

        showFavoritesIfListNone();
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

    void showFavoritesIfListNone(){
        if(favoritesList.size()==0){
            layoutFavoritesIs.setVisibility(View.GONE);
            layoutFavoritesNone.setVisibility(View.VISIBLE);
        } else{ // 눌러놓은게 하나라도 있으면 -> 업데이트한 리스트를 어댑터에 적용시킨다
            layoutFavoritesIs.setVisibility(View.VISIBLE);
            layoutFavoritesNone.setVisibility(View.GONE);
        }
    }



}
