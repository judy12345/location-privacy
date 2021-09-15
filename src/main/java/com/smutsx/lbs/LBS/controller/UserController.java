package com.smutsx.lbs.LBS.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smutsx.lbs.LBS.entity.User;
import com.smutsx.lbs.LBS.entity.Userandtoken;
import com.smutsx.lbs.LBS.entity.receive;
import com.smutsx.lbs.LBS.mapper.UserandtokenMapper;
import com.smutsx.lbs.common.BaseController;
import com.smutsx.lbs.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@RestController
@RequestMapping("/User")
public class UserController extends BaseController {
    @Autowired(required=false)
    private com.smutsx.lbs.LBS.mapper.UserMapper UserMapper;
    @Autowired(required=false)
    private UserandtokenMapper userandtokenMapper;

    @RequestMapping("/login")
    public Result login(User user) {
        Result result = new Result();
        Map map = new HashMap();
        user.setPassword(stringToMD5(user.getPassword()));

        user = UserMapper.selectOne(new QueryWrapper<User>(user));
        if(user!=null)
        {
            Userandtoken usertoken = new Userandtoken();
            String tok = getRandomString(5);
            usertoken.setToken(tok);
            usertoken.setUid(user.getId());
            userandtokenMapper.insert(usertoken);
            map.put("token",tok);
            JSONObject itemJSONObj = JSONObject.parseObject(JSON.toJSONString(map));
            result.setData(itemJSONObj);//需要返回的数据存在data中
        }
        else
        {
            result.fail();
        }
        System.out.println("result: " + result.getData());
        return result;
    }
    @RequestMapping("/logout")
    public Result logout(String token){
        Result result = new Result();
        Userandtoken usertoken = new Userandtoken();
        usertoken.setToken(token);
        userandtokenMapper.delete(new QueryWrapper<Userandtoken>(usertoken));
        result.setData("success");//需要返回的数据存在data中
        return result;
    }
    @RequestMapping("/info")
    public Result info(String token) {
        Result result = new Result();
        User user;
        String[] temp = new String[1];
        Map map = new HashMap();
        Userandtoken usertoken = new Userandtoken();
        usertoken.setToken(token);
        usertoken = userandtokenMapper.selectOne(new QueryWrapper<Userandtoken>(usertoken));
        if(usertoken!=null && usertoken.getUid()!=null)
        {
            user = UserMapper.selectById(usertoken.getUid());
            if(user!=null)
            {
                temp[0] = user.getRoles();
                map.put("id", user.getId());
                map.put("name",user.getUsername());
                map.put("roles",temp);
                map.put("email",user.getEmail());
                map.put("phone",user.getPhone());
                map.put("username",user.getUsername());
                map.put("realname",user.getRealname());
                map.put("gender",user.getGender());
                map.put("birthdate", user.getbirthdate());
                map.put("job",user.getJob());
                JSONObject itemJSONObj = JSONObject.parseObject(JSON.toJSONString(map));
                result.setData(itemJSONObj);//需要返回的数据存在data中
            }
        }
        return result;
    }
    @RequestMapping("/getByPageAndSize")
    public Result getbyuser(int Page, int size)
    {
        Result result = new Result();
        User user = new User();
        List<Map> listmap = new ArrayList<>();
        Map ret = new HashMap();
        Page<User> page = new Page<User>(Page,size);
        QueryWrapper<User> warpper =new QueryWrapper<User>(user);
        IPage<User> Records = UserMapper.selectPage(page, warpper);
        List<User> users =  Records.getRecords();
        for(int i=0;i<users.size();i++)
        {
            String[] roles = new String[1];
            roles[0] = users.get(i).getRoles();
            Map temp = new HashMap();
            temp.put("id",users.get(i).getId());
            temp.put("username",users.get(i).getUsername());
            temp.put("password",users.get(i).getPassword());
            temp.put("email",users.get(i).getEmail());
            temp.put("phone",users.get(i).getPhone());
            temp.put("gender",users.get(i).getGender());
            temp.put("job",users.get(i).getJob());
            temp.put("roles",roles);
            temp.put("realname",users.get(i).getRealname());
            temp.put("birthdate",users.get(i).getbirthdate());
            listmap.add(temp);
        }
        ret.put("records",listmap);
        ret.put("total",Records.getTotal());
        ret.put("size",Records.getSize());
        ret.put("current",Records.getCurrent());
        ret.put("pages",Records.getPages());
        JSONObject itemJSONObj = JSONObject.parseObject(JSON.toJSONString(ret));
        result.setData(itemJSONObj);

        return result;
    }
    @RequestMapping("/createUser")
    public Result createUser(receive temp,@RequestParam(value = "roles[]") String[] roles)
    {
        Result result = new Result();
        System.out.println(temp.getId());
        User NEWuser = conversion(temp);
        if(roles[0]!=null) NEWuser.setRoles(roles[0]);
        if(NEWuser.getPassword()!=null)
            NEWuser.setPassword(stringToMD5(NEWuser.getPassword()));
        UserMapper.insert(NEWuser);
        return result;
    }
    @RequestMapping("/updateUser")
    public Result updateUser(receive user,@RequestParam(value = "roles[]") String[] roles)
    {
        Result result = new Result();
        System.out.println(user.getId());
        User NEWuser = conversion(user);
        if(roles[0]!=null) NEWuser.setRoles(roles[0]);
        if(NEWuser.getPassword()!=null)
            NEWuser.setPassword(stringToMD5(NEWuser.getPassword()));
        UserMapper.updateById(NEWuser);
        return result;
    }
    @RequestMapping("/deleteUser")
    public Result deleteUser(int id)
    {
        Result result = new Result();
        UserMapper.deleteById(id);
        return result;
    }
    @RequestMapping("/searchByUserName")
    public Result searchByUserName(String userName)
    {
        Result result = new Result();
        User user = new User();
        user.setUsername(userName);
        System.out.println(userName);
        result.setData(UserMapper.selectList(new QueryWrapper<User>(user)));
        return result;
    }
    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
    public String stringToMD5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有这个md5算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }
    public User conversion(receive temp)
    {
        User user  = new User();
        if(temp.getId()!=null) user.setId(temp.getId());
        if(temp.getUsername()!=null) user.setUsername(temp.getUsername());
        if(temp.getPassword()!=null) user.setPassword(temp.getPassword());
        if(temp.getBirthdate()!=null) user.setbirthdate(temp.getBirthdate());
        if(temp.getEmail()!=null) user.setEmail(temp.getEmail());
        if(temp.getGender()!=null) user.setGender(temp.getGender());
        if(temp.getJob()!=null) user.setJob(temp.getJob());
        if(temp.getPhone()!=null) user.setPhone(temp.getPhone());
        user.setRealname(temp.getRealname());
//        user.setRoles(temp.getRoles()[0]);
        return user;
    }
}
