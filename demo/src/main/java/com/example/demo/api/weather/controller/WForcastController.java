package com.example.demo.api.weather.controller;


import java.io.IOException;
import java.util.List;

import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.api.weather.dto.Spot;
import com.example.demo.api.weather.dto.Weather;
import com.example.demo.api.weather.service.WForcastService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class WForcastController {
	
	private final WForcastService wForcastService;
	

	@GetMapping("/weather")
	public ResponseEntity<List<Weather>> getWeather(@RequestBody Spot spot) throws IOException{
		
		
		
		//서비스를 통해 API결과를 반환받아 클라이언트에게 응답 데이터로 전송
	    //응답 객체 타입은 잭슨 라이브러리를 통해 json으로 자동 변환
		
		return ResponseEntity.status(HttpStatus.OK).body(wForcastService.getWeatherState(spot.getCity1(), spot.getCity2())) ;
	}
}
