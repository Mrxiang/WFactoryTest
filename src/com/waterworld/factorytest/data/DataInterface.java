package com.waterworld.factorytest.data;

import com.waterworld.factorytest.FactoryBean;

import java.util.List;

public interface DataInterface {

    public  void readDatasStatus(List<FactoryBean> mDatas );

    public  void storeDatas(List<FactoryBean>  mDatas);

}
