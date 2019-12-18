package com.mukireus.muki_celil_workshop;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity<Button> extends AppCompatActivity {
  private EditText txtName, txtEmail,txtPassword;

  private static final String TAG = "EmailPassword";
  private FirebaseAuth mAuth;
  private FirebaseFirestore mFirestore;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);

    mAuth = FirebaseAuth.getInstance();
    mFirestore = FirebaseFirestore.getInstance();

    final View btnLogin = findViewById(R.id.btnLogin);
    final View btnSignUp = findViewById(R.id.btnSignUp);

    txtEmail = findViewById(R.id.txtEmail);
    txtPassword = findViewById(R.id.txtPassword);
    final CheckBox checkShowHidden = findViewById(R.id.checkShowHidden);

    btnLogin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        openLoginActivity();
      }
    });
    btnSignUp.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        registerUser(txtName.getText().toString(), txtEmail.getText().toString(), txtPassword.getText().toString());
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

  @Override
  public void onStart() {
    super.onStart();
    // Check if user is signed in (non-null) and update UI accordingly.
    FirebaseUser currentUser = mAuth.getCurrentUser();
    updateUI(currentUser);
  }

  private void registerUser(final String name, String email, String password) {

    if(!validateForm())
    {
      return;
    }

    mAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
              // Sign in success, update UI with the signed-in user's information
              Log.d(TAG, "createUserWithEmail:success");
              FirebaseUser user = mAuth.getCurrentUser();
              updateProfile(name);
              addDataBaseUsers();
              updateUI(user);
            } else {
              // If sign in fails, display a message to the user.
              Log.w(TAG, "createUserWithEmail:failure", task.getException());
              Toast.makeText(RegisterActivity.this, "Authentication failed.",
                  Toast.LENGTH_SHORT).show();
              updateUI(null);
            }

          }
        });
  }

  private void addDataBaseUsers(){
    String name = txtName.getText().toString().trim();
    String email = txtEmail.getText().toString().trim();

    Map<String, String> userMap = new HashMap<>();
    userMap.put("name",name);
    userMap.put("email",email);

    mFirestore.collection("users").document(email).set(userMap);
  }

  public void updateProfile(String name) {
    // [START update_profile]
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
        .setDisplayName(name)
        .setPhotoUri(null)
        .build();

    user.updateProfile(profileUpdates)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
              Log.d(TAG, "User profile updated.");
            }
          }
        });
    // [END update_profile]
  }

  private void updateUI(FirebaseUser user) {
    if (user != null) {
      openLoginActivity();
    }
  }

  private boolean validateForm() {
    boolean valid = true;

    String name = txtName.getText().toString();
    if (TextUtils.isEmpty(name)) {
      txtName.setError("Lütfen isminizi giriniz.");
      valid = false;
    } else {
      txtName.setError(null);
    }

    String email = txtEmail.getText().toString();
    if (TextUtils.isEmpty(email)) {
      txtEmail.setError("Lütfen email adresi giriniz.");
      valid = false;
    } else {
      txtEmail.setError(null);
    }

    String password = txtPassword.getText().toString();
    if (TextUtils.isEmpty(password) ) {
      txtPassword.setError("Lütfen parola giriniz.");
      valid = false;
    } else {
      txtPassword.setError(null);
    }

    int passwordLength = txtPassword.length();
    if (passwordLength <= 7 ) {
      txtPassword.setError("Lütfen 8 karakter veya daha fazla karakter giriniz.");
      valid = false;
    } else {
      txtPassword.setError(null);
    }
    return valid;
  }

  void openLoginActivity() {
    Intent intent = new Intent(this,LoginActivity.class);
    startActivity(intent);
  }
}
