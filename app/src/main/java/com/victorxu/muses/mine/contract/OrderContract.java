package com.victorxu.muses.mine.contract;

import com.victorxu.muses.gson.PageOrderStatus;

import java.util.List;

import javax.security.auth.callback.Callback;

public interface OrderContract {
    interface Model {
        void getOrderData(okhttp3.Callback callback);

        void getOrderData(int page, okhttp3.Callback callback);

        void getMoreOrderData(okhttp3.Callback callback);

        void deleteOrderData(int position, okhttp3.Callback callback);

        void updateOrderData(int position, okhttp3.Callback callback);

        void addLocalOrderData(List<PageOrderStatus.PageOrder.OrderBean> data);

        List<PageOrderStatus.PageOrder.OrderBean> getLocalOrderData();

        void setLocalOrderData(List<PageOrderStatus.PageOrder.OrderBean> data);

        boolean checkOrderDataStatus();

        void setAllPages(int allPages);

        boolean checkPageStatus();

        void cancelTask();
    }

    interface View {
        void initRootView(android.view.View view);

        void showOrder(List<PageOrderStatus.PageOrder.OrderBean> data);

        void showMoreOrder(List<PageOrderStatus.PageOrder.OrderBean> data);

        void showLoading();

        void hideLoading();

        void showLoadMore();

        void hideLoadMore(boolean isCompleted, boolean isEnd);

        void showEmptyPage();

        void hideEmptyPage();

        void showPaySheet(int position);

        void hidePaySheet();

        void showToast(int resId);

        void showToast(CharSequence text);
    }

    interface Presenter {
        void loadRootView(android.view.View view);

        void loadDataToView();

        void reloadDataToView();

        void loadMoreDataToView();

        void cancelOrder(int position);

        void payOrder(int position);

        void destroy();
    }
}
