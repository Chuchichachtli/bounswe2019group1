package com.project.khajit_app.activity.ui.otherportfolio

import android.graphics.Color
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentManager

import com.project.khajit_app.R
import com.project.khajit_app.activity.FollowListViewAdapter
import com.project.khajit_app.activity.OnePortfolioViewAdapter
import com.project.khajit_app.activity.OtherOnePortfolioViewAdapter
import com.project.khajit_app.activity.PortfolioListAdapter
import com.project.khajit_app.activity.ui.other_profile.OtherUserProfile
import com.project.khajit_app.activity.ui.profile.UserProfile
import com.project.khajit_app.api.RetrofitClient
import com.project.khajit_app.data.models.*
import com.project.khajit_app.global.User
import interfaces.fragmentOperationsInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.math.RoundingMode

class OtherOnePortfolioFragment : Fragment(), fragmentOperationsInterface {
    var containerId : ViewGroup? = null

    private var list_equips = arrayListOf<String>()
    private var list_values = arrayListOf<String>()

    private lateinit var myportfolioModel: OtherOnePortfolioModel
    private lateinit var top_button: Button

    private lateinit var ladapter: OtherOnePortfolioViewAdapter
    private lateinit var lview: ListView

    var isFollowing = false
    private lateinit var followPortfolioButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        myportfolioModel =
            ViewModelProviders.of(this).get(OtherOnePortfolioModel::class.java)

        val root = inflater.inflate(R.layout.activity_otherportfolio_page, container, false)
        containerId = container

        var name = ""
        name = arguments?.getString("name")!!
        var id = ""
        id = arguments?.getInt("id").toString()

        top_button = root.findViewById(R.id.button_other_portfolio_name) as Button
        top_button.text = name

        followPortfolioButton = root.findViewById(R.id.other_portfolio_button_follow) as Button

        RetrofitClient.instance.retrievePortfolio(id.toString()).enqueue(object :
            Callback<OnePortfolioResponse> {
            override fun onResponse(
                call: Call<OnePortfolioResponse>,
                response: Response<OnePortfolioResponse>
            ) {
                println(response.toString())
                if(response.code() == 200 ){
                    if(response.body()?.detail != null){
                        println("NOT CHANGED")
                    }else{
                        println("CHANGED")
                        if(response.body()?.BTC != null){
                            list_equips.add("BTC")
                            list_values.add("$" + response.body()?.BTC.toString())
                        }
                        if(response.body()?.LTC != null){
                            list_equips.add("LTC")
                            list_values.add("$" + response.body()?.LTC.toString())
                        }
                        if(response.body()?.ETH != null){
                            list_equips.add("ETH")
                            list_values.add("$" + response.body()?.ETH.toString())
                        }
                        if(response.body()?.XAG != null){
                            list_equips.add("XAG")
                            var eur = response.body()?.XAG
                            var n_e = BigDecimal(eur!!).setScale(4, RoundingMode.HALF_EVEN)
                            list_values.add("$" + n_e.toString())
                        }
                        if(response.body()?.XAU != null){
                            list_equips.add("XAU")
                            var eur = response.body()?.XAU
                            var n_e = BigDecimal(eur!!).setScale(4, RoundingMode.HALF_EVEN)
                            list_values.add("$" + n_e.toString())
                        }
                        if(response.body()?.GOOGL != null){
                            list_equips.add("GOOGL")
                            list_values.add("$" + response.body()?.GOOGL.toString())
                        }
                        if(response.body()?.AAPL != null){
                            list_equips.add("AAPL")
                            list_values.add("$" + response.body()?.AAPL.toString())
                        }
                        if(response.body()?.GM != null){
                            list_equips.add("GM")
                            list_values.add("$" + response.body()?.GM.toString())
                        }
                        if(response.body()?.EUR != null){
                            list_equips.add("EUR")
                            var eur = response.body()?.EUR
                            var n_e = BigDecimal(1/(eur!!)).setScale(4, RoundingMode.HALF_EVEN)
                            list_values.add("$" + n_e.toString())
                        }
                        if(response.body()?.GBP != null){
                            list_equips.add("GBP")
                            var eur = response.body()?.GBP
                            var n_e = BigDecimal(1/(eur!!)).setScale(4, RoundingMode.HALF_EVEN)

                            list_values.add("$" + n_e.toString())
                        }
                        if(response.body()?.TRY != null){
                            list_equips.add("TRY")
                            var eur = response.body()?.TRY
                            var n_e = BigDecimal(1/(eur!!)).setScale(4, RoundingMode.HALF_EVEN)
                            list_values.add("$" + n_e.toString())
                        }
                        if(response.body()?.DJI != null){
                            list_equips.add("DJI")
                            list_values.add("$" + response.body()?.DJI.toString())
                        }
                        if(response.body()?.IXIC != null){
                            list_equips.add("IXIC")
                            list_values.add("$" + response.body()?.IXIC.toString())
                        }
                        if(response.body()?.INX != null){
                            list_equips.add("INX")
                            list_values.add("$" + response.body()?.INX.toString())
                        }
                        if(response.body()?.SPY != null){
                            list_equips.add("SPY")
                            list_values.add(response.body()?.SPY.toString())
                        }
                        if(response.body()?.IVV != null){
                            list_equips.add("IVV")
                            list_values.add(response.body()?.IVV.toString())
                        }
                        if(response.body()?.VTI != null){
                            list_equips.add("VTI")
                            list_values.add(response.body()?.VTI.toString())
                        }

                        lview =  root.findViewById(R.id.list_other_portfolio_one) as ListView
                        ladapter = OtherOnePortfolioViewAdapter(this@OtherOnePortfolioFragment, list_equips, list_values)
                        lview.adapter = ladapter

                    }
                }else{

                }
            }
            override fun onFailure(call: Call<OnePortfolioResponse>, t: Throwable) {

            }
        })

        RetrofitClient.instance.isFollowingThisPortfolio(id.toString()).enqueue(object :
            Callback<isFollowingPortfolioResponse> {
            override fun onResponse(
                call: Call<isFollowingPortfolioResponse>,
                response: Response<isFollowingPortfolioResponse>
            ) {
                println(response.toString())
                if(response.code() == 200 ){
                    if(response.body()?.detail != null){
                        println("NOT CHANGED")
                    }else{
                        println("CHANGED")
                        if(response.body()?.result == "Found") {
                            isFollowing = true
                            followPortfolioButton.text = "UNFOLLOW"
                            followPortfolioButton.setBackgroundColor(Color.parseColor("#AAB80707"))
                        }else {
                            isFollowing = false
                            followPortfolioButton.text = "FOLLOw"
                            followPortfolioButton.setBackgroundColor(Color.parseColor("#AA4AE608"))
                        }
                    }
                }else{

                }
            }
            override fun onFailure(call: Call<isFollowingPortfolioResponse>, t: Throwable) {

            }
        })

        followPortfolioButton.setOnClickListener { root->
            if(isFollowing == true) {
                unfollowPortfolio(root, id)
            }else {
                followPortfolio(root, id)
            }
        }
        var button_back = root.findViewById(R.id.other_portfolio_button_back_one) as Button
        button_back.setOnClickListener { root ->
            val parentActivityManager: FragmentManager =
                activity?.supportFragmentManager as FragmentManager
            removeFragment(parentActivityManager)
        }
        return root
    }

    fun followPortfolio(view: View, id: String) {

        var obj = FollowUnfollowModel(id.toInt())
        RetrofitClient.instance.followPortfolio(obj).enqueue(object :
            Callback<FollowUnfollowResponseModel> {
            override fun onResponse(
                call: Call<FollowUnfollowResponseModel>,
                response: Response<FollowUnfollowResponseModel>
            ) {
                println(response.toString())
                if(response.code() == 200 ){
                    if(response.body()?.detail != null){
                        println("NOT CHANGED")
                    }else{
                        println("CHANGED")
                    }
                }else{

                }
            }
            override fun onFailure(call: Call<FollowUnfollowResponseModel>, t: Throwable) {

            }
        })

        val parentActivityManager: FragmentManager =
            activity?.supportFragmentManager as FragmentManager
        removeFragment(parentActivityManager)
    }

    fun unfollowPortfolio(view: View, id: String) {

        var obj = FollowUnfollowModel(id.toInt())
        RetrofitClient.instance.unfollowPortfolio(obj).enqueue(object :
            Callback<FollowUnfollowResponseModel> {
            override fun onResponse(
                call: Call<FollowUnfollowResponseModel>,
                response: Response<FollowUnfollowResponseModel>
            ) {
                println(response.toString())
                if(response.code() == 200 ){
                    if(response.body()?.detail != null){
                        println("NOT CHANGED")
                    }else{
                        println("CHANGED")
                    }
                }else{

                }
            }
            override fun onFailure(call: Call<FollowUnfollowResponseModel>, t: Throwable) {

            }
        })

        val parentActivityManager: FragmentManager =
            activity?.supportFragmentManager as FragmentManager
        removeFragment(parentActivityManager)
    }

    companion object {
        fun newInstance(name: String?, id: Int?): OtherOnePortfolioFragment{
            val myportfolioFrag = OtherOnePortfolioFragment()
            val args = Bundle()
            args.putSerializable("name", name)
            args.putSerializable("id", id)
            myportfolioFrag.arguments = args
            return myportfolioFrag
        }

    }

}

