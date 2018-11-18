package com.impakter.seller.adapter.seller;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.impakter.seller.R;
import com.impakter.seller.activity.MainActivity;
import com.impakter.seller.fragment.BrandDetailFragment;
import com.impakter.seller.object.BrandObj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class NewCommentAdapter extends RecyclerView.Adapter<NewCommentAdapter.MyViewHolder> {
    private Activity context;
    private ArrayList<BrandObj> listBrands = new ArrayList<>();
    private LayoutInflater inflater;

    private LinkedHashMap<String, Integer> mMapIndex;
    private LinkedHashMap<String, Integer> mMapIndexViewAll;
    private ArrayList<String> mSectionList;
    private String[] mSections;

    public NewCommentAdapter(Activity context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        fakeData();
        fillSections();
    }

    public NewCommentAdapter(Activity context, ArrayList<BrandObj> listBrands) {
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
        mMapIndexViewAll = new LinkedHashMap<String, Integer>();

        for (int x = 0; x < listBrands.size(); x++) {
            String subCategoryName = listBrands.get(x).getTypeBrand();

            if (subCategoryName.length() > 1) {
                if (!mMapIndex.containsKey(subCategoryName)) {
                    mMapIndex.put(subCategoryName, x);
                }
            }
            if (x == listBrands.size() - 1) {
                String preSub = listBrands.get(x - 1).getTypeBrand();
                if (subCategoryName != preSub) {
                    if (!mMapIndexViewAll.containsKey(subCategoryName)) {
                        mMapIndexViewAll.put(subCategoryName, x);
                    }
                }
            } else {
                String nextSub = listBrands.get(x + 1).getTypeBrand();
                if (subCategoryName != nextSub) {
                    if (!mMapIndexViewAll.containsKey(subCategoryName)) {
                        mMapIndexViewAll.put(subCategoryName, x);
                    }
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
        View view = inflater.inflate(R.layout.item_list_comment_new, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BrandObj brandObj = listBrands.get(position);
        String section = getSection(listBrands.get(position));
        boolean bShowSection = mMapIndex.get(section) == position;
        boolean bShowViewAll = mMapIndexViewAll.get(section) == position;

        holder.layoutComment.setVisibility(bShowSection ? View.VISIBLE : View.GONE);
        holder.ratingBar.setVisibility(bShowSection ? View.VISIBLE : View.GONE);
        holder.tvTime.setVisibility(bShowSection ? View.VISIBLE : View.GONE);
        holder.tvContent.setVisibility(bShowSection ? View.VISIBLE : View.GONE);

        holder.edtContent.setVisibility(bShowViewAll ? View.VISIBLE : View.GONE);
        holder.tvViewAllComment.setVisibility(bShowViewAll ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return listBrands.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private EditText edtContent;
        private LinearLayout layoutSubComment, layoutComment;
        private TextView tvContent,tvTime,tvViewAllComment;
        private MaterialRatingBar ratingBar;
        public MyViewHolder(View itemView) {
            super(itemView);
            edtContent = itemView.findViewById(R.id.edt_content);
            layoutSubComment = itemView.findViewById(R.id.layout_sub_comment);
            layoutComment = itemView.findViewById(R.id.layout_comment);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvTime = itemView.findViewById(R.id.tv_time);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            tvViewAllComment = itemView.findViewById(R.id.tv_view_all_comment);

        }
    }
}
