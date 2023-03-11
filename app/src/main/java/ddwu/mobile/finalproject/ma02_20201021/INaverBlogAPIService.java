package ddwu.mobile.finalproject.ma02_20201021;

import ddwu.mobile.finalproject.ma02_20201021.model.json.BlogRoot;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface INaverBlogAPIService {
    @GET("/v1/search/blog.json")
    Call<BlogRoot> getBlogList (@Header("X-Naver-Client-Id") String clientId,
                                  @Header("X-Naver-Client-Secret") String clientSecret,
                                  @Query("display") int display,
                                  @Query("sort") String sort,
                                  @Query("query") String query);
}
