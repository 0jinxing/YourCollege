package unroll.github.io.yourcollege.util

import okhttp3.Headers
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import unroll.github.io.yourcollege.bean.Course
import java.net.URL
import java.util.*
import java.util.regex.Pattern

class ZSCEduUtil : HttpUtil() {

    private val baseUrl = "http://jwgl.zsc.edu.cn:90/(eggut255qqlxowrzufgmbhvj)"
    private val loginUrl = "$baseUrl/default2.aspx"
    val codeImgUrl = "$baseUrl/CheckCode.aspx"

    var state: String = ""
        get() {
            val regex = "[\\s\\S]*?<input[\\s\\S]*?name=['\"]__VIEWSTATE['\"][\\s\\S]*?value=['\"](\\S*?)['\"][\\s\\S]*?";
            val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            val matcher = pattern.matcher(loginHtml.replace(" ", ""));
            return if (matcher.find()) matcher.group(1) else ""
        }

    var loginHtml: String = ""
        get() {
            val response = doGet(URL(loginUrl))
            return readResponseBodyString(response)
        }

    fun gainTimetableUrl(html: String): String {
        val regex = "[\\s\\S]*?<a\\s?[^<>]*?href\\s?=\\s?['\"]([^<>\\s]*?)['\"][^<>]*?>学生个人课表</a>[\\s\\S]*?"
        val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        val matcher = pattern.matcher(html.replace(" ", ""));
        return if (matcher.find()) "$baseUrl/${matcher.group(1)}" else ""
    }

    fun gainTimetableUrl(account: String, password: String, code: String): String {
        return gainTimetableUrl(gainIndexHtml(account, password, code))
    }

    fun gainTimetableRefererUrl(url: URL): String {
        return gainTimetableRefererUrl(url.toString())
    }

    fun gainTimetableRefererUrl(url: String): String {
        val regex = "([^&]*?)&[\\s\\S]*";
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(url)
        return if (matcher.find()) matcher.group(1) else "";
    }

    fun gainTimetableHtml(url: URL): String {
        val headers = Headers.Builder()
                .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                .add("Accept-Encoding", "gzip, deflate, br")
                .add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36")
                .add("Referer", "${gainTimetableRefererUrl(url)}")
                .build()!!
        var timetableHtml = readResponseBodyString(doGet(url, headers));
        return timetableHtml;
    }

    fun gainTimetableHtml(url: String): String {
        return gainTimetableHtml(URL(url))
    }

    fun gainTimetableHtml(account: String, password: String, code: String): String {
        return gainTimetableHtml(URL(gainTimetableUrl(account, password, code)))
    }

    fun gainIndexHtml(account: String, password: String, code: String): String {
        val data: HashMap<String, String> = HashMap();
        data["__VIEWSTATE"] = state;
        data["txtUserName"] = account;
        data["Textbox1"] = "";
        data["TextBox2"] = password;
        data["txtSecretCode"] = code;
        data["Button1"] = ""

        val headers = Headers.Builder()
                .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                .add("Accept-Encoding", "gzip, deflate, br")
                .add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36")
                .add("Content-Type", "application/x-www-form-urlencoded")
                .add("Referer", loginUrl)
                .build()

        return readResponseBodyString(doPost(URL(loginUrl), formatterPostMapData(data), headers))
    }

    fun gainCourseList(account: String, password: String, code: String): ArrayList<Course> {
        return gainCourseList(gainTimetableHtml(account, password, code))
    }

    fun gainCourseList(html: String): ArrayList<Course> {
        val uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        val courseList = ArrayList<Course>()
        val courseRegex = Regex("\\s*?(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s*?")
        val document: Document = Jsoup.parse(html)
        val elements = document.getElementById("Table1").getElementsByTag("td")
        for (el in elements) {
            val pattern = courseRegex.toPattern()
            val matcher = pattern.matcher(el.text())
            if (matcher.find()) {
                val name = matcher.group(1)
                val time = matcher.group(2)
                val teacher = matcher.group(3)
                val room = matcher.group(4)
                val weekList = gainCourseWeekListFromTimeInfo(time)
                val start = gainCourseStartFromTimeInfo(time)
                val step = gainCourseStepFromTimeInfo(time)
                val dayOfWeek = getCourseDayOfWeekFromTimeInfo(time)
                val course = Course(uuid, name, time, room, teacher, weekList, start, step, dayOfWeek)
                courseList.add(course)
            }
        }
        // 部分课程的合并
        courseList.sortBy {
            it.dayOfWeek * 12 + it.start
        }
        var result = ArrayList<Course>()
        var preCourse: Course? = null
        for (i in 0 until courseList.size) {
            val curCourse = courseList[i]
            preCourse = if (preCourse == null) {
                curCourse
            } else if (preCourse.exceptStartAndStepEquals(curCourse) && preCourse.start + preCourse.step == curCourse.start) {
                merge(preCourse, curCourse)
            } else {
                result.add(preCourse)
                curCourse
            }
            if (i == courseList.size - 1) result.add(preCourse)
        }
        return result
    }

    fun gainCourseWeekListFromTimeInfo(info: String): List<Int> {
        val weekList = ArrayList<Int>()
        val regex = Regex("[\\s\\S]*?\\{\\S*?(\\d+)-?(\\d*)\\S*?(\\|\\S*)?\\}[\\s\\S]*?");

        val matcher = regex.toPattern().matcher(info)
        if (matcher.find()) {
            val startWeek = matcher.group(1).toInt()
            val endtWeek = if (matcher.group(2).isNullOrEmpty()) startWeek else matcher.group(2).toInt()
            val weekStep = if (matcher.group(3).isNullOrEmpty()) 1 else 2
            weekList.addAll(startWeek..endtWeek step weekStep)
        }
        return weekList
    }

    fun gainCourseStartFromTimeInfo(info: String): Int {
        val regex = Regex("[^\\d]*(\\d+)[\\s\\S]*?\\,?(\\d+)?[^\\d]+?\\{")
        val matcher = regex.toPattern().matcher(info)
        if (matcher.find()) {
            return matcher.group(1).toInt()
        } else throw IllegalArgumentException();
    }

    fun gainCourseStepFromTimeInfo(info: String): Int {
        val regex = Regex("[^\\d]*(\\d+)[\\s\\S]*?\\,?(\\d+)?[^\\d]+?\\{")
        val matcher = regex.toPattern().matcher(info)
        if (matcher.find()) {
            val start = matcher.group(1).toInt()
            val end = if (matcher.group(2).isNullOrEmpty()) start else matcher.group(2).toInt()
            return 1 + end - start
        } else throw IllegalArgumentException();
    }

    fun getCourseDayOfWeekFromTimeInfo(info: String): Int {
        when {
            (info.contains("周一")) -> return 1
            (info.contains("周二")) -> return 2
            (info.contains("周三")) -> return 3
            (info.contains("周四")) -> return 4
            (info.contains("周五")) -> return 5
            (info.contains("周六")) -> return 6
            (info.contains("周日")) -> return 7
        }
        throw IllegalArgumentException()
    }

    private fun merge(course: Course, course2: Course): Course {
        var minCourse = if (course.start - course2.start < 0) course else course2
        var maxCourse = if (course.start - course2.start < 0) course2 else course
        if (course.exceptStartAndStepEquals(course2) && minCourse.start + minCourse.step != maxCourse.start) IllegalArgumentException()
        minCourse.step += maxCourse.step
        return minCourse
    }
}
