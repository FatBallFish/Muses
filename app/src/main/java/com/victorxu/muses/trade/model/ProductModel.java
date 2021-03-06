package com.victorxu.muses.trade.model;

import android.content.Context;

import com.google.gson.Gson;
import com.victorxu.muses.gson.Collection;
import com.victorxu.muses.gson.Commodity;
import com.victorxu.muses.gson.ShoppingCart;
import com.victorxu.muses.trade.contract.ProductContract;
import com.victorxu.muses.trade.view.entity.ProductSettleOrderBean;
import com.victorxu.muses.trade.view.entity.StyleSelectItem;
import com.victorxu.muses.util.HttpUtil;
import com.victorxu.muses.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;

public class ProductModel implements ProductContract.Model {

    private static final String TAG = "ProductModel";

    private final String COMMODITY_API_PREFIX = "api/commodity/";
    private final String COMMENT_API_PREFIX = "api/comment/";
    private final String COMMENT_API_SUFFIX = "/1/none";
    private final String SHOPPING_CART_API = "api/cart/";
    private final String FAVORITE_API = "api/favorite/commodity/";

    private int id;
    private int userId = 0;
    private int number = 1;
    private Map<String, Integer> parameter = new HashMap<>();
    private Map<String, String> detail = new HashMap<>();
    private int favId = 0;
    private String image;

    private Commodity.CommodityDetail mCommodityData;

    private Context context;

    private Call mCallProduct;
    private Call mCallComment;
    private Call mCallAdd;
    private Call mCallCheck;
    private Call mCallAddFavorite;
    private Call mCallRemoveFavorite;

    public ProductModel(int id, Context context) {
        this.id = id;
        this.context = context;
    }

    @Override
    public void getProductData(Callback callback) {
        mCallProduct = HttpUtil.getRequest(context, COMMODITY_API_PREFIX + String.valueOf(id), callback);
    }

    @Override
    public void getCommentData(Callback callback) {
        mCallComment = HttpUtil.getRequest(context, COMMENT_API_PREFIX + String.valueOf(id) + COMMENT_API_SUFFIX, callback);
    }

    @Override
    public void addProductDataToCart(Callback callback) {
        ShoppingCart.CartItemBean entity = new ShoppingCart.CartItemBean();
        entity.setParameter(getParameter());
        entity.setUserId(userId);
        entity.setCommodityId(id);
        entity.setDetail(getSelectDetail());
        entity.setNumber(number);
        mCallAdd = HttpUtil.postRequest(context, SHOPPING_CART_API + String.valueOf(userId), new Gson().toJson(entity), callback);
    }

    @Override
    public void checkFavoriteStatus(Callback callback) {
        userId = (int) SharedPreferencesUtil.get(context, "UserId", 0);
        mCallCheck = HttpUtil.getRequest(context, FAVORITE_API + String.valueOf(userId) + "/" + String.valueOf(id), callback);
    }

    @Override
    public void addProductDataToFavorite(Callback callback) {
        Collection.CollectionBean entity = new Collection.CollectionBean();
        entity.setUserId(userId);
        entity.setCommodityId(id);
        mCallAddFavorite = HttpUtil.postRequest(context, FAVORITE_API, new Gson().toJson(entity), callback);
    }

    @Override
    public void removeProductDataFromFavorite(Callback callback) {
        mCallRemoveFavorite = HttpUtil.deleteRequest(context, FAVORITE_API + String.valueOf(favId), callback);
    }

    @Override
    public List<StyleSelectItem> getStyleSelectData(List<Commodity.CommodityDetail.AttributesBean> attributesBeans) {
        List<StyleSelectItem> styleSelectItems = new ArrayList<>();
        for (int i = 0; i < attributesBeans.size(); i++) {
            styleSelectItems.add(new StyleSelectItem(attributesBeans.get(i)));
        }
        styleSelectItems.add(new StyleSelectItem(1));
        return styleSelectItems;
    }

    @Override
    public List<String> getAttributeInfoData(String information) {
        List<String> attributeInfo = new ArrayList<>();
        String data = information.substring(information.indexOf('{') + 1, information.lastIndexOf('}'));
        attributeInfo.addAll(Arrays.asList(data.split(",")));
        return attributeInfo;
    }

    @Override
    public String getSelectDetail() {
        String s = detail.toString();
        s = s.substring(s.indexOf('{') + 1, s.lastIndexOf('}'));
        s = s.replace('=', ':');
        s = s.replace(", ", ";");
        return s;
    }

    @Override
    public void updateStyleSelectNumber(int number) {
        this.number = number;
    }

    @Override
    public void updateStyleSelectDetail(String key, String value, int parameterId, boolean isSelected) {
        if (isSelected) {
            detail.put(key, value);
            parameter.put(key, parameterId);
        } else {
            detail.remove(key);
            parameter.remove(key);
        }
    }

    @Override
    public boolean checkUserStatus() {
        userId = (int) SharedPreferencesUtil.get(context, "UserId", 0);
        return userId != 0;
    }

    @Override
    public void setFavoriteId(int id) {
        favId = id;
    }

    @Override
    public void setProductOrderImage(String image) {
        this.image = image;
    }

    @Override
    public void setCommodityData(Commodity.CommodityDetail data) {
        mCommodityData = data;
    }

    @Override
    public ProductSettleOrderBean getProductSettleData() {
        ProductSettleOrderBean data = new ProductSettleOrderBean();
        data.setCommodityId(mCommodityData.getId());
        data.setDetail(getSelectDetail());
        data.setNumber(String.valueOf(number));
        data.setParameter(getParameter());
        data.setTitle(mCommodityData.getName());
        data.setPrice(String.valueOf(mCommodityData.getDiscountPrice()));
        data.setImage(image);
        return data;
    }

    @Override
    public void cancelTask() {
        cancelCall(mCallProduct);
        cancelCall(mCallComment);
        cancelCall(mCallAdd);
        cancelCall(mCallCheck);
        cancelCall(mCallAddFavorite);
        cancelCall(mCallRemoveFavorite);
    }

    private void cancelCall(Call call) {
        if (call != null && call.isExecuted()) {
            call.cancel();
        }
    }

    private String getParameter() {
        StringBuilder builder = new StringBuilder();
        for (Integer value : parameter.values()) {
            builder.append(value);
            builder.append(',');
        }
        String s = builder.toString();
        return s.substring(0, s.lastIndexOf(','));
    }
}
