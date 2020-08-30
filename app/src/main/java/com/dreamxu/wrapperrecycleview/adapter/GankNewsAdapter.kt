package com.dreamxu.wrapperrecycleview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.dreamxu.wrapperrecycleview.R
import com.dreamxu.wrapperrecycleview.bean.NewsEntity

class GankNewsAdapter(private val listData: List<NewsEntity>, private val typeFrag: String):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_TYPE_LOADMORE = 1 shl 12
    private var mOnFooterClickListener: OnFooterClickListener? = null
    private var loadMoreViewHolder: LoadMoreViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_LOADMORE -> {
                loadMoreViewHolder = mOnFooterClickListener?.let { LoadMoreViewHolder(parent, it) }
                loadMoreViewHolder as LoadMoreViewHolder
            }
            else -> {
                val itemView = if (typeFrag == "Android") {
                    LayoutInflater.from(parent.context).inflate(R.layout.row_gank_news_android, parent, false)
                } else {
                    LayoutInflater.from(parent.context).inflate(R.layout.row_gank_news_ios, parent, false)
                }
                GankNewsViewHolder(itemView)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GankNewsViewHolder) {
            val news = listData[position]
            holder.bindData(news)
        }

    }

    override fun getItemViewType(position: Int): Int {
        if (position == itemCount -1)
            return ITEM_TYPE_LOADMORE
        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int = listData.size + 1

    fun setOnFooterClickListener(listener: OnFooterClickListener) {
        mOnFooterClickListener = listener
    }

    fun changeLoadMoreState(state: Boolean) {
        if (loadMoreViewHolder == null) return
        loadMoreViewHolder?.changeLoadMoreState(state)
    }

    fun changeLoadMoreVisibility(show: Boolean) {
        if (loadMoreViewHolder == null) return
        loadMoreViewHolder?.changeLoadMoreVisibility(show)
    }

    inner class LoadMoreViewHolder(parent: ViewGroup, listener: OnFooterClickListener) :
        RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.row_footer_load_more, parent, false)) {

        private val progressBar: ProgressBar = itemView.findViewById(R.id.news_load_more)
        private val footerClickListener = listener

        init {
            changeLoadMoreState(false)
            changeLoadMoreVisibility(false)
            itemView.setOnClickListener{
                footerClickListener.onLoadMoreNewsClick()
            }
        }

        fun changeLoadMoreState(loading: Boolean) {
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        fun changeLoadMoreVisibility(show: Boolean) {
            itemView.visibility = if (show) View.VISIBLE else View.GONE
        }
    }

    interface OnFooterClickListener {
        fun onLoadMoreNewsClick()
    }

    inner class GankNewsViewHolder(view: View): RecyclerView.ViewHolder(view) {

        @Nullable
        @BindView(R.id.android_item_title)
        lateinit var androidTitle: TextView

        @Nullable
        @BindView(R.id.android_item_subtitle)
        lateinit var androidSubTitle: TextView

        @Nullable
        @BindView(R.id.android_item_image)
        lateinit var androidImage: ImageView

        @Nullable
        @BindView(R.id.ios_item_title)
        lateinit var iosTitle: TextView

        init {
            ButterKnife.bind(this, view)
        }

        fun bindData(news: NewsEntity) {
            if (typeFrag == "Android") {
                androidTitle.text = news.desc
                androidSubTitle.text = news.publishedAt
                val images: List<String> = news.images
                if (images.isNullOrEmpty()) {
                    androidImage.visibility = View.GONE
                } else {
                    androidImage.visibility = View.VISIBLE
                    Glide.with(itemView).asBitmap().load(images[0]).into(androidImage)
                }
            } else {
                iosTitle.text = news.desc
            }

        }
    }
}