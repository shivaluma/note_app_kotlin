package com.shiro.notesapp

import java.util.*
import kotlin.collections.ArrayList

class Note(noteID:Int,noteDesc: String, tagList: String, noteEditTime:String) {
    var ID:Int ?= noteID
    var noteDesc:String ?= noteDesc
    var tagList:String  ?= tagList
    var noteTime:String ?= noteEditTime

    fun getNoteTitle() : String {
        return noteDesc!!.split("\n")[0]
    }

    fun getNoteDescription() : String {
        var temp = ArrayList<String>()
        temp.addAll(noteDesc!!.split("\n"))
        temp.removeAt(0)
        return temp.joinToString("\n")
    }

    fun getTagListString() :String {
        return tagList!!
    }
}

