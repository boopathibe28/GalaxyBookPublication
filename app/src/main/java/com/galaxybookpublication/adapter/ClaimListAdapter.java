package com.galaxybookpublication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.galaxybookpublication.R;
import com.galaxybookpublication.intrefaces.BookOnClick;
import com.galaxybookpublication.modelapi.ClaimOptionListApiResponse;

import java.util.List;

public class ClaimListAdapter extends RecyclerView.Adapter<ClaimListAdapter.ViewHolder> {
    private final Context context;
    private final List<ClaimOptionListApiResponse.Datum> data;
    private final BookOnClick onClick;

    public ClaimListAdapter(Context context, List<ClaimOptionListApiResponse.Datum> data, BookOnClick onClick) {
        this.context = context;
        this.data = data;
        this.onClick = onClick;
    }


    @NonNull
    @Override
    public ClaimListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.adapter_book_list, parent, false);
        return new ClaimListAdapter.ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ClaimListAdapter.ViewHolder holder, int position) {

        ClaimOptionListApiResponse.Datum response = data.get(position);


        holder.txtName.setText(response.getName());

        holder.lyoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.onClickView(response.getId(),response.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtName;
        LinearLayout lyoutParent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            lyoutParent = itemView.findViewById(R.id.lyoutParent);
        }
    }
}