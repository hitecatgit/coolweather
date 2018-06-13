package com.example.hite_cat.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {
    /**創建Weather來引用各類實體類*/
    //返回成功, 還是失敗的數據
    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public Suggestion suggestion;
    //因為daily_forecast是一個數組, 所以要用List集合
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

}
