package com.smartalk.gank.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.smartalk.gank.R;
import com.smartalk.gank.model.entity.Meizi;
import com.smartalk.gank.presenter.MainPresenter;
import com.smartalk.gank.ui.adapter.MeiziAdapter;
import com.smartalk.gank.ui.base.BaseActivity;
import com.smartalk.gank.view.IMainView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements
        SwipeRefreshLayout.OnRefreshListener , IMainView,MeiziAdapter.TouchMeiziListener{

    private List<Meizi> meizis;
    private MeiziAdapter adapter;
    private MainPresenter presenter;
    private int page = 1;
    private boolean isRefresh = true;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.rv_gank)
    RecyclerView rvGank;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        presenter = new MainPresenter(this,this);
        presenter.fetchMeiziData(page);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        page = 1;
        presenter.fetchMeiziData(page);
    }

    @Override
    public void initMainView() {
        setSupportActionBar(toolbar);
        swipeRefreshLayout.setColorSchemeResources(R.color.yellow,R.color.red,R.color.blue);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        rvGank.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isSlidingToBottom;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                isSlidingToBottom = dy > 0;
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    int lastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = layoutManager.getItemCount();
                    if (lastVisibleItem == (totalItemCount-1) && isSlidingToBottom){
                        page++;
                        presenter.fetchMeiziData(page);
                    }
                }
            }
        });
    }

    @Override
    public void showProgress() {
        if (!swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void showMeiziList(List<Meizi> meiziList) {
        if (isRefresh){
            if (meizis == null){
                meizis = meiziList;
                adapter = new MeiziAdapter(this,meizis);
                adapter.setListener(this);
                rvGank.setLayoutManager(new LinearLayoutManager(this));
                rvGank.setAdapter(adapter);
            }else {
                meizis.clear();
                meizis.addAll(meiziList);
                adapter.notifyDataSetChanged();
            }
            isRefresh = false;
        }else {
            meizis.addAll(meiziList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onMeiziClick() {
        //startActivity(new Intent(MainActivity.this,GankActivity.class));
    }
}