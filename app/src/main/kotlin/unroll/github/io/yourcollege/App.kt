package unroll.github.io.yourcollege

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import org.litepal.LitePalApplication

class App : LitePalApplication() {
    object D {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        D.context = getContext()
    }
}

