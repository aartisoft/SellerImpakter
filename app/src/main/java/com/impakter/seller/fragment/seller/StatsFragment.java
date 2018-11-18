package com.impakter.seller.fragment.seller;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.impakter.seller.BaseFragment;
import com.impakter.seller.R;
import com.impakter.seller.adapter.CommonAdapter;
import com.impakter.seller.config.Constants;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.CommonObj;
import com.impakter.seller.object.seller.StatRespond;
import com.impakter.seller.utils.DateTimeUtility;
import com.impakter.seller.utils.NumberUtil;
import com.impakter.seller.widget.CustomSpinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatsFragment extends BaseFragment implements View.OnClickListener {
    private View rootView;
    private CustomSpinner spMonth;

    private TextView tvRevenue, tvGrowthPercent, tvTotalOffer, tvNumberShipped, tvProductSale;
    private TextView tvMostRecentPayment, tvSecondLastPayment, tvBalance;
    private TextView tvSaleToday, tvSale15Days, tvSale30Days;
    private TextView tvUnitToday, tvUnit15Days, tvUnit30Days;

    private LinearLayout btnShowPaymentSummary, btnShowSalesSummary;
    private LinearLayout layoutPaymentSummary, layoutSalesSummary;

    private ImageView ivArrowDownPayment, ivArrowDownSale;
    private ArrayList<CommonObj> listMonths;

    private Calendar calendar;
    private int currentMonth, currentDay, currentYear;
    private String month;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_starts, container, false);
        initViews();
        initData();
        initSpinnerMonth();
        initControl();
        return rootView;
    }

    private void initViews() {
        calendar = Calendar.getInstance();

        spMonth = rootView.findViewById(R.id.sp_month);
        ivArrowDownPayment = rootView.findViewById(R.id.iv_arrow_down_payment);
        ivArrowDownSale = rootView.findViewById(R.id.iv_arrow_down_sale);

        tvRevenue = rootView.findViewById(R.id.tv_revenue);
        tvGrowthPercent = rootView.findViewById(R.id.tv_growth_percen);
        tvGrowthPercent.setSelected(true);

        tvProductSale = rootView.findViewById(R.id.tv_product_sale);
        tvProductSale.setSelected(true);

        tvTotalOffer = rootView.findViewById(R.id.tv_total_offer);
        tvNumberShipped = rootView.findViewById(R.id.tv_number_shipped);
        tvMostRecentPayment = rootView.findViewById(R.id.tv_most_recent_payment);
        tvSecondLastPayment = rootView.findViewById(R.id.tv_second_last_payment);
        tvBalance = rootView.findViewById(R.id.tv_balance);
        tvSaleToday = rootView.findViewById(R.id.tv_sale_today);
        tvSale15Days = rootView.findViewById(R.id.tv_sale_15_days);
        tvSale30Days = rootView.findViewById(R.id.tv_sale_30_days);
        tvUnitToday = rootView.findViewById(R.id.tv_unit_today);
        tvUnit15Days = rootView.findViewById(R.id.tv_unit_15_days);
        tvUnit30Days = rootView.findViewById(R.id.tv_unit_30_days);

        btnShowPaymentSummary = rootView.findViewById(R.id.btn_show_summary);
        btnShowSalesSummary = rootView.findViewById(R.id.btn_show_sales_summary);

        layoutPaymentSummary = rootView.findViewById(R.id.layout_summary);
        layoutSalesSummary = rootView.findViewById(R.id.layout_sales_summary);
    }

    private void initData() {
        currentMonth = calendar.get(Calendar.MONTH) + 1;
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        currentYear = calendar.get(Calendar.YEAR);
        Log.e(TAG, "currentMonth: " + currentMonth);
        Log.e(TAG, "curentDay: " + calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void initControl() {
        btnShowPaymentSummary.setOnClickListener(this);
        btnShowSalesSummary.setOnClickListener(this);

        spMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CommonObj commonObj = (CommonObj) parent.getItemAtPosition(position);
                month = DateTimeUtility.convertStringToDate(commonObj.getTitle(), "MMM, yyyy", "MM-yyyy");
                Log.e(TAG, "onItemSelected: " + month);
                getStats();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initSpinnerMonth() {
        List<String> allDates = new ArrayList<>();
        SimpleDateFormat monthDate = new SimpleDateFormat("MMM, yyyy");
        Calendar cal = Calendar.getInstance();
        for (int i = 1; i <= 12; i++) {
            String monthName = monthDate.format(cal.getTime());
            allDates.add(monthName);
            cal.add(Calendar.MONTH, -1);
        }
        listMonths = new ArrayList<>();
        for (int i = 0; i < allDates.size(); i++) {
            listMonths.add(new CommonObj(i, allDates.get(i)));
        }

        CommonAdapter statusAdapter = new CommonAdapter(self, listMonths);
        spMonth.setAdapter(statusAdapter);

//        for (int i = 0; i < listMonths.size(); i++) {
//            if (listMonths.get(i).getId() == currentMonth) {
//                spMonth.setSelection(i);
//                listMonths.get(i).setTitle(getResources().getString(R.string.this_month));
//                break;
//            }
//        }
    }

    private void getStats() {
        showDialog();
        ConnectServer.getResponseAPI().getStats(15, month).enqueue(new Callback<StatRespond>() {
            @Override
            public void onResponse(Call<StatRespond> call, Response<StatRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        setData(response.body().getData());
                    } else {
                        showToast(response.body().getMessage());
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<StatRespond> call, Throwable t) {
                closeDialog();
                showToast(t.getMessage());
            }
        });
    }

    private void setData(StatRespond.Data data) {
        tvRevenue.setText(getResources().getString(R.string.lbl_currency) + NumberUtil.formatFloatNumber(data.getTotalRevenue()));
        tvGrowthPercent.setText(NumberUtil.formatFloatNumber(data.getPercentGrowth()) + "%");
        tvTotalOffer.setText(NumberUtil.formatIntNumber(data.getTotalOrder()) + "");
        tvNumberShipped.setText(NumberUtil.formatIntNumber(data.getTotalOrderShiped()) + "");
        tvMostRecentPayment.setText(getResources().getString(R.string.lbl_currency) + data.getMostRecentPayment());
        tvSecondLastPayment.setText(getResources().getString(R.string.lbl_currency) + data.getSecondLastPayment());
        tvBalance.setText(getResources().getString(R.string.lbl_currency) + data.getBalance());
        tvSaleToday.setText(getResources().getString(R.string.lbl_currency) + data.getSummaryTodaySales());
        tvSale15Days.setText(getResources().getString(R.string.lbl_currency) + data.getSummary15daySales());
        tvSale30Days.setText(getResources().getString(R.string.lbl_currency) + data.getSummary30daySales());

        tvUnitToday.setText(data.getSummaryTodayUnit() + "");
        tvUnit15Days.setText(data.getSummary15dayUnit() + "");
        tvUnit30Days.setText(data.getSummary30dayUnit() + "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_show_summary:
                if (layoutPaymentSummary.getVisibility() == View.VISIBLE) {
                    layoutPaymentSummary.setVisibility(View.GONE);
                    ivArrowDownPayment.setImageResource(R.drawable.ic_expandle_more);
                    layoutPaymentSummary.setAnimation(AnimationUtils.loadAnimation(self, R.anim.fadeout));
                } else {
                    layoutPaymentSummary.setVisibility(View.VISIBLE);
                    ivArrowDownPayment.setImageResource(R.drawable.ic_expandle_less);
                    layoutPaymentSummary.setAnimation(AnimationUtils.loadAnimation(self, R.anim.fadein));
                }
                break;

            case R.id.btn_show_sales_summary:
                if (layoutSalesSummary.getVisibility() == View.VISIBLE) {
                    layoutSalesSummary.setVisibility(View.GONE);
                    ivArrowDownPayment.setImageResource(R.drawable.ic_expandle_more);
                    layoutSalesSummary.setAnimation(AnimationUtils.loadAnimation(self, R.anim.fadeout));
                } else {
                    layoutSalesSummary.setVisibility(View.VISIBLE);
                    ivArrowDownPayment.setImageResource(R.drawable.ic_expandle_less);
                    layoutSalesSummary.setAnimation(AnimationUtils.loadAnimation(self, R.anim.fadein));
                }
                break;
        }
    }
}
