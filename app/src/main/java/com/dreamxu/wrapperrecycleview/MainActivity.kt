package com.dreamxu.wrapperrecycleview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import com.dreamxu.wrapperrecycleview.adapter.GankViewPagerAdapter

class MainActivity : AppCompatActivity() {

    private val fragmentTypeList: List<String> = mutableListOf("Android", "iOS")

    @BindView(R.id.gank_view_pager)
    lateinit var gankViewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ButterKnife.bind(this)

        gankViewPager.adapter = GankViewPagerAdapter(supportFragmentManager, fragmentTypeList)
    }
}