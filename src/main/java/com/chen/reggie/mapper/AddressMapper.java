package com.chen.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chen.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressMapper extends BaseMapper<AddressBook> {
}
