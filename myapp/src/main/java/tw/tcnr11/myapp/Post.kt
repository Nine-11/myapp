package tw.tcnr11.myapp

import java.io.Serializable

class Post     //標題
    (
    var title: String,
    var url: String,
    var hdurl: String,
    var date: String,
    var copyright: String,
    var description: String
) : Serializable