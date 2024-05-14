package com.example.demo.repository;

import com.example.demo.dto.TbMember;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DemoRepository {

    List<TbMember> findAll();
}
