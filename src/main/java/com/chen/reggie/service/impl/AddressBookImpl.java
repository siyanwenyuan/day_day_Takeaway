package com.chen.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.reggie.entity.AddressBook;
import com.chen.reggie.mapper.AddressMapper;
import com.chen.reggie.service.AddressService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookImpl extends ServiceImpl<AddressMapper, AddressBook> implements AddressService {
}
