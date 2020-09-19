package com.abhisek.bookhub.model

data class Book (
    var bookId: String,
    val bookName : String,
    val bookAuthor : String,
    val bookRating : String,
    val bookPrice : String,
    val bookImage : String
)