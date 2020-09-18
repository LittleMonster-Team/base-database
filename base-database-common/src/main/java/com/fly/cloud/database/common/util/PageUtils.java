package com.fly.cloud.database.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页查询数据
 *
 * @description: 分页查询数据
 * @program: base-database
 * @author: xux
 * @date: 2020-09-16 17:37:41
 **/
public class PageUtils {
    /**
     * 分页查询数据
     *
     * @param newList 数据
     * @param page    第几页
     * @param size    每页条数
     * @return
     */
    public static <T> java.util.List<T> getPage(List<T> newList, String page, long size) {
        List<T> list = new ArrayList<T>();
        List<T> entityLogListTwo = new ArrayList<T>();
        try {
            int pageTotalNumber = newList.size();     //数据记录总条数
            int pageSize = 0;             //总页数
            int k = 0;                  //循环判断条件
            //得到总记录数
            //得到总页数，每一页10条记录
            if (pageTotalNumber % size == 0) {
                pageSize = (int) (pageTotalNumber / size);
            } else {
                //有余数，那么总页数要+1
                pageSize = (int) (pageTotalNumber / size + 1);
            }
            //对页面传回的页数进行判断
            if (Integer.parseInt(page) <= pageSize) {
                /**
                 * 根据访问的页数，取得相应数据
                 * i:表示从哪里开始取数据
                 * pageNumber*10：表示现在第几条记录
                 * */
                for (int i = (int) (Integer.parseInt(page) * size - size); i < pageTotalNumber; i++, k++) {
                    if (k < size) {
                        //把数据从原来List集合中，转移到新的List集合
                        entityLogListTwo.add(newList.get(i));
                    }
                }
                return entityLogListTwo;
            } else {
                return list;
            }
        } catch (Exception e) {
            return list;
        }
    }
}
