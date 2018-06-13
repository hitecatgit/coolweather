package com.example.hite_cat.coolweather.gson;

public class AQI {
    /**因為JSON有些字段不太適合作為Java字段命名, 使用@SerializedName注解讓JSON字段與Java字段建立映射關系*/
    //城市
    public AQICity city;

    public class AQICity {
        //城市AQI
        public String aqi;
        //城市PM25
        public String pm25;

    }

}
