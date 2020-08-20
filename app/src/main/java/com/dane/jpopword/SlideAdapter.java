package com.dane.jpopword;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class SlideAdapter extends PagerAdapter {
  private Context mContext;
  ArrayList<Integer> slides;

  public SlideAdapter(Context mContext, ArrayList<Integer> slides) {
    this.mContext = mContext;
    this.slides = slides;
  }

  @NonNull
  @Override
  public Object instantiateItem(@NonNull ViewGroup container, int position) {
    View view = null;
    if(mContext!=null){
      LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = inflater.inflate(slides.get(position), container, false);

      //ImageView imageViewMain = (ImageView)view.findViewById(R.id.slide_imageView_main);
      //imageViewMain.setImageResource(slideImages.get(position));
    }

    container.addView(view);
    return view;
    //return super.instantiateItem(container, position);

  }

  @Override
  public int getCount() {
    return slides.size();
  }

  @Override
  public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
    return (view == (View)object);
  }

  @Override
  public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    container.removeView((View) object);
  }
}
