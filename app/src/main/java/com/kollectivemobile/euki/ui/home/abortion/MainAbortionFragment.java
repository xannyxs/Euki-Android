package com.kollectivemobile.euki.ui.home.abortion;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewbinding.ViewBinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kollectivemobile.euki.App;
import com.kollectivemobile.euki.R;
import com.kollectivemobile.euki.databinding.FragmentAbortionBinding;
import com.kollectivemobile.euki.manager.ContentManager;
import com.kollectivemobile.euki.model.ContentItem;
import com.kollectivemobile.euki.networking.EukiCallback;
import com.kollectivemobile.euki.networking.ServerError;
import com.kollectivemobile.euki.ui.common.BaseFragment;
import com.kollectivemobile.euki.ui.common.adapter.AbortionAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class MainAbortionFragment extends BaseFragment implements AbortionAdapter.AbortionListener {
    @Inject ContentManager mContentManager;

    private FragmentAbortionBinding binding;
    private AbortionAdapter mAbortionAdapter;
    private List<ContentItem> mContentItems;

    public static MainAbortionFragment newInstance() {
        Bundle args = new Bundle();
        MainAbortionFragment fragment = new MainAbortionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((App)getActivity().getApplication()).getAppComponent().inject(this);
        setUIElements();
        requestItems();
    }

    @Override
    protected ViewBinding getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        binding = FragmentAbortionBinding.inflate(inflater, container, false);
        return binding;
    }

    @Override
    protected View onCreateViewCalled(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_abortion, container, false);
    }

    @Override
    public boolean showBack() {
        return true;
    }

    private void setUIElements() {
        mContentItems = new ArrayList<>();
        mAbortionAdapter = new AbortionAdapter(getActivity(), this);
        binding.rvMain.setAdapter(mAbortionAdapter);
        binding.rvMain.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void requestItems() {
        mContentItems.clear();
        mContentManager.getAbortionWalkthrough(new EukiCallback<ContentItem>() {
            @Override
            public void onSuccess(ContentItem contentItem) {
                mContentItems.add(contentItem);
                mContentManager.getAbortionKnowledge(new EukiCallback<ContentItem>() {
                    @Override
                    public void onSuccess(ContentItem contentItem) {
                        mContentItems.add(contentItem);
                        mAbortionAdapter.update(mContentItems);
                    }

                    @Override
                    public void onError(ServerError serverError) {
                        showError(serverError.getMessage());
                    }
                });
            }

            @Override
            public void onError(ServerError serverError) {
                showError(serverError.getMessage());
            }
        });
    }

    @Override
    public void abortionClicked(int position) {
        ContentItem item = mContentItems.get(position);
        showContentItem(item);
    }
}
