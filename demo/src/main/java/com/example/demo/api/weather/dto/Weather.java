package com.example.demo.api.weather.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Weather {

	String day;
	LocalDateTime date;
	int WeatherStateSKY;
	int WeatherStatePTY;

	int x;
	int y;
	
	
	
	
	String WeatherState;
	
}
