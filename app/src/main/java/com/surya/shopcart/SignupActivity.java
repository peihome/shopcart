package com.surya.shopcart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.surya.shopcart.utils.Utils;

import org.w3c.dom.Text;

import java.util.concurrent.CompletableFuture;

public class SignupActivity extends AppCompatActivity {


    String errMsg;
    boolean result = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        TextView email = findViewById(R.id.email);
        TextView password = findViewById(R.id.password);
        TextView repeatPassword = findViewById(R.id.repeatPassword);


        //Verify OTP
        //Intent otpActivityIntent = new Intent(this, OTPActivity.class);
        Intent loginActivityIntent = new Intent(this, LoginActivity.class);
        Intent productHomePage = new Intent(this, ProductHomePageActivity.class);

        Button registerBtn = findViewById(R.id.register);
        registerBtn.setOnClickListener(view -> {
            validateAndCreateUser(email.getText().toString(), password.getText().toString(), repeatPassword.getText().toString());
            //if(result) {
                //startActivity(loginActivityIntent);
            //}
        });


        //Login Activity
        TextView loginTextView = findViewById(R.id.createAccount);
        loginTextView.setOnClickListener(view -> {
            startActivity(loginActivityIntent);
        });
    }

    public void validateAndCreateUser(String email, String password, String retypedPassword) {

        if (email.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.user_email_empty_error_msg), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Utils.isValidEmail(email)) {
            Toast.makeText(getApplicationContext(), getString(R.string.user_email_invalid_error_msg), Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.user_password_empty_error_msg), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Utils.isValidPassword(password)) {
            Toast.makeText(getApplicationContext(), getString(R.string.user_password_invalid_error_msg), Toast.LENGTH_SHORT).show();
            return;
        }

        if (retypedPassword.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.user_retyped_password_empty_error_msg), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(retypedPassword)) {
            Toast.makeText(getApplicationContext(), getString(R.string.user_password_not_same_msg), Toast.LENGTH_SHORT).show();
            return;
        }

        createUser(email, password);
    }

    public void createUser(String email, String password) {
        final Context context = this;

        try {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, R.string.user_account_creation_success, Toast.LENGTH_SHORT).show();
                                Intent loginPageIntent = new Intent(context, LoginActivity.class);
                                loginPageIntent.putExtra("email", email);
                                context.startActivity(loginPageIntent);
                                ((Activity) context).finish(); // Close the login activity to prevent going back to it with the back button
                            } else {
                                Toast.makeText(context, R.string.user_account_creation_failure, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}