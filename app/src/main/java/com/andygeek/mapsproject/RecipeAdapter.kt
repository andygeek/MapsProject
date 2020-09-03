package com.andygeek.mapsproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class RecipeAdapter(private val context: Context,
                    private val dataSource: ArrayList<Recipe>) : BaseAdapter(){

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
        rowView = inflater.inflate(R.layout.list_item_recipe, parent, false)

        // Views of the list_item_recipe
        val txtNamereviewer = rowView.findViewById(R.id.txt_nameReviewer) as TextView
        val txtTextreviewer = rowView.findViewById(R.id.txt_textReviewer) as TextView
        val imgReviewer = rowView.findViewById(R.id.img_reviewer) as ImageView


        val recipe = getItem(position) as Recipe
        txtNamereviewer.text = recipe.nameReviewer
        txtTextreviewer.text = recipe.textReviewer
        Glide.with(context).load(recipe.urlImageReviewer).centerCrop().into(imgReviewer)
        return rowView
    }

}