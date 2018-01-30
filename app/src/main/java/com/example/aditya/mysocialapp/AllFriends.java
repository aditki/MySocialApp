package com.example.aditya.mysocialapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class AllFriends extends Fragment implements AllFriendsAdapter.AllFriendsListener{

    ArrayList<Request> friends = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    FirebaseAuth mAuth;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    RecyclerView allFriendsView;
    LinearLayoutManager mLayoutManager;
    AllFriendsAdapter mAdapter;

    public AllFriends() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_all_friends, container, false);
        mAuth = FirebaseAuth.getInstance();

        mRootRef.child("FRIENDS").child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Request request = dataSnapshot.getValue(Request.class);
                friends.add(request);
                setAdapter();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        allFriendsView = (RecyclerView) rootView.findViewById(R.id.allfriendsrv);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        allFriendsView.setLayoutManager(mLayoutManager);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void updateList(Request request) {

        friends.remove(request);
        setAdapter();

    }

    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(Uri uri);
    }

    void setAdapter(){
        if (friends.size() >= 0)
        {
            mAdapter = new AllFriendsAdapter(friends,getContext(), this);
            allFriendsView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("DemoRESUME", "RESUMEMEMEM");
    }
}
