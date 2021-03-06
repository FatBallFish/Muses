package com.victorxu.muses.mine.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.victorxu.muses.R;
import com.victorxu.muses.base.BaseSwipeBackFragment;
import com.victorxu.muses.gson.Collection;
import com.victorxu.muses.mine.contract.CollectionContract;
import com.victorxu.muses.mine.presenter.CollectionPresenter;
import com.victorxu.muses.mine.view.adapter.CollectionAdapter;
import com.victorxu.muses.trade.view.ProductFragment;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

public class CollectionFragment extends BaseSwipeBackFragment implements CollectionContract.View {

    private Toolbar mToolbar;
    private SmartRefreshLayout mRefresh;
    private SwipeMenuRecyclerView mRecycler;
    private CollectionAdapter mAdapter;

    private List<Collection.CollectionBean> mCollectionData = new ArrayList<>();

    private CollectionContract.Presenter mPresenter;

    public static CollectionFragment newInstance() {
        return new CollectionFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collect, container, false);
        mPresenter = new CollectionPresenter(this, mActivity);
        mPresenter.loadRootView(view);
        return attachToSwipeBack(view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        mPresenter = null;
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        mPresenter.loadDataToView();
    }

    @Override
    public void initImmersionBar() {
        ImmersionBar.with(mActivity).statusBarDarkFont(true).init();
    }

    @Override
    protected int setTitleBar() {
        return R.id.collect_toolbar;
    }

    @Override
    public void initRootView(View view) {
        mToolbar = view.findViewById(R.id.collect_toolbar);
        mRefresh = view.findViewById(R.id.collect_refresh);
        mRecycler = view.findViewById(R.id.collect_recycler);

        mToolbar.setNavigationOnClickListener(v -> mActivity.onBackPressed());

        mRefresh.setEnableLoadMore(false);
        mRefresh.setEnableLoadMoreWhenContentNotFull(false);
        mRefresh.setRefreshHeader(new MaterialHeader(mActivity));
        mRefresh.setOnRefreshListener((@NonNull RefreshLayout refreshLayout) -> mPresenter.reloadDataToView());

        mAdapter = new CollectionAdapter(mCollectionData);

        mAdapter.setOnItemClickListener((BaseQuickAdapter adapter, View v, int position) ->
                start(ProductFragment.newInstance(mCollectionData.get(position).getCommodityId()))
        );

        mRecycler.setSwipeMenuCreator((SwipeMenu leftMenu, SwipeMenu rightMenu, int viewType) -> {
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            int width = getResources().getDimensionPixelSize(R.dimen.dp_70);
            SwipeMenuItem deleteItem = new SwipeMenuItem(mActivity)
                    .setText(getResources().getString(R.string.delete))
                    .setHeight(height)
                    .setWidth(width)
                    .setTextColor(getResources().getColor(R.color.white))
                    .setBackgroundColor(getResources().getColor(R.color.dark_red));
            rightMenu.addMenuItem(deleteItem);
        });
        mRecycler.setSwipeMenuItemClickListener((SwipeMenuBridge menuBridge) -> {
            menuBridge.closeMenu();
            int adapterPosition = menuBridge.getAdapterPosition();
            int menuPosition = menuBridge.getPosition();
            if (menuPosition == 0) {
                mPresenter.removeFavorite(adapterPosition);
            }
        });
        mRecycler.addItemDecoration(new DefaultItemDecoration(getResources().getColor(R.color.light_white), ViewGroup.LayoutParams.MATCH_PARENT, 5));
        mRecycler.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecycler.setAdapter(mAdapter);

        mRecycler.setAutoLoadMore(false);
    }

    @Override
    public void showLoading() {
        mRefresh.autoRefresh(100, 500, 1.2f, true);
    }

    @Override
    public void hideLoading() {
        mRefresh.finishRefresh(1000);
    }

    @Override
    public void showCollection(List<Collection.CollectionBean> data) {
        mCollectionData.clear();
        mCollectionData.addAll(data);
        post(() -> {
            mAdapter.setNewData(mCollectionData);
            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void showToast(int resId) {
        showToast(getText(resId));
    }

    @Override
    public void showToast(CharSequence text) {
        post(() -> Toast.makeText(mActivity, text, Toast.LENGTH_SHORT).show());
    }
}
