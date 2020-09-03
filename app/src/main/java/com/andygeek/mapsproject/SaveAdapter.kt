package com.andygeek.mapsproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.andygeek.mapsproject.database.AppDatabase
import com.andygeek.mapsproject.database.Place
import com.andygeek.mapsproject.database.PlaceDao
import com.bumptech.glide.Glide
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync

class SaveAdapter(private val context: Context,
                    private val dataSource: ArrayList<Save>) : BaseAdapter(){

    private var db: AppDatabase? = null
    private var placeDao: PlaceDao? = null

    private val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private lateinit var rowView : View

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // inflate rowView
        rowView = inflater.inflate(R.layout.list_item_save, parent, false)

        db = AppDatabase.getAppDatabase(context)
        placeDao = db?.placeDao()

        // Views of the list_item_recipe
        val txtNamesave = rowView.findViewById(R.id.txt_nameSave) as TextView
        val txtAddressSave = rowView.findViewById(R.id.txt_addressSave) as TextView
        val imgSave = rowView.findViewById(R.id.img_save) as ImageView

        val saveItem = getItem(position) as Save

        txtNamesave.text = saveItem.placeName
        txtAddressSave.text = saveItem.address
        //Glide.with(context).load(recipe.url_image_reviewer).centerCrop().into(imgReviewer)
        return rowView
    }


}