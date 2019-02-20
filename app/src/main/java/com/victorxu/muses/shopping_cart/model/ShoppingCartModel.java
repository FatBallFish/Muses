package com.victorxu.muses.shopping_cart.model;


import android.content.Context;

import com.google.gson.Gson;
import com.victorxu.muses.gson.ShoppingCart;
import com.victorxu.muses.shopping_cart.contract.ShoppingCartContract;
import com.victorxu.muses.shopping_cart.view.entity.ShoppingCartProduct;
import com.victorxu.muses.util.HttpUtil;
import com.victorxu.muses.util.SharedPreferencesUtil;


import java.util.ArrayList;
import java.util.List;

import okhttp3.Callback;

public class ShoppingCartModel implements ShoppingCartContract.Model {

    private final String SHOPPING_CART_API = "api/cart/list/";
    private final String SHOPPING_CART_ITEM_API = "/api/cart/";

    private Context context;
    private int userId;
    private List<ShoppingCartProduct> mData = new ArrayList<>();

    public ShoppingCartModel(Context context) {
        this.context = context;
    }

    @Override
    public int getUserId() {
        userId = (int) SharedPreferencesUtil.get(context, "UserId", 0);
        return userId;
    }

    @Override
    public void getCartData(Callback callback) {
        HttpUtil.getRequest(SHOPPING_CART_API + String.valueOf(userId), callback);
    }

    @Override
    public void deleteCartData(Callback callback) {
        boolean flag;
        do {
            flag = false;
            for (int i = 0; i < mData.size(); i++) {
                if (mData.get(i).isChecked()) {
                    deleteCartData(i, callback);
                    flag = true;
                    break;
                }
            }
        } while (flag);
    }

    @Override
    public void deleteCartData(int position, Callback callback) {
        int cartId = mData.get(position).getData().getId();
        mData.remove(position);
        HttpUtil.deleteRequest(SHOPPING_CART_ITEM_API + String.valueOf(cartId), callback);
    }

    @Override
    public void updateCartData(int position, Callback callback) {
        int cartId = mData.get(position).getData().getId();
        String json = new Gson().toJson(mData.get(position).getData());
        HttpUtil.putRequest(SHOPPING_CART_ITEM_API + String.valueOf(cartId), json, callback);
    }

    @Override
    public void updateData(int position, boolean isChecked) {
        mData.get(position).setChecked(isChecked);
    }

    @Override
    public void updateData(int position, int number) {
        mData.get(position).getData().setNumber(number);
    }

    @Override
    public void updateData(int position, String detail) {
        mData.get(position).getData().setDetail(detail);
    }

    @Override
    public int getTotalPrice() {
        int sum = 0;
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).isChecked()) {
                sum += mData.get(i).getData().getCommodity().getDiscountPrice() * mData.get(i).getData().getNumber();
            }
        }
        return sum;
    }

    @Override
    public boolean checkDataStatus() {
        return mData.size() != 0;
    }

    @Override
    public void changeCartMode(boolean isEditMode) {
        for (int i = 0; i < mData.size(); i++) {
            mData.get(i).setEditedMode(isEditMode);
        }
    }

    @Override
    public void checkAllData(boolean isCheckedAll) {
        for (int i = 0; i < mData.size(); i++) {
            mData.get(i).setChecked(isCheckedAll);
        }
    }

    @Override
    public void setShoppingCartData(List<ShoppingCart.CartItemBean> data) {
        mData.clear();
        for (int i = 0; i < data.size(); i++) {
            mData.add(new ShoppingCartProduct(data.get(i)));
        }
    }

    @Override
    public List<ShoppingCartProduct> getShoppingCartData() {
        return mData;
    }
}
