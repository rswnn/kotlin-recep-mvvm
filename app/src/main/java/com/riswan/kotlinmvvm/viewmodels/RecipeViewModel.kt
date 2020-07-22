package com.riswan.kotlinmvvm.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.riswan.kotlinmvvm.models.Recipe
import com.riswan.kotlinmvvm.utils.Constants
import com.riswan.kotlinmvvm.utils.SingleLiveEvent
import com.riswan.kotlinmvvm.utils.WrappedListResponse
import com.riswan.kotlinmvvm.utils.WrappedResponse
import com.riswan.kotlinmvvm.webservices.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeViewModel : ViewModel() {
    private var recipes = MutableLiveData<List<Recipe>>()
    private var recipe = MutableLiveData<Recipe>()
    private var state : SingleLiveEvent<RecipeState> = SingleLiveEvent()
    private var api = ApiClient.instance()

    fun fetchAllPost(token:String) {
        state.value = RecipeState.IsLoading(true)
        api.allRecipe(token).enqueue(object : Callback<WrappedListResponse<Recipe>>{
            override fun onFailure(call: Call<WrappedListResponse<Recipe>>, t: Throwable) {
                state.value = RecipeState.Error(t.message)
            }
            override fun onResponse( call: Call<WrappedListResponse<Recipe>>,response: Response<WrappedListResponse<Recipe>>) {
                if (response.isSuccessful){
                    val body = response.body() as WrappedListResponse<Recipe>
                    if (body.status.equals("1")) {
                        var res = body.data
                        recipes.postValue(res)
                    } else {
                        state.value = RecipeState.Error("Somethink went wrong")
                    }
                    state.value = RecipeState.IsLoading(false)
                }
            }
        })
    }

    fun fetchOnePost(token: String, id:String){
        state.value = RecipeState.IsLoading(true)
        api.getOneRecipe(token, id).enqueue(object: Callback<WrappedResponse<Recipe>> {
            override fun onFailure(call: Call<WrappedResponse<Recipe>>, t: Throwable) {
                state.value = RecipeState.Error(t.message)
            }
            override fun onResponse(call: Call<WrappedResponse<Recipe>>,response: Response<WrappedResponse<Recipe>> ) {
                if (response.isSuccessful) {
                    val body = response.body() as WrappedResponse<Recipe>
                    if (body.status.equals("1")) {
                        var res = body.data
                        recipe.postValue(res)
                    } else {
                        state.value = RecipeState.Error("Somethink went wrong")
                        println("gagal")
                    }
                    state.value = RecipeState.IsLoading(false)
                }
            }
        } )
    }

    fun createRecipe(token: String, title: String, content: String) {
        state.value = RecipeState.IsLoading(true)
        api.createRecipe(token, title, content).enqueue(object :Callback<WrappedResponse<Recipe>> {
            override fun onFailure(call: Call<WrappedResponse<Recipe>>, t: Throwable) {
                state.value = RecipeState.Error(t.message)
            }

            override fun onResponse(call: Call<WrappedResponse<Recipe>>,response: Response<WrappedResponse<Recipe>> ) {
                if(response.isSuccessful){
                    val body = response.body() as WrappedResponse<Recipe>
                    if(body.status.equals("1")) {
                        state.value = RecipeState.IsSuccess(0)
                    } else {
                        state.value = RecipeState.Error("create recipe not created on body")
                    }
                } else {
                    state.value = RecipeState.Error("create recipe not created on response")
                }
                state.value = RecipeState.IsLoading(false)
            }

        })
    }

    fun updateRecipe(token: String, id: String, title: String, content: String) {
        state.value = RecipeState.IsLoading(true)
        api.updateRecipe(token, id, title, content).enqueue(object : Callback<WrappedResponse<Recipe>> {
            override fun onFailure(call: Call<WrappedResponse<Recipe>>, t: Throwable) {
                state.value = RecipeState.Error(t.message)
            }

            override fun onResponse(call: Call<WrappedResponse<Recipe>>,response: Response<WrappedResponse<Recipe>> ) {
                if(response.isSuccessful){
                    val body = response.body() as WrappedResponse<Recipe>
                    if(body.status.equals("1")) {
                        state.value = RecipeState.IsSuccess(1)
                    } else {
                        state.value = RecipeState.Error("update recipe not updated on body")
                    }
                } else {
                    state.value = RecipeState.Error("update recipe not updated on response")
                }
                state.value = RecipeState.IsLoading(false)
            }
        })
    }

    fun deleteRecipe(token: String, id: String) {
        state.value = RecipeState.IsLoading(true)
        api.deleteRecipe(token, id).enqueue(object : Callback<WrappedResponse<Recipe>> {
            override fun onFailure(call: Call<WrappedResponse<Recipe>>, t: Throwable) {
                state.value = RecipeState.Error(t.message)
            }

            override fun onResponse(call: Call<WrappedResponse<Recipe>>,response: Response<WrappedResponse<Recipe>> ) {
                if(response.isSuccessful){
                    val body = response.body() as WrappedResponse<Recipe>
                    if(body.status.equals("1")) {
                        state.value = RecipeState.IsSuccess(2)
                    } else {
                        state.value = RecipeState.Error("delete recipe not deleted on body")
                    }
                } else {
                    state.value = RecipeState.Error("delete recipe not deleted on response")
                }
                state.value = RecipeState.IsLoading(false)
            }
        })
    }

//    fun validate(name:String?, email:String, password:String): Boolean {
//        state.value = RecipeState.Reset
//        if (name != null) {
//            if (name.isEmpty()){
//                state.value = RecipeState.ShowToast("Nama cannot empety")
//                return false
//            }
//            if (name.length < 5) {
//                state.value = RecipeState.RecipeValidaion(name = )
//                return false
//            }
//        }
//        if (email.isEmpty() || password.isEmpty()){
//            state.value = RecipeState.ShowToast("Name and password cannot empety")
//            return false
//        }
//        if (Constants.isValidEmail(email)) {
//            state.value = RecipeState.ShowToast("Name must type of email")
//            return false
//        }
//    }

    fun validate(title: String, content: String): Boolean {
        state.value = RecipeState.Reset
        if (title.isEmpty() || content.isEmpty()) {
            state.value = RecipeState.ShowToast("please insert the form")
            return false
        }
        if (title.length < 10) {
            state.value = RecipeState.RecipeValidaion(title = "the text of title must 10 charaters")
            return false
        }
        if (content.length < 20) {
            state.value = RecipeState.RecipeValidaion(title = "the text of content must 10 charaters")
            return false
        }
        return true
    }

    fun getRecipes() = recipes
    fun getRecipe() = recipe
    fun getState() = state
}

sealed class RecipeState {
    data class ShowToast(var message:String):RecipeState()
    data class IsLoading(var state:Boolean = false):RecipeState()
    data class RecipeValidaion(var title:String? = null, var content:String? = null): RecipeState()
    data class Error(var err:String?): RecipeState()
    data class IsSuccess(var what:Int? = null):RecipeState()
    object Reset : RecipeState()
}