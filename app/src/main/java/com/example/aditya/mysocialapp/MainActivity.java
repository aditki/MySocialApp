package com.example.aditya.mysocialapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
{

    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN=001;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            Intent intent = new Intent(MainActivity.this, HomeScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else
        {
            Button signInButton = (Button) findViewById(R.id.loginBtn);
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final EditText userName = (EditText) findViewById(R.id.username);
                    final EditText password = (EditText) findViewById(R.id.password);
                    String unText = userName.getText().toString();
                    String pwdText = password.getText().toString();

                    if (unText == null || pwdText == null || unText.length() == 0 || pwdText.length() ==0)
                    {
                        Toast.makeText(MainActivity.this, "Enter Valid Details", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        mAuth.signInWithEmailAndPassword(unText,pwdText)
                                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task)
                                    {
                                        if (!task.isSuccessful())
                                        {
                                            Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            userName.setText("");
                                            password.setText("");
                                            Log.d("MySocial", "onComplete: " + "Login Success");
                                            Intent intent = new Intent(MainActivity.this, HomeScreen.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
                                    }
                                });
                    }
                }
            });
            TextView signUp = (TextView) findViewById(R.id.signUp);
            signUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(MainActivity.this, SignUp.class);
                    startActivityForResult(intent, 1001);
                }
            });
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient= new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();

        SignInButton googlesignInButton = (SignInButton) findViewById(R.id.googleSigninButton);

        googlesignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent signInIntent=Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent,RC_SIGN_IN);
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                {

                }
                else
                {

                }
            }
        };

        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        if (user1 != null)
        {
            Intent intent = new Intent(MainActivity.this, HomeScreen.class);
            startActivityForResult(intent, 1001);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN)
        {
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }
    }

    private void handleGoogleSignInResult(GoogleSignInResult result)
    {
        if(result.isSuccess()){
            GoogleSignInAccount acct=result.getSignInAccount();
            signInWithGoogle(acct);
        }
    }

    private void signInWithGoogle(GoogleSignInAccount acct)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"Google Login Failed",Toast.LENGTH_SHORT).show();
                        }else {
                            final FirebaseUser mUser = mAuth.getCurrentUser();
                            mRootRef.child("USERS").child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.hasChild("emailid")) {
                                        DatabaseReference newRef = mRootRef.child("USERS").child(mUser.getUid());
                                        User user = new User(mUser.getUid(),mUser.getDisplayName(),mUser.getEmail(),"","","");
                                        newRef.setValue(user);
                                        Toast.makeText(MainActivity.this, " Successfully Signed In ", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MainActivity.this, HomeScreen.class);
                                        startActivity(intent);
                                    }else {
                                        Intent intent = new Intent(MainActivity.this, HomeScreen.class);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
