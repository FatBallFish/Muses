package com.victorxu.muses.custom;

import android.content.Context;
import android.content.res.TypedArray;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import android.text.Editable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.LinearLayout;
import android.text.TextWatcher;
import android.widget.TextView;

import com.victorxu.muses.R;

public class SearchView extends LinearLayout implements TextWatcher, View.OnClickListener {

    private AppCompatEditText mEtSearchView;
    private OnSearchViewClickListener onSearchViewClickListener;
    private OnEditorActionListener onEditorActionListener;
    private AppCompatImageButton mBtnClear;

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.search_view, this);
        mEtSearchView = findViewById(R.id.search_view);
        mBtnClear = findViewById(R.id.button_search_clear);
        mBtnClear.setVisibility(GONE);
        mEtSearchView.addTextChangedListener(this);
        mBtnClear.setOnClickListener(this);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SearchView);
        String hint = array.getString(R.styleable.SearchView_hint);
        setSearchViewHint(hint);
        boolean isEditable = array.getBoolean(R.styleable.SearchView_editable, false);
        boolean isFocusableInTouchMode = array.getBoolean(R.styleable.SearchView_focusableInTouchMode, false);
        setSearchViewEditable(isEditable);
        setFocusableInTouchMode(isFocusableInTouchMode);
        if (!isEditable) {
            mEtSearchView.setOnClickListener((v) -> {
                if (onSearchViewClickListener != null) {
                    onSearchViewClickListener.onClick(v);
                }
            });
        }
        mEtSearchView.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (onEditorActionListener != null) {
                onEditorActionListener.onEditorAction(v, actionId, event);
            }
            return true;
        });

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String input = mEtSearchView.getText().toString().trim();
        if (input.isEmpty()) {
            mBtnClear.setVisibility(GONE);
        } else {
            mBtnClear.setVisibility(VISIBLE);
        }
    }

    /**
     * Clear Button
     */
    @Override
    public void onClick(View v) {
        mEtSearchView.setText("");
    }

    public void setOnSearchViewClickListener(OnSearchViewClickListener onSearchViewClickListener) {
        this.onSearchViewClickListener = onSearchViewClickListener;
    }

    public void setOnEditorActionListener(OnEditorActionListener onEditorActionListener) {
        this.onEditorActionListener = onEditorActionListener;
    }

    public void setSearchViewHint(String hint) {
        mEtSearchView.setHint(hint);
    }

    public void setSearchViewEditable(boolean isEditable) {
        mEtSearchView.setInputType(isEditable ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_NULL);
    }

    public void setFocusableInTouchMode(boolean focusableInTouchMode) {
        mEtSearchView.setFocusableInTouchMode(focusableInTouchMode);
    }

    public String getSearchViewText() {
        return mEtSearchView.getText().toString();
    }

    public void setSearchViewText(String text) {
        mEtSearchView.setText(text);
    }

    public interface OnSearchViewClickListener {
        void onClick(View view);
    }
    public interface OnEditorActionListener {
        void onEditorAction(TextView v, int actionId, KeyEvent event);
    }
}
