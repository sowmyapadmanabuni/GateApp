/*
package com.oyespace.guards.snippet

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


*/
/**
 * Created by Kalyan on 22-Oct-17.
 *
 *
 *//*

data class HotelInfo(val rest_list: ArrayList<HotelData>?, val banners: ArrayList<BannerInfo>?)

@SuppressLint("ParcelCreator")
@Parcelize
data class BannerInfo(val bgurl: String, val id: Int) : Parcelable

data class HotelMenuReq(val hotelId: String?)

@SuppressLint("ParcelCreator")
@Parcelize
data class MenuData(@SerializedName("count") var itemCount: Int = 0, val menuprice: Int?, val id: String?, val discount: Double?, val discountprice: Int?, val isVeg: Int?, val type: Int?, val avaliabilty: String?, val name: String?, val menuimage: String?, val discription: String?, val cc: Int?) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class CategoryData(val caetgoryId: Int?, val Name: String?, val menu: ArrayList<MenuData>?) : Parcelable

data class MenuListResponse(val uuid: String?, val categories: ArrayList<CategoryData>?)
data class CartData(val hotelData: HotelData?, val menuList: ArrayList<MenuData>)
data class GlobalApiObject<T>(val statuscode: Int?, val statusMessage: String?, val data: T)


@SuppressLint("ParcelCreator")
@Parcelize
data class SearchResult(val name: String?, val isHeader: Boolean = false, val subText: String? = "", val lat: String = "", val lng: String = "") : Parcelable

data class GoogleResults(val predictions: ArrayList<Predictions>?)
data class Predictions(val place_id: String?, val structured_formatting: PredictionsFormat)
data class PredictionsFormat(val main_text: String?, val secondary_text: String?)

data class LoginResp(val userdata: LoginDetails?, val address: ArrayList<AddressData>?)

@SuppressLint("ParcelCreator")
@Parcelize
data class AddressData(var address: String?, val lat: String? = "", val lng: String? = "", var addressID: String = "", var isPrimary: Boolean = false, var apikey: String = "") : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class SignUpReq(val username: String?, val email: String?, val mobile: String?, val password: String?, var otp: String? = "") : Parcelable

data class VerifyOtpResp(val otp: String?, val message: String?)
data class AdressId(val id: String?)
data class UpdateCartReq(val restruantid: String?, val itemjson: ArrayList<MenuData>, val containercharge: Int?)
data class UpdateCartResp(val cartData: ArrayList<MenuData>, val itemSubtotal: Double?, val DiscountApplied: Double?, val discount: Double?, val subtotalAfterDiscount: Double?, val DeliveryCharges: Double?, val GST: Double?, val paybaleamount: Double?, val containercharges: Int?, val onlinePaymentEnable: Int?, val cashOnDeliveryEnable: Int?)
data class OrderListReq(val userapikey: String)

@SuppressLint("ParcelCreator")
@Parcelize
data class CoupanCodeReq(val userapikey: String, var couponcode: String, val totalamount: Double?, var restid: String?) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class CoupanCodeResp(val isCouponApplied: Int, var couponString: String, val CoupondiscountDeduction: Int, val couponpayableamount: Double?) : Parcelable

data class FinalCartReq(val jsonitem: ArrayList<MenuData>, val restid: String, val cc: Int?, val ordercode: String, val userapikey: String, val username: String, val usermobile: String, val useraddress: String, var userpayment: String, val couponstatus: Int, val couponcodeprice: Int, val couponcode: String, val userpayedamount: Double, val userordernote: String, val useremail: String)
@SuppressLint("ParcelCreator")
@Parcelize
data class PaymentData(val nameofpayee: String, val emailofpayee: String, val phoneofpayee: String, val totalmaount: String) : Parcelable


data class FinalCartResp(val ordercode: String)
data class SearchReq(val key: String)

@SuppressLint("ParcelCreator")
@Parcelize
data class Discount(var discount: Double? = 0.0, val discountMinOrder: Double? = 0.0, val discountDiscription: String?) : Parcelable

data class UserDeviceDetailReq(var deviceplatform: String?, val devicename: String?,val deviceid: String?,val registrationid: String?, val apikey: String?)

*/
