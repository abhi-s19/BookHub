package com.abhisek.bookhub.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.abhisek.bookhub.DescriptionActivity
import com.abhisek.bookhub.R
import com.abhisek.bookhub.model.Book
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_dashboard_single_row.view.*

class DashboardRecyclerAdapter(val context: Context, val itemList: ArrayList<Book>) :
    RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {

    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtBookName: TextView = view.findViewById(R.id.txtBookName)
        val txtAuthorName: TextView = view.findViewById(R.id.txtAuthorName)
        val txtBookPrice: TextView = view.findViewById(R.id.txtBookPrice)
        val txtBookRating: TextView = view.findViewById(R.id.txtBookRating)
        val imgBookImage: ImageView = view.findViewById(R.id.imgbookimage)
        val content11: LinearLayout = view.findViewById(R.id.Content11)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_dashboard_single_row, parent, false)

        return DashboardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val Book = itemList[position]
        holder.txtBookName.text = Book.bookName
        holder.txtAuthorName.text = Book.bookAuthor
        holder.txtBookPrice.text = Book.bookPrice
        holder.txtBookRating.text = Book.bookRating
        //holder.imgBookImage.setImageResource(Book.bookImage)
        Picasso.get().load(Book.bookImage).error(R.drawable.default_book_cover)
            .into(holder.imgBookImage)
        holder.content11.setOnClickListener {
            Toast.makeText(context, "Clicked on ${holder.txtBookName.text}", Toast.LENGTH_LONG)
                .show()

            val intent = Intent(context, DescriptionActivity::class.java)
            intent.putExtra("book_id", Book.bookId)
            context.startActivity(intent)
        }
    }
}