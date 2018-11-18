package com.impakter.seller.network;


import com.impakter.seller.adapter.seller.ConversationRespond;
import com.impakter.seller.object.AddProToCollectionRespond;
import com.impakter.seller.object.CartSellerRespond;
import com.impakter.seller.object.CollectionRespond;
import com.impakter.seller.object.CommentRespond;
import com.impakter.seller.object.ContactRespond;
import com.impakter.seller.object.CreateCollectionRespond;
import com.impakter.seller.object.ActionFollowRespond;
import com.impakter.seller.object.HomeCategoryRespond;
import com.impakter.seller.object.HomeLatestRespond;
import com.impakter.seller.object.HomeTrendingRespond;
import com.impakter.seller.object.MenuCategoryRespond;
import com.impakter.seller.object.BaseMessageRespond;
import com.impakter.seller.object.NotificationRespond;
import com.impakter.seller.object.OrderDetailRespond;
import com.impakter.seller.object.OrderRespond;
import com.impakter.seller.object.ProOfCollectionRespond;
import com.impakter.seller.object.ProductByCategoryRespond;
import com.impakter.seller.object.ProductDetailRespond;
import com.impakter.seller.object.seller.CommentItemRespond;
import com.impakter.seller.object.seller.CreateRoomRespond;
import com.impakter.seller.object.seller.ForwardOrderRespond;
import com.impakter.seller.object.seller.ListCommentRespond;
import com.impakter.seller.object.seller.MessageRespond;
import com.impakter.seller.object.seller.ProductSellerRespond;
import com.impakter.seller.object.seller.ProfileRespond;
import com.impakter.seller.object.seller.ReceivedIssueDetailRespond;
import com.impakter.seller.object.seller.ReceivedIssueRespond;
import com.impakter.seller.object.seller.ReceivedOrderDetailRespond;
import com.impakter.seller.object.seller.ReceivedOrderRespond;
import com.impakter.seller.object.RoomRespond;
import com.impakter.seller.object.SellerProfileRespond;
import com.impakter.seller.object.UserRespond;
import com.impakter.seller.object.seller.ReplyCommentRespond;
import com.impakter.seller.object.seller.SendMessageRespond;
import com.impakter.seller.object.seller.StatRespond;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by Minh Toan on 2018-08-16.
 */

public interface ServerAPI {

    // TODO: 16/08/2018 Register Action
    @FormUrlEncoded
    @POST("register")
    Call<UserRespond> register(@Field("firstName") String firstName,
                               @Field("lastName") String lastName,
                               @Field("email") String email,
                               @Field("password") String password);

    // TODO: 16/08/2018 Login Action
    @GET("login")
    Call<UserRespond> login(@Query("email") String email,
                            @Query("password") String password,
                            @Query("deviceToken") String deviceToken,
                            @Query("typeDevice") int typeDevice);

    // TODO: 16/08/2018 Forgot Password Action
    @GET("forgetPassword")
    Call<BaseMessageRespond> forgotPassword(@Query("email") String email);

    // TODO: 16/08/2018 Change password Action
    @FormUrlEncoded
    @POST("changePassword")
    Call<BaseMessageRespond> changePassword(@Field("id") int id,
                                            @Field("oldPassword") String oldPassword,
                                            @Field("newPassword") String newPassword);

    // TODO: 27/08/2018 updateProfile
    @Multipart
    @POST("updateProfile")
    Call<UserRespond> updateProfile(@Part("id") RequestBody id,
                                    @Part("firstName") RequestBody firstName,
                                    @Part("lastName") RequestBody lastName,
                                    @Part("email") RequestBody email,
                                    @Part("address") RequestBody address,
                                    @Part MultipartBody.Part avatar,
                                    @Part MultipartBody.Part cover);

    // TODO: 17/08/2018 Login facebook Action
    @FormUrlEncoded
    @POST("loginFacebook")
    Call<UserRespond> loginFacebook(@Field("socialId") String socialId,
                                    @Field("firstName") String firstName,
                                    @Field("lastName") String lastName,
                                    @Field("email") String email);

    // TODO: 20/08/2018 Get HomeCategory
    @GET("showHomeCategory")
    Call<HomeCategoryRespond> getHomeCategory();

    // TODO: 20/08/2018 GetHomeLatest
    @GET("showHomeLatest")
    Call<HomeLatestRespond> getHomeLatest(@Query("categoryId") String categoryId,
                                          @Query("typeTime") int typeTime,
                                          @Query("page") int page);

    @GET("showHomeTrending")
    Call<HomeTrendingRespond> getHomeTrending(@Query("categoryId") String categoryId,
                                              @Query("page") int page);

    // TODO: 20/08/2018 showHomeBrand
    @GET("showHomeBrand")
    Call<HomeCategoryRespond> getHomeBrand();

    // TODO: 20/08/2018 showHomeBrand
    @GET("productDetails")
    Call<ProductDetailRespond> getProductDetail(@Query("id") int id);

    // TODO: 20/08/2018 showMenuCategory
    @GET("showMenuCategory")
    Call<MenuCategoryRespond> getMenuCategory();

    // TODO: 20/08/2018 get seller profile
    @GET("sellerProfile")
    Call<SellerProfileRespond> getSellerProfile(@Query("sellerId") int sellerId,
                                                @Query("userId") String userId,
                                                @Query("categoryId") String categoryId,
                                                @Query("page") int page);

    // TODO: 20/08/2018 get cart list of seller
    @GET("listCatOfSeller")
    Call<CartSellerRespond> getListCartOfSeller(@Query("sellerId") int sellerId);

    // TODO: 22/08/2018 Get Personal Collection
    @GET("getCollection")
    Call<CollectionRespond> getCollection(@Query("userId") int userId,
                                          @Query("exceptCollectId") String exceptCollectId);

    // TODO: 22/08/2018 Comment
    @GET("comment")
    Call<CommentRespond> comment(@Query("productId") int productId,
                                 @Query("user_id") int userId,
                                 @Query("content") String content,
                                 @Query("page") int page);

    // TODO: 24/08/2018 showProductByCat
    @GET("showProByCat")
    Call<ProductByCategoryRespond> getProductByCategory(@Query("catId") int categoryId,
                                                        @Query("subCatId") int subCatId,
                                                        @Query("page") int page);

    @GET("getProOfCollection")
    Call<ProOfCollectionRespond> getProductOfCollection(@Query("collectId") int collectionId);

    @GET("userInfo")
    Call<UserRespond> getUserInfo(@Query("id") String otherId,
                                  @Query("userId") String userId);

    // TODO: 06/09/2018 create collection
    @FormUrlEncoded
    @POST("createCollection")
    Call<CreateCollectionRespond> createCollection(@Field("name") String name,
                                                   @Field("userId") int userId);

    // TODO: 06/09/2018 Delete collection
    @FormUrlEncoded
    @POST("deleteCollection")
    Call<BaseMessageRespond> deleteCollection(@Field("collectId") int collectId,
                                              @Field("userId") int userId);

    // TODO: 06/09/2018 Add product to collection
    @GET("addProInCollection")
    Call<AddProToCollectionRespond> addProductToCollection(@Query("collectId") int collectId,
                                                           @Query("proId") int proId);

    // TODO: 06/09/2018 Delete product from collection
    @FormUrlEncoded
    @POST("deleteProInCollection")
    Call<BaseMessageRespond> deleteProductFromCollection(@Field("collectId") int collectId,
                                                         @Field("proId") int proId,
                                                         @Field("userId") int userId);

    // TODO: 06/09/2018 get list of notification
    @GET("showActivitySeller")
    Call<NotificationRespond> getNotification(@Query("sellerId") int sellerId,
                                              @Query("page") int page);

    // TODO: 06/09/2018 action follow and unFollow (action 1: follow, 2: unFollow)
    @GET("follow")
    Call<ActionFollowRespond> follow(@Query("userId") int userId,
                                     @Query("receiverId") int receiverId,
                                     @Query("action") int action);

    // TODO: 07/09/2018 get list order
    @GET("order")
    Call<OrderRespond> getOrders(@Query("userId") int userId,
                                 @Query("status") String status,
                                 @Query("sortTime") String sortTime);

    // TODO: 07/09/2018 get order detail
    @GET("orderDetail")
    Call<OrderDetailRespond> getOrderDetail(@Query("orderId") int orderId);

    // TODO: 07/09/2018 Rename Collection
    @GET("renameCollection")
    Call<BaseMessageRespond> renameCollection(@Query("collectId") int collectId,
                                              @Query("name") String name);

    // TODO: 07/09/2018 Move a Product from a collection to another collection
    @GET("moveProInCollection")
    Call<BaseMessageRespond> moveProductToCollection(@Query("fromCollectId") int fromCollectId,
                                                     @Query("toCollectId") int toCollectId,
                                                     @Query("proId") int productId);

    // TODO: 10/09/2018 Get list of message
    @GET("listMessage")
    Call<MessageRespond> getListMessages(@Query("userId") int userId,
                                         @Query("conversationId") int conversationId,
                                         @Query("page") int page);

    // TODO: 10/09/2018 Get list of conversation
    @GET("listMessage")
    Call<ConversationRespond> getListConversation(@Query("userId") int userId,
                                                  @Query("page") int page);

    // TODO: 27/08/2018 sendMessage
    @Multipart
    @POST("sendMessage")
    Call<SendMessageRespond> sendMessage(@Part("senderId") int senderId,
                                         @Part("receiverId") int receiverId,
                                         @Part("roomId") int roomId,
                                         @Part("content") RequestBody content,
                                         @Part MultipartBody.Part file);

    @GET("createRoomMessage")
    Call<CreateRoomRespond> createConversation(@Query("senderId") int senderId,
                                               @Query("receiverId") int receiverId);

    // TODO: 11/09/2018 get list of follower
    @GET("listFollower")
    Call<ContactRespond> getListFollower(@Query("userId") int userId);

    // TODO: 11/09/2018 get list of following
    @GET("listFollowing")
    Call<ContactRespond> getListFollowing(@Query("userId") int userId,
                                          @Query("yourId") int yourId);

    // TODO: 11/09/2018 review order
    @GET("reviewOrder")
    Call<BaseMessageRespond> reviewOrder(@Query("orderId") int orderId,
                                         @Query("productId") int productId,
                                         @Query("userId") int userId,
                                         @Query("content") String content);

    /*Seller*/
    // TODO: 11/09/2018
    @GET("showReceivedOrder")
    Call<ReceivedOrderRespond> getListReceivedOrder(@Query("sellerId") int sellerId,
                                                    @Query("status") String status,
                                                    @Query("sortBy") String sortBy,
                                                    @Query("page") int page);

    // TODO: 11/09/2018 Change status of order
    @GET("changeOrderStatus")
    Call<BaseMessageRespond> changeStatusOrder(@Query("orderId") int orderId,
                                               @Query("orderStatus") int orderStatus);

    // TODO: 12/09/2018 get received order
    @GET("showOrderDetail")
    Call<ReceivedOrderDetailRespond> getReceivedOrderDetail(@Query("orderId") int orderId);

    // TODO: 13/09/2018 show profile
    @GET("showProfile")
    Call<ProfileRespond> getProfile(@Query("sellerId") int sellerId);

    // TODO: 13/09/2018 get list product of seller
    @GET("showProduct")
    Call<ProductSellerRespond> getListProducts(@Query("sellerId") int sellerId,
                                               @Query("categoryId") String categoryId,
                                               @Query("status") String status,
                                               @Query("page") int page);

    // TODO: 13/09/2018 Change product status
    @GET("changeProductStatus")
    Call<BaseMessageRespond> changeProductStatus(@Query("proId") int proId,
                                                 @Query("status") int status);

    // TODO: 13/09/2018 Forward order
    @GET("forwardOrder")
    Call<ForwardOrderRespond> forwardOrder(@Query("orderId") int orderId,
                                           @Query("email") String email,
                                           @Query("message") String message);

    // TODO: 14/09/2018 get stats
    @GET("showStats")
    Call<StatRespond> getStats(@Query("sellerId") int sellerId,
                               @Query("month") String month); //month format: MM-yyyy

    // TODO: 16/09/2018 get list of issue
    @GET("showReceivedIssue")
    Call<ReceivedIssueRespond> getReceivedIssue(@Query("sellerId") int sellerId,
                                                @Query("page") int page);

    // TODO: 16/09/2018 show issue detail
    @GET("showIssueDetail")
    Call<ReceivedIssueDetailRespond> getIssueDetail(@Query("issueId") int issueId);

    // TODO: 13/09/2018 Change issue status
    @GET("changeIssueStatus")
    Call<BaseMessageRespond> changeIssueStatus(@Query("sellerId") int sellerId,
                                               @Query("orderReturnId") int orderReturnId,
                                               @Query("status") int status);

    // TODO: 18/09/2018 foward issue
    @GET("forwardIssue")
    Call<BaseMessageRespond> forwardIssue(@Query("sellerId") int sellerId,
                                          @Query("orderReturnId") int orderReturnId,
                                          @Query("email") String email,
                                          @Query("message") String message);

    // TODO: 19/09/2018 get list comment
    @GET("showListComment")
    Call<ListCommentRespond> getListComment(@Query("productId") int productId,
                                            @Query("page") int page);

    // TODO: 19/09/2018 get list reply of comment
    @GET("showReplyComment")
    Call<ReplyCommentRespond> getListReplyComment(@Query("commentId") int commentId,
                                                  @Query("page") int page);

    // TODO: 19/09/2018 reply to a comment
    @GET("commentItem")
    Call<CommentItemRespond> replyTo(@Query("userId") int userId,
                                     @Query("productId") int productId,
                                     @Query("commentId") int commentId,
                                     @Query("content") String content);

    // TODO: 21/09/2018 search
    @GET("sellerSearchOrder")
    Call<ReceivedOrderRespond> searchReceivedOrder(@Query("sellerId") int sellerId,
                                                   @Query("keyword") String keyword,
                                                   @Query("status") String status,
                                                   @Query("page") int page);

    @GET("sellerSearchIssue")
    Call<ReceivedIssueRespond> searchIssueOrder(@Query("sellerId") int sellerId,
                                                @Query("keyword") String keyword,
                                                @Query("status") String status,
                                                @Query("page") int page);

    @GET("sellerSearchMessage")
    Call<ConversationRespond> searchContact(@Query("sellerId") int sellerId,
                                            @Query("keyword") String keyword,
                                            @Query("page") int page);
}
