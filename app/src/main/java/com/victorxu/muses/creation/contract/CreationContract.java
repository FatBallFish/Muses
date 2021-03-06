package com.victorxu.muses.creation.contract;

import com.victorxu.muses.creation.view.entity.PopularSearchItem;
import com.victorxu.muses.gson.FilterClass;

import java.util.List;

public interface CreationContract {
    interface Model {
        void getFilterClassData(okhttp3.Callback callback);

        List<PopularSearchItem> getPopularSearchData();

        void cancelTask();
    }

    interface View {
        void initRootView(android.view.View view);

        void showFilterClasses(List<FilterClass.FilterClassBean> data);

        void showPopularSearch(List<PopularSearchItem> data);

        void showToast(int resId);

        void showToast(CharSequence text);
    }

    interface Presenter {
        void loadRootView(android.view.View view);

        void loadDataToView();

        void destroy();
    }
}
