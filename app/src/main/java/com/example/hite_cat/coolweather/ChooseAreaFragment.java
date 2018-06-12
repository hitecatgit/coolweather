package com.example.hite_cat.coolweather;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hite_cat.coolweather.db.City;
import com.example.hite_cat.coolweather.db.County;
import com.example.hite_cat.coolweather.db.Province;
import com.example.hite_cat.coolweather.util.HttpUtil;
import com.example.hite_cat.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    //省列表
    private List<Province> provinceList;
    //市列表
    private List<City> cityList;
    //县列表
    private List<County> countyList;
    //選中的省份
    private Province selectedProvince;
    //選中的城市
    private City selectedCity;
    //選中的級別
    private int currentLevel;
    /**-------------------------------------------------------------------------------------- 獲取Layout & 控件 -------------------------------------------------------------------------------------- */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }
    /**--------------------------------------------------------------------------------------  初始化省級數據, 加設市和县的事件 -------------------------------------------------------------------------------------- */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //-------------------------------------------------------------------------------------- listView點擊事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //省級數據
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities();
                //市級數據
                } else if (currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounties();
                //县級數據
                } else if (currentLevel == LEVEL_COUNTY){
                    String weatherId = countyList.get(position).getWeatherId();
                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
                    intent.putExtra("weather_id", weatherId);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        //-------------------------------------------------------------------------------------- 返回事件
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY){
                    queryCities();
                } else if (currentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        //-------------------------------------------------------------------------------------- 加載省級數據
        queryProvinces();
    }
    /**--------------------------------------------------------------------------------------  省級數據 -------------------------------------------------------------------------------------- */
    private void queryProvinces() {
        titleText.setText("中國");
        //將返回按鈕隠藏
        backButton.setVisibility(View.GONE);

        //讀取所有省級數據(從數據庫Province.class)
        provinceList = DataSupport.findAll(Province.class);
        //加載所有省級數據
        if (provinceList.size() > 0){
            dataList.clear();
            for (Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
        //沒有讀取則向服務器查詢
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }
    /**--------------------------------------------------------------------------------------  市級數據 -------------------------------------------------------------------------------------- */
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        //設置返回按鈕
        backButton.setVisibility(View.VISIBLE);
        //讀取所有省級數據(從數據庫City.class)
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        //加載所有市級數據
        if (cityList.size() > 0){
            dataList.clear();
            for (City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        //沒有讀取則向服務器查詢
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }
    /**--------------------------------------------------------------------------------------  县級數據 -------------------------------------------------------------------------------------- */
    private void queryCounties(){
        titleText.setText(selectedCity.getCityName());
        //設置返回按鈕
        backButton.setVisibility(View.VISIBLE);
        //讀取所有县級數據(從數據庫County.class)
        countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);
        //加載所有县級數據
        if (countyList.size() > 0){
            dataList.clear();
            for (County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        //沒有讀取則向服務器查詢
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }
    /**-------------------------------------------------------------------------------------- 向服務器查詢數據 --------------------------------------------------------------------------------------*/
    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        //發送Http請求
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            //--------------------------------------------------------------------------------------請求失敗
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加載失敗", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            //-------------------------------------------------------------------------------------- 請求成功
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //獲取數據
                String responseText = response.body().string();
                boolean result = false;
                //使用JSON解析
                if ("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)){
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                } else if ("county".equals(type)){
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }
                //將數據顯示(主線程)
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            } else if ("city".equals(type)){
                                queryCities();
                            } else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }
    /**-------------------------------------------------------------------------------------- 加載進度條 --------------------------------------------------------------------------------------*/
    private void showProgressDialog() {
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加載...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    /**-------------------------------------------------------------------------------------- 關閉進度條 --------------------------------------------------------------------------------------*/
    private void closeProgressDialog() {
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
