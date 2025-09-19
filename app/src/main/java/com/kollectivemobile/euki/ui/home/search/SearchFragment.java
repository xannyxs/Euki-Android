package com.kollectivemobile.euki.ui.home.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewbinding.ViewBinding;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.kollectivemobile.euki.App;
import com.kollectivemobile.euki.R;
import com.kollectivemobile.euki.databinding.FragmentSearchBinding;
import com.kollectivemobile.euki.manager.ContentManager;
import com.kollectivemobile.euki.model.ContentItem;
import com.kollectivemobile.euki.ui.common.BaseFragment;
import com.kollectivemobile.euki.ui.common.adapter.SearchAdapter;
import com.kollectivemobile.euki.ui.home.content.ContentItemActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class SearchFragment extends BaseFragment implements SearchAdapter.SearchListener {
    @Inject ContentManager mContentManager;

    private FragmentSearchBinding binding;
    private List<ContentItem> mItems;
    private SearchAdapter mSearchAdapter;

    public static SearchFragment newInstance() {
        Bundle args = new Bundle();
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((App)getActivity().getApplication()).getAppComponent().inject(this);
        setUIElements();
        setHasOptionsMenu(true);
    }

    @Override
    protected ViewBinding getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding;
    }

    @Override
    protected View onCreateViewCalled(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_done, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_done) {
            getActivity().finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUIElements() {
        mItems = new ArrayList<>();
        mSearchAdapter = new SearchAdapter(getActivity(), this);
        binding.rvMain.setAdapter(mSearchAdapter);
        binding.rvMain.setLayoutManager(new LinearLayoutManager(getActivity()));

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                processSearch(binding.etSearch.getText().toString());
            }
        });
        binding.etSearch.requestFocus();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void processSearch(String searchedText) {
        mItems.clear();

        if (!searchedText.isEmpty()) {
            List<ContentItem> items = mContentManager.searchContentItem(searchedText);
            mItems.addAll(items);
        }
        mSearchAdapter.update(mItems);
    }

    @Override
    public void itemSelected(int position) {
        startActivity(ContentItemActivity.makeIntent(getActivity(), mItems.get(position)));
    }
}
