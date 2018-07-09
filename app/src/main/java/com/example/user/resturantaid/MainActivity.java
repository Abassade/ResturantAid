package com.example.user.resturantaid;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button button;
    private EditText email, pass;
    private LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.login);
        email =findViewById(R.id.username);
        pass = findViewById(R.id.pass);
        linearLayout = findViewById(R.id.parent);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert methodManager != null;
                methodManager.hideSoftInputFromWindow(email.getWindowToken(), 1);
                methodManager.hideSoftInputFromWindow(pass.getWindowToken(), 2);

            }
        });




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().toString().trim().equals("") || pass.getText().toString().trim().equals("")){

                    Toast.makeText(getBaseContext(), "email and password required", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                    startActivity(intent);
                }

            }
        });
    }
}
