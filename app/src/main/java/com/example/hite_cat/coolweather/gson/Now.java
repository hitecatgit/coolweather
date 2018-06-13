package com.example.hite_cat.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Now {
    /**因為JSON有些字段不太適合作為Java字段命名, 使用@SerializedName注解讓JSON字段與Java字段建立映射關系*/
    //城市溫度
    @SerializedName("tmp")
    public String temperature;
    //更多資料
    @SerializedName("cond")
    public More more;

    public class More {
        //天氣情況
        @SerializedName("txt")
        public String info;
    }
}
