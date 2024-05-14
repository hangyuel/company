package com.example.demo.api.weather.service;


import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.api.weather.WeatherManager;
import com.example.demo.api.weather.dto.Spot;
import com.example.demo.api.weather.dto.Weather;
import com.example.demo.api.weather.repository.WForcastRepository;

import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class WForcastService {
	
	private final WForcastRepository wForcastRepository;

	/**
	 * 기상청 api 데이터 중 단기 예보의 날씨 상태(하늘상태,강수상태)  
	 * @param city1
	 * @param city2
	 * @return
	 * @throws IOException
	 */
	public List<Weather> getWeatherState(String city1, String city2) throws IOException{
		Spot spot = new Spot();
		spot.setCity1(city1);
		spot.setCity2(city2);
		
		// 좌표(x,y) 흭득
		spot = wForcastRepository.findSpot(spot);
		
		
	    // WeatherManager는 API결과값을 전달받아 weather 객체를 가공(날씨 상태 정보 흭득)하는 클래스
	    WeatherManager weatherManager = new WeatherManager();
	    
	    String result = weatherManager.getFcstAPI(spot.getX(),spot.getY());
	    
		return weatherManager.getVillageFcst(result);
	}
}
