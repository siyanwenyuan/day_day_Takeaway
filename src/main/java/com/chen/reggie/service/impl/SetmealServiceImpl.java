package com.chen.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.reggie.common.CustomException;
import com.chen.reggie.dto.DishDto;
import com.chen.reggie.dto.SetmealDto;
import com.chen.reggie.entity.Setmeal;
import com.chen.reggie.entity.SetmealDish;
import com.chen.reggie.mapper.SetmealMapper;
import com.chen.reggie.service.SetmealDishService;
import com.chen.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 因为操作两张表，其中牵涉到套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    @Transactional//添加事务注解，因为操作两张表，为了保证数据的一致性
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息
        this.save(setmealDto);
        //保存套餐和菜品的关联信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
          item.setSetmealId(setmealDto.getId());
          return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 同时删除套餐和菜品相关联的数据
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
     //进行查询状态信息，通过状态信息来确定是否可以删除
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        //添加ids条件
        lambdaQueryWrapper.in(Setmeal::getId,ids);
        //添加状态条件
        lambdaQueryWrapper.eq(Setmeal::getStatus,1);
        //进行判断，看是否有查询到相关的数据，通过count记录
        int count = this.count(lambdaQueryWrapper);
        if(count>0){
            //查询到了对应数据，说明不能删除，抛出业务异常
            throw  new CustomException("套餐正在售卖中，不能删除");
        }
        //可以进行删除，先删除套餐表中的数据----setmeal
        this.removeByIds(ids);

        //再删除关联表的数据---setmealdish
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper1=new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.in(SetmealDish::getDishId,ids);
        setmealDishService.remove(lambdaQueryWrapper1);

    }

}
