package com.project.khajit_app.api

import com.project.khajit_app.data.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface Api {

    @Headers("Content-Type: application/json")
    @POST("user/registerbasic/")
    fun createBasicUser(@Body body: BasicUser?):Call<BasicRegisterResponse>

    @Headers("Content-Type: application/json")
    @POST("user/registertrader/")
    fun createTraderUser(@Body body: TraderUser):Call<BasicRegisterResponse>  //trader user response yapılması lazım

    @Headers("Content-Type: application/json")
    @POST("user/login/")
    fun loginUser(@Body body: userToBeLogin?):Call<LoginResponse>

    @Headers("Content-Type: application/json")
    @GET("user/retrieve/{id}")
    fun getInfo(@Path(value = "id", encoded = true) userId: String):Call<GenericUserModel>
    //fun getInfo(@Body body: UserInfoGet?):Call<UserAllInfo>

    @Headers("Content-Type: application/json")
    @GET("follow/listFollowing/")
    fun followingList():Call<GeneralFollowModel>

    @Headers("Content-Type: application/json")
    @GET("follow/listFollower/")
    fun followerList():Call<GeneralFollowModel2>

    @Headers("Content-Type: application/json")
    @GET("follow/listFollowingWithIdFront/{id}")
    fun followingListID(@Path(value = "id", encoded = true) userId: String):Call<GeneralFollowModel>

    @Headers("Content-Type: application/json")
    @GET("follow/listFollowerWithIdFront/{id}")
    fun followerListID(@Path(value = "id", encoded = true) userId: String):Call<GeneralFollowModel2>

    @Headers("Content-Type: application/json")
    @PUT("user/updateuser/")
    fun updateUser(@Body body: UpdateUser):Call<UpdateUserResponse>

    //@Headers("Content-Type: application/json")
    @PUT("user/updateuser/")
    @Multipart
    fun updateProfilePicture(@Part file: MultipartBody.Part):Call<UpdateUserResponse>

    @Headers("Content-Type: application/json")
    @PUT("user/updateuser/")
    fun upgrade_downgrade(@Body body: UpgradeDowngrade):Call<UpdateUserResponse>

    @Headers("Content-Type: application/json")
    @PUT("user/updateuser/")
    fun changePrivacy(@Body body: ChangePrivacy):Call<UpdateUserResponse>

    @Headers("Content-Type: application/json")
    @POST("user/search_user/")
    fun searchUsername(@Body body: SearchRequest):Call<SearchResponse>

    @Headers("Content-Type: application/json")
    @GET("article/search/{key}/")
    fun searchArticle(@Path(value = "key", encoded = true) key: String):Call<ArticleSearchResponse>

    @Headers("Content-Type: application/json")
    @GET("event/search/{key}/")
    fun searchEvent(@Path(value = "key", encoded = true) key: String):Call<ListEventResponse>

    // TODO
    @Headers("Content-Type: application/json")
    @PUT("user/updatepass/")
    fun changePassword(@Body body: PasswordChange):Call<GenericUserModel>

    @Headers("Content-Type: application/json")
    @PUT("user/updateuser/")
    fun changeIban(@Body body: UpgradeDowngrade):Call<UpdateUserResponse>

    @Headers("Content-Type: application/json")
    @PUT("user/userupgrade/")
    fun upgradeUser():Call<GenericUserModel>

    @Headers("Content-Type: application/json")
    @PUT("user/userdowngrade/")
    fun downgradeUser():Call<GenericUserModel>

    @Headers("Content-Type: application/json")
    @GET("follow/isFollowingFront/{id}/")
    fun isFollowing(@Path(value = "id", encoded = true) userId: String):Call<isFollowingResponseModel>

    @Headers("Content-Type: application/json")
    @POST("follow/follow/")
    fun followUser(@Body body: FollowIDModel?):Call<FollowIDModelResponse>

    @Headers("Content-Type: application/json")
    @HTTP(method="DELETE", path="follow/delete/", hasBody=true)
    fun unfollowUser(@Body body: FollowIDModel?):Call<FollowIDModelResponse>

    @Headers("Content-Type: application/json")
    @GET("equipment/currency/")
    fun currencyValues():Call<CurrencyResponse>

    @Headers("Content-Type: application/json")
    @GET("equipment/metalcurrency/")
    fun commodityValues():Call<CommodityResponse>

    @Headers("Content-Type: application/json")
    @GET("equipment/cryptocurrency/")
    fun cryptoValues():Call<CryptoResponse>

    @Headers("Content-Type: application/json")
    @GET("equipment/etfs/")
    fun etfValues():Call<ETFResponse>

    @Headers("Content-Type: application/json")
    @GET("equipment/stock/")
    fun stockValues():Call<StockResponse>

    @Headers("Content-Type: application/json")
    @GET("equipment/traceindices/")
    fun tradeValues():Call<TradeIndiceResponse>

    /*@Headers("Content-Type: application/json")
    @POST("article/create/")
    fun createArticle(@Body body: CreateArticleModel?):Call<CreateArticleResponseModel>*/

    @POST("article/create/")
    @Multipart
    fun createArticle(
        @Part("title") title: RequestBody,
        @Part("content") content : RequestBody,
        @Part("is_public") is_public : Boolean,
        @Part file: MultipartBody.Part): Call<CreateArticleResponseModel>

    @Headers("Content-Type: application/json")
    @POST("article/create/")
    fun createArticleNoImage(@Body body:CreateArticleModel): Call<CreateArticleResponseModel>

    @Headers("Content-Type: application/json")
    @PUT("wallet/sendUSD/")
    fun depositFunds(@Body body: DepositFundsModel):Call<DepositFundsResponse>

    @Headers("Content-Type: application/json")
    @PUT("wallet/takeequipment/")
    fun buyEquipment(@Body body: EquipmentBSModel):Call<DepositFundsResponse>

    @Headers("Content-Type: application/json")
    @PUT("wallet/sellequipment/")
    fun sellEquipment(@Body body: EquipmentBSModel):Call<DepositFundsResponse>

    @Headers("Content-Type: application/json")
    @GET("wallet/retrieve/")
    fun myWallet():Call<WalletResponse>

    @Headers("Content-Type: application/json")
    @GET("article/listPublicArticles/" )
    fun getPublicArticles():Call<PublicArticleListResponse>

    @Headers("Content-Type: application/json")
    @GET("article/feed/" )
    fun getUserFeedArticles():Call<PublicArticleListResponse>


    @Headers("Content-Type: application/json")
    @GET("portfolio/listportfolio/{id}/")
    fun listPortfolio(@Path(value = "id", encoded = true) userId: String):Call<ListPortfolioResponse>

    @Headers("Content-Type: application/json")
    @GET("portfolio/retrieveportfolio/{id}/")
    fun retrievePortfolio(@Path(value = "id", encoded = true) userId: String):Call<OnePortfolioResponse>

    @Headers("Content-Type: application/json")
    @PUT("portfolio/updateportfolio/{id}/")
    fun updatePortfolio(@Path(value = "id", encoded = true) userId: String, @Body body: PortfolioEditRequestModel):Call<PortfolioEditResponseModel>

    @Headers("Content-Type: application/json")
    @DELETE("portfolio/deleteportfolio/{id}/")
    fun deletePortfolio(@Path(value = "id", encoded = true) userId: String):Call<PortfolioDeleteResponseModel>

    @Headers("Content-Type: application/json")
    @POST("portfolio/createportfolio/")
    fun createPortfolio(@Body body: PortfolioEditRequestModel):Call<PortfolioEditResponseModel>

    @Headers("Content-Type: application/json")
    @GET("portfolio-follow/isFollowing/{id}/")
    fun isFollowingThisPortfolio(@Path(value = "id", encoded = true) userId: String):Call<isFollowingPortfolioResponse>

    @Headers("Content-Type: application/json")
    @POST("portfolio-follow/follow/")
    fun followPortfolio(@Body body: FollowUnfollowModel):Call<FollowUnfollowResponseModel>

    @Headers("Content-Type: application/json")
    @HTTP(method="DELETE", path="portfolio-follow/unfollow/", hasBody=true)
    fun unfollowPortfolio(@Body body: FollowUnfollowModel):Call<FollowUnfollowResponseModel>

    @Headers("Content-Type: application/json")
    @POST("wallet/createWallet/")
    fun createWallet():Call<createWalletResponse>

    @Headers("Content-Type: application/json")
    @DELETE("wallet/delete/")
    fun deleteWallet():Call<createWalletResponse>

    @Headers("Content-Type: application/json")
    @GET("follow/listFollowingPending/" )
    fun getFollowingPending():Call<FollowingPendingResponseModel>

    @Headers("Content-Type: application/json")
    @GET("article/listArticleByUserId/{id}/")
    fun getArticlesByUserId(@Path(value = "id", encoded = true) userId: Int):Call<PublicArticleListResponse>

    @Headers("Content-Type: application/json")
    @POST("prediction/predict/")
    fun makePrediction(@Body body: PredictionModel):Call<PredictionResponseModel>

    @Headers("Content-Type: application/json")
    @GET("event/list/" )
    fun getAllEvents():Call<ListEventModel>

    @Headers("Content-Type: application/json")
    @GET("notification/listnotification/" )
    fun getNotifications():Call<ListNotificationsResponse>

    @Headers("Content-Type: application/json")
    @GET("follow/listFollowerPending/" )
    fun getPendingFollowers():Call<PendingFollowerResponse>

    @Headers("Content-Type: application/json")
    @PUT("follow/approvefollow/{id}/" )
    fun approveFollower(@Path(value = "id", encoded = true) followId: Int):Call<FollowModel3>

    @Headers("Content-Type: application/json")
    @PUT("follow/rejectfollow/{id}/" )
    fun rejectFollower(@Path(value = "id", encoded = true) followId: Int):Call<FollowModel3>

    @Headers("Content-Type: application/json")
    @POST("notification/createsellorder/")
    fun createSellOrder(@Body body: EquipmentBSOrderModel?):Call<EquipmentBSOrderResponse>

    @Headers("Content-Type: application/json")
    @POST("notification/createbuyorder/")
    fun createBuyOrder(@Body body: EquipmentBSOrderModel?):Call<EquipmentBSOrderResponse>

    @Headers("Content-Type: application/json")
    @GET("article-comment/list/{id}/" )
    fun getArticleComments(@Path(value = "id", encoded = true) articleId: Int):Call<ListArticleCommentModel>

    @Headers("Content-Type: application/json")
    @POST("article-comment/create/")
    fun createComment(@Body body: CreateCommentModel):Call<CreateCommentResponseModel>

    @Headers("Content-Type: application/json")
    @POST("article-like/like/")

    fun likeArticle(@Body body: ArticleLikeDislikeModel):Call<ArticleLikeResponseModel>

    @Headers("Content-Type: application/json")
    @POST("article-like/dislike/")
    fun dislikeArticle(@Body body: ArticleLikeDislikeModel):Call<ArticleDislikeResponseModel>

    @Headers("Content-Type: application/json")
    @GET("article-like/likeCountByArticleId/{articleId}/" )
    fun getLikeCountByArticleId(@Path(value = "articleId", encoded = true) articleId: Int):Call<ArticleLikeDislikeCountResponseModel>

    @Headers("Content-Type: application/json")
    @GET("article-like/dislikeCountByArticleId/{articleId}/" )
    fun getDislikeCountByArticleId(@Path(value = "articleId", encoded = true) articleId: Int):Call<ArticleLikeDislikeCountResponseModel>

    @Headers("Content-Type: application/json")
    @GET("article-like/isDislikedByUser/{articleId}/" )
    fun getIsDislikeByArticleId(@Path(value = "articleId", encoded = true) articleId: Int):Call<ArticleLikeDisLikeResponseModel>

    @Headers("Content-Type: application/json")
    @GET("article-like/isLikedByUser/{articleId}/" )
    fun getIsLikeByArticleId(@Path(value = "articleId", encoded = true) articleId: Int):Call<ArticleLikeDisLikeResponseModel>



}
