package com.chen.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chen.reggie.common.CustomException;
import com.chen.reggie.entity.Category;
import com.chen.reggie.entity.Dish;
import com.chen.reggie.entity.Setmeal;
import com.chen.reggie.mapper.CategoryMapper;
import com.chen.reggie.service.CategoryService;
import com.chen.reggie.service.DishService;
import com.chen.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id 删除分类，删除之前许需要判断
     * @param id
     */

    @Override
    public void remove(Long id) {
        /**
         * 判断id是否和菜品关联
         */
        //构造条件构造器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        //添加条件
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        //得到查询到的数据量，如果说count等于10，则说明当前id下，有诗歌菜品量的id相同
        int count1 = dishService.count(dishLambdaQueryWrapper);
        //判断id是否和菜品关联，如果关联，则抛出一个业务异常
        if(count1>0){
            //已经关联，抛出一个业务异常
            throw  new CustomException("当前分类下关联了菜品，不能删除");

        }


        /**
         * 判断id是否和套餐关联
         */
        //创建一个条件构造器
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        //添加一个条件
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if(count2>0){
            //已经关联，则抛出一个业务异常
            throw  new CustomException("当前分类下关联了套餐，不能删除");
        }
        /**
         * 都没有关联，则直接删除
         */
        super.removeById(id);
    }
}
