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



public class PendingRequestsAdapter extends RecyclerView.Adapter<PendingRequestsAdapter.ViewHolder>{

    public ArrayList<RequestObj> mData;
    Context mContext;
    PendingRequestsListener mActivity;
    FirebaseAuth mAuth;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    public PendingRequestsAdapter(ArrayList<RequestObj> mData, Context mContext, PendingRequestsListener mActivity) {
        this.mData = mData;
        this.mContext = mContext;
        this.mActivity = mActivity;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rootView = inflater.inflate(R.layout.pendingrequests,parent,false);
        ViewHolder viewHolder = new ViewHolder(rootView,this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final RequestObj requestObj = mData.get(position);

        final Request request = requestObj.getRequest();

        mRootRef.child("USERS").child(request.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                holder.fullname.setText(user.getFirstName()+" "+user.getLastName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (requestObj.getType().equals("RECEIVE")){

            holder.accept.setVisibility(View.VISIBLE);
            holder.decline.setVisibility(View.VISIBLE);
            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Request request1 = new Request(mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getUid());

                    mRootRef.child("FRIENDS").child(mAuth.getCurrentUser().getUid()).child(request.getId()).setValue(request);
                    mRootRef.child("FRIENDS").child(request.getId()).child(request1.getId()).setValue(request1);

                    mRootRef.child("REQUESTS").child(mAuth.getCurrentUser().getUid()).child("RECEIVE").child(request.getId()).removeValue();
                    mRootRef.child("REQUESTS").child(request.getId()).child("SENT").child(mAuth.getCurrentUser().getUid()).removeValue();

                    mActivity.updateList(requestObj);
                }
            });

            holder.decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mRootRef.child("REQUESTS").child(mAuth.getCurrentUser().getUid()).child("RECEIVE").child(request.getId()).removeValue();
                    mRootRef.child("REQUESTS").child(request.getId()).child("SENT").child(mAuth.getCurrentUser().getUid()).removeValue();
                    mActivity.updateList(requestObj);
                }
            });
        }else {
            holder.accept.setVisibility(View.GONE);
            holder.decline.setVisibility(View.VISIBLE);

            holder.decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mRootRef.child("REQUESTS").child(mAuth.getCurrentUser().getUid()).child("SENT").child(request.getId()).removeValue();
                    mRootRef.child("REQUESTS").child(request.getId()).child("RECEIVE").child(mAuth.getCurrentUser().getUid()).removeValue();
                    mActivity.updateList(requestObj);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public PendingRequestsAdapter parent;
        public TextView fullname;
        public ImageView accept, decline;

        public ViewHolder(View itemView, PendingRequestsAdapter parent) {
            super(itemView);
            this.parent = parent;

            this.fullname = (TextView) itemView.findViewById(R.id.requestname);
            this.accept = (ImageView) itemView.findViewById(R.id.requestAcceptButton);
            this.decline = (ImageView) itemView.findViewById(R.id.requestDeclineButton);
        }
    }

    interface PendingRequestsListener{
        public void updateList(RequestObj request);
    }
}
