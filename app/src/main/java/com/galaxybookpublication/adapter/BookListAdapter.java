package com.galaxybookpublication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.galaxybookpublication.R;
import com.galaxybookpublication.intrefaces.BookListOnClick;
import com.galaxybookpublication.modelapi.BookListApiResponse;

import java.util.ArrayList;
import java.util.List;

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.ViewHolder> {
    private final Context context;
    private final List<BookListApiResponse.Datum> data;
    private final BookListOnClick onClick;
    private final ArrayList<String> bookKey;

    public BookListAdapter(Context context, ArrayList<String> bookKey, List<BookListApiResponse.Datum> data, BookListOnClick onClick) {
        this.context = context;
        this.data = data;
        this.onClick = onClick;
        this.bookKey = bookKey;
    }


    @NonNull
    @Override
    public BookListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.adapter_book_list_multiple, parent, false);
        return new BookListAdapter.ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final BookListAdapter.ViewHolder holder, int position) {

        BookListApiResponse.Datum response = data.get(position);


        if (bookKey.size() > 0){
            for (int i = 0; i < bookKey.size(); i++) {
                if (bookKey.get(i).equals(response.getId())){
                    holder.txtName.setChecked(true);
                    break;
                }
                else {
                    holder.txtName.setChecked(false);
                }
            }
        }
        else {
            holder.txtName.setChecked(false);
        }

        holder.txtName.setText(response.getName());

        holder.txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String is_placed = "false";
                String is_addOrRemove = "";
                if (bookKey.size() > 0){
                    for (int i = 0; i < bookKey.size(); i++) {
                        if (bookKey.get(i).equals(response.getId())){
                            is_placed = "true";
                            is_addOrRemove = "remove";
                            bookKey.remove(i);
                            break;
                        }
                        else {
                            is_placed = "false";
                        }
                    }
                }
                else {
                    is_placed = "true";
                    is_addOrRemove = "add";
                    bookKey.add(response.getId());
                }


                if (is_placed.equals("false")){
                    is_addOrRemove = "add";
                    bookKey.add(response.getId());
                }

                onClick.onClickView(response.getUuid(),response.getName(),is_addOrRemove);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox txtName;
        LinearLayout lyoutParent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            lyoutParent = itemView.findViewById(R.id.lyoutParent);
        }
    }
}