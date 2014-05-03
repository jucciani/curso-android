package com.ar.listener;

import android.widget.AbsListView;

import com.ar.activity.ListItemsActivity;
import com.ar.activity.SearchFragment;

/**
 * Created by jucciani on 24/04/14.
 */
public class EndlessScrollListener implements AbsListView.OnScrollListener{
    private boolean loading = true;

    private SearchFragment activity;

    public EndlessScrollListener(SearchFragment activity) {
        this.activity = activity;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
        if (!(loading) && (totalItemCount - visibleItemCount) <= (firstVisibleItem)) {
           activity.searchItems();
           loading = true;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {}

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

}