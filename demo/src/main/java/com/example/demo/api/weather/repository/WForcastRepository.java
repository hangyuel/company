package com.example.demo.api.weather.repository;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.api.weather.dto.Spot;

@Mapper
public interface WForcastRepository {
	

	Spot findSpot(Spot spot); 
	
}
