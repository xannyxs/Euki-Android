package com.kollectivemobile.euki.ui.home.content;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.kollectivemobile.euki.App;
import com.kollectivemobile.euki.R;
import com.kollectivemobile.euki.databinding.FragmentContentItemBinding;
import com.kollectivemobile.euki.listeners.HeightListener;
import com.kollectivemobile.euki.manager.BookmarkManager;
import com.kollectivemobile.euki.model.ContentItem;
import com.kollectivemobile.euki.ui.common.BaseFragment;
import com.kollectivemobile.euki.ui.common.adapter.ContentGridSelectableAdapter;
import com.kollectivemobile.euki.ui.common.adapter.ContentGridSelectableAdapter.ContentGridSelectableListener;
import com.kollectivemobile.euki.ui.common.adapter.ContentHeaderFooterAdapter;
import com.kollectivemobile.euki.ui.common.adapter.ContentRowExpandableAdapter;
import com.kollectivemobile.euki.ui.common.adapter.ContentRowSelectableAdapter;
import com.kollectivemobile.euki.utils.advrecyclerview.adapter.AdapterPath;
import com.kollectivemobile.euki.utils.advrecyclerview.adapter.AdapterPathSegment;
import com.kollectivemobile.euki.utils.advrecyclerview.animator.GeneralItemAnimator;
import com.kollectivemobile.euki.utils.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.kollectivemobile.euki.utils.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.kollectivemobile.euki.utils.advrecyclerview.headerfooter.AbstractHeaderFooterWrapperAdapter;

import java.util.ArrayList;

import javax.inject.Inject;

public class ContentItemFragment extends BaseFragment implements ContentGridSelectableListener,
        ContentRowSelectableAdapter.ContentRowSelectableListener,
        ContentRowExpandableAdapter.ContentRowExpandableListener,
        RecyclerViewExpandableItemManager.OnGroupCollapseListener,
        RecyclerViewExpandableItemManager.OnGroupExpandListener,
        HeightListener {
    private static final String SAVED_STATE_EXPANDABLE_ITEM_MANAGER = "RecyclerViewExpandableItemManager";

    @Inject
    BookmarkManager mBookmarkManager;

    private FragmentContentItemBinding binding;
    private RecyclerViewExpandableItemManager mRecyclerViewExpandableItemManager;
    private AbstractHeaderFooterWrapperAdapter mHeaderFooterAdapter;

    protected ContentItem mContentItem;
    protected ContentItem mExpandContentItem;
    private Integer mLastIndexExpanded;
    private Integer mHeaderHeight = 0;

    public static ContentItemFragment newInstance(ContentItem contentItem) {
        return newInstance(contentItem, null);
    }

    public static ContentItemFragment newInstance(ContentItem contentItem, ContentItem expandContentItem) {
        Bundle args = new Bundle();
        ContentItemFragment fragment = new ContentItemFragment();
        fragment.mContentItem = contentItem;
        fragment.mExpandContentItem = expandContentItem;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((App) getActivity().getApplication()).getAppComponent().inject(this);
        setUIElements(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected ViewBinding getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        binding = FragmentContentItemBinding.inflate(inflater, container, false);
        return binding;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        int resId = mBookmarkManager.isBookmark(mContentItem.getId()) ? R.menu.menu_favorite_on : R.menu.menu_favorite_off;
        inflater.inflate(resId, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_bookmark) {
            if (mBookmarkManager.isBookmark(mContentItem.getId())) {
                mBookmarkManager.removeBookmark(mContentItem.getId());
            } else {
                mBookmarkManager.addBookmark(mContentItem.getId());
            }
            getActivity().invalidateOptionsMenu();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected View onCreateViewCalled(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content_item, container, false);
    }

    @Override
    public boolean showBack() {
        return true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRecyclerViewExpandableItemManager != null) {
            outState.putParcelable(SAVED_STATE_EXPANDABLE_ITEM_MANAGER, mRecyclerViewExpandableItemManager.getSavedState());
        }
    }

    private void setUIElements(Bundle savedInstanceState) {
        RecyclerView.Adapter mainAdapter;
        RecyclerView.LayoutManager mainLayoutManager;
        RecyclerView.Adapter secondaryAdapter = null;
        RecyclerView.LayoutManager secondaryLayoutManager = null;

        if (mContentItem.getSelectableItems() != null && mContentItem.getSelectableItems().size() > 0) {
            mainAdapter = new ContentGridSelectableAdapter(getContext(), mContentItem.getSelectableItems(), this);
            mainLayoutManager = new GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false);
        } else if (mContentItem.getSelectableRowItems() != null && mContentItem.getSelectableRowItems().size() > 0) {
            mainAdapter = new ContentRowSelectableAdapter(getContext(), mContentItem.getSelectableRowItems(), this);
            mainLayoutManager = new LinearLayoutManager(getContext());
        } else {
            mainAdapter = new ContentRowSelectableAdapter(getContext(), new ArrayList<>(), this);
            mainLayoutManager = new LinearLayoutManager(getContext());
        }

        if (mContentItem.getExpandableItems() != null && mContentItem.getExpandableItems().size() > 0) {
            RecyclerView.Adapter expandableAdapter = new ContentRowExpandableAdapter(getContext(), mContentItem.getExpandableItems(), this, this);

            final Parcelable eimSavedState = (savedInstanceState != null) ? savedInstanceState.getParcelable(SAVED_STATE_EXPANDABLE_ITEM_MANAGER) : null;
            mRecyclerViewExpandableItemManager = new RecyclerViewExpandableItemManager(eimSavedState);
            mRecyclerViewExpandableItemManager.setOnGroupExpandListener(this);
            mRecyclerViewExpandableItemManager.setOnGroupCollapseListener(this);

            secondaryAdapter = mRecyclerViewExpandableItemManager.createWrappedAdapter(expandableAdapter);
            GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();
            animator.setSupportsChangeAnimations(false);
            binding.rvSecondary.setItemAnimator(animator);
            binding.rvSecondary.setHasFixedSize(false);
            mRecyclerViewExpandableItemManager.attachRecyclerView(binding.rvSecondary);

            secondaryLayoutManager = new LinearLayoutManager(getContext());
        }

        // Set up the main RecyclerView
        mHeaderFooterAdapter = getHeaderFooterAdapter(mainAdapter);
        binding.rvMain.setLayoutManager(mainLayoutManager);
        binding.rvMain.setAdapter(mHeaderFooterAdapter);
        binding.rvMain.setNestedScrollingEnabled(false);

        // Set up the secondary RecyclerView, if applicable
        if (secondaryAdapter != null) {
            binding.rvSecondary.setLayoutManager(secondaryLayoutManager);
            binding.rvSecondary.setAdapter(secondaryAdapter);
            binding.rvSecondary.setNestedScrollingEnabled(false);
        } else {
            binding.rvSecondary.setVisibility(View.GONE);
        }

        if (mExpandContentItem != null) {
            Integer index = -1;
            for (int i = 0; i < mContentItem.getExpandableItems().size(); i++) {
                ContentItem contentItem = mContentItem.getExpandableItems().get(i);
                if (contentItem.getId().equals(mExpandContentItem.getId())) {
                    index = i;
                    break;
                }
            }

            if (index != -1) {
                mRecyclerViewExpandableItemManager.expandGroup(index);

                final int indexFinal = index;
                final Handler handler = new Handler();
                handler.postDelayed(() -> binding.rvSecondary.smoothScrollToPosition(indexFinal + 1), 250);
                binding.rvSecondary.scrollToPosition(mContentItem.getExpandableItems().size() - 1);
            }
        }
    }

    protected AbstractHeaderFooterWrapperAdapter getHeaderFooterAdapter(RecyclerView.Adapter adapter) {
        ContentHeaderFooterAdapter headerFooterAdapter = new ContentHeaderFooterAdapter(adapter, mContentItem, this, this);
        return headerFooterAdapter;
    }

    @Override
    public void gridRowSelected(int position) {
        ContentItem item = mContentItem.getSelectableItems().get(position);
        showContentItem(item);
    }

    @Override
    public void rowSelected(int position) {
        ContentItem item = mContentItem.getSelectableRowItems().get(position);
        showContentItem(item);
    }

    @Override
    public void rowExpandableSelected(int position) {

    }

    @Override
    public void onGroupExpand(final int groupPosition, boolean fromUser, Object payload) {
        if (mLastIndexExpanded != null && mLastIndexExpanded != groupPosition) {
            mRecyclerViewExpandableItemManager.collapseGroup(mLastIndexExpanded);
        }
        mLastIndexExpanded = groupPosition;

        if (fromUser) {
            adjustScrollPositionOnGroupExpanded(groupPosition);
        }
    }

    @Override
    public void onGroupCollapse(int groupPosition, boolean fromUser, Object payload) {
    }

    private void adjustScrollPositionOnGroupExpanded(int groupPosition) {
        int childItemHeight = getActivity().getResources().getDimensionPixelSize(R.dimen.content_cells_height);
        int topMargin = (int) (getActivity().getResources().getDisplayMetrics().density * 50); // top-spacing: 16dp
        int bottomMargin = (int) (getActivity().getResources().getDisplayMetrics().density * 300); // top-spacing: 16dp

        AdapterPath path = new AdapterPath();

        path.append(new AdapterPathSegment(binding.rvMain.getAdapter(), null));
        path.append(mHeaderFooterAdapter.getWrappedAdapterSegment());

        mRecyclerViewExpandableItemManager.scrollToGroup(groupPosition, childItemHeight, topMargin, bottomMargin, path);
    }

    @Override
    public void heightChanged(int height) {
        mHeaderHeight = height;
    }
}
