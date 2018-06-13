package com.example.hite_cat.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Suggestion {
    /**因為JSON有些字段不太適合作為Java字段命名, 使用@SerializedName注解讓JSON字段與Java字段建立映射關系*/
    //舒適度
    @SerializedName("comf")
    public Comfort comfort;
    //洗車情況
    @SerializedName("cw")
    public CarWash carWash;
    //運動情況
    public Sport sport;

    public class Comfort {
        //舒適度更多資訊
        @SerializedName("txt")
        public String info;
    }

    public class CarWash {
        //洗車更多資訊
        @SerializedName("txt")
        public String info;

    }

    public class Sport {
        //運動更多資訊
        @SerializedName("txt")
        public String info;

    }

}
