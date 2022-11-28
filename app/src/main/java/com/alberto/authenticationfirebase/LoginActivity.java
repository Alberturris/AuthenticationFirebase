package com.alberto.authenticationfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button register, login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initComponents();
        initAnalytics();

        setupAuthentication();

    }

    private void initComponents(){

        email = (EditText) findViewById(R.id.editEmail);
        password = (EditText) findViewById(R.id.editPassword);
        register = (Button) findViewById(R.id.bRegister);
        login = (Button) findViewById(R.id.bLogin);

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
                                goToHome(Objects.requireNonNull(task.getResult().getUser()).getEmail(), HomeActivity.PROVIDER_TYPE.BASIC);
                            }else{
                                showAlert();
                            }
                        }
                    });
                }
            }
        });

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
        startActivity(intent);
    }
}