package com.dreamxu.wrapperrecycleview.contract

import com.dreamxu.wrapperrecycleview.bean.NewsEntity

interface GankListener {

    interface View {
        fun setListData(newsList: List<NewsEntity>, type: String)
        fun onInitLoadFailed()
        fun stopRefresh()
        fun stopLoadMore()
    }

    interface Presenter {
        fun onViewCreate()
        fun startRefresh()
        fun startLoadMore()
    }
}
