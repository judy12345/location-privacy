package com.smutsx.lbs.LBS.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smutsx.lbs.LBS.entity.Algorithm;
import com.smutsx.lbs.LBS.mapper.AlgorithmMapper;
import com.smutsx.lbs.common.BaseController;
import com.smutsx.lbs.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Algorithm")
public class AlgorithmController extends BaseController {
    @Autowired(required=false)
    private AlgorithmMapper algorithmMapper;

    @RequestMapping("/createAlgorithm")
    public Result addalgorithm(Algorithm algorithm)
    {
        Result result = new Result();
        algorithmMapper.insert(algorithm);
        return result;
    }
    @RequestMapping("/updateAlgorithm")
    public Result updataalgorithm(Algorithm algorithm)
    {
        Result result = new Result();
        algorithmMapper.updateById(algorithm);
        return result;
    }
    @RequestMapping("/deleteAlgorithm")
    public Result delalgorithm(int AlgorithmId)
    {
        Result result = new Result();
        algorithmMapper.deleteById(AlgorithmId);
        return result;
    }
    @RequestMapping("/getByPageAndSize")
    public Result getAll(int currentPage)
    {
        Result result = new Result();
        Page<Algorithm> page = new Page<Algorithm>(currentPage,20);
        QueryWrapper<Algorithm> warpper =new QueryWrapper<Algorithm>(new Algorithm());
        IPage<Algorithm> Records = algorithmMapper.selectPage(page, warpper);
        result.setData(Records);
        return result;
    }
    @RequestMapping("/getAlgorithm")
    public Result getAlgorithm()
    {
        Result result = new Result();
        result.setData(algorithmMapper.selectList(new QueryWrapper<Algorithm>()));
        return result;
    }
}
