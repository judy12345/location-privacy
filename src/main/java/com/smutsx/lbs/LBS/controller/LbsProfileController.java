package com.smutsx.lbs.LBS.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smutsx.lbs.LBS.entity.*;
import com.smutsx.lbs.LBS.mapper.LbsProfileMapper;
import com.smutsx.lbs.LBS.mapper.WaypointMapper;
import com.smutsx.lbs.LBS.server.AlgorithmSelect;
import com.smutsx.lbs.LBS.server.utils.Path;
import com.smutsx.lbs.common.BaseController;
import com.smutsx.lbs.common.Result;
import io.jenetics.jpx.WayPoint;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.*;

@RestController
@RequestMapping("/Message")
public class LbsProfileController extends BaseController {
    @Autowired(required=false)
    private LbsProfileMapper lbsmapper;
    @Autowired(required=false)
    private WaypointMapper waypointMapper;

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
    @RequestMapping("/upload_profile")
    public Long getLbsProfile(@RequestBody LbsProfile lbsProfile)
    {
        Long result;
        lbsProfile.setUid("0");
        lbsProfile.setK(null);
        lbsProfile.setTime(Long.parseLong("1"));
        result= lbsmapper.selectOne(new QueryWrapper<LbsProfile>(lbsProfile)).getK();

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
        Long Ki = null;
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
            temp.setK(Ki);
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
        if(k!=null)
        {
            profile.setK(k);
        }
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
//        Path res3 = select.select(1,10,examplePath);
        result.setData(examplePath);
        return result;
    }
    @RequestMapping("/uptrace")
    public Result uptrace(@RequestBody Map<String,Object> request)
    {

        Result result = new Result();
        Waypoints up = new Waypoints();
        System.out.println(request.get("path"));
        up.setWaypoints(request.get("path").toString());
        up.setIstrue(Long.parseLong("1"));
        Date date = new Date();
        up.setTime(date.getTime());

        waypointMapper.insert(up);

        return result;
    }

    @RequestMapping("/getcout")
    public int getcout(String time)
    {
        Result result = new Result();
        int cout = 0;
        Waypoints up = new Waypoints();
        List<Waypoints> listpoint =  waypointMapper.selectList(new QueryWrapper<Waypoints>(up));
        for (int i = 0;i < listpoint.size();i++)
        {
            up = listpoint.get(i);
            if(up.getTime()>Long.parseLong(time)) cout++;
        }
        result.setData(cout);
        return cout;
    }

    @RequestMapping("/gettrueway")
    public Result getoneway(int k,String time)
    {
        Result result = new Result();
        int cout = 0;
        Path examplePath = new Path();
        Waypoints up = new Waypoints();
        List<Waypoints> listpoint =  waypointMapper.selectList(new QueryWrapper<Waypoints>(up));
        for (int i = 0;i < listpoint.size();i++)
        {
            up = listpoint.get(i);
            if(up.getTime()>Long.parseLong(time)) cout++;
            if(cout==k) break;
        }
        AlgorithmSelect select = new AlgorithmSelect();
        examplePath = select.Creatwaypoint(up.getWaypoints());
        result.setData(examplePath);
        return result;
    }



    @RequestMapping("/getfalseway")
    public Result getfalseway(int k,String time)
    {
        Result result = new Result();
        Path examplePath = new Path();
        int cout = 0;

        Waypoints up = new Waypoints();
        List<Waypoints> listpoint =  waypointMapper.selectList(new QueryWrapper<Waypoints>(up));
        for (int i = 0;i < listpoint.size();i++)
        {
            up = listpoint.get(i);
            if(up.getTime()>Long.parseLong(time)) cout++;
            if(cout==k) break;
        }
        AlgorithmSelect select = new AlgorithmSelect();
        examplePath = select.Creatwaypoint(up.getWaypoints());
        examplePath = select.select((int)(Math.random()*3)+1,10,examplePath);
        result.setData(examplePath);
        return result;
    }

    @RequestMapping("/testway")
    public Result testway()
    {
        Result result = new Result();
        AlgorithmSelect select = new AlgorithmSelect();
        result.setData(select.selectway().getWayPoints());
        return result;
    }

    @RequestMapping("/true")
    public Result true_path(String num)
    {
        Result result = new Result();
        Path examplePath = new Path();
        try {
            String temp =  num + ".gpx";
            Resource resource = new ClassPathResource(temp);
            InputStream in = resource.getInputStream();
            examplePath.loadFromGPX(in);
        }
        catch (IOException e) {
        }
        result.setData(examplePath);
        return result;
    }
    @RequestMapping("/false")
    public Result false_path(String num)
    {
        Result result = new Result();
        Path examplePath = new Path();
        try {
            String temp = num + ".gpx";
            Resource resource = new ClassPathResource(temp);
            InputStream in = resource.getInputStream();
            examplePath.loadFromGPX(in);
        }
        catch (IOException e) {
        }
        AlgorithmSelect select = new AlgorithmSelect();
//        examplePath = select.Creatwaypoint(wayPoints);
        Path res3 = select.select((int)(Math.random()*3)+1,10,examplePath);
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
    public Send getsendMessage(String json)
    {
        Send meseage = new Send();
        String time = new String();
        AlgorithmSelect algorithmSelect = new AlgorithmSelect();
        System.out.println("===json==="+com.alibaba.fastjson.JSONObject.parseObject(json)+"=======");
        com.alibaba.fastjson.JSONObject req = com.alibaba.fastjson.JSONObject.parseObject(json, (Type) SendMessage.class);
        int token = Integer.parseInt(req.get("token").toString());
        System.out.println(token);
        if (token != 201) return meseage;
        time = req.get("time").toString();
        int cout;
        Path fakePath = new Path();
        Path examplePath = new Path();
        Waypoints up = new Waypoints();
        List<Waypoints> listpoint =  this.waypointMapper.selectList(new QueryWrapper<Waypoints>(up));
        for (int i = 0;i < listpoint.size();i++)
        {
            up = listpoint.get(i);
            if(up.getTime()>Long.parseLong(time))
            {
                examplePath = algorithmSelect.Creatwaypoint(up.getWaypoints());
                fakePath = algorithmSelect.select((int)(Math.random()*3)+1,10,examplePath);
                if(examplePath.getWayPoints().size()>=fakePath.getWayPoints().size()) cout = fakePath.getWayPoints().size();
                else cout = examplePath.getWayPoints().size();
                meseage.count += cout;
                for(int j = 0;j < cout;j++)
                {
                    SendMessage temp = new SendMessage();
                    temp.time = examplePath.getWayPoints().get(j).getTime().get().toInstant().toEpochMilli();
                    temp.trueLat = examplePath.getWayPoints().get(j).getLatitude().doubleValue();
                    temp.trueLon = examplePath.getWayPoints().get(j).getLongitude().doubleValue();
                    temp.fakeLat = fakePath.getWayPoints().get(j).getLatitude().doubleValue();
                    temp.fakeLon = fakePath.getWayPoints().get(j).getLongitude().doubleValue();
                    meseage.message.add(temp);
                }
            }

        }

        return meseage;
    }
}
