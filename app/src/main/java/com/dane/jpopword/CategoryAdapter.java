package com.dane.jpopword;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private ArrayList<Category> itemList = null;
    Context mContext = null;

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textViewName;
        TextView textViewPercent;
        ProgressBar progressBar;
        ViewHolder(View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewName = itemView.findViewById(R.id.textView_name);
            textViewPercent = itemView.findViewById(R.id.textView_percent);
            progressBar = itemView.findViewById(R.id.progressBar);

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

    public CategoryAdapter(Context context, ArrayList<Category> itemList) {
        this.mContext = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.category_in_about, parent,false);
        CategoryAdapter.ViewHolder vh = new CategoryAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        holder.textViewName.setText(mContext.getResources().getString(R.string.category_adapter_name, itemList.get(position).getName(), String.valueOf(itemList.get(position).getClearedBadgeCount()), String.valueOf(itemList.get(position).getSongs().size())));
        //holder.textViewName.setText(itemList.get(position).getName()+" ("+itemList.get(position).getClearedBadgeCount()+"/"+itemList.get(position).getSongs().size()+")");
        holder.textViewPercent.setText(mContext.getResources().getString(R.string.category_adapter_percent, String.valueOf((int)(itemList.get(position).getPercent()*100))));
        //holder.textViewPercent.setText((int)(itemList.get(position).getPercent()*100)+"%");
        holder.progressBar.setProgress((int)(itemList.get(position).getPercent()*100), false);
        Glide.with(mContext)
                //.load("https://search.pstatic.net/common?type=a&size=120x150&quality=95&direct=true&src=http%3A%2F%2Fsstatic.naver.net%2Fpeople%2Fportrait%2F201912%2F2019122317494290-2068798.jpg")
                .load("https://i.ytimg.com/vi/"+itemList.get(position).getSongs().get(0).getYoutubeId()+"/mqdefault.jpg")
                .error(
                        Glide.with(holder.itemView.getContext())
                                .load(R.drawable.mqdefault)
                                .apply(new RequestOptions().transform(new CircleCrop())))
                .circleCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // 아이템 자체에 대한 클릭을 처리하는 애
    private OnItemClickListener listener;
    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }


} // end of script

