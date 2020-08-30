package com.dreamxu.wrapperrecycleview.contract

import com.dreamxu.wrapperrecycleview.bean.GankResponse
import com.dreamxu.wrapperrecycleview.bean.NewsEntity
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.properties.Delegates

class GankPresenter(view: GankListener.View, titleType: String): GankListener.Presenter {
    private val TAG = "GankPresenter"

    private val retrofit: Retrofit
    private val mListData: ArrayList<NewsEntity> = ArrayList()
    private val mType: String
    private val mView: GankListener.View
    private val mUrlService: GankURLService
    private var mCurrentPage by Delegates.notNull<Int>()

    init {
        val client = OkHttpClient.Builder()
            .addInterceptor(LogInterceptor())
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(GankURLService.BASE_URL)
            .client(client)
            .addConverterFactory(NullOrEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        mUrlService = retrofit.create(GankURLService::class.java)
        mType = titleType
        mView = view
    }

    override fun onViewCreate() {
        mCurrentPage = 1
        loadData()
    }

    override fun startRefresh() {
        mCurrentPage = 1
        loadData()
    }

    override fun startLoadMore() {
        mCurrentPage++
        loadData()
    }

    private fun loadData() {
        mUrlService.requestData(mType, mCurrentPage, 20)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<GankResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: GankResponse) {
                    if (mCurrentPage == 1) {
                        mListData.clear()
                    }

                    mListData.addAll(t.result.subList(0, 10))
                    mView.setListData(mListData, mType)

                    if (mCurrentPage == 1) {
                        mView.stopRefresh()
                    } else {
                        mView.stopLoadMore()
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    mView.onInitLoadFailed()
                }
                override fun onComplete() {
                }
            })
    }
}
