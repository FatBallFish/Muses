package com.victorxu.muses.creation.model;

import android.content.Context;

import com.victorxu.muses.creation.contract.FilterClassContract;
import com.victorxu.muses.util.HttpUtil;

import okhttp3.Call;
import okhttp3.Callback;

public class FilterClassModel implements FilterClassContract.Model {

    private final String FILTER_API = "api/filter/";

    private String key;
    private int id;
    private Context context;
    private int currentPage = 1;
    private int allPages = 0;

    private Call mCallFilter;

    public FilterClassModel(String key, int id, Context context) {
        this.key = key;
        this.id = id;
        this.context = context;
    }

    @Override
    public void getFilterData(Callback callback) {
        allPages = 0;
        getFilterData(1, callback);
    }

    @Override
    public void getFilterData(int page, Callback callback) {
        currentPage = page;
        mCallFilter = HttpUtil.getRequest(context, FILTER_API + key + "/" + String.valueOf(id) + "/"
                + String.valueOf(currentPage), callback);
    }

    @Override
    public void getMoreFilterData(Callback callback) {
        getFilterData(currentPage + 1, callback);
    }

    @Override
    public int getCurrentPage() {
        return currentPage;
    }

    @Override
    public int getAllPages() {
        return allPages;
    }

    @Override
    public void setAllPages(int allPages) {
        this.allPages = allPages;
    }

    @Override
    public void cancelTask() {
        cancelCall(mCallFilter);
    }

    private void cancelCall(Call call) {
        if (call != null && call.isExecuted()) {
            call.cancel();
        }
    }
}
