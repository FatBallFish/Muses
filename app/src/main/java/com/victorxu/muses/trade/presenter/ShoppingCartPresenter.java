package com.victorxu.muses.trade.presenter;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.victorxu.muses.R;
import com.victorxu.muses.gson.Commodity;
import com.victorxu.muses.gson.ShoppingCart;
import com.victorxu.muses.gson.Status;
import com.victorxu.muses.trade.contract.ShoppingCartContract;
import com.victorxu.muses.trade.model.ShoppingCartModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@SuppressWarnings({"NullableProblems", "ConstantConditions"})
public class ShoppingCartPresenter implements ShoppingCartContract.Presenter {

    private static final String TAG = "ShoppingCartPresenter";

    private ShoppingCartContract.Model mModel;
    private ShoppingCartContract.View mView;

    public ShoppingCartPresenter(ShoppingCartContract.View mView, Context context) {
        this.mView = mView;
        mModel = new ShoppingCartModel(context);
    }

    @Override
    public void loadRootView(View view) {
        mView.initRootView(view);
    }

    @Override
    public void loadDataToView(boolean isEditMode) {
        if (mModel.getUserId() == 0) {
            mView.hideShoppingCart();
            mView.showEmptyView();
        } else {
            mView.showLoading();
            mModel.getCartData(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "onFailure: getCartData");
                    if (!e.getMessage().equals("Socket closed")) {
                        mView.hideLoading();
                        if (!mModel.checkDataStatus()) {
                            mView.hideShoppingCart();
                            mView.showEmptyView();
                        }
                        mView.showToast(R.string.network_error_please_try_again);
                    }
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        ShoppingCart cart = new Gson().fromJson(response.body().string(), ShoppingCart.class);
                        if (cart.getCode().equals("OK")) {
                            mModel.setShoppingCartData(cart.getData());
                        } else {
                            mView.showToast(cart.getMessage());
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "onResponse: getCartData DATA ERROR");
                        mView.showToast(R.string.data_store_error_please_login_again);
                        e.printStackTrace();
                    } finally {
                        if (!mModel.checkDataStatus()) {
                            mView.hideShoppingCart();
                            mView.showEmptyView();
                        } else {
                            mView.hideEmptyView();
                            mView.showShoppingCart();
                            changeCartMode(isEditMode);
                        }
                        mView.hideLoading();
                    }
                }
            });
        }
    }

    @Override
    public void reloadDataToView(boolean isEditMode) {
        loadDataToView(isEditMode);
    }

    @Override
    public void loadStyleSelectData(int position) {
        mModel.getProductData(position, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: getProductData");
                if (!e.getMessage().equals("Socket closed")) {
                    mView.showToast(R.string.network_error_please_try_again);
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    Commodity commodity = new Gson().fromJson(response.body().string(), Commodity.class);
                    if (commodity.getCode().equals("OK") && commodity.getData() != null) {
                        mView.showBottomSheet(mModel.getStyleSelectData(commodity.getData().getAttributes()));
                    }
                } catch (Exception e) {
                    Log.w(TAG, "onResponse: getProductData DATA ERROR");
                    mView.showToast(R.string.data_error_please_try_again);
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void updateData(int position, boolean isChecked) {
        mModel.updateData(position, isChecked);
        mView.showCartItem(mModel.getShoppingCartData());
        mView.showPrice(String.valueOf(mModel.getTotalPrice()));
    }

    @Override
    public void updateData(int position, int number) {
        mModel.updateData(position, number);
        mView.showCartItem(mModel.getShoppingCartData());
        mView.showPrice(String.valueOf(mModel.getTotalPrice()));
        mModel.updateCartData(position, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: update number");
                if (!e.getMessage().equals("Socket closed")) {
                    mView.showToast(R.string.network_error_please_try_again);
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    Status status = new Gson().fromJson(response.body().string(), Status.class);
                    if (!status.getCode().equals("OK")) {
                        mView.showToast(status.getMessage());
                    }
                } catch (Exception e) {
                    mView.showToast(R.string.data_error_please_try_again);
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void updateData(int position, String detail, String parameter) {
        mModel.updateData(position, detail, parameter);
        mView.showCartItem(mModel.getShoppingCartData());
        mView.showPrice(String.valueOf(mModel.getTotalPrice()));
        mModel.updateCartData(position, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: detail");
                if (!e.getMessage().equals("Socket closed")) {
                    mView.showToast(R.string.network_error_please_try_again);
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    Status status = new Gson().fromJson(response.body().string(), Status.class);
                    if (!status.getCode().equals("OK")) {
                        mView.showToast(status.getMessage());
                    }
                } catch (Exception e) {
                    mView.showToast(R.string.data_error_please_try_again);
                }
            }
        });
    }

    @Override
    public void removeDataFromView() {
        mModel.deleteCartData(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: delete all checked");
                if (!e.getMessage().equals("Socket closed")) {
                    mView.showToast(R.string.network_error_please_try_again);
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    Status status = new Gson().fromJson(response.body().string(), Status.class);
                    if (!status.getCode().equals("OK")) {
                        mView.showToast(status.getMessage());
                    }
                    if (!mModel.checkDataStatus()) {
                        mView.hideShoppingCart();
                        mView.showEmptyView();
                    }
                } catch (Exception e) {
                    mView.showToast(R.string.data_error_please_try_again);
                    e.printStackTrace();
                }
            }
        });
        mView.showCartItem(mModel.getShoppingCartData());
        mView.showPrice(String.valueOf(mModel.getTotalPrice()));
    }

    @Override
    public void removeDataFromView(int position) {
        mModel.deleteCartData(position, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: delete");
                if (!e.getMessage().equals("Socket closed")) {
                    mView.showToast(R.string.network_error_please_try_again);
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    Status status = new Gson().fromJson(response.body().string(), Status.class);
                    if (!status.getCode().equals("OK")) {
                        mView.showToast(status.getMessage());
                    }
                    if (!mModel.checkDataStatus()) {
                        mView.hideShoppingCart();
                        mView.showEmptyView();
                    }
                } catch (Exception e) {
                    mView.showToast(R.string.data_error_please_try_again);
                    e.printStackTrace();
                }

            }
        });
        mView.showCartItem(mModel.getShoppingCartData());
        mView.showPrice(String.valueOf(mModel.getTotalPrice()));
    }

    @Override
    public void collectDataFromView() {
        mModel.addCartDataToFavorite(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: addCartDataToFavorite");
                if (!e.getMessage().equals("Socket closed")) {
                    mView.showToast(R.string.network_error_please_try_again);
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    Status status = new Gson().fromJson(response.body().string(), Status.class);
                    mView.showToast(status.getMessage());
                } catch (Exception e) {
                    Log.w(TAG, "onResponse: addCartDataToFavorite DATA ERROR");
                    mView.showToast(R.string.data_error_please_try_again);
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void collectDataFromView(int position) {
        mModel.addCartDataToFavorite(position, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: addCartDataToFavorite");
                if (!e.getMessage().equals("Socket closed")) {
                    mView.showToast(R.string.network_error_please_try_again);
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    Status status = new Gson().fromJson(response.body().string(), Status.class);
                    mView.showToast(status.getMessage());
                } catch (Exception e) {
                    Log.w(TAG, "onResponse: addCartDataToFavorite DATA ERROR");
                    mView.showToast(R.string.data_error_please_try_again);
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void changeCartMode(boolean isEditMode) {
        mModel.changeCartMode(isEditMode);
        mView.switchCartMode(isEditMode);
        mView.showPrice(String.valueOf(mModel.getTotalPrice()));
        mView.showCartItem(mModel.getShoppingCartData());
    }

    @Override
    public void checkAllData(boolean isCheckedAll) {
        mModel.checkAllData(isCheckedAll);
        mView.showCartItem(mModel.getShoppingCartData());
        mView.showPrice(String.valueOf(mModel.getTotalPrice()));
    }

    @Override
    public void settleShoppingCart() {
        mView.showSettleFragment(mModel.getCheckedData());
    }

    @Override
    public void destroy() {
        mView = null;
        if (mModel != null) {
            mModel.cancelTask();
            mModel = null;
        }
    }
}
