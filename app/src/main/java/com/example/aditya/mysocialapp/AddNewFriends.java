package com.example.aditya.mysocialapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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


public class AddNewFriends extends Fragment implements AddFriendAdapter.AddFriendListener, PostsAdapter.updateList {

    private OnFragmentInteractionListener mListener;
    ArrayList<Request> requests = new ArrayList<>();
    ArrayList<Request> recrequests = new ArrayList<>();

    ArrayList<Request> friends = new ArrayList<>();
    ArrayList<User> users = new ArrayList<>();
    ArrayList<User> filteredusers = new ArrayList<>();
    FirebaseAuth mAuth;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    RecyclerView addFriendsView;
    LinearLayoutManager mLayoutManager;
    AddFriendAdapter mAdapter;

    public AddNewFriends() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_new_friends, container, false);
        mAuth = FirebaseAuth.getInstance();

        mRootRef.child("REQUESTS").child(mAuth.getCurrentUser().getUid()).child("SENT").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Request request = dataSnapshot.getValue(Request.class);
                requests.add(request);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Request request = dataSnapshot.getValue(Request.class);
                removeRequests(request);
                filterUsers();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRootRef.child("REQUESTS").child(mAuth.getCurrentUser().getUid()).child("RECEIVE").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Request request = dataSnapshot.getValue(Request.class);
                recrequests.add(request);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Request request = dataSnapshot.getValue(Request.class);
                removeRecRequests(request);
                filterUsers();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mRootRef.child("FRIENDS").child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Request friend = dataSnapshot.getValue(Request.class);
                friends.add(friend);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Request removedFriend = dataSnapshot.getValue(Request.class);
                removeFriends(removedFriend);
                filterUsers();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRootRef.child("USERS").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                users.add(user);
                filterUsers();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                removeUser(user);
                filterUsers();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        addFriendsView = (RecyclerView) rootView.findViewById(R.id.addfriendsrv);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        addFriendsView.setLayoutManager(mLayoutManager);

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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void updateList(String uid) {
        User foundUser = null;
        for (User user: filteredusers){
            if (user.getId().equals(uid)){
                foundUser = user;
                //users.remove(user);
                break;
            }
        }
        if (foundUser!=null)
            filteredusers.remove(foundUser);

        setAdapter();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    boolean checkUser(User user){
        if (user.getId().equals(mAuth.getCurrentUser().getUid()))
            return false;

        for (Request request: requests)
            if (request.getUid().equals(user.getId()))
                return false;

        for (Request request: recrequests)
            if (request.getUid().equals(user.getId()))
                return false;

        for (Request friend: friends)
            if (friend.getUid().equals(user.getId()))
                return false;

        return true;
    }

    public void setAdapter()
    {
        if (filteredusers.size() >= 0)
        {
            mAdapter = new AddFriendAdapter(filteredusers, getContext(), this);
            addFriendsView.setAdapter(mAdapter);
        }
    }

    @Override
    public void openProfile(String uid) {

    }

    void filterUsers(){
        filteredusers = new ArrayList<>();
        for (User user: users){
            if (checkUser(user)){
                filteredusers.add(user);
            }
        }

        setAdapter();
    }

    void removeUser(User user){
        User userToRemove = null;

        for (User user1: users){
            if (user1.getId().equals(user.getId())){
                userToRemove = user1;
                break;
            }
        }

        if (userToRemove != null)
            users.remove(userToRemove);
    }

    void removeRequests(Request request){
        Request requestToDelete = null;

        for (Request request1: requests){
            if (request1.getUid().equals(request.getId())){
                requestToDelete = request1;
                break;
            }
        }

        if (requestToDelete != null)
            requests.remove(requestToDelete);
    }

    void removeRecRequests(Request request){
        Request requestToDelete = null;

        for (Request request1: recrequests){
            if (request1.getUid().equals(request.getId())){
                requestToDelete = request1;
                break;
            }
        }

        if (requestToDelete != null)
            recrequests.remove(requestToDelete);
    }

    void removeFriends(Request friend){
        Request friendToDelete = null;

        for (Request friend1: friends){
            if (friend1.getUid().equals(friend.getId())){
                friendToDelete = friend1;
                break;
            }
        }

        if (friendToDelete != null)
            friends.remove(friendToDelete);
    }


}