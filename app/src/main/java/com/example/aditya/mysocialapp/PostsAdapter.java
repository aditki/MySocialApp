package com.example.aditya.mysocialapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;


public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>
{
    ArrayList<Post> posts = new ArrayList<>();
    Context mContext;
    updateList mActivity;
    String key;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView userName;
        public TextView postTime;
        public TextView postText;
        public PostsAdapter parent;
        public ImageView deleteButton;
        public ViewHolder(View view, PostsAdapter parent)
        {
            super(view);
            this.parent = parent;
            userName = view.findViewById(R.id.name);
            postTime = view.findViewById(R.id.time);
            postText = view.findViewById(R.id.postMessage);
            deleteButton = view.findViewById(R.id.deleteImage);
        }
    }

    public PostsAdapter(ArrayList<Post> posts, Context mContext, updateList mActivity, String key) {
        this.posts = posts;
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.key = key;
    }

    @Override
    public PostsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rootView = inflater.inflate(R.layout.recycler_post,parent,false);
        ViewHolder viewHolder;
        viewHolder = new ViewHolder(rootView,this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PostsAdapter.ViewHolder holder, final int position)
    {
        final Post post = posts.get(position);
        //Log.d("Adapter", post.getPostedBy());


        if (key.equals("profile") && mAuth.getCurrentUser().getUid().equals(post.getPostedBy())){
            holder.deleteButton.setVisibility(View.VISIBLE);
        }else {
            holder.deleteButton.setVisibility(View.GONE);
        }

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRootRef.child("POSTS").child(post.getPost_id()).removeValue();
            }
        });

        mRootRef.child("USERS").child(post.getPostedBy()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                holder.userName.setText(user.getFirstName()+" "+user.getLastName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        PrettyTime p = new PrettyTime();
        holder.postTime.setText(p.format(post.getTimestamp()));
        holder.postText.setText(post.getContents());

        if (key.equals("home")){
            holder.userName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mActivity.openProfile(post.getPostedBy());
                }
            });
        }

    }

    @Override
    public int getItemCount()
    {
        return posts.size();
    }

    public interface updateList
    {
        public void setAdapter();
        public void openProfile(String uid);
    }
}
