/*
 * Copyright (C) 2020 Sandip Vaghela
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.afterroot.watchdone.ui.home

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afterroot.tmdbapi.TmdbApi
import com.afterroot.tmdbapi.model.MovieDb
import com.afterroot.watchdone.R
import com.afterroot.watchdone.adapter.DelegateAdapter
import com.afterroot.watchdone.adapter.ItemSelectedCallback
import kotlinx.android.synthetic.main.content_add_watched.*
import kotlinx.android.synthetic.main.content_add_watched.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.list_dialog.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.toast
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale

class HomeFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    lateinit var dialogCustomView: View
    private lateinit var homeViewModel: HomeViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onResume() {
        super.onResume()
        button_action_add_watched.setOnClickListener {
            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(text = "Add Watched")
                customView(R.layout.content_add_watched)
                dialogCustomView = getCustomView().apply {
                    text_input_date.setOnClickListener {
                        showDatePicker()
                    }
                }
                positiveButton(text = "Add") {
                    lifecycleScope.launch {
                        //TODO Add Task
                        showSelectDialog(input_title.text.toString())
                    }
                }
            }
        }
    }

    private fun showSelectDialog(title: String) = GlobalScope.launch(Dispatchers.Main) {
        val movies = withContext(Dispatchers.Default) { get<TmdbApi>().search.searchMovie(title) }
        Log.d(TAG, "showSelectDialog: ${movies.results}")
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.MATCH_PARENT)).show {
            title(text = "Add Watched")
            customView(R.layout.list_dialog)
            dialogCustomView = getCustomView().apply {
                val adapter = DelegateAdapter(object : ItemSelectedCallback<MovieDb> {
                    override fun onClick(position: Int, view: View?, item: MovieDb) {
                        super.onClick(position, view, item)
                        requireContext().toast(item.title.toString())
                    }
                }, getKoin())
                list.apply {
                    val lm = GridLayoutManager(requireContext(), 3)
                    layoutManager = lm
                }
                adapter.add(movies.results)
                list.adapter = adapter
                list.scheduleLayoutAnimation()

            }
        }
    }

    private lateinit var cal: Calendar
    private var day: Int? = 0
    private var hourOfDay: Int? = 0
    private var millis: Long = 0
    private var minute: Int? = 0
    private var month: Int? = 0
    private var pickedDate: GregorianCalendar? = null
    private var year: Int? = 0

    private fun showDatePicker() {
        cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        hourOfDay = cal.get(Calendar.HOUR_OF_DAY)
        minute = cal.get(Calendar.MINUTE)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        val datePicker = DatePickerDialog(requireContext(), this, year!!, month!!, day!!)
        datePicker.show()
    }

    private fun showTimePicker() {
        val picker = TimePickerDialog(this.context, this, hourOfDay!!, minute!!, false)
        picker.show()
    }

    override fun onDateSet(p0: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        this.year = year
        this.month = monthOfYear
        this.day = dayOfMonth
        showTimePicker()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        pickedDate = GregorianCalendar(year!!, month!!, day!!, hourOfDay, minute)
        millis = pickedDate!!.timeInMillis

        val formatter = SimpleDateFormat(getString(R.string.date_time_format), Locale.US)
        dialogCustomView.text_input_date.text = formatter.format(Date(millis))
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}
