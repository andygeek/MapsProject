package com.andygeek.mapsproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class RecipeAdapter(private val context: Context,
                    private val dataSource: ArrayList<Recipe>) : BaseAdapter(){

    private val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    lateinit var rowView : View

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
        val txt_nameReviewer = rowView.findViewById(R.id.txt_nameReviewer) as TextView
        val txt_textReviewer = rowView.findViewById(R.id.txt_textReviewer) as TextView
        val img_reviewer = rowView.findViewById(R.id.img_reviewer) as ImageView

        val recipe = getItem(position) as Recipe
        txt_nameReviewer.text = recipe.name_reviewer
        txt_textReviewer.text = recipe.text_reviewer
        Glide.with(context).load(recipe.url_image_reviewer).centerCrop().into(img_reviewer)
        return rowView
    }

}