package com.chen.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.reggie.common.R;
import com.chen.reggie.dto.DishDto;
import com.chen.reggie.dto.SetmealDto;
import com.chen.reggie.entity.Category;
import com.chen.reggie.entity.Setmeal;
import com.chen.reggie.service.CategoryService;
import com.chen.reggie.service.SetmealDishService;
import com.chen.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody  SetmealDto setmealDto){
        log.info("新增套餐菜品信息：{}",setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //创建一个分页构造器
        Page<Setmeal> pageInfo=new Page<>(page,pageSize);
        //其中套餐分类时setmeal中没有的属性，需要用一下Setmealdto.
        Page<SetmealDto> dtoPage=new Page<>();
        //创造一个条件构造器
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        //添加条件，用模糊查询
        lambdaQueryWrapper.like(name!=null,Setmeal::getName,name);
        //添加一个时间的降序条件
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,lambdaQueryWrapper);
        //使用对象拷贝,需要忽略掉records，这是一个集合，
        BeanUtils.copyProperties(pageInfo, dtoPage,"records");
        //对records进行处理
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list= records.stream().map((item)->{
            //创建一个SetmelDto对象
            SetmealDto setmealDto=new SetmealDto();
            //需要进行一个对象拷贝，因为这里只是设置了categoryname,但是setmelDto中其他属性没有值，需要从item中拷贝过来
            BeanUtils.copyProperties(item,setmealDto);
            //得到分类的id
            Long categoryId = item.getCategoryId();
            //通过id得到对应的对象
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;

        }).collect(Collectors.toList());

        //设置dtopage中的属性
        dtoPage.setRecords(list);
        //返回dtopage，则页面才有套餐分类的显示
        return R.success(dtoPage);
    }

    /**
     * 通过ids批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){

        setmealService.removeWithDish(ids);
        return null;
    }

    /**
     * 编写售卖状态
     * @param status
     * @param ids
     * @return
     */

    @PostMapping("/status/{status}")
    public R<String> update(@PathVariable Integer status,@RequestParam List<Long> ids){
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(ids != null, Setmeal::getId, ids);
        List<Setmeal> list = setmealService.list(lambdaQueryWrapper);
        for (Setmeal setmeal : list) {
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);
        }
        return R.success("状态修改成功");
    }

    /**
     * 根据条件查询
     * 注意：
     * 如果前端返回的参数是一个json格式，则需要requestbody注解
     * 如果前端的参数是一个键值对形式，则直接用
     * 如果前端参数是一个变量，则需要接受变量，用PathVariable
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){

        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        lambdaQueryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(lambdaQueryWrapper);
        if(list==null){
            return R.error("查询失败");
        }
        return R.success(list);
    }


}
