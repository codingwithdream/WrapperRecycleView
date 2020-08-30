package com.dreamxu.wrapperrecycleview.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.dreamxu.wrapperrecycleview.GankFragment

class GankViewPagerAdapter(
    fragmentManager: FragmentManager, titleList: List<String>) : FragmentPagerAdapter(fragmentManager) {

    private val mTabTitleList: List<String> = titleList

    override fun getItem(position: Int): Fragment {
        return GankFragment.newInstance(mTabTitleList[position])
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mTabTitleList[position]
    }

    override fun getCount(): Int = mTabTitleList.size
}
