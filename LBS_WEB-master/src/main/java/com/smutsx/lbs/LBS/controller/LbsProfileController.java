package com.smutsx.lbs.LBS.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smutsx.lbs.LBS.entity.LbsProfile;
import com.smutsx.lbs.LBS.mapper.LbsProfileMapper;
import com.smutsx.lbs.LBS.server.AlgorithmSelect;
import com.smutsx.lbs.LBS.server.utils.Path;
import com.smutsx.lbs.common.BaseController;
import com.smutsx.lbs.common.Result;
import io.jenetics.jpx.WayPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Message")
public class LbsProfileController extends BaseController {
    @Autowired(required=false)
    private LbsProfileMapper lbsmapper;

    @RequestMapping("/getByPageAndSize")
    public Result getbyuser(int currentPage,String uid)
    {
        Result result = new Result();
        LbsProfile profile = new LbsProfile();
        profile.setUid(uid);
        Page<LbsProfile> page = new Page<LbsProfile>(currentPage,20);
        QueryWrapper<LbsProfile> warpper =new QueryWrapper<LbsProfile>(profile);
        IPage<LbsProfile> Records = lbsmapper.selectPage(page, warpper);
        result.setData(Records);
        return result;
    }
    @RequestMapping("/getMessage")
    public Result getall(String uid)
    {
        Result result = new Result();
        LbsProfile profile = new LbsProfile();
        profile.setUid(uid);
        QueryWrapper<LbsProfile> warpper =new QueryWrapper<LbsProfile>(profile);
        result.setData(lbsmapper.selectList(warpper));
        return result;
    }
    @RequestMapping("/deleteMessage")
    public Result delalgorithm(int Messageid)
    {
        Result result = new Result();
        lbsmapper.deleteById(Messageid);
        return result;
    }
    @RequestMapping("/addaProfile")
    public Long addaLbsProfile(LbsProfile lbsProfile)
    {
        Long result;
        lbsmapper.insert(lbsProfile);
        result= lbsmapper.selectOne(new QueryWrapper<LbsProfile>(lbsProfile)).getId();

        return result;
    }

    @RequestMapping("/updateMessage")
    public Result updataProfile(LbsProfile lbsProfile)
    {
        Result result = new Result();
        lbsmapper.updateById(lbsProfile);
        return result;
    }
    @RequestMapping("/createMessage")
    public Result MatchProfile(LbsProfile user_profile)
    {
        Long Ki;
        String Uid;
        user_profile.setId(null);
        Map map = new HashMap();
        Result result = new Result();
        if(user_profile.getK()!=null)
        {
            Ki= user_profile.getK();
            user_profile.setK(null);
        }
        LbsProfile profile = lbsmapper.selectOne(new QueryWrapper<LbsProfile>(user_profile));
        if(profile==null)
        {
            LbsProfile temp = new LbsProfile();
            temp.copy(user_profile);
            Long id = addaLbsProfile(temp);//如果没有数据，插入一条数据
            map.put("id",id);
            user_profile.setUid("0");
            profile  = lbsmapper.selectOne(new QueryWrapper<LbsProfile>(user_profile));
        }

//        map.put("level",PrivacyLevel(profile));
        map.put("k",profile.getK());
        System.out.println(map.toString());
        JSONObject itemJSONObj = JSONObject.parseObject(JSON.toJSONString(map));
        result.setData(itemJSONObj);//需要返回的数据存在data中
        result.setMessage("是否使用该K值");
        return result;
    }

    @RequestMapping("/confirm")
    public Result ConfirmProfile(int id,Long k)
    {
        Result result = new Result();
        LbsProfile profile = lbsmapper.selectById(id);
        profile.setK(k);
        lbsmapper.updateById(profile);
        result.setMessage("确认完成");
        return result;
    }
    @RequestMapping("/trace")
    public Result test(String wayPoints)
    {
        Result result = new Result();
        Path examplePath = new Path();
        try {
            Resource resource = new ClassPathResource("test1.gpx");
            InputStream in = resource.getInputStream();
            examplePath.loadFromGPX(in);
        }
        catch (IOException e) {
        }
        AlgorithmSelect select = new AlgorithmSelect();
//        examplePath = select.Creatwaypoint(wayPoints);
        Path res3 = select.select(1,10,examplePath);
        result.setData(res3);
        return result;
    }

    /**
     隐私保护强度：0~10，0：无需保护，10：完全保护
     **/
    public long PrivacyLevel(LbsProfile user_profile) {
        //对不同的信息设置权重
        long level;
        level = user_profile.getTime() * 7  + user_profile.getLocation() * 10 + user_profile.getAge() * 8 + user_profile.getSensitivity() * 12 + user_profile.getPurpose() * 9;
        level = level/10;
        return level;
    }
}
