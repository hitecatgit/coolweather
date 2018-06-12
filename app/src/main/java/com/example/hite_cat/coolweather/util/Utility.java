package com.example.hite_cat.coolweather.util;

import android.text.TextUtils;

import com.example.hite_cat.coolweather.db.City;
import com.example.hite_cat.coolweather.db.County;
import com.example.hite_cat.coolweather.db.Province;
import com.example.hite_cat.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    /**-------------------------------------------------------------------------------------- 解析和處理服務器返回的省級數據 --------------------------------------------------------------------------------------*/
    public static boolean handleProvinceResponse(String response){
        //如果返回的數據不為空
        if (!TextUtils.isEmpty(response)){
            try{
                //使用JSON解析
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++){
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    /**-------------------------------------------------------------------------------------- 解析和處理服務器返回的市級數據 --------------------------------------------------------------------------------------*/
    public static boolean handleCityResponse(String response, int provinceId){
        //如果返回的數據不為空
        if (!TextUtils.isEmpty(response)){
            try {
                //使用JSON解析
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    /**-------------------------------------------------------------------------------------- 解析和處理服務器返回的县級數據 --------------------------------------------------------------------------------------*/
    public static boolean handleCountyResponse(String response, int cityId){
        //如果返回的數據不為空
        if (!TextUtils.isEmpty(response)){
            try{
                //使用JSON解析
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++){
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    /**-------------------------------------------------------------------------------------- 將返回的數據解析成Weather實體類 --------------------------------------------------------------------------------------*/
    public static Weather handleWeatherResponse(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, Weather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
