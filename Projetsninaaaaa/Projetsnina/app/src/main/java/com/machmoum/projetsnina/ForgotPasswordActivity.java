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

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnRecoverPassword;
    private RequestQueue requestQueue;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.etEmail);
        btnRecoverPassword = findViewById(R.id.btnRecoverPassword);

        requestQueue = Volley.newRequestQueue(this);

        btnRecoverPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recoverPassword();
            }
        });
    }

    private void recoverPassword() {
        String email = etEmail.getText().toString().trim();

        // Vérifier si l'email est valide (vous pouvez ajouter une validation plus robuste ici)

        // Construire l'URL de votre endpoint de récupération de mot de passe
        String recoveryUrl = "http://128.10.1.143:8087/forgotPassword";

        // Créer une demande de chaîne POST avec Volley
        StringRequest request = new StringRequest(Request.Method.POST, recoveryUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Gérer la réponse du serveur
                        Toast.makeText(ForgotPasswordActivity.this, "Mot de passe récupéré avec succès", Toast.LENGTH_SHORT).show();

                        // Rediriger vers ResetPasswordActivity avec l'email


                        // Fermer cette activité après la récupération réussie
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Gérer l'erreur
                        Toast.makeText(ForgotPasswordActivity.this, "Code envoyé avec succes", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Ajouter les paramètres de la demande (par exemple, l'email de l'utilisateur)
                Map<String, String> params = new HashMap<>();
                params.put("email", email);

                return params;
            }
        };

        // Ajouter la demande à la file d'attente de Volley
        requestQueue.add(request);
    }
}
