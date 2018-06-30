package unroll.github.io.yourcollege.activity

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ListHolder
import com.orhanobut.dialogplus.ViewHolder
import com.zhuangfei.timetable.TimetableView
import com.zhuangfei.timetable.model.Schedule
import org.litepal.LitePal
import unroll.github.io.yourcollege.App
import unroll.github.io.yourcollege.R
import unroll.github.io.yourcollege.activity.base.BaseActivity
import unroll.github.io.yourcollege.TabsFragmentStatePagerAdapter
import unroll.github.io.yourcollege.bean.Course
import unroll.github.io.yourcollege.fragment.LibraryFragment
import unroll.github.io.yourcollege.fragment.TimetableFragment
import unroll.github.io.yourcollege.task.ImportTimetableTask
import unroll.github.io.yourcollege.util.ZSCEduUtil
import java.util.*


class MainActivity : BaseActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var tabs: TabLayout
    private lateinit var pager: ViewPager
    private lateinit var drawer: DrawerLayout
    private lateinit var navigation: NavigationView

    private lateinit var timeTableFragment: TimetableFragment
    private lateinit var libraryFragment: LibraryFragment

    private val zscEduUtil = ZSCEduUtil()

    override var layoutResID: Int? = R.layout.activity_main

    override fun afterCreate() {
        super.afterCreate()

        toolbar = findViewById(R.id.toolbar)
        tabs = findViewById(R.id.tabs)
        pager = findViewById(R.id.pager)
        drawer = findViewById(R.id.drawer)
        navigation = findViewById(R.id.navigation)

        timeTableFragment = TimetableFragment()
        libraryFragment = LibraryFragment()

        initBar()
        initTabs()
        initNavigationView()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            (android.R.id.home) -> drawer.openDrawer(GravityCompat.START)
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
            tabs.addTab(tabs.newTab().setText(tab))
        }

        val fragments = ArrayList<Fragment>()
        fragments.add(timeTableFragment)
        fragments.add(libraryFragment)

        val pagerAdapter = TabsFragmentStatePagerAdapter(supportFragmentManager, fragments, titles)
        pager.adapter = pagerAdapter;
        tabs.setupWithViewPager(pager)
    }

    private fun initNavigationView() {
        navigation.setNavigationItemSelectedListener {
            when (it.itemId) {
                (R.id.import_timetable) -> {
                    showImportLoginDialog()
                }
                (R.id.select_week) -> {
                    showSelectWeekDialog()
                }
            }
            drawer.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun loadCodeImage(view: View, path: String, imageView: ImageView) {
        Glide.with(view)
                .setDefaultRequestOptions(RequestOptions()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE))
                .load(path)
                .into(imageView)
    }

    private fun showImportLoginDialog() {
        val importDialog = DialogPlus.newDialog(that)
                .setExpanded(true, 1000)
                .setContentHeight(1700)
                .setContentHolder(ViewHolder(R.layout.dialog_import_timetable))
                .setPadding(14, 100, 14, 100)
                .setOnDismissListener {
                    val imm = this@MainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(that.currentFocus.windowToken, 0)
                }
                .create()
        val codeImage = importDialog.holderView.findViewById<ImageView>(R.id.code_img)
        loadCodeImage(importDialog.holderView, zscEduUtil.codeImgUrl, codeImage)
        codeImage.setOnClickListener {
            loadCodeImage(importDialog.holderView, zscEduUtil.codeImgUrl, codeImage)
        }

        val login = importDialog.holderView.findViewById<Button>(R.id.login)

        login.setOnClickListener {
            val account = importDialog.holderView.findViewById<EditText>(R.id.account).text.toString()
            val password = importDialog.holderView.findViewById<EditText>(R.id.password).text.toString()
            val code = importDialog.holderView.findViewById<EditText>(R.id.code).text.toString()
            if (account.isNullOrEmpty() || password.isNullOrEmpty() || code.isNullOrEmpty()) {
                Toast.makeText(App.D.context, "请输入完整", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            ImportTimetableTask(account, password, code, timeTableFragment.timetable).execute()
            importDialog.dismiss()
        }
        importDialog.show()
    }

    private fun showSelectWeekDialog() {
        val weekList = ArrayList<Map<String, String>>()
        for (i in 1..25) {
            weekList.add(mapOf(Pair("name", "第${i}周")))
        }
        val dialog = DialogPlus.newDialog(that)
                .setExpanded(true)
                .setContentHolder(ListHolder())
                .setAdapter(
                        SimpleAdapter(that,
                                weekList,
                                android.R.layout.simple_expandable_list_item_1,
                                arrayOf("name"),
                                intArrayOf(android.R.id.text1)))
                .setOnItemClickListener { dialog, item, view, position ->
                    val calendar = Calendar.getInstance()
                    val first = calendar.get(Calendar.DAY_OF_YEAR) - position * 7 - calendar.get(Calendar.DAY_OF_WEEK)
                    App.D.context.getSharedPreferences("unroll.github.io.yourcollege", Context.MODE_PRIVATE).edit().putInt("first", first).apply()
                    timeTableFragment.timetable.setCurWeek(position + 1).showView()
                    dialog.dismiss()
                }
                .create()
        dialog.show()
    }
}
