package com.chen.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.reggie.dto.DishDto;
import com.chen.reggie.entity.Dish;
import com.chen.reggie.entity.DishFlavor;
import com.chen.reggie.mapper.DishMapper;
import com.chen.reggie.service.DishFlavorService;
import com.chen.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 这里牵涉到多张表的操作
     * 保存菜品和菜品口味
     * @param dishDto
     */
    @Transactional//因为牵涉到两张表的操作，此处需要开启事务注解
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息
        this.save(dishDto);
        //保存菜品口味
        //得到菜品id
        Long dishId = dishDto.getId();
        //得到菜品口味的一个集合，遍历得到的集合，将得到的dishId赋给实体类中的dishId
        List<DishFlavor> flavors = dishDto.getFlavors();
        //通过 stream流 和lambda表达式
        flavors=flavors.stream().map((item)->{
           item.setDishId(dishId);
           return item;
        }).collect(Collectors.toList());
        //批量保存saveBatch
        dishFlavorService.saveBatch(flavors);

        


    }

    /**
     * 根据id查询菜品信息和菜品口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品信息
        Dish dish = this.getById(id);
        DishDto dishDto=new DishDto();
        //拷贝dish基本信息
        BeanUtils.copyProperties(dish,dishDto);
        //查询菜品口味信息
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        //添加条件
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dish.getId());
        //进行一个查询
        List<DishFlavor> list = dishFlavorService.list(lambdaQueryWrapper);
        //将得到的口味信息写入dishDto中
        dishDto.setFlavors(list);
        //返回封装好的dishDto
        return dishDto;
    }

    @Override
    @Transactional//加入事务注解，保证数据被修改后的一致性
    public void updateWithFlavor(DishDto dishDto) {
        //修改基本菜品信息
        this.updateById(dishDto);
        //修改菜品口味信息

        //先清理掉当前菜品口味信息
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(lambdaQueryWrapper);

        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors=flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        //批量保存
       dishFlavorService.saveBatch(flavors);


    }
}
