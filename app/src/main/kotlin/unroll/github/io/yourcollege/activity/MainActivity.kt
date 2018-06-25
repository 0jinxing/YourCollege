package unroll.github.io.yourcollege.activity

import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import unroll.github.io.yourcollege.R
import unroll.github.io.yourcollege.activity.base.BaseActivity
import unroll.github.io.yourcollege.adapter.TabsFragmentStatePagerAdapter
import unroll.github.io.yourcollege.fragment.LibraryFragment
import unroll.github.io.yourcollege.fragment.TimetableFragment

class MainActivity : BaseActivity() {

    private var toolbar: Toolbar? = null
    private var tabs: TabLayout? = null
    private var pager: ViewPager? = null
    private var drawer: DrawerLayout? = null
    private var navigation: NavigationView? = null

    private val that = this

    override var layoutResID: Int? = R.layout.activity_main

    override fun afterCreate() {
        super.afterCreate()
        toolbar = findViewById(R.id.toolbar)
        tabs = findViewById(R.id.tabs)
        pager = findViewById(R.id.pager)
        drawer = findViewById(R.id.drawer)
        navigation = findViewById(R.id.navigation)

        initBar()
        initTabs()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            (android.R.id.home) -> drawer!!.openDrawer(GravityCompat.START)
        }
        return true
    }

    private fun initBar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.abc_ic_menu_overflow_material)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun initTabs() {
        val titles = resources.getStringArray(R.array.tabs).toList()
        for (tab in titles) {
            tabs!!.addTab(tabs!!.newTab().setText(tab))
        }

        val fragments = ArrayList<Fragment>()
        fragments.add(TimetableFragment())
        fragments.add(LibraryFragment())

        val pagerAdapter = TabsFragmentStatePagerAdapter(supportFragmentManager, fragments, titles)
        pager!!.adapter = pagerAdapter;
        tabs!!.setupWithViewPager(pager)
    }
}
