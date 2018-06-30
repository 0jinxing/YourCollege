package unroll.github.io.yourcollege.task

import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import com.zhuangfei.timetable.TimetableView
import com.zhuangfei.timetable.model.Schedule
import org.litepal.LitePal
import unroll.github.io.yourcollege.App
import unroll.github.io.yourcollege.bean.Course
import unroll.github.io.yourcollege.util.ZSCEduUtil

class ImportTimetableTask : AsyncTask<Void, Int, Boolean> {

    private var zscEduUtil = ZSCEduUtil()

    private var account: String
    private var password: String
    private var code: String

    private var timetable: TimetableView? = null

    lateinit var courseList: ArrayList<Course>
    lateinit var group: String

    constructor(account: String, password: String, code: String, timetableView: TimetableView? = null) : super() {
        this.account = account
        this.password = password
        this.code = code
        this.timetable = timetableView
    }

    override fun onPreExecute() {
    }

    override fun doInBackground(vararg p0: Void?): Boolean {
        try {
            courseList = zscEduUtil.gainCourseList(account, password, code)
        } catch (e: Exception) {
            return false
        }
        group = courseList.first().group
        return true
    }

    override fun onPostExecute(result: Boolean?) {
        if (result!!) {
            LitePal.saveAll(courseList)
            App.D.context.getSharedPreferences("unroll.github.io.yourcollege", Context.MODE_PRIVATE).edit().putString("group", group).apply()
            if (timetable != null) {
                val data = ArrayList<Schedule>()
                for (course in courseList) data.add(course.schedule)
                timetable!!.setData(data).showView()
            }
            Toast.makeText(App.D.context, "课表导入完成", Toast.LENGTH_SHORT).show()
        } else Toast.makeText(App.D.context, "课表导入失败，请重新尝试导入", Toast.LENGTH_SHORT).show()
    }
}