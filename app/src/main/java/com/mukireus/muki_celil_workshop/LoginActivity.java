package com.mukireus.muki_celil_workshop;

import android.content.Intent;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

  private static final String TAG = "EmailPassword";

  private EditText txtEmail, txtPassword;

  private FirebaseAuth mAuth;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    mAuth = FirebaseAuth.getInstance();

    if(mAuth.getCurrentUser() != null) {
      finish();
      startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }

    // Views
    final Button btnLogin = findViewById(R.id.btnLogin);
    final Button btnSignUp = findViewById(R.id.btnSignUp);
    txtEmail = findViewById(R.id.txtEmail);
    txtPassword = findViewById(R.id.txtPassword);
    final CheckBox checkShowHidden = findViewById(R.id.checkShowHidden);

    btnLogin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        userLogin(txtEmail.getText().toString(),txtPassword.getText().toString());
      }
    });
    btnSignUp.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        openSignUpActivity();
      }
    });
    checkShowHidden.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (!isChecked) {
          // show password
          txtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
          // hide password
          txtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
      }
    });
  }

  // [START on_start_check_user]
  @Override
  public void onStart() {
    super.onStart();
    // Check if user is signed in (non-null) and update UI accordingly.
    FirebaseUser currentUser = mAuth.getCurrentUser();
    updateUI(currentUser);
  }
  // [END on_start_check_user]


  private void userLogin(String email, String password){

    Log.d(TAG, "signIn:" + email);

    if (!validateForm()) {
      return;
    }

    // [START sign_in_with_email]
    mAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
              // Sign in success, update UI with the signed-in user's information
              Log.d(TAG, "signInWithEmail:success");
              Toast.makeText(LoginActivity.this, "Authentication success.",
                  Toast.LENGTH_SHORT).show();
              FirebaseUser user = mAuth.getCurrentUser();
              updateUI(user);
            } else {
              // If sign in fails, display a message to the user.
              Log.w(TAG, "signInWithEmail:failure", task.getException());
              Toast.makeText(LoginActivity.this, "Authentication failed.",
                  Toast.LENGTH_SHORT).show();
              updateUI(null);
            }

            // [START_EXCLUDE]
            if (!task.isSuccessful()) {
              Toast.makeText(LoginActivity.this, "Authentication failed.",
                  Toast.LENGTH_SHORT).show();
            }
            // [END_EXCLUDE]
          }
        });
    // [END sign_in_with_email]
  }

  private boolean validateForm() {
    boolean valid = true;

    String email = txtEmail.getText().toString();
    if (TextUtils.isEmpty(email)) {
      txtEmail.setError("Lütfen email adresi giriniz.");
      valid = false;
    } else {
      txtEmail.setError(null);
    }

    String password = txtPassword.getText().toString();
    if (TextUtils.isEmpty(password)) {
      txtPassword.setError("Lütfen parola giriniz.");
      valid = false;
    } else {
      txtPassword.setError(null);
    }

    return valid;
  }

  private void updateUI(FirebaseUser user) {
    if (user != null) {
      openLoginActivity();
    }
  }

  private void openLoginActivity() {
    Intent intent = new Intent(this,MainActivity.class);
    startActivity(intent);
  }
  private void openSignUpActivity() {
    Intent intent = new Intent(this,RegisterActivity.class);
    startActivity(intent);
  }
}
