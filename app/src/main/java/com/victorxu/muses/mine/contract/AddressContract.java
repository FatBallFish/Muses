package com.victorxu.muses.mine.contract;

import com.victorxu.muses.gson.Address;

import java.util.List;

public interface AddressContract {
    interface Model {
        void getAddressData(okhttp3.Callback callback);
        void addAddressData(okhttp3.Callback callback);
        void deleteAddressData(okhttp3.Callback callback);
        void updateAddressData(okhttp3.Callback callback);
    }

    interface View {
        void initRootView(android.view.View view);
        void showAddress(List<Address.AddressBean> data);
        void showToast(int resId);
        void showToast(CharSequence text);
    }

    interface Presenter {
        void loadRootView(android.view.View view);
        void loadDataToView();
        void addAddress();
        void deleteAddress(int position);
        void updateAddress(int position);
    }
}
