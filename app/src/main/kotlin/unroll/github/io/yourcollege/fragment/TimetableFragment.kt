package unroll.github.io.yourcollege.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhuangfei.timetable.TimetableView
import com.zhuangfei.timetable.model.Schedule
import org.litepal.LitePal
import unroll.github.io.yourcollege.App
import unroll.github.io.yourcollege.R
import unroll.github.io.yourcollege.bean.Course
import unroll.github.io.yourcollege.fragment.base.BaseFragment
import java.util.*

class TimetableFragment : BaseFragment() {
    object D {
        val SharedPreferencesName = "unroll.github.io.yourcollege"
    }

    lateinit var timetable: TimetableView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_timetable, container, false)
    }

    override fun onStart() {
        super.onStart()
        timetable = view!!.findViewById(R.id.timetable)
        loadData()
    }

    private fun loadData() {
        val group = App.D.context.getSharedPreferences(D.SharedPreferencesName, Context.MODE_PRIVATE).getString("group", "")
        var first = App.D.context.getSharedPreferences(D.SharedPreferencesName, Context.MODE_PRIVATE).getInt("first", -1)
        val calendar = Calendar.getInstance()
        if (first < 0) {
            first = calendar.get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_WEEK)
            App.D.context.getSharedPreferences("unroll.github.io.yourcollege", Context.MODE_PRIVATE).edit().putInt("first", first).apply()
        }
        val week = Math.round((calendar.get(Calendar.DAY_OF_YEAR) - first) / 7F)
        val courseList = LitePal.where("group = ?", group).find(Course::class.java)
        var data: ArrayList<Schedule> = ArrayList()
        for (course in courseList) data.add(course.schedule)
        timetable.setData(data).setCurWeek(if (week < 0) 1 else week).showView()
    }
}