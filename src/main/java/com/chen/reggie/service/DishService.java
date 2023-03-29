package com.chen.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.reggie.dto.DishDto;
import com.chen.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    //新增菜品，同时需要新增菜品口味，也就是需要操作两张表
    public void saveWithFlavor(DishDto dishDto);
    //根据id查询
    public DishDto getByIdWithFlavor(Long id);

  public   void updateWithFlavor(DishDto dishDto);

}
