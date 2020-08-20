package com.dane.jpopword;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class SongsInCategoryAdapter extends RecyclerView.Adapter<SongsInCategoryAdapter.ViewHolder>{
    ArrayList<Song> list = null;
    Context mContext = null;

    public SongsInCategoryAdapter(ArrayList<Song> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewTitle;
        TextView textViewDescription;
        ImageView imageViewThumbnail;

        ViewHolder(View itemView){
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.category_each_song_title);
            textViewDescription = itemView.findViewById(R.id.category_each_song_description);
            imageViewThumbnail = itemView.findViewById(R.id.category_each_song_thumbnail);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION && listener!=null){
                        listener.onItemClick(v, position) ;
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public SongsInCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.category_each_song, parent, false);
        SongsInCategoryAdapter.ViewHolder vh = new SongsInCategoryAdapter.ViewHolder(view);
        return vh;
    }

    public void onBindViewHolder(@NonNull SongsInCategoryAdapter.ViewHolder holder, int position) {
        holder.textViewTitle.setText(list.get(position).getTitleKorAndJap());
        holder.textViewDescription.setText(list.get(position).getSingerKorAndJap());
        Glide.with(mContext)
                .load("https://i.ytimg.com/vi/"+list.get(position).getYoutubeId()+"/mqdefault.jpg")
                .apply(new RequestOptions().transform(new RoundedCorners(25)))
                .into(holder.imageViewThumbnail);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // 아이템 자체에 대한 클릭을 처리하는 애
    private OnItemClickListener listener;
    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

}