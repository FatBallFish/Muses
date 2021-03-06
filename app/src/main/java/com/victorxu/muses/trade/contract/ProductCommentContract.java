package com.victorxu.muses.trade.contract;

import com.victorxu.muses.gson.PageComment;

import java.util.List;

public interface ProductCommentContract {
    interface Model {

        boolean checkPageStatus();

        boolean checkDataStatus();

        void setAllPages(int allPages);

        void setPageList(List<PageComment> data);

        List<String> getTagData();

        void getCommentCountData(okhttp3.Callback callback);

        void getCommentData(okhttp3.Callback callback);

        void getCommentData(int page, okhttp3.Callback callback);

        void getMoreCommentData(okhttp3.Callback callback);

        void addPage(PageComment page);

        void setFilter(int filterId);

        void cancelTask();
    }

    interface View {
        void initRootView(android.view.View view);

        void showLoading();

        void hideLoading();

        void showTag(List<String> data, String rate);

        void showComment(List<PageComment.PageCommentData.CommentBean> data);

        void showMoreComment(List<PageComment.PageCommentData.CommentBean> moreData);

        void showLoadingMore();

        void hideLoadingMore(boolean isCompeted, boolean isEnd);

        void showEmptyPage();

        void hideEmptyPage();

        void showToast(int resId);

        void showToast(CharSequence text);
    }

    interface Presenter {
        void loadRootView(android.view.View view);

        void loadDataToView();

        void loadMoreDataToView();

        void switchDataFilterMode(int filterId);

        void destroy();
    }
}
