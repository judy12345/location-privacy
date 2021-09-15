package com.smutsx.lbs.LBS.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;

public class Send {
    public int type;
    public int count;
    public ArrayList<SendMessage> message;
    public Send()
    {
        count = 0;
        message = new ArrayList<SendMessage>();
        type = 0;
    }
    public String toString()
    {
        JSONObject temp = new JSONObject();
        try {
            //添加
            temp.put("type",type);
            temp.put("count",count);
            JSONArray jsonArray = JSONObject.parseArray(JSON.toJSON(message).toString());
            temp.put("message",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return JSONObject.toJSONString(temp);
    }
}
