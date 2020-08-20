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

import static com.dane.jpopword.SongsActivity.vocaList;
import static com.dane.jpopword.SplashActivity.wordsAll;
import static com.dane.jpopword.YoutubeActivity.LogFirebaseAnalytics;

public class VocaFragment extends Fragment implements WordCardAdapter.OnStarClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String FILE_NAME_SHAREDPREF = "sFile";
    private String VOCA = "voca";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private LinearLayout layoutVocaNone, layoutVocaIs;
    private RecyclerView recyclerViewVoca;
    private WordCardAdapter adapterVoca;

    public VocaFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static VocaFragment newInstance(String param1, String param2) {
        VocaFragment fragment = new VocaFragment();
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
        View view = inflater.inflate(R.layout.fragment_voca, container, false);

        // 1) 유저가 저장해 놓은 단어들 불러와서 ArrayList 만들고
        /*SharedPreferences prefVoca = getActivity().getSharedPreferences("sFile", Context.MODE_PRIVATE);
        HashSet<String> data = new HashSet<String>(prefVoca.getStringSet("voca", new HashSet<String>()));
        Log.d("FUCK","저장된 voca: " + data);*/
        HashSet<String> data = SharedPrefControl.loadDataSetByKeyInSharedPref(getActivity(),FILE_NAME_SHAREDPREF,VOCA);

        layoutVocaNone = view.findViewById(R.id.vocaFragment_layout_voca_none);
        layoutVocaIs = view.findViewById(R.id.vocaFragment_layout_voca_is);

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
                if (!isDuplication) {
                    tempList.add(vocaList.get(k));
                    index++;
                }
            }
        }
        vocaList.clear();
        vocaList.addAll(tempList);
        //

        // Adapter 만들어서 recylcerView에 붙여주고
        recyclerViewVoca = view.findViewById(R.id.vocaFragment_recyclerView_voca);
        recyclerViewVoca.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        // voca 리사이클러뷰에 어댑터 붙일거야
        adapterVoca = new WordCardAdapter(vocaList, getActivity(), this, 2);
        //adapterVoca.setHasStableIds(true);
        adapterVoca.setOnItemClickListener(recyclerViewVocaListener);
        recyclerViewVoca.setAdapter(adapterVoca);

        LinearSnapHelper snapHelperVoca = new LinearSnapHelper();
        snapHelperVoca.attachToRecyclerView(recyclerViewVoca);

        showVocaIfListNone(); // 만약 저장해 놓은 단어가 하나도 없으면 없다고 멘트 보여주고

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

    WordCardAdapter.OnItemClickListener recyclerViewVocaListener = new WordCardAdapter.OnItemClickListener(){
        @Override
        public void onItemClick(View v, int position, int layoutType) { // 아이템 클릭 이벤트를 MainActivity에서 처리
            // 누르면 상세화면(Words Activity)로 넘어감
            Intent intent = new Intent(getActivity(), WordsActivity.class);
            intent.putExtra("mode", 2); // mode=1이 feed->youtube->word 로 이어지는 경우, mode=2가 voca->word로 열리는 경우
            intent.putExtra("selectedVocaWordNum", position);
            startActivity(intent);
        }
    };

    @Override
    public void onStarClick(View v, int pos, String wordSelected) {
        // 이건 songs의 voca에서 눌린거니까
        adapterVoca.removeItem(pos);
        adapterVoca.notifyItemRemoved(pos);
        adapterVoca.notifyDataSetChanged(); // 이걸 안넣어주면 맨 위 아이템을 클릭했을 때 Inconsistency detected 에러로 죽음
        //adapterVoca.notifyItemRangeChanged(pos,adapterVoca.getItemCount()); // 이거 이렇게 쓰는거 맞음?
        //refreshWholeStarBtns(wordSelected, false); // payload주고(true) 부분만 바꾸는게 아니라 바로 삭제
//        LogFirebaseAnalytics("VOCA_STAR","VOCA_STAR"+wordSelected,"Click");
        Log.d("STAR CHECK", "voca tab에서 눌림");

        showVocaIfListNone();
    }

    void showVocaIfListNone(){
        if(vocaList.size()==0){
            layoutVocaIs.setVisibility(View.GONE);
            layoutVocaNone.setVisibility(View.VISIBLE);
        } else{ // 눌러놓은게 하나라도 있으면 -> 리사이클러뷰에 담아서 보여주고
            layoutVocaIs.setVisibility(View.VISIBLE);
            layoutVocaNone.setVisibility(View.GONE);
        }
    }

}
