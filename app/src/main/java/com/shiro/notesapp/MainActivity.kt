package com.shiro.notesapp

import android.app.SearchManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.*
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.row.*
import kotlinx.android.synthetic.main.row.view.*
import kotlinx.android.synthetic.main.tagchip.view.*
import java.util.*
import kotlin.collections.ArrayList
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SearchView

import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.Toast
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T













class MainActivity : AppCompatActivity() {

    var listNotes = ArrayList<Note>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //load du lieu tu database
        loadQuery("%")

    }

    fun loadQuery(title: String) {
        var dbManager = DbManager(this)
        val projections = arrayOf("ID","Description","Taglist", "Datetime")
        val selectionArgs = arrayOf(title)
        val cursor = dbManager.query(projections, "Description Like ?", selectionArgs,"ID DESC")
        listNotes.clear()
        if (cursor.moveToFirst()) {
            do {
                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val Description = cursor.getString(cursor.getColumnIndex("Description"))
                val TagList = cursor.getString(cursor.getColumnIndex("Taglist"))
                val DateTime = cursor.getString(cursor.getColumnIndex("Datetime"))



                listNotes.add(Note(ID,Description,TagList,DateTime))
            }
                while (cursor.moveToNext())
        }

        var myNoteAdapter = MyNoteAdapter(listNotes,this)
        noteListView.adapter = myNoteAdapter
        noteListView.setOnItemClickListener { parent, view, position, id ->
            val element = listNotes[position] // The item that was clicked
            val intent = Intent(this, AddNoteActivity::class.java)
            intent.putExtra("IDExtra",element.ID)
            intent.putExtra("DescExtra",element.noteDesc)
            intent.putExtra("TagListExtra",element.tagList)
            intent.putExtra("LastModExtra", element.noteTime)
            startActivity(intent)
        }
    }

    fun loadQueryFromSearch(title: String) {
        var dbManager = DbManager(this)
        val projections = arrayOf("ID","Description","Taglist", "Datetime")

        val tagList = ArrayList<String>()
        tagList.addAll(title.split(","))
        tagList.add(title)

        for (i in 0 until tagList.size-1) {
            if (tagList[i].first() != '%') {
                tagList[i] = "%" + tagList[i]
            }

            if (tagList[i].last() != '%') {
                tagList[i] = tagList[i] + "%"
            }
        }

        var selectionArgs = tagList.toTypedArray()

        var query = "Taglist Like ?"

        if (tagList.size > 1) {
        for (i in 0 until tagList.size-2) {
            if (i < tagList.size-2) {
                query += " AND Taglist Like ?"
            }
        }
            }

        query += " OR Description Like ?"
        val cursor = dbManager.query(projections, query, selectionArgs,"ID DESC")
        listNotes.clear()
        if (cursor.moveToFirst()) {
            do {
                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val Description = cursor.getString(cursor.getColumnIndex("Description"))
                val TagList = cursor.getString(cursor.getColumnIndex("Taglist"))
                val DateTime = cursor.getString(cursor.getColumnIndex("Datetime"))

                listNotes.add(Note(ID,Description,TagList,DateTime))
            }
            while (cursor.moveToNext())
        }

        var myNoteAdapter = MyNoteAdapter(listNotes,this)
        noteListView.adapter = myNoteAdapter
        noteListView.setOnItemClickListener { parent, view, position, id ->
            val element = listNotes[position] // The item that was clicked
            val intent = Intent(this, AddNoteActivity::class.java)
            intent.putExtra("IDExtra",element.ID)
            intent.putExtra("DescExtra",element.noteDesc)
            intent.putExtra("TagListExtra",element.tagList)
            intent.putExtra("LastModExtra", element.noteTime)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadQuery("%")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)

        //search view
        var sv : SearchView = menu!!.findItem(R.id.app_bar_search).actionView as SearchView
        var sm = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))



        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                loadQueryFromSearch("%"+query+"%")
                return false
            }

            override fun onQueryTextChange(newtext: String?): Boolean {
                loadQueryFromSearch("%"+newtext+"%")
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item != null) {
            // Handle item selection
            when (item.itemId) {
                R.id.app_bar_addnote -> {
                    startActivity(Intent(this,AddNoteActivity::class.java))
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }




    inner class MyNoteAdapter : BaseAdapter {

        var listNoteArr = ArrayList<Note>()
        var context:Context ?= null

        constructor(listNoteArr: ArrayList<Note>, context: Context) : super() {
            this.listNoteArr = listNoteArr
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            //dua item vao
            var myView = layoutInflater.inflate(R.layout.row, null);
            var myNote = listNoteArr[position]
            myView.titleItem.text = myNote.getNoteTitle()
            myView.descItem.text =  myNote.getNoteDescription()
            myView.timeEdit.text = myNote.noteTime

            var TagArr = ArrayList<String>()
            TagArr.addAll(myNote.tagList!!.split(","))
            var tagAdapter = RecyclerViewAdapter(TagArr)
            val layoutManager = LinearLayoutManager(applicationContext)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL

            myView.tagslist.layoutManager = layoutManager
            myView.tagslist.adapter = tagAdapter

            // delete
            myView.removeBtn.setOnClickListener {
                var dbManager = DbManager(this.context!!)
                val selectionArgs = arrayOf(myNote.ID.toString())
                dbManager.delete("ID=?",selectionArgs)
                loadQuery("%")
            }
            return myView
        }

        override fun getItem(position: Int): Any {
            return listNoteArr[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listNoteArr.size
        }

    }


    inner class RecyclerViewAdapter(data: ArrayList<String>) :
        RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>() {

       var data = ArrayList<String>()

        init {
            this.data = data
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.tagchip, parent, false)
            return RecyclerViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            holder.txtUserName.setText(data.get(position))
        }

        override fun getItemCount(): Int {
            return data.size
        }


        inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            internal var txtUserName: TextView

            init {
                txtUserName = itemView.findViewById(R.id.chiptags) as TextView
            }
        }
    }




}
