package com.example.hite_cat.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {
    /**因為JSON有些字段不太適合作為Java字段命名, 使用@SerializedName注解讓JSON字段與Java字段建立映射關系*/
    //城市名
    @SerializedName("city")
    public String cityName;
    //城市ID
    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {
        //天氣更新時間
        @SerializedName("loc")
        public String updateTime;
    }
}
