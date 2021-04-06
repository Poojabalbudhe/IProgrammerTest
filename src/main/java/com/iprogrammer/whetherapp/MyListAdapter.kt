package com.iprogrammer.whetherapp

import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class MyListAdapter(private val context: MainActivity, private val name: Array<String>, private val temp: Array<String>, private val datetime: Array<String>)
    : ArrayAdapter<String>(context, R.layout.custom_list, name) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.custom_list, null, true)

        val nameText = rowView.findViewById(R.id.textViewId) as TextView
        val tempText = rowView.findViewById(R.id.textViewName) as TextView
        val datetimeText = rowView.findViewById(R.id.textViewEmail) as TextView

        nameText.text = "Name: ${name[position]}"
        tempText.text = "Temp: ${temp[position]}"
        datetimeText.text = "DateTime: ${datetime[position]}"
        return rowView
    }
}