package com.example.dynamicform

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import com.szagurskii.patternedtextwatcher.PatternedTextWatcher
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {
    private lateinit var allViewsList: ArrayList<String>
    private lateinit var allViews: HashMap<Int, String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val container = findViewById<LinearLayout>(R.id.container)
        allViewsList = ArrayList()
        allViews = HashMap()
        val jsonObject = JSONObject(CONST.FORM_DATA)
        val jsonArray: JSONArray =
            jsonObject.getJSONArray("form_builder_json").getJSONObject(0).getJSONArray("data")
        for (i in 0 until jsonArray.length()) {
            val field = jsonArray.getJSONObject(i)
            val type = field.get("type")
            container.addView(generatedTextView(field))
            if (type == "text") {
                if (field.has("enabled") && field.get("enabled") == "true") {
                    val editText = generatedEditText(field, i)
                    editText.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
                    container.addView(editText)
                }
            } else if (type == "date") {
                container.addView(generatedDateTimePicker(i))
            } else if (type == "textArea") {
                container.addView(generatedEditText(field, i))
            } else if (type == "formated_number") {
                container.addView(formattedEditText(field, i))
            } else if (type == "select") {
                container.addView(generatedSpinner(field, i))
            }
        }
        container.addView(myButton())
    }

    @SuppressLint("SetTextI18n")
    private fun myButton(): Button {
        val button = Button(this)
        button.text = "Submit"
        button.setPadding(20)
        button.textSize = 14f
        button.setOnClickListener {
            if (validateInputs()) {
                startActivity(
                    Intent(this, ResultActivity::class.java).putExtra(
                        "result",
                        allViewsList
                    )
                )
            }
        }
        return button
    }

    private fun validateInputs(): Boolean {
        Log.d("MyForm", allViews.size.toString())
        for (item in allViews.entries) {
            if (item.value == "ET") {
                val editText = findViewById<EditText>(item.key)
                if (editText.text.isEmpty()) {
                    editText.error = "Please Enter Value"
                    allViewsList.clear()
                    break
                } else {
                    allViewsList.add(editText.text.toString())
                    Log.d("MyForm", editText.text.toString())
                }
            } else if (item.value == "SP") {
                val spinner = findViewById<Spinner>(item.key)
                allViewsList.add(spinner.selectedItem.toString())
                Log.d("MyForm", spinner.selectedItem.toString())
            }
        }
        return allViewsList.size > 0
    }

    private fun generatedTextView(jsonObject: JSONObject): TextView {
        val textView = TextView(this)
        textView.gravity = Gravity.CENTER
        textView.text = jsonObject.get("title").toString()
        textView.setTextColor(Color.BLACK)
        textView.textSize = 14F
        textView.setPadding(10)
        return textView
    }

    private fun generatedEditText(jsonObject: JSONObject, id: Int): EditText {
        val editText = EditText(this)
        if (jsonObject.has("placeholder")) {
            editText.hint = jsonObject.get("placeholder").toString()
        }
        editText.id = id
        allViews[id] = "ET"
        return editText
    }

    private fun formattedEditText(jsonObject: JSONObject, id: Int): EditText {
        val editText = EditText(this)
        editText.inputType = InputType.TYPE_CLASS_PHONE
        var pattern = "(###) ###-####"
        if (jsonObject.has("formatted_numeric_formate")) {
            pattern = jsonObject.get("formatted_numeric_formate").toString()
        }
        //editText.keyListener = DigitsKeyListener.getInstance("(012) 345-6789")
        editText.addTextChangedListener(PatternedTextWatcher(pattern))
        editText.id = id
        allViews[id] = "ET"
        return editText
    }

    private fun generatedSpinner(jsonObject: JSONObject, id: Int): Spinner {
        val spinner = Spinner(this)
        val paths = ArrayList<String>()
        val options = JSONObject(jsonObject.get("list_options").toString())
        for (item in options.keys()) {
            paths.add(options.get(item).toString())
        }
        val adapter = ArrayAdapter(
            this@MainActivity,
            android.R.layout.simple_spinner_item, paths
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.gravity = Gravity.CENTER
        spinner.id = id
        allViews[id] = "SP"
        return spinner
    }

    private fun generatedDateTimePicker(id: Int): EditText {
        val editText = EditText(this)
        editText.isClickable = true
        editText.isFocusable = false
        editText.inputType = InputType.TYPE_NULL
        editText.setOnClickListener {
            val myCalendar = Calendar.getInstance()
            DatePickerDialog(
                this, { _, year, month, dayOfMonth ->
                    myCalendar.set(Calendar.YEAR, year)
                    myCalendar.set(Calendar.MONTH, month)
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val myFormat = "MM/dd/yy"
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    editText.setText(sdf.format(myCalendar.time).toString())
                }, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        editText.id = id
        allViews[id] = "ET"
        return editText
    }
}