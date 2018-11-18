package com.impakter.seller.adapter.seller;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.impakter.seller.R;
import com.impakter.seller.events.OnDeleteClickListener;
import com.impakter.seller.events.OnItemClickListener;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> implements Filterable {
    private Activity context;
    private ArrayList<String> listContacts;
    private ArrayList<String> listContactFiltered;
    private LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;
    private OnDeleteClickListener onDeleteClickListener;

    public ContactAdapter(Activity context, ArrayList<String> listContacts) {
        this.context = context;
        this.listContacts = listContacts;
        this.listContactFiltered = listContacts;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_list_recent_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvTitle.setText(listContactFiltered.get(position));
    }

    @Override
    public int getItemCount() {
        return listContactFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    listContactFiltered = listContacts;
                } else {
                    ArrayList<String> filteredList = new ArrayList<>();
                    for (String item : listContacts) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (item.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(item);
                        }
                    }

                    listContactFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listContactFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listContactFiltered = (ArrayList<String>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private ImageView ivDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            ivDelete = itemView.findViewById(R.id.iv_delete);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            });
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteClickListener.onDeleteClick(v, getAdapterPosition());
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }
}
