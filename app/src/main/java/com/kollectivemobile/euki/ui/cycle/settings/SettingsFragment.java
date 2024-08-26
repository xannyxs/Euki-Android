package com.kollectivemobile.euki.ui.cycle.settings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kollectivemobile.euki.App;
import com.kollectivemobile.euki.R;
import com.kollectivemobile.euki.databinding.FragmentCycleSettingsBinding;
import com.kollectivemobile.euki.manager.AppSettingsManager;
import com.kollectivemobile.euki.ui.common.BaseFragment;

import javax.inject.Inject;

public class SettingsFragment extends BaseFragment {
    @Inject AppSettingsManager mAppSettingsManager;

    private FragmentCycleSettingsBinding binding;

    public static SettingsFragment newInstance() {
        Bundle args = new Bundle();
        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null) {
            ((App) getActivity().getApplication()).getAppComponent().inject(this);
        }
        setUIElements();
    }

    @Override
    protected ViewBinding getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        binding = FragmentCycleSettingsBinding.inflate(inflater, container, false);
        return binding;
    }

    @Override
    protected View onCreateViewCalled(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cycle_settings, container, false);
    }

    @Override
    public boolean showBack() {
        return true;
    }

    private void setUIElements() {
        Boolean trackPeriodEnabled = mAppSettingsManager.trackPeriodEnabled();
        binding.stchTrackPeriod.setChecked(trackPeriodEnabled);
        binding.stchPeriodPrediction.setChecked(mAppSettingsManager.periodPredictionEnabled());
        binding.stchPeriodPrediction.setEnabled(trackPeriodEnabled);

        binding.stchTrackPeriod.setOnCheckedChangeListener((buttonView, isChecked) -> trackPeriod(isChecked));
        binding.stchPeriodPrediction.setOnCheckedChangeListener((buttonView, isChecked) -> periodPrediction(isChecked));
    }

    void trackPeriod(boolean checked) {
        mAppSettingsManager.saveTrackPeriodEnabled(checked);
        if (!checked) {
            mAppSettingsManager.savePeriodPredictionEnabled(false);
        }
        setUIElements();
    }

    void periodPrediction(boolean checked) {
        mAppSettingsManager.savePeriodPredictionEnabled(checked);
    }
}
