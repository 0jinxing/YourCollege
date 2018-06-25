package unroll.github.io.yourcollege.activity.base

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    open var layoutResID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (layoutResID != null) setContentView(layoutResID!!)
        afterCreate()
    }

    open fun afterCreate() {}
}