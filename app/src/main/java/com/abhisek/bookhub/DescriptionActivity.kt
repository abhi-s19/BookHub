package com.abhisek.bookhub

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.abhisek.bookhub.database.BookEntity
import com.abhisek.bookhub.database.BookDataBase
import com.abhisek.bookhub.util.ConnectionManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject

class DescriptionActivity : AppCompatActivity() {

    lateinit var imgBookImage: ImageView
    lateinit var txtBookName: TextView
    lateinit var txtAuthorName: TextView
    lateinit var txtBookPrice: TextView
    lateinit var txtBookRating: TextView
    lateinit var txtBookDesc: TextView
    lateinit var btAddToFav: Button
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var toolbar: Toolbar

    var bookId: String? = "100"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        imgBookImage = findViewById(R.id.imgbookimage)
        txtBookName = findViewById(R.id.txtBookName)
        txtAuthorName = findViewById(R.id.txtAuthorName)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookRating = findViewById(R.id.txtBookRating)
        txtBookDesc = findViewById(R.id.txtBookDesc)
        btAddToFav = findViewById(R.id.btAddToFav)
        progressLayout = findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Book Details"

        if (intent != null) {
            bookId = intent.getStringExtra("book_id")
        } else {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some unexpected error occured!!!",
                Toast.LENGTH_LONG
            ).show()
        }
        if (bookId == "100") {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some unexpected error occured!!!",
                Toast.LENGTH_LONG
            ).show()
        }

        val queue = Volley.newRequestQueue(this@DescriptionActivity)
        val url = "http://13.235.250.119/v1/book/get_book/"
        val jsonParams = JSONObject()
        jsonParams.put("book_id", bookId)

        if (ConnectionManager().checkConnectivity(this@DescriptionActivity)) {
            val jsonRequest =
                @SuppressLint("SetTextI18n")
                object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {

                    try {
                        val success = it.getBoolean("success")
                        if (success) {
                            val bookJsonObject = it.getJSONObject("book_data")
                            progressLayout.visibility = View.GONE

                            val bookImageUrl = bookJsonObject.getString("image")
                            Picasso.get().load(bookJsonObject.getString("image"))
                                .error(R.drawable.default_book_cover).into(imgBookImage)
                            txtBookName.text = bookJsonObject.getString("name")
                            txtAuthorName.text = bookJsonObject.getString("author")
                            txtBookPrice.text = bookJsonObject.getString("price")
                            txtBookRating.text = bookJsonObject.getString("rating")
                            txtBookDesc.text = bookJsonObject.getString("description")

                            val bookEntity = BookEntity(
                                bookId?.toInt() as Int,
                                txtBookName.text.toString(),
                                txtAuthorName.text.toString(),
                                txtBookPrice.text.toString(),
                                txtBookRating.text.toString(),
                                txtBookDesc.text.toString(),
                                bookImageUrl
                            )
                            val checkFav = DBAsyncTask(applicationContext, bookEntity, 1).execute()
                            val isFav = checkFav.get()

                            if (isFav) {
                                btAddToFav.text = "Remove from Favourites"
                                val favColor = ContextCompat.getColor(
                                    applicationContext,
                                    R.color.colorFavourite
                                )
                                btAddToFav.setBackgroundColor(favColor)
                            } else {
                                btAddToFav.text = "Add to Favourites"
                                val noFavColor = ContextCompat.getColor(
                                    applicationContext,
                                    R.color.colorPrimary
                                )
                                btAddToFav.setBackgroundColor(noFavColor)
                            }

                            btAddToFav.setOnClickListener {
                                if (!DBAsyncTask(applicationContext, bookEntity, 1).execute()
                                        .get()
                                ) {
                                    val async =
                                        DBAsyncTask(applicationContext, bookEntity, 2).execute()
                                    val result = async.get()
                                    if (result) {
                                        Toast.makeText(
                                            this@DescriptionActivity,
                                            "Book Added to Favourite",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        btAddToFav.text = "Remove from Favourite"
                                        val favColor = ContextCompat.getColor(
                                            applicationContext,
                                            R.color.colorFavourite
                                        )
                                        btAddToFav.setBackgroundColor(favColor)
                                    } else {
                                        Toast.makeText(
                                            this@DescriptionActivity,
                                            "Some Error Occurred",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } else {
                                    val async =
                                        DBAsyncTask(applicationContext, bookEntity, 3).execute()
                                    val result = async.get()
                                    if (result) {
                                        Toast.makeText(
                                            this@DescriptionActivity,
                                            "Book Removed from Favourite",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        btAddToFav.text = "Add to Favourites"
                                        val noFavColor = ContextCompat.getColor(
                                            applicationContext,
                                            R.color.colorPrimary
                                        )
                                        btAddToFav.setBackgroundColor(noFavColor)
                                    } else {
                                        Toast.makeText(
                                            this@DescriptionActivity,
                                            "Some Error Occurred",
                                            Toast.LENGTH_LONG
                                        ).show()

                                    }
                                }
                            }

                        } else {
                            Toast.makeText(
                                this@DescriptionActivity,
                                "some error Occurred!!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@DescriptionActivity,
                            "some error occured!!aaa",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                    Response.ErrorListener {
                        Toast.makeText(
                            this@DescriptionActivity,
                            "Volley error $it",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "c43157f9cf15b5"
                        return headers
                    }
                }
            queue.add(jsonRequest)
        } else {
            val dialog = AlertDialog.Builder(this@DescriptionActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not found")
            dialog.setPositiveButton("Open Settings") { text, listner ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { text, listner ->
                ActivityCompat.finishAffinity(this@DescriptionActivity)
            }
            dialog.create()
            dialog.show()
        }

    }

    class DBAsyncTask(val context: Context, val bookEntity: BookEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        /*
        Mode-1 -> Check DB if the BOOK is favourite or not
        Mode-2 -> Save the book into DB as favourite
        Mode-3 -> Remove the favourite book
         */
        val db = Room.databaseBuilder(context, BookDataBase::class.java, "books-db").build()
        override fun doInBackground(vararg p0: Void?): Boolean {

            when (mode) {

                1 -> {
                    //Check DB if the BOOK is favourite or not
                    val book: BookEntity? = db.bookDao().getBookById(bookEntity.book_id.toString())
                    db.close()
                    return book != null
                }
                2 -> {
                    //Save the book into DB as favourite
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true   //since this function is used to add book so chance of getting null value
                }
                3 -> {
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true
                }
            }

            return false
        }

    }
}