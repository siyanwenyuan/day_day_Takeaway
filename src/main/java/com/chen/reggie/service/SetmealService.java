package com.chen.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.reggie.dto.DishDto;
import com.chen.reggie.dto.SetmealDto;
import com.chen.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    //因为牵涉到两张表的操作，同时需要保存套餐和菜品的关联关系
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐和相关联的菜品数据
     * @param ids
     */
    public void removeWithDish(List<Long> ids);
}
