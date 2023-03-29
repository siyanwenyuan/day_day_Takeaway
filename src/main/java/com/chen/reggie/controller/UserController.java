package com.chen.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chen.reggie.common.R;
import com.chen.reggie.entity.User;
import com.chen.reggie.service.UserService;
import com.chen.reggie.utils.SMSUtils;
import com.chen.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;


    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //先得到用户的手机号码
        String phone = user.getPhone();
        //进行空值的判断
        if(StringUtils.isNotEmpty(phone)){
            //随机生成验证码，调用工具类
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("验证码是：{}",code);
            //调用短信服务
            //SMSUtils.sendMessage("瑞吉外卖","",phone,code);
            //将得到的code存入session中
            session.setAttribute(phone,code);
            return R.success("验证码发送成功");

        }
        return R.error("验证码发送失败");

    }

    /**
     * 用户登录
     * @param map 其中前端返回的参数格式是json 此处可以用一个dto接受，但是，由于map中也是key-value形式,所以也可用map来接受参数
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());
        //从map中获取phone
        String phone = map.get("phone").toString();
        //从map中获取code
        String code = map.get("code").toString();
        //得到Session中生成的验证码
        Object codeSession = session.getAttribute(phone);
        //将map中的code与session中的验证码进行比对
        if(code!=null&&codeSession.equals(code)){
            //说明比对成功，则通过phone对User表进行查询
            //创建构造器
            LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            //添加条件
            lambdaQueryWrapper.eq(User::getPhone,phone);
            //调用getone
            User user = userService.getOne(lambdaQueryWrapper);
            //如果查询到的user为空，说明没有该用户，则自动注册
            if(user==null){
                //自动注册,创建一个user对象
                 user=new User();
                user.setPhone(phone);
                user.getStatus();
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }

        return R.error("登录失败");
    }
}
