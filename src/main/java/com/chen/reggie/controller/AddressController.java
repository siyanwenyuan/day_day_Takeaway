package com.chen.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chen.reggie.common.BaseContext;
import com.chen.reggie.common.R;
import com.chen.reggie.entity.AddressBook;
import com.chen.reggie.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressController {
    @Autowired
    private AddressService addressService;

    /**
     * 新增操作
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook){
        //需要设置一下id，通过basecontext获取
        addressBook.setUserId(BaseContext.getCurrentId());
        addressService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<AddressBook> setDefult(@RequestBody AddressBook addressBook){
        LambdaUpdateWrapper<AddressBook> lambdaUpdateWrapper=new LambdaUpdateWrapper<>();
        //先通过得到登陆时的id进行查询，添加这个条件
        lambdaUpdateWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        //设置默认的值是0
        lambdaUpdateWrapper.set(AddressBook::getIsDefault,0);
        //调用方法
        addressService.update(lambdaUpdateWrapper);

        //设置一下这个需要更新的id
        addressBook.setIsDefault(1);
        addressService.updateById(addressBook);

        return R.success(addressBook);
    }

    @GetMapping("/{id}")
    public R get(@PathVariable Long id){
        AddressBook id1= addressService.getById(id);
        if(id1==null) {
            R.error("查询失败");
        }
        return R.success(id1);
    }

    /**
     * 查询默认地址
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        //这里需要得到登陆时的id来进行相关业务功能的开发
        lambdaQueryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        lambdaQueryWrapper.eq(AddressBook::getIsDefault,1);
        //getone查询对象的所有数据进行封装成一个对象
        AddressBook one = addressService.getOne(lambdaQueryWrapper);
        if(one==null){
            return R.error("没有找到该对象");
        }
        return R.success(one);
    }

    /**
     * 查询所有地址
     * @param addressBook
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook){
        //通过获取登录时的id来进行查询
        addressBook.setUserId(BaseContext.getCurrentId());
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(null!=addressBook.getUserId(),AddressBook::getUserId,addressBook.getUserId());
        lambdaQueryWrapper.orderByDesc(AddressBook::getUpdateTime);

        List<AddressBook> list = addressService.list(lambdaQueryWrapper);
        return R.success(list);

    }

}
