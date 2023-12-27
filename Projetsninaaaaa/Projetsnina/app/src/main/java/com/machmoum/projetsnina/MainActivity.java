package com.machmoum.projetsnina;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private String email,password;
    private Button btnLogin;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        requestQueue = Volley.newRequestQueue(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView forgotPasswordLink = findViewById(R.id.forgotPasswordLink);
        forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open the ResetPasswordActivity when the user clicks on "Forgot Password?"
                Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }




    public void loginUser() {
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        System.out.println(email+password);
        String url = "http://128.10.1.143:8087/etudiant/register";
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", email);
            jsonBody.put("password", password);
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                Intent intent = new Intent(MainActivity.this, PwActivity.class);
                                JSONObject jsonObject= response;
                                long id = jsonObject.getInt("id");
                                String email=jsonObject.getString("email");
                                String username=jsonObject.getString("username");
                                String role=jsonObject.getString("role");
                                String number=jsonObject.getString("number");
                                if (!jsonObject.isNull("photo")) {
                                    byte[] photoBytes = Base64.decode(response.getString("photo"), Base64.DEFAULT);
                                    // Convertir les octets de l'image en Bitmap
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length);

                                    // Convertir le Bitmap en tableau d'octets
                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                                    byte[] bitmapByteArray = byteArrayOutputStream.toByteArray();
                                    intent.putExtra("bitmap", bitmapByteArray);
                                }

                                JSONObject groupe = jsonObject.getJSONObject("groupe");
                                String code=groupe.getString("code");
                                intent.putExtra("studentid",id);
                                intent.putExtra("code", code);
                                intent.putExtra("email", email);
                                intent.putExtra("role", role);
                                intent.putExtra("number", number);
                                intent.putExtra("username", username);
                                startActivity(intent);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MainActivity.this, "Erreur d'authentification", Toast.LENGTH_SHORT).show();
                        }
                    }
            );


            Volley.newRequestQueue(this).add(request);

        } catch (JSONException e) {
            e.printStackTrace();
}
}

}


