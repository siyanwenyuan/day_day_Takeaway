package com.chen.reggie.controller;

import com.chen.reggie.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class OrderDetailController {
    @Autowired
    private OrderDetailService orderDetailService;
}
