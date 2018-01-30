package com.example.aditya.mysocialapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    TextView firstname, lastname, email, password;

    FirebaseAuth mAuth;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.app_icon_logo);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        mRootRef.child("USERS").child(mAuth.getCurrentUser().getUid()).child("firstName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firstname.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRootRef.child("USERS").child(mAuth.getCurrentUser().getUid()).child("lastName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lastname.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRootRef.child("USERS").child(mAuth.getCurrentUser().getUid()).child("email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                email.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRootRef.child("USERS").child(mAuth.getCurrentUser().getUid()).child("password").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                password.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        firstname.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showPopUPForEdit(1);
                return false;
            }
        });

        lastname.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showPopUPForEdit(2);
                return false;
            }
        });

        password.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showPopUPForEdit(3);
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId()  == R.id.action_logout)
        {
            mAuth.signOut();
            Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    void showPopUPForEdit(final int value){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (value == 1)
            builder.setTitle("First Name");
        else if (value == 2)
            builder.setTitle("Last Name");
        else if (value == 3)
            builder.setTitle("Password");

        final EditText nameInput = new EditText(this);
        nameInput.setInputType(InputType.TYPE_CLASS_TEXT);

        final EditText password = new EditText(this);
        password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setHint("Password");

        final EditText retypePassword = new EditText(this);
        retypePassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        retypePassword.setHint("Retype Password");

        if (value == 1 || value == 2)
            builder.setView(nameInput);
        else if (value == 3){
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);

            layout.addView(password);
            layout.addView(retypePassword);

            builder.setView(layout);
        }

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (value == 1){
                    String nameValue = nameInput.getText().toString().trim();
                    if (nameValue == null || nameValue.length()==0){

                    }else {
                        DatabaseReference fNameChild = mRootRef.child("USERS").child(mAuth.getCurrentUser().getUid()).child("firstName");
                        fNameChild.setValue(nameValue);
                    }
                }else if (value == 2){
                    String nameValue = nameInput.getText().toString().trim();
                    if (nameValue == null || nameValue.length()==0){

                    }else {
                        DatabaseReference lNameChild = mRootRef.child("USERS").child(mAuth.getCurrentUser().getUid()).child("lastName");
                        lNameChild.setValue(nameValue);
                    }
                }else if (value == 3){
                    String passwordValue = password.getText().toString();
                    String retypePasswordValue = retypePassword.getText().toString();
                    if (passwordValue == null || passwordValue.length()==0 || retypePasswordValue == null || retypePasswordValue.length()==0 || !passwordValue.equals(retypePasswordValue)){

                    }else {
                        DatabaseReference passwordChild = mRootRef.child("USERS").child(mAuth.getCurrentUser().getUid()).child("password");
                        passwordChild.setValue(passwordValue);
                        mAuth.getCurrentUser().updatePassword(passwordValue).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Message","Password Changed");
                            }
                        });
                    }
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();

    }
}
