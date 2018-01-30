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

import java.util.ArrayList;



public class AllFriendsAdapter extends RecyclerView.Adapter<AllFriendsAdapter.ViewHolder>
{
    public ArrayList<Request> mData;
    Context mContext;
    AllFriendsListener mActivity;
    FirebaseAuth mAuth;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    public AllFriendsAdapter(ArrayList<Request> mData, Context mContext, AllFriendsListener mActivity) {
        this.mData = mData;
        this.mContext = mContext;
        this.mActivity = mActivity;

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public AllFriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View rootView = layoutInflater.inflate(R.layout.allfriends,parent,false);
        ViewHolder viewHolder = new ViewHolder(rootView,this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AllFriendsAdapter.ViewHolder holder, int position)
    {
        final Request friend = mData.get(position);

        mRootRef.child("USERS").child(friend.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                holder.fullname.setText(user.getFirstName()+" "+user.getLastName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.removeFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                mRootRef.child("FRIENDS").child(friend.getId()).child(mAuth.getCurrentUser().getUid()).removeValue();
                mRootRef.child("FRIENDS").child(mAuth.getCurrentUser().getUid()).child(friend.getId()).removeValue();

                mActivity.updateList(friend);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public AllFriendsAdapter parent;
        public TextView fullname;
        public ImageView removeFriend;

        public ViewHolder(View itemView, AllFriendsAdapter parent)
        {
            super(itemView);
            this.parent = parent;

            this.fullname = (TextView) itemView.findViewById(R.id.friendName);
            this.removeFriend = (ImageView) itemView.findViewById(R.id.removeFriendButton);
        }
    }

    interface AllFriendsListener
    {
        public void updateList(Request request);
    }
}
