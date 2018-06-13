package com.example.hite_cat.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Forecast {
    /**因為JSON有些字段不太適合作為Java字段命名, 使用@SerializedName注解讓JSON字段與Java字段建立映射關系*/
    //當日日期
    public String date;
    //當日溫度
    @SerializedName("tmp")
    public Temperature temperature;
    //更多資訊
    @SerializedName("cond")
    public More more;

    public class Temperature {
        //當日最高溫度
        public String max;
        //當日最低溫度
        public String min;

    }

    public class More {
        //當日天氣情況
        @SerializedName("txt_d")
        public String info;
    }
}
