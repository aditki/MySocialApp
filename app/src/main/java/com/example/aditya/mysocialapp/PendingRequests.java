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

public class PendingRequests extends Fragment implements PendingRequestsAdapter.PendingRequestsListener{

    private OnFragmentInteractionListener mListener;

    FirebaseAuth mAuth;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    ArrayList<RequestObj> requests = new ArrayList<>();

    RecyclerView pendingRequestView;
    LinearLayoutManager mLayoutManager;
    PendingRequestsAdapter mAdapter;

    public PendingRequests() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_requests, container, false);
        mAuth = FirebaseAuth.getInstance();

        mRootRef.child("REQUESTS").child(mAuth.getCurrentUser().getUid()).child("RECEIVE").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Request request = dataSnapshot.getValue(Request.class);
                RequestObj requestObj = new RequestObj("RECEIVE", request);
                requests.add(requestObj);
                setAdapter();
                Log.d("PENDINGREQ", "");
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

        mRootRef.child("REQUESTS").child(mAuth.getCurrentUser().getUid()).child("SENT").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Request request = dataSnapshot.getValue(Request.class);
                RequestObj requestObj = new RequestObj("SENT", request);
                requests.add(requestObj);
                setAdapter();
                Log.d("PENDINGREQ", "");
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

        pendingRequestView = (RecyclerView) view.findViewById(R.id.pendingrequestsrv);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        pendingRequestView.setLayoutManager(mLayoutManager);

        return view;
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void updateList(RequestObj request) {

//        Request request = null;
//        for (User user: users){
//            if (user.getId().equals(uid)){
//                fou ndUser = user;
//                //users.remove(user);
//                break;
//            }
//        }
//        if (foundUser!=null)
//            users.remove(foundUser);

//        for (Request request1: requests){
//            if (request1.getId().equals(request.getId())){
//                requests.remove(request1);
//                setAdapter();
//            }
//        }

        requests.remove(request);
        setAdapter();
    }

    void setAdapter(){
        if (requests.size() >= 0)
        {
            mAdapter = new PendingRequestsAdapter(requests, getContext(), this);
            pendingRequestView.setAdapter(mAdapter);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
