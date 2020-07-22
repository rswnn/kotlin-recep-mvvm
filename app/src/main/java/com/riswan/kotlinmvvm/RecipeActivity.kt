package com.riswan.kotlinmvvm

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.riswan.kotlinmvvm.models.Recipe
import com.riswan.kotlinmvvm.utils.Constants
import com.riswan.kotlinmvvm.viewmodels.RecipeState
import com.riswan.kotlinmvvm.viewmodels.RecipeViewModel

import kotlinx.android.synthetic.main.activity_recipe.*
import kotlinx.android.synthetic.main.content_recipe.*

class RecipeActivity : AppCompatActivity() {
    private lateinit var recipeViewModel: RecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)
        setSupportActionBar(toolbar)

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }

        recipeViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)
        if (isUpdate()) {
            doUpdate()
            recipeViewModel.fetchOnePost(Constants.getToken(this@RecipeActivity), getId().toString() )
            recipeViewModel.getRecipe().observe(this, Observer {
                fill(it)
            })
        } else {
            doCreate()
        }
        recipeViewModel.getState().observer(this, Observer {
            handleUIState(it)
        })
    }
    private fun isUpdate() = intent.getBooleanExtra("is_update", false)
    private fun getId() = intent.getIntExtra("id", 0)
    private fun doUpdate() {
        btn_submit.setOnClickListener {
            val title = et_title.text.toString().trim()
            val content = et_content.text.toString().trim()

            if (recipeViewModel.validate(title, content)) {
                recipeViewModel.updateRecipe(Constants.getToken(this), getId().toString(), title, content)
            }
        }
    }

    private fun doCreate() {
        btn_submit.setOnClickListener {
            val title = et_title.text.toString().trim()
            val content = et_content.text.toString().trim()

            if (recipeViewModel.validate(title, content)) {
                recipeViewModel.createRecipe(Constants.getToken(this), title, content)
            }
        }
    }

    private fun fill(recipe:Recipe) {
        et_title.setText(recipe.title)
        et_content.setText(recipe.content)
    }

    private fun handleUIState (it: RecipeState) {
        when(it) {
            is RecipeState.IsLoading -> isLoading(it.state)
            is RecipeState.Error -> {
                toast(it.err)
                isLoading(false)
            }
            is RecipeState.ShowToast -> toast(it.message)
            is RecipeState.RecipeValidaion -> {
                it.title?.let {
                    setTitleError(it)
                }
                it.content?.let {
                    setContentError(it)
                }
            }
            is RecipeState.Reset -> {
                setTitleError(null)
                setContentError(null)
            }
            is RecipeState.IsSuccess -> {
                when(it.what) {
                    0 -> {
                        toast("berhasil dibuat")
                        finish()
                    }
                    1 -> {
                        toast("berhasil diupdate")
                        finish()
                    }
                    2 -> {
                        toast("berhasil dihapus")
                        finish()
                    }
                }
            }
        }
    }

    private fun isLoading(state:Boolean) { btn_submit.isEnabled = !state}
    private fun toast(message:String?) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    private fun setTitleError(title:String?) {in_title.error = title}
    private fun setContentError(content:String?) {in_content.error = content}


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (isUpdate()) {
            menuInflater.inflate(R.menu.menu_recipe, menu)
            return true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_delete -> {
                recipeViewModel.deleteRecipe(Constants.getToken(this@RecipeActivity), getId().toString())
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
