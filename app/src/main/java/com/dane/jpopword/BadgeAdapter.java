package com.dane.jpopword;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class BadgeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  ArrayList<Song> badges;

  Context mContext;

  public BadgeAdapter(ArrayList<Song> badges, Context context) {
    this.badges = badges;
    this.mContext = context;
  }

  @Override
  public int getItemCount() {
    return badges.size();
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.badge, parent,false);
    RecyclerView.ViewHolder vh = new BadgeAdapter.BadgeViewHolder(view);
    return vh;
  }

  public static int getPixelByDp(float dp){
    return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
  }
  public static int getDpByPixel(int px){
    return (int) (px / Resources.getSystem().getDisplayMetrics().density);
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    //((BadgeViewHolder)holder).imageViewBadge =
    ((BadgeViewHolder)holder).textViewTitle.setText(badges.get(position).getTitleKor());
    Glide.with(holder.itemView.getContext())
            .load("https://i.ytimg.com/vi/"+badges.get(position).getYoutubeId()+"/mqdefault.jpg")
            .error(
                    Glide.with(holder.itemView.getContext())
                            .load(R.drawable.mqdefault)
                            .apply(new RequestOptions().transform(new CircleCrop())))
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
            .apply(new RequestOptions().transform(new CircleCrop()))
            .into(((BadgeViewHolder)holder).imageViewBadge);
  }

  class BadgeViewHolder extends RecyclerView.ViewHolder{
    ImageView imageViewBadge;
    TextView textViewTitle;

    public BadgeViewHolder(@NonNull final View itemView){
      super(itemView);
        imageViewBadge = (ImageView)itemView.findViewById(R.id.badge_imageView);
        textViewTitle = (TextView)itemView.findViewById(R.id.badge_textView_title);
    }

  }

}
