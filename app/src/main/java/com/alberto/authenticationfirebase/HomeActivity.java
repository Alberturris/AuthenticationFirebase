package com.alberto.authenticationfirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private TextView tEmail, tProvider;
    private Button logOut;

    public enum PROVIDER_TYPE{
        BASIC
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initComponents();

        Bundle bundle = getIntent().getExtras();
        String email = bundle.getString("email");
        String provider = bundle.getString("provider");
        setup(email, provider);
    }

    private void initComponents(){
        tEmail = (TextView) findViewById(R.id.tEmail);
        tProvider = (TextView) findViewById(R.id.tProvider);
        logOut = (Button) findViewById(R.id.bLogOut);
    }

    private void setup(String email, String provider_type){
        tEmail.setText(email);
        tProvider.setText(provider_type);

        // Log out
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                onBackPressed();
            }
        });
    }
}