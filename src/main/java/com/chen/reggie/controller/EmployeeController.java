package com.chen.reggie.controller;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.reggie.common.R;
import com.chen.reggie.entity.Employee;
import com.chen.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    /**
     * 员工登录
     * @param request 将获取到的employee对象中的id存到cession中，通过id得到员工的登录信息
     * @param employee 请求体中的数据
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){


        /**
         * 
         */
        //将得到的数据进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //根据页面提交的用户名username进行查询
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);// getOne方法：因为查询到的字段在数据库中已经进行了唯一标识，则可以使用该方法进行查询
        //如果查询失败则返回登录失败的结果
        if(emp==null){
            return R.error("登录失败");//R就是返回一个结果
        }
        //密码比对，如果失败返回一个失败的结果
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败");

        }
        //查看员工状态，如果已经禁用，则返回禁用状态
        if(emp.getStatus()==0){
            return R.error("员工状态已经禁用");
        }
        //登录成功，将员工id存入session并返回登录成功
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);//返回成功查询到的对象
        }

    /**
     * 退出方法
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //将存到session中的id清理掉就可以实现退出功能
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");

    }


    /**
     *
     * 新增员工
     * @RequestBody  使用这个注解使用为前端请求的数据格式是jason,因为前端请求的参数封装到employee对象中
     * @PostMapping  此注解后面没有路劲，是因为在类中的路劲就是这个路径
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工你的信息：{}",employee.toString());//将前端请求过来的新增员工信息打印到日志上面

        //设置一个初始密码，需要进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
      /*  //对于其他参数为空，也需要进行手动设置
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        //对这两个初始值进行初始化,通过获取当前登录用户的id,
        long empId = (long)request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);*/
        //调用service层接口
        employeeService.save(employee);
        //返回成功的信息
        return R.success("新增员工成功");
    }


    /**
     * 员工信息分页查询方法
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        //分页构造器
        Page pageInfo=new Page<>();
        //条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper=new LambdaQueryWrapper();
        //添加过滤条件
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,lambdaQueryWrapper);
        return  R.success(pageInfo);
    }

    /**
     *根据id对员工信息进行修改
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody  Employee employee){
        log.info(employee.toString());

        long empId = (long) request.getSession().getAttribute("employee");

        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");


    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable long id){
        log.info("根据id查询员工信息");
        Employee employee = employeeService.getById(id);
        if(employee!=null){

            return R.success(employee);
        }
        return R.error("没有查询到对应员工的信息");


    }
}
