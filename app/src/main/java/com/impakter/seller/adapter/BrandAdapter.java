package com.impakter.seller.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.constraint.solver.GoalRow;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.impakter.seller.R;
import com.impakter.seller.activity.MainActivity;
import com.impakter.seller.fragment.BrandDetailFragment;
import com.impakter.seller.object.BrandObj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.MyViewHolder> {
    private Activity context;
    private ArrayList<BrandObj> listBrands = new ArrayList<>();
    private LayoutInflater inflater;

    private LinkedHashMap<String, Integer> mMapIndex;
    private ArrayList<String> mSectionList;
    private String[] mSections;

    public BrandAdapter(Activity context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        fakeData();
        fillSections();
    }

    public BrandAdapter(Activity context, ArrayList<BrandObj> listBrands) {
        this.context = context;
        this.listBrands = listBrands;
    }

    private void fakeData() {
        listBrands.add(new BrandObj(1, "Followed Brand", 1, "Brand name", "Description"));
        listBrands.add(new BrandObj(1, "Followed Brand", 1, "Brand name", "Description"));
        listBrands.add(new BrandObj(1, "Followed Brand", 1, "Brand name", "Description"));
        listBrands.add(new BrandObj(1, "Followed Brand", 1, "Brand name", "Description"));
        listBrands.add(new BrandObj(1, "Followed Brand", 1, "Brand name", "Description"));
        listBrands.add(new BrandObj(1, "Followed Brand", 1, "Brand name", "Description"));

        listBrands.add(new BrandObj(1, "New Brand", 1, "Brand name", "Description"));
        listBrands.add(new BrandObj(1, "New Brand", 1, "Brand name", "Description"));
        listBrands.add(new BrandObj(1, "New Brand", 1, "Brand name", "Description"));
        listBrands.add(new BrandObj(1, "New Brand", 1, "Brand name", "Description"));
        listBrands.add(new BrandObj(1, "New Brand", 1, "Brand name", "Description"));

        listBrands.add(new BrandObj(1, "All Brand", 1, "Brand name", "Description"));
        listBrands.add(new BrandObj(1, "All Brand", 1, "Brand name", "Description"));
        listBrands.add(new BrandObj(1, "All Brand", 1, "Brand name", "Description"));
        listBrands.add(new BrandObj(1, "All Brand", 1, "Brand name", "Description"));
        listBrands.add(new BrandObj(1, "All Brand", 1, "Brand name", "Description"));
    }

    public void fillSections() {
        mMapIndex = new LinkedHashMap<String, Integer>();

        for (int x = 0; x < listBrands.size(); x++) {
            String subCategoryName = listBrands.get(x).getTypeBrand();
            if (subCategoryName.length() > 1) {
                if (!mMapIndex.containsKey(subCategoryName)) {
                    mMapIndex.put(subCategoryName, x);
                }
            }
        }
        Set<String> sectionLetters = mMapIndex.keySet();
        // create a list from the set to sort
        mSectionList = new ArrayList<String>(sectionLetters);
        Collections.sort(mSectionList);

        mSections = new String[mSectionList.size()];
        mSectionList.toArray(mSections);
    }

    private String getSection(BrandObj brandObj) {
        return brandObj.getTypeBrand();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_list_brand, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BrandObj brandObj = listBrands.get(position);
        String section = getSection(listBrands.get(position));
        boolean bShowSection = mMapIndex.get(section) == position;
        holder.tvTypeBrand.setVisibility(bShowSection ? View.VISIBLE : View.GONE);
        holder.line.setVisibility(bShowSection ? View.VISIBLE : View.GONE);
        if (position == 0) holder.line.setVisibility(View.GONE);

        holder.tvTypeBrand.setText(brandObj.getTypeBrand());
    }

    @Override
    public int getItemCount() {
        return listBrands.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTypeBrand;
        private TextView tvBrandName, tvDescription;
        private View line;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTypeBrand = itemView.findViewById(R.id.tv_type_brand);
            tvBrandName = itemView.findViewById(R.id.tv_brand_name);
            tvDescription = itemView.findViewById(R.id.tv_description);
            line = itemView.findViewById(R.id.line);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) context).showFragment(new BrandDetailFragment(), true);
                }
            });
        }
    }
}
