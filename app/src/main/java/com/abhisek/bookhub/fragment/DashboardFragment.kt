package com.abhisek.bookhub.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abhisek.bookhub.R
import com.abhisek.bookhub.adapter.DashboardRecyclerAdapter
import com.abhisek.bookhub.model.Book
import com.abhisek.bookhub.util.ConnectionManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_description.view.*
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var btCheckInternet: Button

    val bookInfoList = arrayListOf<Book>()
    
    val ratingComparator = Comparator<Book>{ book1,book2 ->
        if (book1.bookRating.compareTo(book2.bookRating,true)==0)
        {
            book1.bookName.compareTo(book2.bookName,true)
        }
        else
        {
            book1.bookRating.compareTo(book2.bookRating,true)
        }

    }
    /*val bookList = arrayListOf<String>(
        "P.S. I love you",
        "The Great Gatsby",
        "Anna Karenina",
        "Madame Bovary",
        "War and Peace",
        "Lolita",
        "Middlemarch",
        "The Adventures of Huckleberry Finn",
        "Moby-Dick",
        "The Lord of the Rings"
    )*/
    /* val bookInfoList = arrayListOf<Book>(
         Book("P.S. I love You", "Cecelia Ahern", "Rs. 299", "4.5", R.drawable.ps_ily),
         Book("The Great Gatsby", "F. Scott Fitzgerald", "Rs. 399", "4.1", R.drawable.great_gatsby),
         Book("Anna Karenina", "Leo Tolstoy", "Rs. 199", "4.3", R.drawable.anna_kare),
         Book("Madame Bovary", "Gustave Flaubert", "Rs. 500", "4.0", R.drawable.madame),
         Book("War and Peace", "Leo Tolstoy", "Rs. 249", "4.8", R.drawable.war_and_peace),
         Book("Lolita", "Vladimir Nabokov", "Rs. 349", "3.9", R.drawable.lolita),
         Book("Middlemarch", "George Eliot", "Rs. 599", "4.2", R.drawable.middlemarch),
         Book(
             "The Adventures of Huckleberry Finn",
             "Mark Twain",
             "Rs. 699",
             "4.5",
             R.drawable.adventures_finn
         ),
         Book("Moby-Dick", "Herman Melville", "Rs. 499", "4.5", R.drawable.moby_dick),
         Book("The Lord of the Rings", "J.R.R Tolkien", "Rs. 749", "5.0", R.drawable.lord_of_rings)
     )*/


    lateinit var recyclerAdapter: DashboardRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        setHasOptionsMenu(true)

        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)
        layoutManager = LinearLayoutManager(activity)


        btCheckInternet = view.findViewById(R.id.btCheckInternet)

        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE

        btCheckInternet.setOnClickListener {
            if (ConnectionManager().checkConnectivity(activity as Context)) {
                val dialog = AlertDialog.Builder(activity as Context)
                dialog.setTitle("Success")
                dialog.setMessage("Internet Connection Found")
                dialog.setPositiveButton("Ok") { text, listner -> }
                dialog.setNegativeButton("Cancel") { text, listner -> /*Do Nothing*/ }
                dialog.create()
                dialog.show()
            } else {
                val dialog = AlertDialog.Builder(activity as Context)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not found")
                dialog.setPositiveButton("Ok") { text, listner -> }
                dialog.setNegativeButton("Cancel") { text, listner -> }
                dialog.create()
                dialog.show()
            }
        }


        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v1/book/fetch_books/"
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        progressLayout.visibility = View.GONE
                        val success = it.getBoolean("success")
                        if (success) {
                            val data = it.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val bookJsonObject = data.getJSONObject(i)
                                val bookObject = Book(
                                    bookJsonObject.getString("book_id"),
                                    bookJsonObject.getString("name"),
                                    bookJsonObject.getString("author"),
                                    bookJsonObject.getString("rating"),
                                    bookJsonObject.getString("price"),
                                    bookJsonObject.getString("image")
                                )
                                bookInfoList.add(bookObject)
                                recyclerAdapter =
                                    DashboardRecyclerAdapter(activity as Context, bookInfoList)

                                recyclerDashboard.adapter = recyclerAdapter
                                recyclerDashboard.layoutManager = layoutManager
                                recyclerDashboard.addItemDecoration(
                                    DividerItemDecoration(
                                        recyclerDashboard.context,
                                        (layoutManager as LinearLayoutManager).orientation
                                    )
                                )
                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some error occurred!!!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some unexpected error occured!!!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }, Response.ErrorListener {
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley error occured",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "c43157f9cf15b5"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
        } else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not found")
            dialog.setPositiveButton("Open Settings") { text, listner ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listner ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_dashboard,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item?.itemId
        if(id == R.id.action_sort)
        {
            Collections.sort(bookInfoList,ratingComparator)
            bookInfoList.reverse()
        }

        recyclerAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DashboardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DashboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}