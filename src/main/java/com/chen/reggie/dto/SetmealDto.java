package com.chen.reggie.dto;

import com.chen.reggie.entity.Setmeal;
import com.chen.reggie.entity.SetmealDish;
import com.chen.reggie.entity.Setmeal;
import com.chen.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
