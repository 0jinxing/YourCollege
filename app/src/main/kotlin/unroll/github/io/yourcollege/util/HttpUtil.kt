package unroll.github.io.yourcollege.util

import okhttp3.*
import java.net.URL
import java.net.URLEncoder


open class HttpUtil {

    private val client = OkHttpClient.Builder()
            .cookieJar(object : CookieJar {
                private var cookieStore = HashMap<String, List<Cookie>>()

                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                    cookieStore[url.host()] = cookies
                }

                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                    return cookieStore[url.host()] ?: ArrayList()
                }
            })
            .build();

    private val accept = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"
    private val acceptEncoding = "gzip, deflate, br"
    private val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36"


    private val defaultHeaders = Headers.Builder()
            .add("Accept", accept)
            .add("Accept-Encoding", acceptEncoding)
            .add("User-Agent", userAgent)
            .build()!!

    private val postFormHeaders = Headers.Builder()
            .add("Accept", accept)
            .add("Accept-Encoding", acceptEncoding)
            .add("User-Agent", userAgent)
            .add("Content-Type", "application/x-www-form-urlencoded")
            .build()!!

    private val postJsonHeaders = Headers.Builder()
            .add("Accept", accept)
            .add("Accept-Encoding", acceptEncoding)
            .add("User-Agent", userAgent)
            .add("Content-Type", "application/json")
            .build()!!

    fun doGet(url: URL, headers: Headers? = null): Response {
        var request = Request.Builder()
                .url(url)
                .headers(headers ?: defaultHeaders)
                .build()
        return client.newCall(request).execute()
    }

    fun doPost(url: URL, data: String, headers: Headers? = null): Response {
        var curHeaders = headers ?: postJsonHeaders;
        var requestBody = RequestBody.create(MediaType.parse(curHeaders["Content-Type"]!!), data)
        var request = Request.Builder()
                .url(url)
                .headers(curHeaders)
                .post(requestBody)
                .build()
        return client.newCall(request).execute()
    }

    fun postFormDataToUrl(url: URL, data: String): Response {
        return doPost(url, data, postFormHeaders)
    }

    fun postJsonDataToURL(url: URL, json: String): Response {
        return doPost(url, json, postJsonHeaders);
    }

    fun readResponseBodyString(response: Response): String {
        return response.body()!!.string()
    }

    fun formatterPostMapData(data: Map<String, String>): String {
        var stringBuilder = StringBuilder();
        for (k in data.keys) {
            var keyValue = "${k}=${URLEncoder.encode(data[k], "utf-8")}";
            var appendContent = if (stringBuilder.isNotEmpty()) "&$keyValue" else "$keyValue";
            stringBuilder.append(appendContent);
        }
        return stringBuilder.toString()
    }
}