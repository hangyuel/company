package com.example.demo.api.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.demo.api.weather.dto.Weather;

public class WeatherManager {
	
	/**
	 * 기상청 API 요청
	 * @param x
	 * @param y
	 * @return
	 * @throws IOException
	 */
	public String getFcstAPI(int x, int y) throws IOException {
		/* API 요청 URL */
		String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
		LocalDateTime now = LocalDateTime.now(); // 요청 날짜 변수
		LocalDateTime minusDay = now.minusDays(1); // 하루 전 날짜 획득
		String spotLati = Integer.toString(x); // 지역 x좌표 // 파라미터
		String spotLongi = Integer.toString(y); // 지역 y좌표 // 파라미터

		String serviceKey = "7qCUGKm9LJ5xYB4GKJWtC%2FHB%2FOVqnv8AvLsAjiozdcuFhvaJPgxuNrrs8Xa5URGtEbEKlHepH9ZChm8h6gpbwA%3D%3D";
		String pageNo = "1";
		String numOfRows = "1000";
		String dataType = "JSON";
		String base_date = minusDay.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String base_time = "2300";
		String nx = spotLati;
		String ny = spotLongi;

		StringBuilder urlBuilder = new StringBuilder(apiUrl);

		urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey);
		urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(pageNo, "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode(numOfRows, "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode(dataType, "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(base_date, "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(base_time, "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8"));

		/*
		 * 조합완료된 요청 URL 생성 HttpURLConnection 객체 활용 API요청
		 */
		URL url = new URL(urlBuilder.toString());

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		connection.setRequestMethod("GET");
		connection.setRequestProperty("Content-Type", "application/json");

		BufferedReader br = null;

		if (connection.getResponseCode() >= 200 && connection.getResponseCode() <= 300) {
			br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		} else {
			br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
		}

		// 요청 결과 출력을 위한 준비
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		// 시스템 자원 반납 및 연결해제
		br.close();
		connection.disconnect();

		String result = sb.toString();


		return result;

	}
	
	/**
	 * API 반환결과에서 필요데이터 추출 및 가공
	 * @param result
	 * @return
	 */
	public List<Weather> getVillageFcst(String result) {
		JSONObject resultObj = new JSONObject(result);
		JSONObject response = resultObj.getJSONObject("response");
		JSONObject body = response.getJSONObject("body");
		JSONObject items = body.getJSONObject("items");
		JSONArray itemList = items.getJSONArray("item");

		// 조건에 해당하는 item을 담을 새로운 배열 생성
		JSONArray selectList = new JSONArray();
		for (int i = 0; i < itemList.length(); i++) {
			// 하나의 아이템 추출
			JSONObject item = itemList.getJSONObject(i);
			// 당일 오전 08시 기준으로 조건 분석을 위해 변수로 추출
			String fcstTime = item.getString("fcstTime");
			// 카테고리별 데이터 추출
			String category = item.getString("category");

			if (fcstTime.equals("0600")) {
				switch (category) {
				case "SKY":
					selectList.put(item);
					break;
				case "PTY":
					selectList.put(item);
					break;
				}
			}
		}
		// 단기예보에 대한 날씨 DTO 생성
		Weather today = new Weather();

		Calendar calendar = Calendar.getInstance();
		LocalDateTime now = LocalDateTime.now();
		
		
		
		

		for (int i = 0; i < selectList.length() - 4; i++) {
			JSONObject item = selectList.getJSONObject(i);
			System.out.println(item);

			String category = item.getString("category");
			String fcstDate = item.getString("fcstDate");
			String fcstValue = item.getString("fcstValue");
			
			// 확인용
			today.setX(item.getInt("nx"));
			today.setY(item.getInt("ny"));

			if (category.equals("SKY")) {
				today.setWeatherStateSKY(Integer.parseInt(fcstValue));
			} else if (category.equals("PTY")) {
				today.setWeatherStatePTY(Integer.parseInt(fcstValue));
			}

			int StateSKY = today.getWeatherStateSKY();
			int StatePTY = today.getWeatherStatePTY();
			

			if (StatePTY == 0) {
				if (StateSKY == 1) {
					today.setWeatherState("맑음");
				} else if (StateSKY == 3) {
					today.setWeatherState("구름 많음");
				} else if (StateSKY == 4) {
					today.setWeatherState("흐림");
				}
			} else if (StatePTY == 1) {
				today.setWeatherState("비");
			} else if (StatePTY == 2) {
				today.setWeatherState("s비/눈");
			} else if (StatePTY == 3) {
				today.setWeatherState("눈");
			} else if (StatePTY == 4) {
				today.setWeatherState("소나기");
			}

			// 1~3일 간의 데이터를 추출하기 위해 조건을 통한 분리
			if (fcstDate.equals(now.format(DateTimeFormatter.ofPattern("yyyyMMdd")))) {
				int day = calendar.get(Calendar.DAY_OF_WEEK);
				if (day == 1) {
					today.setDay("일");
					today.setDate(now);
				} else if (day == 2) {
					today.setDay("월");
					today.setDate(now);
				} else if (day == 3) {
					today.setDay("화");
					today.setDate(now);
				} else if (day == 4) {
					today.setDay("수");
					today.setDate(now);
				} else if (day == 5) {
					today.setDay("목");
					today.setDate(now);
				} else if (day == 6) {
					today.setDay("금");
					today.setDate(now);
				} else if (day == 7) {
					today.setDay("토");
					today.setDate(now);
				}

			}

		}
		
		List<Weather> weatherList = new ArrayList<>();

		// 데이터가 채워진 weather 객체 List에 주입

		weatherList.add(today);



		// 해당 리스트 반환
		return weatherList;
	}

}
