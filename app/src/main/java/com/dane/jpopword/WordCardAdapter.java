package com.dane.jpopword;

import android.content.Context;
//import android.content.SharedPreferences;
import com.dane.jpopword.model.SharedPrefControl;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<WordCard> words = null;
    private Context mContext;
    private int mLayoutType;

    private int LAYOUT_WORD_ACT = 1;
    private int LAYOUT_VOCA_TAB_ADDED_WORD = 2;
    private int LAYOUT_QUIZ_ACT = 3;

    private String FILE_NAME_SHAREDPREF = "sFile";
    private String VOCA = "voca";

    // 생성자에서 데이터 리스트 객체를 전달받음.
    WordCardAdapter(ArrayList<WordCard> list, Context context, OnStarClickListener starClickListener, int layoutType) {
        this.words = list ;
        this.mContext = context;
        this.mStarListener = starClickListener;
        this.mLayoutType = layoutType;
        Log.d("WORDCARD ADAPTER","어댑터 객체 생성됨. 전달받은 layoutType="+layoutType+"저장된 layoutType="+mLayoutType);
    }

    @Override
    public long getItemId(int position) {
        Log.d("WORDCARD ADAPTER",String.valueOf(words.get(position).getSongId()));
        return words.get(position).getSongId();
    }

    // ViewHolder가 create 되는 순간에 어떤 탭이냐에 따라 item들의 layout이 다르게 생성된다
    @Override
    public int getItemViewType(int position) {
        // position은 여기선 의미 없어
        //return super.getItemViewType(position);
        if(mLayoutType==1){
            return 1;
        }else if(mLayoutType==2){
            return 2;
        }else{
            return 3;
        }
    }

    public void removeItem(int position) {
        words.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        if(getItemViewType(100)==LAYOUT_WORD_ACT){
            View view = inflater.inflate(R.layout.words_each_word, parent, false) ;
            RecyclerView.ViewHolder vh = new WordCardAdapter.WordsViewHolder(view) ;
            return vh ;
        }else if(getItemViewType(100)==LAYOUT_VOCA_TAB_ADDED_WORD){ // getItemViewType(1) == LAYOUT_VOCA_TAB_ADDED_WORD
            View view = inflater.inflate(R.layout.voca_each_words, parent, false) ;
            RecyclerView.ViewHolder vh = new WordCardAdapter.VocaTabWordsViewHolder(view) ;
            return vh ;
        } else{ // LAYOUT_QUIZ_ACT
            View view = inflater.inflate(R.layout.quiz_result_word, parent, false) ;
            RecyclerView.ViewHolder vh = new WordCardAdapter.QuizResultWordsViewHolder(view) ;
            return vh ;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)==LAYOUT_WORD_ACT){
            ((WordsViewHolder)holder).textViewWordJap.setText(words.get(position).wordJap);
            ((WordsViewHolder)holder).textViewWordJapPron.setText(words.get(position).wordJapPron);
            ((WordsViewHolder)holder).textViewWordKor.setText(words.get(position).wordKor);
            ((WordsViewHolder)holder).textViewWordKorPron.setText(words.get(position).wordKorPron);
            ((WordsViewHolder)holder).textViewSentenceJap.setText(words.get(position).sentenceJap);
            ((WordsViewHolder)holder).textViewSentenceKor.setText(words.get(position).sentenceKor);
            ((WordsViewHolder)holder).textViewSentencePron.setText(words.get(position).sentencePron);
            ((WordsViewHolder)holder).textViewWordNum.setText(String.valueOf(position+1)+"/"+words.size());

            if(words.get(position).wordJapPron.equals("")||words.get(position).wordJapPron==null){
                ((WordsViewHolder)holder).textViewWordJapPron.setVisibility(View.GONE);
            }
            // 뷰 만들 때 voca 데이터 돌면서 이미 단어장에 있는 애들은 좋아요(별) 눌린 이미지로 시작하게 만들어주는 부분
            /*SharedPreferences pref = mContext.getSharedPreferences("sFile", Context.MODE_PRIVATE);
            HashSet<String> dataForVocaCheck = new HashSet<String>(pref.getStringSet("voca", new HashSet<String>())); // "voca"이라는 키로 저장되어있던 데이터*/
            HashSet<String> dataForVocaCheck = SharedPrefControl.loadDataSetByKeyInSharedPref(mContext, FILE_NAME_SHAREDPREF, VOCA);
            if(dataForVocaCheck.contains(words.get(position).wordSaveFormat())){
                ((WordCardAdapter.WordsViewHolder)holder).imageButtonStar.setBackgroundTintList((ColorStateList.valueOf(Color.parseColor("#F57F17")))); // #F57F17, 100(A) 245,127,23
                ((WordCardAdapter.WordsViewHolder)holder).mIsStarBtnClicked = true;
            }else{
                ((WordCardAdapter.WordsViewHolder)holder).imageButtonStar.setBackgroundTintList((ColorStateList.valueOf(Color.parseColor("#FFFFFF")))); //
                ((WordCardAdapter.WordsViewHolder)holder).mIsStarBtnClicked = false;
            }
        }
        else if(getItemViewType(position)==LAYOUT_VOCA_TAB_ADDED_WORD){ // LAYOUT_VOCA_TAB_ADDED_WORD
            ((VocaTabWordsViewHolder)holder).textViewWordJap.setText(words.get(position).wordJap);
            ((VocaTabWordsViewHolder)holder).textViewWordJapPron.setText(words.get(position).wordJapPron);
            ((VocaTabWordsViewHolder)holder).textViewWordKor.setText(words.get(position).wordKor);

            if(words.get(position).wordJapPron.equals("")||words.get(position).wordJapPron==null){
                ((VocaTabWordsViewHolder)holder).textViewWordJapPron.setVisibility(View.GONE);
            }

            // 얘들은 어차피 단어장에 들어있는 애들이니까 체크 안하고 그냥 눌린 이미지로 바꿈
            ((WordCardAdapter.VocaTabWordsViewHolder)holder).imageViewStar.setBackgroundTintList((ColorStateList.valueOf(Color.parseColor("#F57F17")))); // #F57F17, 100(A) 245,127,23
            ((WordCardAdapter.VocaTabWordsViewHolder)holder).mIsStarBtnClicked = true;
        }else{ // LAYOUT_QUIZ_
            ((QuizResultWordsViewHolder)holder).textViewWordJap.setText(words.get(position).wordJap);
            ((QuizResultWordsViewHolder)holder).textViewWordJapPron.setText(words.get(position).wordJapPron);
            ((QuizResultWordsViewHolder)holder).textViewWordKor.setText(words.get(position).wordKor);
            ((QuizResultWordsViewHolder)holder).textViewWordKorPron.setText(words.get(position).wordKorPron);

            if(words.get(position).wordJapPron.equals("")||words.get(position).wordJapPron==null){
                ((QuizResultWordsViewHolder)holder).textViewWordJapPron.setVisibility(View.GONE);
            }

            // 뷰 만들 때 voca 데이터 돌면서 이미 단어장에 있는 애들은 좋아요(별) 눌린 이미지로 시작하게 만들어주는 부분
            /*SharedPreferences pref = mContext.getSharedPreferences("sFile", Context.MODE_PRIVATE);
            HashSet<String> dataForVocaCheck = new HashSet<String>(pref.getStringSet("voca", new HashSet<String>())); // "voca"이라는 키로 저장되어있던 데이터*/
            HashSet<String> dataForVocaCheck = SharedPrefControl.loadDataSetByKeyInSharedPref(mContext, FILE_NAME_SHAREDPREF, VOCA);
            if(dataForVocaCheck.contains(words.get(position).wordSaveFormat())){
                ((WordCardAdapter.QuizResultWordsViewHolder)holder).imageViewStar.setBackgroundTintList((ColorStateList.valueOf(Color.parseColor("#F57F17")))); // #F57F17, 100(A) 245,127,23
                ((WordCardAdapter.QuizResultWordsViewHolder)holder).mIsStarBtnClicked = true;
            }else{
                ((WordCardAdapter.QuizResultWordsViewHolder)holder).imageViewStar.setBackgroundTintList((ColorStateList.valueOf(Color.parseColor("#FFFFFF")))); // #F57F17, 100(A) 245,127,23
                ((WordCardAdapter.QuizResultWordsViewHolder)holder).mIsStarBtnClicked = false;
            }


        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if(payloads.isEmpty()){
            super.onBindViewHolder(holder, position, payloads);
        }else{
            for (Object payload : payloads) {
                if (payload instanceof String) {
                    String type = (String) payload;
                    if (TextUtils.equals(type, "favoriteIcon") && (holder instanceof WordsViewHolder || holder instanceof VocaTabWordsViewHolder|| holder instanceof QuizResultWordsViewHolder)) {

                        // 뷰 만들 때 단어저장 눌린 곡이라면 단어저장 눌린 이미지로 시작하게 만들어주는 부분
                        /*SharedPreferences pref = mContext.getSharedPreferences("sFile", Context.MODE_PRIVATE);
                        HashSet<String> dataForStarCheck = new HashSet<String>(pref.getStringSet("voca", new HashSet<String>())); // "voca"이라는 키로 저장되어있던 데이터*/
                        HashSet<String> dataForStarCheck = SharedPrefControl.loadDataSetByKeyInSharedPref(mContext, FILE_NAME_SHAREDPREF, VOCA);
                        if(dataForStarCheck.contains(words.get(position).wordSaveFormat())){
                            if(holder instanceof WordsViewHolder){
                                ((WordsViewHolder)holder).imageButtonStar.setBackgroundTintList((ColorStateList.valueOf(Color.parseColor("#F57F17")))); // #F57F17, 100(A) 245,127,23
                                ((WordsViewHolder)holder).mIsStarBtnClicked = true;
                            }else if(holder instanceof VocaTabWordsViewHolder){
                                ((VocaTabWordsViewHolder)holder).imageViewStar.setBackgroundTintList((ColorStateList.valueOf(Color.parseColor("#F57F17")))); // #F57F17, 100(A) 245,127,23
                                ((VocaTabWordsViewHolder)holder).mIsStarBtnClicked = true;
                            }else{ // holder instanceof VocaViewHolder
                                ((QuizResultWordsViewHolder)holder).imageViewStar.setBackgroundTintList((ColorStateList.valueOf(Color.parseColor("#F57F17")))); // #F57F17, 100(A) 245,127,23
                                ((QuizResultWordsViewHolder)holder).mIsStarBtnClicked = true;
                            }
                        }else{
                            if(holder instanceof WordsViewHolder){
                                ((WordsViewHolder)holder).imageButtonStar.setBackgroundTintList((ColorStateList.valueOf(Color.parseColor("#FFFFFF")))); //
                                ((WordsViewHolder)holder).mIsStarBtnClicked = false;
                            }else if(holder instanceof VocaTabWordsViewHolder){
                                ((VocaTabWordsViewHolder)holder).imageViewStar.setBackgroundTintList((ColorStateList.valueOf(Color.parseColor("#FFFFFF")))); //
                                ((VocaTabWordsViewHolder)holder).mIsStarBtnClicked = false;
                            }else{ // holder instanceof iewHolder
                                ((QuizResultWordsViewHolder)holder).imageViewStar.setBackgroundTintList((ColorStateList.valueOf(Color.parseColor("#FFFFFF"))));
                                ((QuizResultWordsViewHolder)holder).mIsStarBtnClicked = false;
                            }
                        }
                    }
                }
            }
        }
    }

    class WordsViewHolder extends RecyclerView.ViewHolder{
        TextView textViewWordJap, textViewWordJapPron, textViewWordKor, textViewWordKorPron,
                textViewSentenceJap, textViewSentenceKor, textViewSentencePron, textViewWordNum;
        ImageButton imageButtonStar;
        private boolean mIsStarBtnClicked = false; // 단어에 추가(별) 버튼이 눌려져있는가 여부. false면 안눌려져있는 상태인거지.

        public WordsViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewWordJap = itemView.findViewById(R.id.textView_wordJap);
            textViewWordJapPron = itemView.findViewById(R.id.textView_wordJapPron);
            textViewWordKor = itemView.findViewById(R.id.textView_wordKor);
            textViewWordKorPron = itemView.findViewById(R.id.textView_wordKorPron);
            textViewSentenceJap = itemView.findViewById(R.id.textView_sentenceJap);
            textViewSentenceKor = itemView.findViewById(R.id.textView_sentenceKor);
            textViewSentencePron = itemView.findViewById(R.id.textView_sentencePron);
            textViewWordNum = itemView.findViewById(R.id.textView_wordNum);
            Log.d("WORDCARD ADAPTER","Song(via Youtube)-뷰홀더 생성됨.");
            //
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION && listener!=null){
                        listener.onItemClick(v, position, mLayoutType);
                    }
                }
            });

            imageButtonStar = itemView.findViewById(R.id.words_btnStar);
            imageButtonStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                // 기기에 저장되어있던 데이터 불러와서
                /*SharedPreferences pref = mContext.getSharedPreferences("sFile", Context.MODE_PRIVATE);
                HashSet<String> dataForSaveVoca = new HashSet<String>(pref.getStringSet("voca", new HashSet<String>())); // "voca"이라는 키로 저장되어있던 스트링셋 불러오고
                Log.d("VOCA SAVE PROCESS","VOCA Before-Saved Data: " + dataForSaveVoca);*/
                HashSet<String> dataForSaveVoca = SharedPrefControl.loadDataSetByKeyInSharedPref(mContext, FILE_NAME_SHAREDPREF, VOCA);

                String wordSelected = words.get(getAdapterPosition()).wordSaveFormat();

                if(!mIsStarBtnClicked){ // 단어 추가(별) 가 안되어있는 상태면
                    // 단어장 리스트에 추가하고
                    dataForSaveVoca.add(wordSelected); // 단어추가(star) 하면 HashSet에 wordSaveFormat()를 추가하고
                    // 버튼 색깔 바꾸는 코드
                    imageButtonStar.setBackgroundTintList((ColorStateList.valueOf(Color.parseColor("#F57F17")))); // #F57F17, 100(A) 245,127,23
                }else{ // 단어 추가(별) 가 이미 되어있는 상태면
                    // 단어장 리스트에서 삭제하고
                    dataForSaveVoca.remove(wordSelected); // 단어추가(star) 취소하면 wordSaveFormat()를 HashSet에서 빼고
                    // 버튼 색깔 바꾸는 코드
                    imageButtonStar.setBackgroundTintList((ColorStateList.valueOf(Color.parseColor("#FFFFFF")))); //
                }
                mIsStarBtnClicked = !mIsStarBtnClicked;

                // 기기에 다시 저장해주는 과정
                /*SharedPreferences.Editor editor = pref.edit(); // 에디터를 열어서
                editor.putStringSet("voca", (Set<String>) dataForSaveVoca);
                editor.apply(); // 변경 완료. .commit()을 써도 댐
                Log.d("VOCA SAVE PROCESS","VOCA After-Saved Data: " + pref.getStringSet("voca", new HashSet<String>()));*/
                SharedPrefControl.saveDataSetByKeyInSharedPref(mContext, FILE_NAME_SHAREDPREF, VOCA, dataForSaveVoca);

                // 리스너에 신호 줘야지
                mStarListener.onStarClick(v, getAdapterPosition(), wordSelected);
                }
            });
        }
    }

    class VocaTabWordsViewHolder extends RecyclerView.ViewHolder{
        TextView textViewWordJap, textViewWordJapPron, textViewWordKor;
        ImageView imageViewStar;
        private boolean mIsStarBtnClicked = false; // 단어에 추가(별) 버튼이 눌려져있는가 여부. false면 안눌려져있는 상태인거지.
        public VocaTabWordsViewHolder(@NonNull View itemView){
            super(itemView);
            textViewWordJap = itemView.findViewById(R.id.voca_textView_wordJap);
            textViewWordJapPron = itemView.findViewById(R.id.voca_textView_wordJapPron);
            textViewWordKor = itemView.findViewById(R.id.voca_textView_wordKor);
            imageViewStar = itemView.findViewById(R.id.voca_imageView_star);

            Log.d("WORDCARD ADAPTER","Voca-뷰홀더 생성됨.");
            //
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                int position = getAdapterPosition();
                if(position!=RecyclerView.NO_POSITION && listener!=null){
                    listener.onItemClick(v, position, mLayoutType);
                }
                }
            });

            imageViewStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                // 기기에 저장되어있던 데이터 불러와서
                /*SharedPreferences pref = mContext.getSharedPreferences("sFile", Context.MODE_PRIVATE);
                HashSet<String> dataForSaveVoca = new HashSet<String>(pref.getStringSet("voca", new HashSet<String>())); // "voca"이라는 키로 저장되어있던 스트링셋 불러오고
                Log.d("VOCA SAVE PROCESS","VOCA Before-Saved Data: " + dataForSaveVoca);*/
                HashSet<String> dataForSaveVoca = SharedPrefControl.loadDataSetByKeyInSharedPref(mContext, FILE_NAME_SHAREDPREF, VOCA);

                String wordSelected = words.get(getAdapterPosition()).wordSaveFormat();

                // 사실 여기는 지우는 코드만 필요하니까. else만 남길게. 별 색깔 바꾸는 것도 필요 없겠구나
                //if(!mIsStarBtnClicked){ // 단어 추가(별) 가 안되어있는 상태면 => 단어장 리스트에 추가하고
                //    dataForSaveVoca.add(wordSelected); // 단어추가(star) 하면 HashSet에 wordSaveFormat()를 추가하고
                //    imageViewStar.setBackgroundTintList((ColorStateList.valueOf(Color.parseColor("#F57F17")))); // #F57F17, 100(A) 245,127,23  버튼 색깔 바꾸는 코드
                //}else{ // 단어 추가(별) 가 이미 되어있는 상태면 => 단어장 리스트에서 삭제하고
                    dataForSaveVoca.remove(wordSelected); // 단어추가(star) 취소하면 wordSaveFormat()를 HashSet에서 빼고
                //  imageViewStar.setBackgroundTintList((ColorStateList.valueOf(Color.parseColor("#FFFFFF")))); // 버튼 색깔 바꾸는 코드
                //}
                //mIsStarBtnClicked = !mIsStarBtnClicked;

                // 기기에 다시 저장해주는 과정
                /*SharedPreferences.Editor editor = pref.edit(); // 에디터를 열어서
                editor.putStringSet("voca", (Set<String>) dataForSaveVoca);
                editor.apply(); // 변경 완료. .commit()을 써도 댐
                Log.d("VOCA SAVE PROCESS","VOCA After-Saved Data: " + pref.getStringSet("voca", new HashSet<String>()));*/
                SharedPrefControl.saveDataSetByKeyInSharedPref(mContext, FILE_NAME_SHAREDPREF, VOCA, dataForSaveVoca);

                // 리스너에 신호 줘야지
                mStarListener.onStarClick(v, getAdapterPosition(), wordSelected);
                }
            });
        }
    }

    class QuizResultWordsViewHolder extends RecyclerView.ViewHolder{
        TextView textViewWordJap, textViewWordJapPron, textViewWordKor, textViewWordKorPron;
        ImageView imageViewStar;

        private boolean mIsStarBtnClicked = false; // 단어에 추가(별) 버튼이 눌려져있는가 여부. false면 안눌려져있는 상태인거지.
        public QuizResultWordsViewHolder(@NonNull View itemView){
            super(itemView);
            textViewWordJap = itemView.findViewById(R.id.quiz_textView_wordJap);
            textViewWordJapPron = itemView.findViewById(R.id.quiz_textView_wordJapPron);
            textViewWordKor = itemView.findViewById(R.id.quiz_textView_wordKor);
            textViewWordKorPron = itemView.findViewById(R.id.quiz_textView_wordKorPron);
            imageViewStar = itemView.findViewById(R.id.quiz_imageView_star);

            Log.d("WORDCARD ADAPTER","Quiz-뷰홀더 생성됨.");
            //
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION && listener!=null){
                        listener.onItemClick(v, position, mLayoutType);
                    }
                }
            });

            imageViewStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 기기에 저장되어있던 데이터 불러와서
                    /*SharedPreferences pref = mContext.getSharedPreferences("sFile", Context.MODE_PRIVATE);
                    HashSet<String> dataForSaveVoca = new HashSet<String>(pref.getStringSet("voca", new HashSet<String>())); // "voca"이라는 키로 저장되어있던 스트링셋 불러오고
                    Log.d("VOCA SAVE PROCESS","VOCA Before-Saved Data: " + dataForSaveVoca);*/
                    HashSet<String> dataForSaveVoca = SharedPrefControl.loadDataSetByKeyInSharedPref(mContext, FILE_NAME_SHAREDPREF, VOCA);

                    String wordSelected = words.get(getAdapterPosition()).wordSaveFormat();

                    if(!mIsStarBtnClicked){ // 단어 추가(별) 가 안되어있는 상태면
                        // 단어장 리스트에 추가하고
                        dataForSaveVoca.add(wordSelected); // 단어추가(star) 하면 HashSet에 wordSaveFormat()를 추가하고
                        // 버튼 색깔 바꾸는 코드
                        imageViewStar.setBackgroundTintList((ColorStateList.valueOf(Color.parseColor("#F57F17")))); // #F57F17, 100(A) 245,127,23
                    }else{ // 단어 추가(별) 가 이미 되어있는 상태면
                        // 단어장 리스트에서 삭제하고
                        dataForSaveVoca.remove(wordSelected); // 단어추가(star) 취소하면 wordSaveFormat()를 HashSet에서 빼고
                        // 버튼 색깔 바꾸는 코드
                        imageViewStar.setBackgroundTintList((ColorStateList.valueOf(Color.parseColor("#FFFFFF")))); //
                    }
                    mIsStarBtnClicked = !mIsStarBtnClicked;

                    // 기기에 다시 저장해주는 과정
                    /*SharedPreferences.Editor editor = pref.edit(); // 에디터를 열어서
                    editor.putStringSet("voca", (Set<String>) dataForSaveVoca);
                    editor.apply(); // 변경 완료. .commit()을 써도 댐
                    Log.d("VOCA SAVE PROCESS","VOCA After-Saved Data: " + pref.getStringSet("voca", new HashSet<String>()));*/
                    SharedPrefControl.saveDataSetByKeyInSharedPref(mContext, FILE_NAME_SHAREDPREF, VOCA, dataForSaveVoca);

                    // 리스너에 신호 줘야지
                    mStarListener.onStarClick(v, getAdapterPosition(), wordSelected);
                }
            });
        }
    }


    // 아이템 전체에 대한 클릭 이벤트를 처리하는 리스너
    private WordCardAdapter.OnItemClickListener listener;
    public interface OnItemClickListener{
        void onItemClick(View v, int position, int layoutType);
    }
    public void setOnItemClickListener(WordCardAdapter.OnItemClickListener listener){
        this.listener = listener;
    }

    // 아이템 내부 이미지(별)에 대한 이벤트를 처리하는 리스너
    // 여기 Adapter 스크립트에 리스너 만들고 Activity에 실제 호출되는 애에 달아주면 될 듯
    private OnStarClickListener mStarListener = null;
    public interface OnStarClickListener{
        void onStarClick(View v, int pos, String wordSelected);
    }
    public void setOnStarClickListener(WordCardAdapter.OnStarClickListener listener){
        this.mStarListener = listener;
    }





}
