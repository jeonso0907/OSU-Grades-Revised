package edu.osu.hack.OSUGrades;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private ArrayList<Grade> gpalist;
    private Context context;

    public CustomAdapter(ArrayList<Grade> list, Context cont) {
        gpalist = list;
        context = cont;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gpa_list, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Glide.with(holder.itemView).load(gpalist.get(position).getProfile()).into(holder.iv_profile);
        holder.tv_Name.setText(gpalist.get(position).getProfessorName());
        holder.tv_GPA.setText(String.valueOf(gpalist.get(position).getGPA()));
    }

    @Override
    public int getItemCount() {
        if ( gpalist != null ) {
            return 0;
        }
        else {
            return gpalist.size();
        }

    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_profile;
        TextView tv_Name;
        TextView tv_GPA;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_profile = itemView.findViewById(R.id.iv_default);
            this.tv_Name = itemView.findViewById(R.id.professorName);
            this.tv_GPA = itemView.findViewById(R.id.GPA);
        }
    }
}
