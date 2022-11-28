package com.alberto.authenticationfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button register, login;
    private LinearLayout loginLayout;
    LoadingDialog loadingDialog;

    private SignInButton googleButton;
    private final int GOOGLE_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initComponents();
        initAnalytics();

        setupAuthentication();
        session();

    }

    @Override
    protected void onStart() {
        super.onStart();

        loginLayout.setVisibility(View.VISIBLE);
    }

    private void initComponents(){

        email = (EditText) findViewById(R.id.editEmail);
        password = (EditText) findViewById(R.id.editPassword);
        register = (Button) findViewById(R.id.bRegister);
        login = (Button) findViewById(R.id.bLogin);
        googleButton = (SignInButton) findViewById(R.id.googleButton);
        loginLayout = (LinearLayout) findViewById(R.id.loginLayout);

        loadingDialog = new LoadingDialog(LoginActivity.this);

    }

    private void initAnalytics(){
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString("Message", "Integración de Firebase completa");
        analytics.logEvent("LoginScreen", bundle);
    }

    private void setupAuthentication(){
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(),
                            password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                loadingDialog.startLoadingDialog();
                                goToHome(Objects.requireNonNull(task.getResult().getUser()).getEmail(), HomeActivity.PROVIDER_TYPE.BASIC);
                            }else{
                                showAlert();
                            }
                        }
                    });
                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(),
                            password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                loadingDialog.startLoadingDialog();
                                goToHome(Objects.requireNonNull(task.getResult().getUser()).getEmail(), HomeActivity.PROVIDER_TYPE.BASIC);
                            }else{
                                showAlert();
                            }
                        }
                    });
                }
            }
        });
        googleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, googleSignInOptions);
                googleSignInClient.signOut();

                startActivityForResult(googleSignInClient.getSignInIntent(), GOOGLE_SIGN_IN);
                googleSignInClient.signOut();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN){
            final Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                final GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null){
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                e.printStackTrace();
                showAlert();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account){

        final AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    loadingDialog.startLoadingDialog();
                    goToHome(account.getEmail(), HomeActivity.PROVIDER_TYPE.GOOGLE);
                }else{
                    showAlert();
                }
            }
        });
    }

    private void session(){
        SharedPreferences preferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        String email = preferences.getString("email", null);
        String provider = preferences.getString("provider",null);

        if (email != null && provider != null){
            loginLayout.setVisibility(View.INVISIBLE);
            loadingDialog.startLoadingDialog();
            goToHome(email, HomeActivity.PROVIDER_TYPE.valueOf(provider));
        }
    }

    private void showAlert(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Error");
        alert.setMessage("An error occurred while authenticating the user");
        alert.setPositiveButton("Aceptar", null);
        alert.show();
    }

    private void goToHome(String email, HomeActivity.PROVIDER_TYPE providerType){
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("provider", providerType.name());


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dimissDialog();
                startActivity(intent);
            }
        }, 500);

    }
}