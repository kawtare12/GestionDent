package com.machmoum.projetsnina;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etVerificationCode;
    private Button btnSubmitCode;
    private RequestQueue requestQueue;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        etVerificationCode = findViewById(R.id.etVerificationCode);
        btnSubmitCode = findViewById(R.id.btnSubmitCode);

        requestQueue = Volley.newRequestQueue(this);

        btnSubmitCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitVerificationCode();
            }
        });
    }

    private void submitVerificationCode() {
        String verificationCode = etVerificationCode.getText().toString().trim();

        // Remplacez l'URL par votre endpoint de vérification du code
        String verificationUrl = "http://128.10.1.143:8087/reset-password";

        StringRequest request = new StringRequest(Request.Method.POST, verificationUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Gérer la réponse du serveur
                        if ("success".equals(response)) {
                            // Le code de vérification est correct, redirigez l'utilisateur vers la prochaine étape
                            Toast.makeText(ResetPasswordActivity.this, "Code de vérification correct", Toast.LENGTH_SHORT).show();
                            // Ajoutez la redirection vers la page appropriée ici
                            startActivity(new Intent(ResetPasswordActivity.this, NouvellePageActivity.class));
                            // N'oubliez pas de fermer cette activité après la réussite
                            finish();
                        } else {
                            // Le code de vérification est incorrect, affichez un message d'erreur
                            Toast.makeText(ResetPasswordActivity.this, "Code de vérification correct", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ResetPasswordActivity.this, NouvellePageActivity.class));

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Gérer l'erreur
                        Toast.makeText(ResetPasswordActivity.this, "Erreur de vérification du code", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Ajouter le paramètre de la demande (par exemple, le code de vérification de l'utilisateur)
                Map<String, String> params = new HashMap<>();
                params.put("verificationCode", verificationCode);
                return params;
            }
        };

        requestQueue.add(request);
    }

}
