package unroll.github.io.yourcollege.bean

import com.zhuangfei.timetable.model.Schedule
import com.zhuangfei.timetable.model.ScheduleEnable
import unroll.github.io.yourcollege.bean.base.BaseBean
import java.util.*


class Course : BaseBean, ScheduleEnable {

    var id: Int = 0
    lateinit var group: String
    lateinit var name: String    // 课程名
    lateinit var time: String    // 上课时间
    lateinit var room: String    // 课室
    lateinit var teacher: String // 老师
    lateinit var weekList: List<Int> // 上课的周列表
    var start: Int = 0  // 开始上课的节次
    var step: Int = 0   // 上课的节数
    var dayOfWeek: Int = 0  // 星期几上课
    var colorRandom = 0 // 颜色相关

    constructor() {}

    constructor(group: String, name: String, time: String, room: String, teacher: String, weekList: List<Int>, start: Int, step: Int, dayOfWeek: Int) {
        this.group = group
        this.name = name
        this.time = time
        this.room = room
        this.teacher = teacher
        this.weekList = weekList
        this.start = start
        this.step = step
        this.dayOfWeek = dayOfWeek
        this.colorRandom = ColorRandom.color()
    }

    override fun getSchedule(): Schedule {
        val schedule = Schedule()
        schedule.day = dayOfWeek
        schedule.name = name
        schedule.room = room
        schedule.start = start
        schedule.step = step
        schedule.teacher = teacher
        schedule.weekList = weekList
        schedule.colorRandom = ColorRandom.color()
        return schedule
    }

    object ColorRandom {
        private val random = Random()
        fun color(): Int {
            return random.nextInt()
        }
    }

    fun exceptStartAndStepEquals(other: Course): Boolean {
        return name.equals(other.name) && room.equals(other.room) && teacher.equals(other.teacher) &&
                weekList.equals(other.weekList) && dayOfWeek.equals(dayOfWeek)
    }
}