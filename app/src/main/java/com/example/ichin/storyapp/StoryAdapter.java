package com.example.ichin.storyapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ichin.storyapp.listeners.OnStoryCoverListener;
import com.example.ichin.storyapp.model.StoryModel;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    private Context mContext;
    private List<StoryModel> stories;
    private OnStoryCoverListener mListener;
    public StoryAdapter(Context mContext, OnStoryCoverListener listener) {
        this.mContext = mContext;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View storyView = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_story_items,parent,false);
        return new StoryViewHolder(storyView);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        holder.bind(stories.get(position),mListener);
    }

    @Override
    public int getItemCount() {
        if(stories == null){
            return 0;
        }
        return stories.size();
    }

    public List<StoryModel> getStories() {
        return stories;
    }

    public void setStories(List<StoryModel> stories) {
        this.stories = stories;
        notifyDataSetChanged();
    }

    class StoryViewHolder extends RecyclerView.ViewHolder{

        ImageView posterImage;
        TextView storyTitle;
        TextView storyWords;

        public StoryViewHolder(View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.iv_story_poster);
            storyTitle = itemView.findViewById(R.id.tv_story_title);
            storyWords = itemView.findViewById(R.id.tv_words);
        }

        void bind(final StoryModel story, final OnStoryCoverListener mListener){

            storyTitle.setText(story.getTitle());
            storyWords.setText(""+story.getSize());

            posterImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(story.getId());
                }
            });


        }
    }
}
