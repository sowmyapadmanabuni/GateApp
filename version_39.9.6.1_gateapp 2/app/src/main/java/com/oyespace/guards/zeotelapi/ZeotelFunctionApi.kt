package oyespace.guards.cloudfunctios

import com.oyespace.guards.pojo.*
import io.reactivex.Single
import retrofit2.http.*

interface ZeotelFunctionApi {

    @GET("c2c?")
    fun getCall(@Query("key") key: String,@Query("ac")ac: String,@Query("ph")ph:String,@Query("user_vars")user_vars:String,@Query("tl")tl:String,@Query("df")df:String)
            : Single<GetCallResponse>
}


