package com.machmoum.projetsnina;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class NouvellePageActivity extends AppCompatActivity {
    private EditText etVerificationCode;
    private Button btnSubmitCode;
    private RequestQueue requestQueue;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nouvelle_page);
        etVerificationCode = findViewById(R.id.etVerificationCode);
        btnSubmitCode = findViewById(R.id.btnSubmitCode);

        requestQueue = Volley.newRequestQueue(this);

        btnSubmitCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NouvellePageActivity.this, MainActivity.class));

            }
        });
    }

}