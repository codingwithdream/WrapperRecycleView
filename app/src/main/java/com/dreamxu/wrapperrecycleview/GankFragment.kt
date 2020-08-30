package com.dreamxu.wrapperrecycleview

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.dreamxu.wrapperrecycleview.adapter.GankNewsAdapter
import com.dreamxu.wrapperrecycleview.bean.NewsEntity
import com.dreamxu.wrapperrecycleview.contract.GankListener
import com.dreamxu.wrapperrecycleview.contract.GankPresenter

class GankFragment: Fragment(), GankListener.View, SwipeRefreshLayout.OnRefreshListener {

    private val TAG = "GankFragment"
    private var mState = STATE_IDLE
    private var fragType: String? = null
    private var newsAdapter: GankNewsAdapter? = null
    private lateinit var newsPresenter: GankPresenter

    @BindView(R.id.gank_recycler_view)
    lateinit var newsRecyclerView: RecyclerView

    @BindView(R.id.gank_load_failed)
    lateinit var newsLoadFailed: TextView

    @BindView(R.id.gank_swipe_refresh)
    lateinit var newsSwipeRefresh: SwipeRefreshLayout

    @Nullable
    @BindView(R.id.load_more_layout)
    lateinit var loadMoreLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            fragType = it.getString(FRAGMENT_TYPE_KEY)
        }
        newsPresenter = GankPresenter(this, fragType!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ButterKnife.bind(this, view)

        newsSwipeRefresh.setOnRefreshListener(this)
        newsRecyclerView.layoutManager = LinearLayoutManager(activity)
        startRefreshing()
        newsPresenter.onViewCreate()
    }

    override fun setListData(newsList: List<NewsEntity>, type: String) {
        Log.d(TAG, "Notify data set changed")
        if (newsAdapter == null) {
            newsAdapter = GankNewsAdapter(newsList, type)
            newsRecyclerView.adapter = newsAdapter
            newsRecyclerView.addOnScrollListener(ScrollListener(newsRecyclerView, newsAdapter!!))
            newsAdapter?.setOnFooterClickListener(object : GankNewsAdapter.OnFooterClickListener{
                override fun onLoadMoreNewsClick() {
                    switchState(STATE_LOADINGMORE)
                    newsPresenter.startLoadMore()
                }
            })
        } else {
            newsAdapter?.notifyDataSetChanged()
        }
    }

    override fun onInitLoadFailed() {
        newsRecyclerView.visibility = View.GONE
        newsLoadFailed.visibility = View.VISIBLE
    }

    // refreshing finish
    override fun stopRefresh() {
        if (mState != STATE_REFRESHING) return
        switchState(STATE_IDLE)
    }


    override fun stopLoadMore() {
        if (mState != STATE_LOADINGMORE) return
        switchState(STATE_IDLE)
    }

    override fun onRefresh() {
        if (mState != STATE_IDLE) {
            if (mState == STATE_REFRESHING) {
                newsSwipeRefresh.isRefreshing = false
            }
            return
        }
        switchState(STATE_REFRESHING)
        newsPresenter.startRefresh()
    }

    private fun startRefreshing() {
        Log.d(TAG, "enter the fragment, start refreshing")
        if (mState != STATE_IDLE) return
        switchState(STATE_REFRESHING)
    }

    private fun switchState(newState: Int) {
        // out from old state
        if (mState == STATE_LOADINGMORE) {
            newsAdapter?.changeLoadMoreState(false)
            newsAdapter?.changeLoadMoreVisibility(false)
        } else if (mState == STATE_REFRESHING) {
            newsSwipeRefresh.isRefreshing = false
        }

        // into new state
        if (newState == STATE_REFRESHING) {
            newsSwipeRefresh.isRefreshing = true
        } else if (newState == STATE_LOADINGMORE) {
            newsAdapter?.changeLoadMoreState(true)
            newsAdapter?.changeLoadMoreVisibility(true)
        }
        mState = newState
    }

    inner class ScrollListener(
        recyclerView: RecyclerView,
        private val adapter: GankNewsAdapter): RecyclerView.OnScrollListener() {

        private val manager = recyclerView.layoutManager

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (manager !is LinearLayoutManager || mState != STATE_IDLE)
                return
            val lastVisiblePosition = manager.findLastVisibleItemPosition()
            val lastCompletePosition = manager.findLastCompletelyVisibleItemPosition()
            val newState = recyclerView.scrollState
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                if (lastCompletePosition == adapter.itemCount - 1 || lastVisiblePosition == adapter.itemCount -1) {
                    adapter.changeLoadMoreVisibility(true)
                    adapter.changeLoadMoreState(false)
                }
            }

        }
    }

    companion object {

        private const val FRAGMENT_TYPE_KEY = "Gank_Fragment_Type_Key"
        private const val STATE_IDLE = 0
        private const val STATE_REFRESHING = 1
        private const val STATE_LOADINGMORE = 2

        @JvmStatic
        fun newInstance(fragType: String) = GankFragment().apply {
            arguments = Bundle().apply {
                putString(FRAGMENT_TYPE_KEY, fragType)
            }
        }
    }
}