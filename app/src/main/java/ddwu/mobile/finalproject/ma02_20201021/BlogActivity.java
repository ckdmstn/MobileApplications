package ddwu.mobile.finalproject.ma02_20201021;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import ddwu.mobile.finalproject.ma02_20201021.model.json.BlogRoot;
import ddwu.mobile.finalproject.ma02_20201021.model.json.NaverBlogDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BlogActivity extends AppCompatActivity implements BlogAdapter.BlogClickListener {
    public static final String TAG = "20201021_ma";

    private Retrofit retrofit;
    private INaverBlogAPIService blogAPIService;

    String apiUrl;
    String clientId;
    String clientSecret;
    String query;

    ListView lvList;
    ArrayList<Blog> dataList;
    BlogAdapter adapter;
    ArrayList<NaverBlogDto> resultList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        apiUrl = getResources().getString(R.string.api_url);
        clientId = getResources().getString(R.string.client_id);
        clientSecret = getResources().getString(R.string.client_secret);
        lvList = findViewById(R.id.listView);

        if (retrofit == null) {
            try {
                retrofit = new Retrofit.Builder()
                        .baseUrl(apiUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        blogAPIService = retrofit.create(INaverBlogAPIService.class);

        resultList = new ArrayList();
        dataList = new ArrayList<Blog>();
        adapter = new BlogAdapter(this, R.layout.blog_adapter_view, dataList, this);
        lvList.setAdapter(adapter);

        Search search = (Search) getIntent().getSerializableExtra("search");
        String[] strSplit = search.getAddress().split(" "); // 주소 @@구 혹은 @@시
        String query = strSplit[1] + " " + search.getTitle(); // ex) 성북구 @@동물병원, 안산시 @@동물병원 (같은 이름의 동물병원 최대한 제외하기 위함)

        Call<BlogRoot> apiCall =
                blogAPIService.getBlogList(clientId, clientSecret, 20, "sim", query);
        apiCall.enqueue(apiCallback);
    }

    Callback<BlogRoot> apiCallback = new Callback<BlogRoot>() {
        @Override
        public void onResponse(Call<BlogRoot> call, Response<BlogRoot> response) {
            BlogRoot blogRoot = response.body();
            List<NaverBlogDto> list = blogRoot.getItems();

            dataList.clear();
            int count = 1;
            for (NaverBlogDto l : list) {
                String description = l.getDescription().replace("<b>", "");
                description = description.replace("</b>", "");
                String title = l.getTitle().replace("<b>", "");
                title = title.replace("</b>", "");
                Blog blog = new Blog(1, title, description, l.getPostdate(), l.getLink());
                dataList.add(blog);
                count++;
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onFailure(Call<BlogRoot> call, Throwable t) {
            Log.e(TAG, t.toString());
        }
    };

    @Override
    public void onBlogClick(String link) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(link));
        startActivity(intent);
    }
}
