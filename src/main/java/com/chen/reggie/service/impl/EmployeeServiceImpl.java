package com.chen.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.reggie.entity.Employee;
import com.chen.reggie.mapper.CategoryMapper;
import com.chen.reggie.mapper.EmployeeMapper;
import com.chen.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

//加入注解给spring管理
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService {
}
