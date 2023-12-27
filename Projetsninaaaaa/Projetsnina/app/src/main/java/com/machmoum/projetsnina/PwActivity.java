package com.machmoum.projetsnina;

import static com.google.android.material.color.utilities.MaterialDynamicColors.error;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.machmoum.projetsnina.adapter.PwAdapter;
import com.machmoum.projetsnina.models.PW;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PwActivity extends AppCompatActivity implements View.OnClickListener{


    byte[] bitmapByteArray;
    String email, username, number, role, code;
    long studentId;
    List<PW> pws=new ArrayList<>();
    RecyclerView recyclerView;
    PwAdapter studentadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pw);


        Intent intent = getIntent();
        bitmapByteArray = intent.getByteArrayExtra("bitmap");
        email = intent.getStringExtra("email");
        username = intent.getStringExtra("username");
        role = intent.getStringExtra("role");
        number = intent.getStringExtra("number");
        code = intent.getStringExtra("code");
        studentId= getIntent().getLongExtra("studentid", -1);
        System.out.println(code);





        loadProfs();

        recyclerView = findViewById(R.id.pwRecyclerView);
        studentadapter = new PwAdapter(PwActivity.this,PwActivity.this,pws);
        recyclerView.setAdapter(studentadapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(PwActivity.this));



    }



    private void loadProfs() {
        String URL_LOAD = "http://128.10.1.143:8087/etudiant/all?code=" + code;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL_LOAD,null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("JSONResponse", response.toString());

                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                int id = jsonObject.getInt("id");
                                String title = jsonObject.getString("title");
                                String objectif = jsonObject.getString("objectif");


                                PW pw = new PW(id,studentId,title, objectif);
                                pws.add(pw);


                            }
                            studentadapter.notifyDataSetChanged();
                        }

                            catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PwActivity.this, "Erreur de chargement de donnes", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(PwActivity.this, ProfilAcitvity.class);

}
}
