package unroll.github.io.yourcollege.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import unroll.github.io.yourcollege.R
import unroll.github.io.yourcollege.fragment.base.BaseFragment

class TimetableFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_timetable, container, false)
    }
}