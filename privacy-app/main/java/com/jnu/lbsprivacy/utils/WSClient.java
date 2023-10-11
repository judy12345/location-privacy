package com.jnu.lbsprivacy.utils;

import java.util.Map;

import okhttp3.*;


public class WSClient {

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