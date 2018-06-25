package unroll.github.io.yourcollege.util

import okhttp3.Headers
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import unroll.github.io.yourcollege.bean.Course
import java.net.URL
import java.util.regex.Pattern

class ZSCEduUtil : HttpUtil() {

    private val baseUrl = "http://jwgl.zsc.edu.cn:90/(gqwfv0ynvlvbwz4524mde455)"
    private val loginUrl = "$baseUrl/default2.aspx"
    val codeImgUrl = "$baseUrl/CheckCode.aspx"

    // 获得asp隐藏表单字段
    fun getViewStateFromLoginHtml(html: String): String {
        val regex = "[\\s\\S]*?<input[\\s\\S]*?name=['\"]__VIEWSTATE['\"][\\s\\S]*?value=['\"](\\S*?)['\"][\\s\\S]*?";
        val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        val matcher = pattern.matcher(html.replace(" ", ""));
        return if (matcher.find()) matcher.group(1) else ""
    }

    // 获得课表页url
    fun getTimetableUrlFromIndexHtml(html: String): String {
        val regex = "[\\s\\S]*?<a\\s?[^<>]*?href\\s?=\\s?['\"]([^<>\\s]*?)['\"][^<>]*?>学生个人课表</a>[\\s\\S]*?"
        val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        val matcher = pattern.matcher(html.replace(" ", ""));
        return if (matcher.find()) "$baseUrl/${matcher.group(1)}" else ""
    }

    // 获得登陆页html
    fun getLoginHtml(): String {
        val response = doGet(URL(loginUrl))
        return readResponseBodyString(response)
    }

    // 获得课表页请求头Referer字段
    private fun getTimetableRefererUrlFromTimetableUrl(url: URL): String {
        val regex = "([^&]*?)&[\\s\\S]*";
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(url.toString())
        return if (matcher.find()) matcher.group(1) else "";
    }

    // 获得课表页html
    fun getTimetableHtmlFromUrl(url: URL): String {
        val headers = Headers.Builder()
                .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                .add("Accept-Encoding", "gzip, deflate, br")
                .add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36")
                .add("Referer", "${getTimetableRefererUrlFromTimetableUrl(url)}")
                .build()!!
        var timetableHtml = readResponseBodyString(doGet(url, headers));
        return timetableHtml;
    }

    // 获得登陆的response
    fun login(account: String, password: String, code: String, state: String): Response {
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

        return doPost(URL(loginUrl), formatterPostMapData(data), headers);
    }

    // 根据课表页获得课程
    fun formatterCourseTimetableHtmlToCourseList(html: String): ArrayList<Course> {
        val courseList = ArrayList<Course>()
        val courseRegex = Regex("\\s*?(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s*?")
        val document: Document = Jsoup.parse(html)
        val elements = document.getElementById("Table1").getElementsByTag("td")
        for (el in elements) {
            val pattern = courseRegex.toPattern()
            val matcher = pattern.matcher(el.text())
            if (matcher.find()) {
                val course = Course()
                course.name = matcher.group(1)
                course.time = matcher.group(2)
                course.teacher = matcher.group(3)
                course.room = matcher.group(4)
                course.weekList = getCourseWeekListFromTimeInfo(course.time!!)
                course.start = getCourseStartFromTimeInfo(course.time!!)
                course.step = getCourseStepFromTimeInfo(course.time!!)
                course.dayOfWeek = getCourseDayOfWeekFromTimeInfo(course.time!!)
                courseList.add(course)
            }
        }
        return courseList
    }

    private fun getCourseWeekListFromTimeInfo(info: String): List<Int> {
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

    private fun getCourseStartFromTimeInfo(info: String): Int {
        val regex = Regex("[^\\d]*(\\d+)[\\s\\S]*?\\,?(\\d+)?[^\\d]+?\\{")
        val matcher = regex.toPattern().matcher(info)
        if (matcher.find()) {
            return matcher.group(1).toInt()
        } else throw IllegalArgumentException();
    }

    private fun getCourseStepFromTimeInfo(info: String): Int {
        val regex = Regex("[^\\d]*(\\d+)[\\s\\S]*?\\,?(\\d+)?[^\\d]+?\\{")
        val matcher = regex.toPattern().matcher(info)
        if (matcher.find()) {
            val start = matcher.group(1).toInt()
            val end = if (matcher.group(2).isNullOrEmpty()) start else matcher.group(2).toInt()
            return 1 + end - start
        } else throw IllegalArgumentException();
    }

    private fun getCourseDayOfWeekFromTimeInfo(info: String): Int {
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
}
