package com.surya.shopcart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.surya.shopcart.utils.Utils;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialise FireBase
        FirebaseApp.initializeApp(this);

        EditText email = findViewById(R.id.email);

        //Setting email from signup page
        EditText password = findViewById(R.id.password);

        //Login Button Activity
        Button loginBtn = findViewById(R.id.register);
        Intent productPageIntent = new Intent(this, ProductHomePageActivity.class);
        loginBtn.setOnClickListener(view -> {
            isValidEmailAndPassword(email.getText().toString(), password.getText().toString());
        });


        //Intent productPage = findViewById(R.id.)

        //Signup Page Activity
        TextView createAccount = findViewById(R.id.createAccount);
        Intent signupPage = new Intent(this, SignupActivity.class);
        createAccount.setOnClickListener(view -> {
            startActivity(signupPage);
        });
    }

    public void isValidEmailAndPassword(String email, String password){

        if(email.isEmpty()){
            Toast.makeText(getApplicationContext(), getString(R.string.user_email_empty_error_msg), Toast.LENGTH_SHORT).show();
            return;
        }

        /*else if(!Utils.isValidEmailAddress(email)){
            Toast.makeText(getApplicationContext(), getString(R.string.user_email_invalid), Toast.LENGTH_SHORT).show();
            return;
        }*/

        if(password.isEmpty()){
            Toast.makeText(getApplicationContext(), getString(R.string.user_password_empty_error_msg), Toast.LENGTH_SHORT).show();
            return;
        }

        isValidUser(email, password);
    }


    public void isValidUser(String email, String password) {
        final Context context = this; // Capture the activity context

        Toast.makeText(context, getString(R.string.user_signin_process), Toast.LENGTH_SHORT).show();
        try {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, start the new activity
                                Toast.makeText(context, getString(R.string.user_signin_success), Toast.LENGTH_SHORT).show();
                                Intent productPageIntent = new Intent(context, ProductHomePageActivity.class);
                                context.startActivity(productPageIntent);
                                ((Activity) context).finish(); // Close the login activity to prevent going back to it with the back button
                            } else {
                                // Sign in failed, display a message to the user
                                Toast.makeText(context, getString(R.string.user_signin_failure), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}