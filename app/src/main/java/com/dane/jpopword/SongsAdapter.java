package com.dane.jpopword;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import android.content.Context;
//import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.dane.jpopword.model.SharedPrefControl;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class SongsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Song> songs = null ;
    private Context mContext;

    private static int LAYOUT_SONGS_ACT = 1;
    private static int LAYOUT_FAVORITES_ACT = 2;
    //private static int LAYOUT_TYPE = 0;

    int adapterNum;

    String FILE_NAME_SHAREDPREF = "sFile";
    String FAVORITES = "favorites";

    // 생성자에서 데이터 리스트 객체를 전달받음.
    SongsAdapter(ArrayList<Song> list, Context context, OnHeartClickListener heartClickListener, int layoutType, int adapterNum) {
        this.songs = list ;
        this.mContext = context;
        this.mHeartListener = heartClickListener;
        //this.LAYOUT_TYPE = layoutType;
        this.adapterNum = adapterNum;
    }

    public SongsAdapter(){
        super();
    }

    @Override
    public long getItemId(int position) {
        return songs.get(position).getSongId();
    }

    // ViewHolder가 create 되는 순간에 어떤 탭이냐에 따라 item들의 layout이 다르게 생성된다
    // ㄴ 아 새로운 케이스가 생겼어
    @Override
    public int getItemViewType(int position) {
        //return super.getItemViewType(position);
        if(SongsActivity.CURRENT_TAB_NUM==2){ // favortes가 3번째 탭이니까
            return 2;
        }else{
            return 1;
        }
    }

    public void removeItem(int position) {
        songs.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        if(songs==null){
            return 0;
        }else{
            return songs.size();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        if((getItemViewType(1)==LAYOUT_SONGS_ACT)||(context.getClass().equals(YoutubeActivity.class))){
            View view = inflater.inflate(R.layout.songs_each_song, parent, false) ;
            RecyclerView.ViewHolder vh = new SongsAdapter.SongsViewHolder(view) ;
            return vh ;
        }else{ // getItemViewType(1) == LAYOUT_FAVORITES_ACT
            View view = inflater.inflate(R.layout.favorites_each_song, parent, false) ;
            RecyclerView.ViewHolder vh = new SongsAdapter.FavoritesViewHolder(view) ;
            return vh ;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if((getItemViewType(position)==LAYOUT_SONGS_ACT)||mContext.getClass().equals(YoutubeActivity.class)){
            ((SongsViewHolder)holder).textViewTitleKor.setText(songs.get(position).getTitleKorAndJap());
            //((SongsViewHolder)holder).textViewTitleJap.setText("("+songs.get(position).titleJap+")");
            ((SongsViewHolder)holder).textViewSingerKor.setText(songs.get(position).getSingerKorAndJap());
            //((SongsViewHolder)holder).textViewSingerJap.setText("("+songs.get(position).singerJap+")");

            Glide.with(holder.itemView.getContext())
                    .load("https://i.ytimg.com/vi/"+songs.get(position).getYoutubeId()+"/mqdefault.jpg")
                    .error(
                            Glide.with(holder.itemView.getContext())
                                    //.load(R.drawable.mqdefault)
                                    .load("https://i.ytimg.com/vi/"+songs.get(position).getSubYoutubeId()+"/mqdefault.jpg")
                                    .apply(new RequestOptions().transform(new RoundedCorners(50))))
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
                    .apply(new RequestOptions().transform(new RoundedCorners(50)))
                    .into(((SongsViewHolder)holder).imageView);

            // 뷰 만들 때 좋아요 눌린 곡이라면 좋아요 눌린 이미지로 시작하게 만들어주는 부분
            /*SharedPreferences pref = mContext.getSharedPreferences("sFile", Context.MODE_PRIVATE);
            HashSet<String> dataForHeartCheck = new HashSet<String>(pref.getStringSet("favorites", new HashSet<String>())); // "favorite"이라는 키로 저장되어있던 데이터*/
            HashSet<String> dataForHeartCheck = SharedPrefControl.loadDataSetByKeyInSharedPref(mContext, FILE_NAME_SHAREDPREF, FAVORITES);
            if(dataForHeartCheck.contains(songs.get(position).songSaveFormat())){
                ((SongsViewHolder)holder).imageViewHeart.setImageResource(R.drawable.heart_red);
                ((SongsViewHolder)holder).mIsHeartBtnClicked = true;
            }else{
                ((SongsViewHolder)holder).imageViewHeart.setImageResource(R.drawable.heart_white);
                ((SongsViewHolder)holder).mIsHeartBtnClicked = false;
            }
        }
        else{ // LAYOUT_FAVORITES_ACT
            ((FavoritesViewHolder)holder).textViewTitleKor.setText(songs.get(position).getTitleKorAndJap());
            //((FavoritesViewHolder)holder).textViewTitleJap.setText("("+songs.get(position).getTitleJap()+")");
            ((FavoritesViewHolder)holder).textViewSingerKor.setText(songs.get(position).getSingerKorAndJap());
            //((FavoritesViewHolder)holder).textViewSingerJap.setText("("+songs.get(position).getSingerJap()+")");

            //Glide.with(holder.itemView.getContext()).load("https://i.ytimg.com/vi/"+songs.get(position).youtubeId+"/mqdefault.jpg").into(holder.imageView);
            Glide.with(holder.itemView.getContext())
                    .load("https://i.ytimg.com/vi/"+songs.get(position).getYoutubeId()+"/mqdefault.jpg")
                    .error(
                            Glide.with(holder.itemView.getContext())
                                    //.load(R.drawable.mqdefault)
                                    .load("https://i.ytimg.com/vi/"+songs.get(position).getSubYoutubeId()+"/mqdefault.jpg")
                                    .apply(new RequestOptions().transform(new RoundedCorners(10))))
                    .override(160,90)
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
                    .apply(new RequestOptions().transform(new RoundedCorners(10)))
                    .into(((FavoritesViewHolder)holder).imageView);

            // 뷰 만들 때 좋아요 눌린 곡이라면 좋아요 눌린 이미지로 시작하게 만들어주는 부분
            /*SharedPreferences pref = mContext.getSharedPreferences("sFile", Context.MODE_PRIVATE);
            HashSet<String> dataForHeartCheck = new HashSet<String>(pref.getStringSet("favorites", new HashSet<String>())); // "favorite"이라는 키로 저장되어있던 데이터*/
            HashSet<String> dataForHeartCheck = SharedPrefControl.loadDataSetByKeyInSharedPref(mContext, FILE_NAME_SHAREDPREF, FAVORITES);
            if(dataForHeartCheck.contains(songs.get(position).songSaveFormat())){
                ((FavoritesViewHolder)holder).imageViewHeart.setImageResource(R.drawable.heart_red);
                ((FavoritesViewHolder)holder).mIsHeartBtnClicked = true;
            }else{
                ((FavoritesViewHolder)holder).imageViewHeart.setImageResource(R.drawable.heart_white);
                ((FavoritesViewHolder)holder).mIsHeartBtnClicked = false;
            }
        }
    }

    // payload를 통해 recyclerView의 Item을 통째로 바꾸는게 아니라 Item 내부의 특정 요소만 바꿈. (전체가 깜빡이는 문제 해결)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        }else {
            for (Object payload : payloads) {
                if (payload instanceof String) {
                    String type = (String) payload;
                    if (TextUtils.equals(type, "favoriteIcon") && (holder instanceof SongsViewHolder || holder instanceof FavoritesViewHolder)) {

                        // 뷰 만들 때 좋아요 눌린 곡이라면 좋아요 눌린 이미지로 시작하게 만들어주는 부분
                        /*SharedPreferences pref = mContext.getSharedPreferences("sFile", Context.MODE_PRIVATE);
                        HashSet<String> dataForHeartCheck = new HashSet<String>(pref.getStringSet("favorites", new HashSet<String>())); // "favorite"이라는 키로 저장되어있던 데이터*/
                        HashSet<String> dataForHeartCheck = SharedPrefControl.loadDataSetByKeyInSharedPref(mContext, FILE_NAME_SHAREDPREF, FAVORITES);

                        if(dataForHeartCheck.contains(songs.get(position).songSaveFormat())){
                            if(holder instanceof SongsViewHolder){
                                ((SongsViewHolder)holder).imageViewHeart.setImageResource(R.drawable.heart_red);
                                ((SongsViewHolder)holder).mIsHeartBtnClicked = true;
                            }else{ // holder instanceof FavoritesViewHolder
                                ((FavoritesViewHolder)holder).imageViewHeart.setImageResource(R.drawable.heart_red);
                                ((FavoritesViewHolder)holder).mIsHeartBtnClicked = true;
                            }
                        }else{
                            if(holder instanceof SongsViewHolder){
                                ((SongsViewHolder)holder).imageViewHeart.setImageResource(R.drawable.heart_white);
                                ((SongsViewHolder)holder).mIsHeartBtnClicked = false;
                            }else{ // holder instanceof FavoritesViewHolder
                                ((FavoritesViewHolder)holder).imageViewHeart.setImageResource(R.drawable.heart_white);
                                ((FavoritesViewHolder)holder).mIsHeartBtnClicked = false;
                            }
                        }
                    }
                }
            }
        }
    }


    class SongsViewHolder extends RecyclerView.ViewHolder{
        TextView textViewTitleKor, textViewTitleJap, textViewSingerKor, textViewSingerJap;
        ImageView imageView, imageViewHeart;
        private boolean mIsHeartBtnClicked = false;

        public SongsViewHolder(@NonNull final View itemView) {
            super(itemView);
            textViewTitleKor = itemView.findViewById(R.id.textViewTitleKor);
            textViewSingerKor = itemView.findViewById(R.id.textViewSingerKor);
            imageView = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    //if(position!=RecyclerView.NO_POSITION && listener!=null){ // 이건 왜 주석 처리해놨었지?
                        //Log.d("여기는 도나?", "여기 뭐라고 나옴? position: "+position + "/ adapternum : "+adapterNum);
                        listener.onItemClick(v, position, adapterNum) ;
                    //}
                }
            });

            imageViewHeart = itemView.findViewById(R.id.imageViewStar);
            imageViewHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 기기에 저장되어있던 데이터 불러와서
                    /*SharedPreferences pref = mContext.getSharedPreferences("sFile", Context.MODE_PRIVATE);
                    HashSet<String> dataForSaveHeart = new HashSet<String>(pref.getStringSet("favorites", new HashSet<String>())); // "favorite"이라는 키로 저장되어있던 스트링셋 불러오고
                    Log.d("Heart SAVE PROCESS","Heart Before-Saved Data: " + dataForSaveHeart);*/
                    HashSet<String> dataForSaveHeart = SharedPrefControl.loadDataSetByKeyInSharedPref(mContext,FILE_NAME_SHAREDPREF,FAVORITES);

                    String songToSaveFormat = songs.get(getAdapterPosition()).songSaveFormat();

                    if(mIsHeartBtnClicked){
                        dataForSaveHeart.remove(songToSaveFormat); // 좋아요 취소하면 SaveFormat을 HashSet에서 빼고
                        imageViewHeart.setImageResource(R.drawable.heart_white);
                    }else{
                        dataForSaveHeart.add(songToSaveFormat); // 좋아요 하면 HashSet에 SaveFormat을 추가하고
                        imageViewHeart.setImageResource(R.drawable.heart_red);
                    }
                    mIsHeartBtnClicked = !mIsHeartBtnClicked;

                    // 기기에 다시 저장해주는 과정
                    /*SharedPreferences.Editor editor = pref.edit(); // 에디터를 열어서
                    editor.putStringSet("favorites", (Set<String>) dataForSaveHeart);
                    editor.apply(); // 변경 완료. .commit()을 써도 댐
                    Log.d("Heart SAVE PROCESS","Heart After-Saved Data: " + pref.getStringSet("favorites", new HashSet<String>()));*/
                    SharedPrefControl.saveDataSetByKeyInSharedPref(mContext,FILE_NAME_SHAREDPREF, FAVORITES, dataForSaveHeart);

                    // 리스너에 신호 줘야지
                    mHeartListener.onHeartClick(v, getAdapterPosition(), songToSaveFormat);
                }
            });
        }
    }

    // Favorites 탭에 들어갈 형태. 위에 있는 SongsViewHolder와 클래스 명만 다르고 모두 똑같음,...? 그럼 이렇게 할 필요가 없는거 아닌가? 레이아웃만 만들면 되잖아
    class FavoritesViewHolder extends RecyclerView.ViewHolder{
        TextView textViewTitleKor, textViewTitleJap, textViewSingerKor, textViewSingerJap;
        ImageView imageView, imageViewHeart;
        private boolean mIsHeartBtnClicked = false; // 좋아요(하트) 버튼이 눌려져있는가 여부. false면 안눌려져있는 상태인거지.

        public FavoritesViewHolder(@NonNull final View itemView) {
            super(itemView);
            textViewTitleKor = itemView.findViewById(R.id.favorites_textView_titleKor);
            //textViewTitleJap = itemView.findViewById(R.id.favorites_textView_titleJap);
            textViewSingerKor = itemView.findViewById(R.id.favorites_textView_singerKor);
            //textViewSingerJap = itemView.findViewById(R.id.favorites_textView_singerJap);
            imageView = itemView.findViewById(R.id.favorites_imageView_thumb);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION && listener!=null){
                        listener.onItemClick(v, position, adapterNum) ;
                    }
                }
            });

            imageViewHeart = itemView.findViewById(R.id.favorites_imageBtn_heart);
            imageViewHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 기기에 저장되어있던 데이터 불러와서
                    /*SharedPreferences pref = mContext.getSharedPreferences("sFile", Context.MODE_PRIVATE);
                    HashSet<String> dataForSaveHeart = new HashSet<String>(pref.getStringSet("favorites", new HashSet<String>())); // "favorite"이라는 키로 저장되어있던 스트링셋 불러오고
                    Log.d("SAVE PROCESS","Before-Saved Data: " + dataForSaveHeart);*/
                    HashSet<String> dataForSaveHeart = SharedPrefControl.loadDataSetByKeyInSharedPref(mContext,FILE_NAME_SHAREDPREF, FAVORITES);

                    String songToSaveFormat = songs.get(getAdapterPosition()).songSaveFormat();

                    if(mIsHeartBtnClicked){
                        dataForSaveHeart.remove(songToSaveFormat); // 좋아요 취소하면 타이틀을 HashSet에서 빼고
                        imageViewHeart.setImageResource(R.drawable.heart_white);
                    }else{
                        dataForSaveHeart.add(songToSaveFormat); // 좋아요 하면 HashSet에 타이틀을 추가하고
                        imageViewHeart.setImageResource(R.drawable.heart_red);
                    }
                    mIsHeartBtnClicked = !mIsHeartBtnClicked;

                    // 기기에 다시 저장해주는 과정
                    /*SharedPreferences.Editor editor = pref.edit(); // 에디터를 열어서
                    editor.putStringSet("favorites", (Set<String>) dataForSaveHeart);
                    editor.apply(); // 변경 완료. .commit()을 써도 댐
                    Log.d("SAVE PROCESS","After-Saved Data: " + pref.getStringSet("favorites", new HashSet<String>()));*/
                    SharedPrefControl.saveDataSetByKeyInSharedPref(mContext,FILE_NAME_SHAREDPREF,FAVORITES,dataForSaveHeart);

                    // 리스너에 신호 줘야지
                    mHeartListener.onHeartClick(v, getAdapterPosition(), songToSaveFormat);
                }
            });
        }
    }

    // [클릭 이벤트 관련]

    // 아이템 자체에 대한 클릭을 처리하는 애
    private OnItemClickListener listener;
    public interface OnItemClickListener{
        void onItemClick(View v, int position, int adapterNum);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }


    // 아이템 내부에 있는 하트 이미지에 대한 클릭을 처리하는 애
    private OnHeartClickListener mHeartListener = null;
    public interface OnHeartClickListener{
        void onHeartClick(View v, int pos, String titleKorSelected);
    }
    public void setOnHeartClickListener(OnHeartClickListener listener){
        this.mHeartListener = listener;
    }




} // end of script
