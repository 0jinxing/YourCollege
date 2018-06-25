package unroll.github.io.yourcollege.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import unroll.github.io.yourcollege.R
import unroll.github.io.yourcollege.fragment.base.BaseFragment
import java.io.FileDescriptor
import java.io.PrintWriter
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.OnCancelListener
import com.orhanobut.dialogplus.OnDismissListener
import com.orhanobut.dialogplus.ViewHolder


class LibraryFragment : BaseFragment() {
    private val that = this

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onStart() {
        super.onStart()
        val test: Button = view!!.findViewById(R.id.test)
        test.setOnClickListener {
            val dialog = DialogPlus.newDialog(that.context)
                    .setExpanded(true)
                    .setOnDismissListener(OnDismissListener { Toast.makeText(that.context, "Dismiss", Toast.LENGTH_LONG).show() })
                    .setOnCancelListener(OnCancelListener { Toast.makeText(that.context, "Cancel", Toast.LENGTH_LONG).show() })
                    .create()
            dialog.show()
        }
    }
}