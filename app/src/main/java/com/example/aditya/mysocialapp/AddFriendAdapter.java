package com.example.aditya.mysocialapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class AddFriendAdapter extends RecyclerView.Adapter<AddFriendAdapter.ViewHolder> {

    public ArrayList<User> mData;
    Context mContext;
    AddFriendListener mActivity;
    FirebaseAuth mAuth;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    public AddFriendAdapter(ArrayList<User> mData, Context mContext, AddFriendListener mActivity) {
        this.mData = mData;
        this.mContext = mContext;
        this.mActivity = mActivity;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rootView = inflater.inflate(R.layout.addfriend,parent,false);
        ViewHolder viewHolder;
        viewHolder = new ViewHolder(rootView,this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final User user = mData.get(position);
        holder.fullname.setText(user.getFirstName()+" "+user.getLastName());

        holder.addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Request request = new Request(mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getUid());
                mRootRef.child("REQUESTS").child(user.getId()).child("RECEIVE").child(mAuth.getCurrentUser().getUid()).setValue(request);

                Request request1 = new Request(user.getId(), user.getId());
                mRootRef.child("REQUESTS").child(mAuth.getCurrentUser().getUid()).child("SENT").child(user.getId()).setValue(request1);

                mActivity.updateList(user.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public AddFriendAdapter parent;
        public TextView fullname;
        public ImageView addFriend;

        public ViewHolder(View itemView, AddFriendAdapter parent) {
            super(itemView);
            this.parent = parent;

            this.fullname = (TextView) itemView.findViewById(R.id.addFriendName);
            this.addFriend = (ImageView) itemView.findViewById(R.id.addFriendButton);
        }
    }

    interface AddFriendListener{
        public void updateList(String uid);
    }
}
