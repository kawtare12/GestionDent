package com.machmoum.projetsnina.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.machmoum.projetsnina.HomeActivity;
import com.machmoum.projetsnina.MainActivity;
import com.machmoum.projetsnina.R;
import com.machmoum.projetsnina.models.PW;

import java.text.BreakIterator;
import java.util.List;

public class PwAdapter extends RecyclerView.Adapter<PwAdapter.ViewHolder> {

    private Activity activity;

    private List<PW> pws;
    private Context context;

    public PwAdapter(Activity activity,Context context, List<PW> pws) {
        this.activity = activity;
        this.context = context;
        this.pws = pws;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PW pw = pws.get(position);
        holder.title.setText(pw.getTitle());
        holder.objectif.setText(pw.getObjectif());
        holder.studentid.setText(String.valueOf(pw.getStudentid()));
        holder.pwid.setText(String.valueOf(pw.getId()));
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, HomeActivity.class);
                intent.putExtra("PWid",holder.pwid.getText());
                intent.putExtra("STUDENTid",holder.studentid.getText());
                activity.startActivityForResult(intent, 1);
            }
        });



    }



    @Override
    public int getItemCount() {
        return pws.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout mainLayout;


        TextView title, objectif,studentid,pwid,tooth;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pwid = itemView.findViewById(R.id.pwid);
            studentid=itemView.findViewById(R.id.studentid);
            title = itemView.findViewById(R.id.title);
            objectif = itemView.findViewById(R.id.objectif);
            mainLayout = itemView.findViewById(R.id.parent);

        }
    }

}
