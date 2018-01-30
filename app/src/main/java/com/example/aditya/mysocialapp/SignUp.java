package com.example.aditya.mysocialapp;

import android.app.DatePickerDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SignUp extends AppCompatActivity
{
    FirebaseAuth mAuth;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUSERID;

    DatePickerDialog.OnDateSetListener date;
    Calendar myCalendar = Calendar.getInstance();

    EditText dob;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mUSERID = mRootRef.child("USERS");

        final EditText firstName = (EditText) findViewById(R.id.firstName);
        final EditText lastName = (EditText) findViewById(R.id.lastName);
        final EditText email = (EditText) findViewById(R.id.email);
        dob = (EditText) findViewById(R.id.dateOfBirth);
        final EditText password = (EditText) findViewById(R.id.password);
        final EditText confirmPassword = (EditText) findViewById(R.id.confirmPassword);

        Button signup = (Button) findViewById(R.id.signupBtn);
        Button cancel = (Button) findViewById(R.id.cancelBtn);

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)

            {

                DatePickerDialog dialog = new DatePickerDialog(SignUp.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis()- 410240038000L);
                dialog.show();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                final String firstNameText = firstName.getText().toString();
                final String lastNameText = lastName.getText().toString();
                final String emailText = email.getText().toString();
                final String dobText = dob.getText().toString();
                final String passwordText = password.getText().toString();
                final String confirmPasswordText = confirmPassword.getText().toString();

                if (firstNameText == null || lastNameText == null || emailText == null || dobText == null || passwordText == null
                        || confirmPasswordText == null || firstNameText.length() == 0 || lastNameText.length() == 0 || emailText.length() == 0
                        || dobText.length() == 0 || passwordText.length() == 0 || confirmPasswordText.length() == 0)
                {
                    Toast.makeText(SignUp.this,"Enter Valid Details",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mAuth.createUserWithEmailAndPassword(emailText,passwordText)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    if (!task.isSuccessful())
                                    {
                                        Toast.makeText(SignUp.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        DatabaseReference newRef = mUSERID.child(mAuth.getCurrentUser().getUid());
                                        User user = new User(mAuth.getCurrentUser().getUid(), firstNameText, lastNameText, emailText, dobText, passwordText);
                                        newRef.setValue(user);
                                        Toast.makeText(SignUp.this,"USer has been created", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            });
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
    }

    private void updateLabel()
    {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);



        dob.setText(sdf.format(myCalendar.getTime()));
    }
}
