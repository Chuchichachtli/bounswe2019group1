package com.project.khajit_app.activity.ui.article

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.core.text.trimmedLength
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.project.khajit_app.R
import com.project.khajit_app.activity.ui.annotation.CreateAnnotationFragment
import com.project.khajit_app.activity.ui.annotation.DisplayAnnotationFromSourceFragment
import com.project.khajit_app.api.RetrofitClient
import com.project.khajit_app.data.annotationModels.GetAnnotationModelResponse
import com.project.khajit_app.data.annotationModels.ShowTextAnnotationModel
import com.project.khajit_app.data.annotationModels.sourceModel
import com.project.khajit_app.data.models.*
import com.project.khajit_app.databinding.DisplayArticleFragmentBinding
import com.project.khajit_app.databinding.DisplayCommentFragmentRecyclerviewItemBinding
import com.project.khajit_app.global.User
import interfaces.fragmentOperationsInterface
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.net.URLEncoder

class displayArticleFragment : Fragment(), fragmentOperationsInterface {

    private var isGuest : Int = 0
    private var isLoggedInUser : Int = 0
    private var isFeedPage: Int = 0
    private var isFollowing : Int = 0
    private var userId : Int = 0
    private var article_id : Int = 0
    private lateinit var contentOfComment : EditText
    private lateinit var articleModel : GeneralArticleModel
    private lateinit var articleSpannableModel : GeneralArticleSpannableModel
    private var containerId : ViewGroup? = null
    private var annotationTexts = ArrayList<ShowTextAnnotationModel>()
    private  var annotationSource = ""
    private lateinit var spannable : SpannableString
    private lateinit var imageView : ImageView
    private lateinit var contentView : TextView
    private lateinit var displayArticleFragmentBinding : DisplayArticleFragmentBinding
    private lateinit var recyclerView : RecyclerView
    private lateinit var  likeLayout : RelativeLayout
    private lateinit var dislikeLayout : RelativeLayout
    private lateinit var likeCountView : TextView
    private lateinit var dislikeCountView : TextView
    private var likeCount : Int = 0
    private var dislikeCount : Int = 0
    private var commentIds = ArrayList<Int>()
    private var commentTexts = ArrayList<String>()
    private var authors = ArrayList<String>()


    companion object {

        private const val ARTICLEMODEL = "model"
        private const val ISGUEST = "isGuest"
        private const val ISLOGGEDINUSER = "isLoggedInUser"
        private const val ISFEEDPAGE = "isFeedPage"
        private  const val ISFOLLOWING = "isFollowing"
        private  const val USERID = "userId"

        fun newInstance(generalArticleModel: GeneralArticleModel,isGuest : Int,isLoggedInUser : Int,isFeedPage: Int, isFollowing : Int, userId: Int): displayArticleFragment {
            val args = Bundle()
            args.putSerializable(ARTICLEMODEL, generalArticleModel)
            args.putInt(ISGUEST,isGuest)
            args.putInt(ISLOGGEDINUSER,isLoggedInUser)
            args.putInt(ISFEEDPAGE,isFeedPage)
            args.putInt(ISFOLLOWING,isFollowing)
            args.putInt(USERID,userId)
            val fragment = displayArticleFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var viewModel: DisplayArticleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //return inflater.inflate(R.layout.display_article_fragment, container, false)
        annotationTexts.clear()
        displayArticleFragmentBinding =
            DisplayArticleFragmentBinding.inflate(inflater, container, false)
        val parentActivityManager : FragmentManager = activity?.supportFragmentManager as FragmentManager

        displayArticleFragmentBinding.articlSpanableModel = null
        imageView = displayArticleFragmentBinding.articleImageDisplayDetails
        containerId = container
        contentView = displayArticleFragmentBinding.articleContentDetails
        articleModel = arguments!!.getSerializable(ARTICLEMODEL) as GeneralArticleModel
        isGuest = arguments!!.getInt(ISGUEST)
        isLoggedInUser = arguments!!.getInt(ISLOGGEDINUSER)
        isFeedPage = arguments!!.getInt(ISFEEDPAGE)
        isFollowing = arguments!!.getInt(ISFOLLOWING)
        userId = arguments!!.getInt(USERID)

        article_id = articleModel.id!!
        spannable = SpannableString(articleModel.content)

        val createCommentItem = displayArticleFragmentBinding.displayArticleWriteCommentButton
        contentOfComment = displayArticleFragmentBinding.displayArticleWriteComment
        likeLayout = displayArticleFragmentBinding.likeLayout!!
        dislikeLayout = displayArticleFragmentBinding.dislikeLayout!!

        likeCountView = displayArticleFragmentBinding.numberOfLike
        dislikeCountView = displayArticleFragmentBinding.numberOfDislike


        if(articleModel.image != null)
            Glide.with(activity).load(articleModel.image).into(imageView)
        else{
            val imgResId = R.drawable.ic_article_image_no_content_foreground
            imageView.setImageResource(imgResId)

        }
        val user = "http://www.khajiittraders.tk/user/{${User.id!!}/"

        if(isGuest == 1){
            createCommentItem.visibility = View.INVISIBLE
            contentOfComment.visibility = View.INVISIBLE
            getLikeDislike()

        }else{

            createCommentItem.visibility = View.VISIBLE
            contentOfComment.visibility = View.VISIBLE
            createCommentItem.setOnClickListener(createCommentButtonListener)

            likeLayout.setOnClickListener(likeViewListener)
            dislikeLayout.setOnClickListener(dislikeViewListener)

            contentView.setCustomSelectionActionModeCallback(object :
                ActionMode.Callback {
                override fun onCreateActionMode(
                    mode: ActionMode,
                    menu: Menu?
                ): Boolean {
                    mode.menuInflater.inflate(R.menu.annotation_create_menu, menu)
                    return true
                }

                override fun onPrepareActionMode(
                    mode: ActionMode?,
                    menu: Menu?
                ): Boolean {
                    return false
                }

                override fun onActionItemClicked(
                    mode: ActionMode,
                    item: MenuItem
                ): Boolean {
                    if (item.getItemId() == R.id.annotation_write_comment) {
                        val selStart: Int = contentView.getSelectionStart()
                        val selEnd: Int = contentView.getSelectionEnd()
                        fragmentTransaction(
                            parentActivityManager,
                            CreateAnnotationFragment.newInstance(user,annotationSource,selStart,selEnd),
                            (containerId!!.id),
                            true,
                            true,
                            false)
                        return true// annotateClicked(selStart, selEnd)
                    }
                    return false
                }

                override fun onDestroyActionMode(mode: ActionMode) {}
            })

            val handlerThreadInitializeLikes = HandlerThread("LIKE DISLIKE INITIALIZE THREAD")
            handlerThreadInitializeLikes.start()
            val handlerThreadInitializeLikesLoop = Handler(handlerThreadInitializeLikes.looper)
            handlerThreadInitializeLikesLoop.postDelayed({

                RetrofitClient.instance.getIsLikeByArticleId(article_id).enqueue(object :
                    Callback<ArticleLikeDisLikeResponseModel> {
                    override fun onResponse(
                        call: Call<ArticleLikeDisLikeResponseModel>,
                        response: Response<ArticleLikeDisLikeResponseModel>
                    ) {
                        println(response.toString())
                        print("response " + (response.code() == 200 ))

                        val isLiked = response.body()!!.result

                        if(isLiked){
                            likeLayout.setBackgroundColor(Color.parseColor("#B5A33535"))
                            dislikeLayout.setBackgroundColor(Color.parseColor("#ADFFFFFF"))

                        }else{
                            RetrofitClient.instance.getIsDislikeByArticleId(article_id).enqueue(object :
                                Callback<ArticleLikeDisLikeResponseModel> {
                                override fun onResponse(
                                    call: Call<ArticleLikeDisLikeResponseModel>,
                                    response: Response<ArticleLikeDisLikeResponseModel>
                                ) {
                                    println(response.toString())
                                    print("response " + (response.code() == 200 ))
                                    val isDisLiked = response.body()!!.result

                                    if(isDisLiked){
                                        dislikeLayout.setBackgroundColor(Color.parseColor("#B5A33535"))
                                        likeLayout.setBackgroundColor(Color.parseColor("#ADFFFFFF"))
                                    }else{
                                        likeLayout.setBackgroundColor(Color.parseColor("#ADFFFFFF"))
                                        dislikeLayout.setBackgroundColor(Color.parseColor("#ADFFFFFF"))
                                    }




                                }
                                override fun onFailure(call: Call<ArticleLikeDisLikeResponseModel>, t: Throwable) {
                                    println(t.message)
                                    println(t)
                                    Toast.makeText(context,t.message,Toast.LENGTH_LONG).show()
                                }
                            })

                        }



                        RetrofitClient.instance.getLikeCountByArticleId(article_id).enqueue(object :
                            Callback<ArticleLikeDislikeCountResponseModel> {
                            override fun onResponse(
                                call: Call<ArticleLikeDislikeCountResponseModel>,
                                response: Response<ArticleLikeDislikeCountResponseModel>
                            ) {
                                if(response.code() == 200 ){
                                    likeCount = response.body()!!.count!!
                                    likeCountView.setText(likeCount.toString() )


                                }else{
                                    print("nalaka")
                                    Log.d("error message:", response.message())
                                }
                            }
                            override fun onFailure(call: Call<ArticleLikeDislikeCountResponseModel>, t: Throwable) {
                                println(t.message)
                                println(t)
                                Toast.makeText(context,t.message,Toast.LENGTH_LONG).show()
                            }
                        })

                        RetrofitClient.instance.getDislikeCountByArticleId(article_id).enqueue(object :
                            Callback<ArticleLikeDislikeCountResponseModel> {
                            override fun onResponse(
                                call: Call<ArticleLikeDislikeCountResponseModel>,
                                response: Response<ArticleLikeDislikeCountResponseModel>
                            ) {
                                if(response.code() == 200 ){
                                    dislikeCount = response.body()!!.count!!


                                    dislikeCountView.setText(dislikeCount.toString())

                                }else{
                                    print("nalaka")
                                    Log.d("error message:", response.message())
                                }
                            }
                            override fun onFailure(call: Call<ArticleLikeDislikeCountResponseModel>, t: Throwable) {
                                println(t.message)
                                println(t)
                                Toast.makeText(context,t.message,Toast.LENGTH_LONG).show()
                            }
                        })



                    }

                    override fun onFailure(call: Call<ArticleLikeDisLikeResponseModel>, t: Throwable) {
                        println(t.message)
                        println(t)
                        Toast.makeText(context,t.message,Toast.LENGTH_LONG).show()
                    }
                })

            }, 1000)

        }













        recyclerView = displayArticleFragmentBinding.displayArticleFragmentCommentRecyclerview






        val handlerThread = HandlerThread("NETWORK_FALAN MAHMUT")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        handler.postDelayed({
            annotationSource = "http://www.khajiittraders.tk/article/" + article_id+ "/"
            RetrofitClient.instanceAnnotation.getAnnotationsBySource(annotationSource).enqueue(object :
                Callback<GetAnnotationModelResponse> {
                override fun onResponse(
                    call: Call<GetAnnotationModelResponse>,
                    response: Response<GetAnnotationModelResponse>
                ) {

                    println("\n #########################")
                    println(response.body())
                    //val oneAnnotation = ShowTextAnnotationModel("sagasg",null,null, "ljsfgh",8,16)
                    if(response.code() == 200 && response.body()!!.result != null){
                        for(resultAnnotationModel in response.body()!!.result) {
                            if( resultAnnotationModel.target!!.type == "text" || resultAnnotationModel.target!!.type == "Text"){
                                val startChar = resultAnnotationModel.target!!.selector.refinedBy.start
                                val endChar = resultAnnotationModel.target!!.selector.refinedBy.end
                                val annotationText = articleModel.content!!.substring(startChar,endChar)
                                println("-------------> " + annotationText)
                                val oneAnnotation = ShowTextAnnotationModel(resultAnnotationModel.id!!,resultAnnotationModel.creator!!,resultAnnotationModel.body!!,annotationText,startChar,endChar,"","")
                                annotationTexts.add(oneAnnotation)



                            }

                        }
                        for(annotation in annotationTexts){
                            spannable.setSpan( BackgroundColorSpan(Color.parseColor("#FFFFC107")), annotation.startChar, annotation.endChar,Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                            spannable.setSpan(ClickHandler(activity as Context,annotation.startChar,annotation.endChar,parentActivityManager),annotation.startChar, annotation.endChar,Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                            articleSpannableModel = GeneralArticleSpannableModel(articleModel.id,articleModel.title,spannable,articleModel.author,articleModel.is_public,articleModel.created_date,articleModel.image)
                            displayArticleFragmentBinding.articlSpanableModel = articleSpannableModel
                            articleSpannableModel.author!!.first_name = articleSpannableModel.author!!.first_name + " "
                            contentView.movementMethod = LinkMovementMethod.getInstance()
                        }
                        //Toast.makeText(context,"Response döndü 1 " + annotationTexts.size,Toast.LENGTH_LONG).show()


                        //spannable.setSpan( BackgroundColorSpan(Color.YELLOW), 8, 16,Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                        //spannable.setSpan(ClickHandler(activity as Context,8 ),8,16,Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                        articleSpannableModel = GeneralArticleSpannableModel(articleModel.id,articleModel.title,spannable,articleModel.author,articleModel.is_public,articleModel.created_date,articleModel.image)
                        displayArticleFragmentBinding.articlSpanableModel = articleSpannableModel
                        articleSpannableModel.author!!.first_name = articleSpannableModel.author!!.first_name + " "
                        contentView.movementMethod = LinkMovementMethod.getInstance()



                    }else{
                        println(response.message())
                    }


                }
                override fun onFailure(call: Call<GetAnnotationModelResponse>, t: Throwable) {
                    println(t.message)
                    println(t)
                    Toast.makeText(context,t.message,Toast.LENGTH_LONG).show()
                }
            })




        }, 1000)


        val handlerThreadComment = HandlerThread("COMMENT THREAD")
        handlerThreadComment.start()
        val handlerComment = Handler(handlerThreadComment.looper)
        handlerComment.postDelayed({
            RetrofitClient.instance.getArticleComments(article_id).enqueue(object :
                Callback<ListArticleCommentModel> {
                override fun onResponse(
                    call: Call<ListArticleCommentModel>,
                    response: Response<ListArticleCommentModel>
                ) {
                    println(response.toString())
                    print("response " + (response.code() == 200 ))
                    if(response.code() == 200 ){
                        print("burdayız")
                        commentIds.clear()
                        commentTexts.clear()
                        authors.clear()


                        var results = response.body()?.results as List<ArticleCommentItem>
                        if(results != null){

                            for (comment in results) {
                                commentIds.add(comment.id as Int)
                                commentTexts.add(comment.text as String)
                                authors.add((comment.user!!.first_name + " " + comment.user!!.last_name ))
                            }
                            recyclerView.layoutManager = GridLayoutManager(activity, 1)
                            recyclerView.adapter = CommentListAdapter(commentIds,commentTexts,authors,activity as Context)

                        }


                    }else{
                        print("nalaka")
                        Log.d("error message:", response.message())
                    }
                }
                override fun onFailure(call: Call<ListArticleCommentModel>, t: Throwable) {
                    println(t.message)
                    println(t)
                    Toast.makeText(context,t.message,Toast.LENGTH_LONG).show()
                }
            })



        }, 1000)







        //recyclerView.layoutManager = GridLayoutManager(activity, 1)
        //recyclerView.adapter = CommentListAdapter(commentIds,commentTexts,authors,activity as Context)

        return displayArticleFragmentBinding.root
    }


        internal inner class ClickHandler(

            private val context : Context,
            private val start_position: Int,
            private val end_position: Int,
            private val manager: FragmentManager
        ) : ClickableSpan() {
            override fun onClick(widget: View) {
                //Toast.makeText(context,"annotated position " + position, Toast.LENGTH_LONG).show()


                    fragmentTransaction(
                    manager,
                    DisplayAnnotationFromSourceFragment.newInstance(annotationTexts,start_position,end_position),
                    (containerId!!.id),
                    false,
                    true,
                    false)

            }

            override fun updateDrawState(ds: TextPaint) {
                ds?.isUnderlineText = false
                ds?.color = Color.BLACK
            }
        }

    internal inner class CommentListAdapter(

        val commentIds :ArrayList<Int>,
        val    commentTexts :ArrayList<String>,
        val   authors : ArrayList<String>,
        val context: Context) : RecyclerView.Adapter<ViewHolder>(){

        private val layoutInflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val DisplayArticleCommentRecyclerviewItemBinding =
                DisplayCommentFragmentRecyclerviewItemBinding.inflate(layoutInflater, viewGroup, false)
            return ViewHolder(DisplayArticleCommentRecyclerviewItemBinding.root, DisplayArticleCommentRecyclerviewItemBinding)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

            val commentItem = ShowArticleCommentModel(commentIds[position], commentTexts[position],
                 authors[position])

            viewHolder.setData(commentItem)
        }

        override fun getItemCount() = commentIds.size

    }



    internal inner class ViewHolder constructor(itemView: View,
                                                private val displayArticleListBinding:
                                                DisplayCommentFragmentRecyclerviewItemBinding
    ) :
        RecyclerView.ViewHolder(itemView) {
        fun setData(commentModel : ShowArticleCommentModel) {
            displayArticleListBinding.articleCommentModel = commentModel
        }
    }

    private val createCommentButtonListener = View.OnClickListener { view ->
        var comment_info = contentOfComment.text.toString().trim()
        if (comment_info.isEmpty()) {
            contentOfComment.error = "Title is required."
            contentOfComment.requestFocus()
            return@OnClickListener
        }


        val commentInfo = CreateCommentModel(comment_info,article_id)



        RetrofitClient.instance.createComment(commentInfo).enqueue(object :
            Callback<CreateCommentResponseModel> {
            override fun onResponse(
                call: Call<CreateCommentResponseModel>,
                response: Response<CreateCommentResponseModel>
            ) {
                println(response.toString())
                if(response.code() == 200 ){
                    if(response.body()?.created_date != null){
                        Toast.makeText(activity?.baseContext, "Comment Published!", Toast.LENGTH_SHORT).show()
                        println("Everything looks fine!")
                        val parentActivityManager : FragmentManager = activity?.supportFragmentManager as FragmentManager


                        fragmentTransaction(
                            parentActivityManager,
                            displayArticleFragment.newInstance(articleModel,isGuest,isLoggedInUser,isFeedPage,isFollowing,userId),
                            (containerId!!.id),
                            true,
                            true,
                            false)
                    }else{

                        println("Something went wrong!")
                    }
                }else{

                }
            }
            override fun onFailure(call: Call<CreateCommentResponseModel>, t: Throwable) {
                Toast.makeText(activity?.baseContext, "Request error!", Toast.LENGTH_SHORT).show()

            }
        })


    }


    private val likeViewListener = View.OnClickListener { view ->
        val articleLikeDislike = ArticleLikeDislikeModel(article_id)

        RetrofitClient.instance.likeArticle(articleLikeDislike).enqueue(object :
            Callback<ArticleLikeResponseModel> {
            override fun onResponse(
                call: Call<ArticleLikeResponseModel>,
                response: Response<ArticleLikeResponseModel>
            ) {
                println(response.toString())
                print("response " + (response.code() == 200 ))
                if(response.code() == 200 ){
                    likeLayout.setBackgroundColor(Color.parseColor("#B5A33535"))
                    dislikeLayout.setBackgroundColor(Color.parseColor("#ADFFFFFF"))

                    RetrofitClient.instance.getLikeCountByArticleId(article_id).enqueue(object :
                        Callback<ArticleLikeDislikeCountResponseModel> {
                        override fun onResponse(
                            call: Call<ArticleLikeDislikeCountResponseModel>,
                            response: Response<ArticleLikeDislikeCountResponseModel>
                        ) {
                            if(response.code() == 200 ){
                                likeCount = response.body()!!.count!!
                                likeCountView.setText(likeCount.toString())
                            }else{
                                print("nalaka")
                                Log.d("error message:", response.message())
                            }
                        }
                        override fun onFailure(call: Call<ArticleLikeDislikeCountResponseModel>, t: Throwable) {
                            println(t.message)
                            println(t)
                            Toast.makeText(context,t.message,Toast.LENGTH_LONG).show()
                        }
                    })

                    RetrofitClient.instance.getDislikeCountByArticleId(article_id).enqueue(object :
                        Callback<ArticleLikeDislikeCountResponseModel> {
                        override fun onResponse(
                            call: Call<ArticleLikeDislikeCountResponseModel>,
                            response: Response<ArticleLikeDislikeCountResponseModel>
                        ) {
                            if(response.code() == 200 ){
                                dislikeCount = response.body()!!.count!!
                                dislikeCountView.setText(dislikeCount.toString())

                            }else{
                                print("nalaka")
                                Log.d("error message:", response.message())
                            }
                        }
                        override fun onFailure(call: Call<ArticleLikeDislikeCountResponseModel>, t: Throwable) {
                            println(t.message)
                            println(t)
                            Toast.makeText(context,t.message,Toast.LENGTH_LONG).show()
                        }
                    })





                }else{
                    print("nalaka")
                    Log.d("error message:", response.message())
                }
            }
            override fun onFailure(call: Call<ArticleLikeResponseModel>, t: Throwable) {
                println(t.message)
                println(t)
                Toast.makeText(context,t.message,Toast.LENGTH_LONG).show()
            }
        })

    }

    private val dislikeViewListener = View.OnClickListener { view ->
        val articleDislike = ArticleLikeDislikeModel(article_id)
        RetrofitClient.instance.dislikeArticle(articleDislike).enqueue(object :
            Callback<ArticleDislikeResponseModel> {
            override fun onResponse(
                call: Call<ArticleDislikeResponseModel>,
                response: Response<ArticleDislikeResponseModel>
            ) {
                println(response.toString())
                print("response " + (response.code() == 200 ))
                if(response.code() == 200 ){
                    dislikeLayout.setBackgroundColor(Color.parseColor("#B5A33535"))
                    likeLayout.setBackgroundColor(Color.parseColor("#ADFFFFFF"))
                    RetrofitClient.instance.getLikeCountByArticleId(article_id).enqueue(object :
                        Callback<ArticleLikeDislikeCountResponseModel> {
                        override fun onResponse(
                            call: Call<ArticleLikeDislikeCountResponseModel>,
                            response: Response<ArticleLikeDislikeCountResponseModel>
                        ) {
                            if(response.code() == 200 ){
                                likeCount = response.body()!!.count!!
                                likeCountView.setText(likeCount.toString())

                            }else{
                                print("nalaka")
                                Log.d("error message:", response.message())
                            }
                        }
                        override fun onFailure(call: Call<ArticleLikeDislikeCountResponseModel>, t: Throwable) {
                            println(t.message)
                            println(t)
                            Toast.makeText(context,t.message,Toast.LENGTH_LONG).show()
                        }
                    })

                    RetrofitClient.instance.getDislikeCountByArticleId(article_id).enqueue(object :
                        Callback<ArticleLikeDislikeCountResponseModel> {
                        override fun onResponse(
                            call: Call<ArticleLikeDislikeCountResponseModel>,
                            response: Response<ArticleLikeDislikeCountResponseModel>
                        ) {
                            if(response.code() == 200 ){
                                dislikeCount = response.body()!!.count!!
                                dislikeCountView.setText(dislikeCount.toString())

                            }else{
                                print("nalaka")
                                Log.d("error message:", response.message())
                            }
                        }
                        override fun onFailure(call: Call<ArticleLikeDislikeCountResponseModel>, t: Throwable) {
                            println(t.message)
                            println(t)
                            Toast.makeText(context,t.message,Toast.LENGTH_LONG).show()
                        }
                    })





                }else{
                    print("nalaka")
                    Log.d("error message:", response.message())
                }
            }
            override fun onFailure(call: Call<ArticleDislikeResponseModel>, t: Throwable) {
                println(t.message)
                println(t)
                Toast.makeText(context,t.message,Toast.LENGTH_LONG).show()
            }
        })

    }

    fun getLikeDislike(){
        RetrofitClient.instance.getLikeCountByArticleId(article_id).enqueue(object :
            Callback<ArticleLikeDislikeCountResponseModel> {
            override fun onResponse(
                call: Call<ArticleLikeDislikeCountResponseModel>,
                response: Response<ArticleLikeDislikeCountResponseModel>
            ) {
                if(response.code() == 200 ){
                    likeCount = response.body()!!.count!!
                    likeCountView.setText(likeCount.toString())

                }else{
                    print("nalaka")
                    Log.d("error message:", response.message())
                }
            }
            override fun onFailure(call: Call<ArticleLikeDislikeCountResponseModel>, t: Throwable) {
                println(t.message)
                println(t)
                Toast.makeText(context,t.message,Toast.LENGTH_LONG).show()
            }
        })

        RetrofitClient.instance.getDislikeCountByArticleId(article_id).enqueue(object :
            Callback<ArticleLikeDislikeCountResponseModel> {
            override fun onResponse(
                call: Call<ArticleLikeDislikeCountResponseModel>,
                response: Response<ArticleLikeDislikeCountResponseModel>
            ) {
                if(response.code() == 200 ){
                    dislikeCount = response.body()!!.count!!
                    dislikeCountView.setText(dislikeCount.toString())

                }else{
                    print("nalaka")
                    Log.d("error message:", response.message())
                }
            }
            override fun onFailure(call: Call<ArticleLikeDislikeCountResponseModel>, t: Throwable) {
                println(t.message)
                println(t)
                Toast.makeText(context,t.message,Toast.LENGTH_LONG).show()
            }
        })
    }






}



