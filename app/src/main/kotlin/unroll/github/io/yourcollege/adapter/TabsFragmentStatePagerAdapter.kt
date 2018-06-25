package unroll.github.io.yourcollege.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.widget.DialogTitle

class TabsFragmentStatePagerAdapter : FragmentStatePagerAdapter {

    private var fragments: List<Fragment>? = null;
    private var titles: List<String>? = null;

    constructor(fragmentManager: FragmentManager, fragments: List<Fragment>, titles: List<String>) : super(fragmentManager) {
        this.fragments = fragments;
        this.titles = titles;
    }

    override fun getItem(position: Int): Fragment {
        return fragments!![position]
    }

    override fun getCount(): Int {
        return fragments!!.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles!![position]
    }
}