package com.shiro.notesapp



import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.util.Log
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import kotlinx.android.synthetic.main.activity_add_note.*
import java.lang.Exception


class AddNoteActivity : AppCompatActivity() {


    var dbTable = "NoteTable"

    var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)



        try {
            val bundle: Bundle = intent.extras!!
            id = bundle.getInt("IDExtra",0)
            if (id != 0) {
                descEdit.setText(bundle.getString("DescExtra"))
                tagsInput.setText(bundle.getString("TagListExtra"))
                lastModifiedTextView.text = ("Lần chỉnh sửa cuối : " + bundle.getString("LastModExtra"))

            }
        }
        catch (ex:Exception){}

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.new_note_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item != null) {
            // Handle item selection
            when (item.itemId) {
                R.id.app_bar_confirm -> {
                    addNewNoteFunc()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun addNewNoteFunc() {
        var dbManager = DbManager(this)
        var values = ContentValues()
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
        val formatted = current.format(formatter)
        values.put("Description", descEdit.text.toString())
        values.put("Datetime", formatted)
        var tagArr = tagsInput.text.toString()
        if (tagArr.isEmpty()) {
            values.put("Taglist", "None")
        }
        else {
            values.put("Taglist", tagArr)
        }

        if (id == 0) {
            val ID = dbManager.insert(values)
            if (ID > 0) {
                Toast.makeText(this,"Đã thêm note mới.",Toast.LENGTH_SHORT).show()
                finish()
            }
            else {
                Toast.makeText(this,"Có lỗi xảy ra!",Toast.LENGTH_SHORT).show()
            }
        }
        else {
            var selectionArgs = arrayOf(id.toString())
            val ID = dbManager.update(values,"ID=?",selectionArgs)
            if (ID > 0) {
                Toast.makeText(this,"Đã thêm note mới.",Toast.LENGTH_SHORT).show()
                finish()
            }
            else {
                Toast.makeText(this,"Có lỗi xảy ra!",Toast.LENGTH_SHORT).show()
            }
        }
    }
}