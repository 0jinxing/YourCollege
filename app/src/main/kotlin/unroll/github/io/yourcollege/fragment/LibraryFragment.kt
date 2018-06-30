package unroll.github.io.yourcollege.fragment

import android.os.Bundle
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.SimpleAdapter
import android.widget.Toast
import com.bumptech.glide.Glide
import com.orhanobut.dialogplus.*
import unroll.github.io.yourcollege.R
import unroll.github.io.yourcollege.fragment.base.BaseFragment
import unroll.github.io.yourcollege.util.ZSCEduUtil
import java.io.FileDescriptor
import java.io.PrintWriter


class LibraryFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }
}