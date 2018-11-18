package com.impakter.seller.adapter.seller;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.impakter.seller.R;

import java.util.ArrayList;
import java.util.List;

public class ListEmailAdapter extends RecyclerView.Adapter<ListEmailAdapter.ViewHolder> {
    private Activity context;
    private List<String> listEmails = new ArrayList<>();
    private LayoutInflater inflater;

    public ListEmailAdapter(Activity context, List<String> listEmails) {
        this.context = context;
        this.listEmails = listEmails;
        inflater = LayoutInflater.from(context);
        fakeData();
    }
    public ListEmailAdapter(Activity context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        fakeData();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_list_email, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return listEmails.size();
    }

    private void fakeData() {
        listEmails.add("a");
        listEmails.add("a");
        listEmails.add("a");
        listEmails.add("a");
        listEmails.add("a");
        listEmails.add("a");
        listEmails.add("a");
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvEmail;
        private ImageView ivDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tv_email);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }
    }
}
