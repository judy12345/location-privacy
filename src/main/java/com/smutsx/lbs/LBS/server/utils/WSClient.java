package com.smutsx.lbs.LBS.server.utils;
import java.io.IOException;
import java.util.Map;

import okhttp3.*;

public class WSClient {
    public static String httpGet(String url) throws IOException {
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = httpClient.newCall(request).execute();
        return response.body().string(); // 返回的是string 类型，json的mapper可以直接处理
    }
    public static String httpPost(String url, Map<String, String> map){
        String result = "";

        try{
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("ak", map.get("ak"))
                    .addFormDataPart("point_list", map.get("point_list"))
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .method("POST", body)
                    .addHeader("Cookie", "BAIDUID=05770C8DE7571B7DE6B98AC808B02AB5:FG=1")
                    .build();
            Response response = client.newCall(request).execute();
            result = response.body().string();

        } catch (Exception e){
            System.out.println("请求异常");
            throw new RuntimeException(e);
        } finally{
        }
        return result;
    }

}
