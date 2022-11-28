package com.alberto.authenticationfirebase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {

    private TextView tEmail, tProvider;
    private Button logOut;

    public enum PROVIDER_TYPE{
        BASIC,
        GOOGLE,
        FACEBOOK
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

        SharedPreferences preferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", email);
        editor.putString("provider", provider);
        editor.apply();
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
                SharedPreferences preferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();

                FirebaseAuth.getInstance().signOut();
                onBackPressed();
            }
        });
    }
}