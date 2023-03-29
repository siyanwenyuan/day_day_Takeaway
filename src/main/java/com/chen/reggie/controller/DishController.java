package com.chen.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.reggie.common.R;
import com.chen.reggie.dto.DishDto;
import com.chen.reggie.entity.Category;
import com.chen.reggie.entity.Dish;
import com.chen.reggie.entity.DishFlavor;
import com.chen.reggie.service.CategoryService;
import com.chen.reggie.service.DishFlavorService;
import com.chen.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){

        log.info("新增菜品信息：{}",dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){

        //构造分页构造器
        Page<Dish> pageInfo=new Page(page,pageSize);
        Page<DishDto> dishDtoPage=new Page<>();
        //构造条件构造器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        //添加条件
        lambdaQueryWrapper.like(name!=null,Dish::getName,name);
        //添加一个排序条件
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        //调用mapper
        dishService.page(pageInfo,lambdaQueryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        //
        List<Dish> records = pageInfo.getRecords();
        //通过stream流和lambda表达式对records进行遍历
       List<DishDto> list= records.stream().map((item)->{
            DishDto dishDto=new DishDto();
            //用对象的拷贝，因为还有其他的属性
            BeanUtils.copyProperties(item,dishDto);
            //得到id
            Long categoryId = item.getCategoryId();
            //通过id查询，得到对象
            Category category = categoryService.getById(categoryId);
            //防止出现空指针异常
            if(category!=null){
                //通过对象得到对应的名字
                String categoryName = category.getName();
                //设置新的名字
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());//转换成集合

        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和菜品口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
   /* @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        //构造条件构造器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        //添加条件
        lambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //添加排序条件
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //添加状态条件，查询状态(在售状态) 状态为1的就是在售状态
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        //调用Service
        List<Dish> list = dishService.list(lambdaQueryWrapper);
        return R.success(list);

    }*/
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        //构造条件构造器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        //添加条件
        lambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //添加排序条件
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //添加状态条件，查询状态(在售状态) 状态为1的就是在售状态
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        //调用Service
        List<Dish> list = dishService.list(lambdaQueryWrapper);

        List<DishDto> dishDtoList= list.stream().map(this::apply).collect(Collectors.toList());//转换成集合

        return R.success(dishDtoList);

    }


    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status,@RequestParam List<Long> ids){
        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(ids!=null,Dish::getId,ids);
        List<Dish> list = dishService.list(lambdaQueryWrapper);
        for (Dish dish : list) {
            dish.setStatus(status);
            dishService.updateById(dish);
        }
        return R.success("修改状态成功");
    }

    private DishDto apply(Dish item) {
        DishDto dishDto = new DishDto();
        //用对象的拷贝，因为还有其他的属性
        BeanUtils.copyProperties(item, dishDto);
        //得到id
        Long categoryId = item.getCategoryId();
        //通过id查询，得到对象
        Category category = categoryService.getById(categoryId);
        //防止出现空指针异常
        if (category != null) {
            //通过对象得到对应的名字
            String categoryName = category.getName();
            //设置新的名字
            dishDto.setCategoryName(categoryName);
        }

        //通过Id进行查询菜品口味
        Long id = item.getId();
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(DishFlavor::getDishId, id);
        List<DishFlavor> list1 = dishFlavorService.list(lambdaQueryWrapper1);
        //通过查询得到的菜品口味对扩展的dishDt的flavors进行设置对应的属性
        dishDto.setFlavors(list1);
        return dishDto;
    }
}
