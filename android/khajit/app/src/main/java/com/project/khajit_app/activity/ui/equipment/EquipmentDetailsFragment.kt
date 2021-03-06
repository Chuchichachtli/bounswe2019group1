package com.project.khajit_app.activity.ui.equipment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.project.khajit_app.R
import com.project.khajit_app.activity.ui.prediction.MakePredictionFragment
import com.project.khajit_app.api.RetrofitClient
import interfaces.fragmentOperationsInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EquipmentDetailsFragment : Fragment(), fragmentOperationsInterface {

    var containerId : ViewGroup? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)

        val root = inflater.inflate(R.layout.fragment_equipment_details, container, false)
        containerId = container

        val equipmentName = root.findViewById(R.id.details_equipment_name) as TextView
        equipmentName.text = arguments?.getString("equipment")

        val equipmentValue = root.findViewById(R.id.details_equipment_value) as TextView
        equipmentValue.text = arguments?.getString("value")
        val isGuest = arguments?.getInt("isGuest")
        //this means user is not guess
        val equipmentBuy = root.findViewById(R.id.details_equipment_buy) as Button
        val equipmentSell = root.findViewById(R.id.details_equipment_sell) as Button
        val equipmentPredict = root.findViewById(R.id.details_equipment_predict) as Button
        val equipmentBuyOrder = root.findViewById(R.id.details_equipment_buy_order) as Button
        val equipmentSellOrder = root.findViewById(R.id.details_equipment_sell_order) as Button
        //val equipmentFollow = root.findViewById(R.id.details_equipment_follow) as Button
        if(isGuest == 0){

            equipmentBuy.visibility = View.VISIBLE
            equipmentSell.visibility = View.VISIBLE
            equipmentPredict.visibility = View.VISIBLE
            equipmentBuyOrder.visibility = View.VISIBLE
            equipmentSellOrder.visibility = View.VISIBLE
            equipmentBuy.setOnClickListener { goToEquipmentBuy(arguments?.getString("equipment")!!, arguments?.getString("value")!!) }
            equipmentSell.setOnClickListener { goToEquipmentSell(arguments?.getString("equipment")!!, arguments?.getString("value")!!) }
            equipmentPredict.setOnClickListener { goToEquipmentPredict(arguments?.getString("equipment")!!, arguments?.getString("value")!!) }
            equipmentBuyOrder.setOnClickListener { goToEquipmentBuyOrder(arguments?.getString("equipment")!!, arguments?.getString("value")!!) }
            equipmentSellOrder.setOnClickListener { goToEquipmentSellOrder(arguments?.getString("equipment")!!, arguments?.getString("value")!!) }
            //equipmentFollow.setOnClickListener { equipmentFollow(arguments?.getString("equipment")!!) }

        }else{
            equipmentBuy.visibility = View.INVISIBLE
            equipmentSell.visibility = View.INVISIBLE
            equipmentPredict.visibility = View.INVISIBLE
            equipmentBuyOrder.visibility = View.INVISIBLE
            equipmentSellOrder.visibility = View.INVISIBLE
        }


        return root
    }

    fun goToEquipmentBuy(equipment : String, value : String) {

        val parentActivityManager : FragmentManager = activity?.supportFragmentManager as FragmentManager

        fragmentTransaction(
            parentActivityManager,
            EquipmentBuyFragment.newInstance(equipment, value),
            (containerId!!.id),
            false,
            true,
            false
        )
    }

    fun goToEquipmentSell(equipment : String, value : String) {

        val parentActivityManager : FragmentManager = activity?.supportFragmentManager as FragmentManager

        fragmentTransaction(
            parentActivityManager,
            EquipmentSellFragment.newInstance(equipment, value),
            (containerId!!.id),
            false,
            true,
            false
        )
    }

    fun goToEquipmentPredict(equipment : String, value : String) {

        val parentActivityManager : FragmentManager = activity?.supportFragmentManager as FragmentManager

        fragmentTransaction(
            parentActivityManager,
            MakePredictionFragment.newInstance(equipment, value),
            (containerId!!.id),
            false,
            true,
            false
        )
    }

    fun goToEquipmentBuyOrder(equipment : String, value : String) {

        val parentActivityManager : FragmentManager = activity?.supportFragmentManager as FragmentManager

        fragmentTransaction(
            parentActivityManager,
            EquipmentBuyOrderFragment.newInstance(equipment, value),
            (containerId!!.id),
            false,
            true,
            false
        )
    }

    fun goToEquipmentSellOrder(equipment : String, value : String) {

        val parentActivityManager : FragmentManager = activity?.supportFragmentManager as FragmentManager

        fragmentTransaction(
            parentActivityManager,
            EquipmentSellOrderFragment.newInstance(equipment, value),
            (containerId!!.id),
            false,
            true,
            false
        )
    }

    fun equipmentFollow(equipment : String) {
        // Follow the equipment

    }


    companion object {
        fun newInstance(equipment : String, value : String,isGuest : Int): EquipmentDetailsFragment {
            val fragmentEquipmentDetails = EquipmentDetailsFragment()
            val args = Bundle()
            args.putSerializable("equipment",equipment)
            args.putSerializable("value",value)
            args.putSerializable("isGuest",isGuest)
            fragmentEquipmentDetails.arguments = args
            return fragmentEquipmentDetails
        }

    }
}