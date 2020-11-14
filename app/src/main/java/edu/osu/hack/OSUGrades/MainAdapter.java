package edu.osu.hack.OSUGrades;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.CustomViewHolder> {

    private ArrayList<String> arrayList;

    public MainAdapter(ArrayList<String> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MainAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.professor_list, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MainAdapter.CustomViewHolder holder, int position) {
        holder.Name.setText(arrayList.get(position));

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curName = holder.Name.getText().toString();
                Toast.makeText(v.getContext(), curName, Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                remove(holder.getAdapterPosition());

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        if ( arrayList != null )  {
            return 0;
        }
        else {
            return arrayList.size();
        }
    }

    public void remove(int position) {
        try {
            arrayList.remove(position);
            notifyItemRemoved(position);
        } catch ( IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView Name;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.Name = (TextView) itemView.findViewById(R.id.professor);
        }
    }
}
