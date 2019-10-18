package com.shiro.notesapp

import java.util.*
import kotlin.collections.ArrayList
import android.text.Html
import android.util.Log
import org.jsoup.Jsoup
import androidx.core.text.HtmlCompat

class Note(noteID:Int,noteDesc: String, tagList: String, noteEditTime:String) {
    var ID:Int ?= noteID
    var noteDesc:String ?= noteDesc
    var tagList:String  ?= tagList
    var noteTime:String ?= noteEditTime

    fun getNoteTitle() : String {

        return Html.fromHtml(noteDesc,HtmlCompat.FROM_HTML_MODE_LEGACY).toString().split("\n")[0]
    }

    fun getNoteDescription() : String {
        var temp = ArrayList<String>()
        temp.addAll(Html.fromHtml(noteDesc,HtmlCompat.FROM_HTML_MODE_LEGACY).toString().split("\n"))
        temp.removeAt(0)
        temp.removeIf({ i -> i=="" || i=="\n" })
        return temp.joinToString("\n")
    }

    fun getTagListString() :String {
        return tagList!!
    }
}

