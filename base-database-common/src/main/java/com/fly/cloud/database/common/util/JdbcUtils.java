package com.fly.cloud.database.common.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * JDBC工具类
 *
 * @description: JDBC工具类
 * @program: base-database
 * @author: xux
 * @date: 2020-09-16 17:37:41
 **/
public class JdbcUtils<T> {
    private static DataSource source = null;// 注意这里是私有，静态变量。

    /**
     * 初始化数据源
     *
     * @param p 属性值
     */
    private static void init(Properties p) {
        try {
            // 加载文件  得到一个  druid.properties 的文件。
            // p.load(JdbcUtils.class.getClassLoader().getResourceAsStream("druid.properties"));
            // 获取数据源
            synchronized (JdbcUtils.class) {
                source = DruidDataSourceFactory.createDataSource(p);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 建立连接
     *
     * @param p 属性值
     * @return
     */
    public static Connection getConnetion(Properties p) {
        try {
            if (source == null) {
                init(p);
            }
            //利用连接池连接对象
            Connection con = source.getConnection();
            return con;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 增删改的通用
     *
     * @param p    属性值
     * @param sql  SQL语句
     * @param args 字段值
     * @return
     */
    public static int exectueUpdate(Properties p, String sql, Object... args) {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = JdbcUtils.getConnetion(p);
            ps = con.prepareStatement(sql);
            if (args != null && args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }
            }
            int i = ps.executeUpdate();
            return i;

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return 0;
    }

    /**
     * 批量插入
     *
     * @param p           属性值
     * @param tableName   表名
     * @param tableFields 字段
     * @param dataList    插入数据
     * @param objClass    实体类
     */
    public static void svaeBatch(Properties p, String tableName, String tableFields, List<Object> dataList, Class objClass) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            //获取数据表列字段
            Field[] fields = ReflectUtil.getFieldsDirectly(objClass, false);
            StringBuilder valueSb = new StringBuilder();
            for (Field field : fields) {
                if ("serialVersionUID".equalsIgnoreCase(field.getName())) {
                    continue;
                }
                valueSb.append("?").append(",");
            }
            String valueSql = valueSb.toString().toString().substring(0, valueSb.toString().lastIndexOf(","));
            String sql = "insert into " + tableName + " (" + tableFields + ") values (" + valueSql + ")";
            con = JdbcUtils.getConnetion(p);
            con.setAutoCommit(false);
            ps = con.prepareStatement(sql);
            if (dataList != null && dataList.size() > 0) {
                int len = dataList.size();
                for (int i = 0; i < dataList.size(); i++) {
                    Object data = dataList.get(i);
                    int index = 1;
                    for (Field field : fields) {
                        if ("serialVersionUID".equalsIgnoreCase(field.getName())) {
                            continue;
                        }
                        ps.setObject(index++, ReflectUtil.getFieldValue(data, field.getName()));
                    }
                    ps.addBatch();
                    if ((i != 0 && i % 200 == 0) || i == len - 1) {//可以设置不同的大小；如50，100，200，500，1000等等
                        ps.executeBatch();
                        //优化插入第三步提交，批量插入数据库中。
                        con.commit();
                        ps.clearBatch();//提交后，Batch清空。
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    /**
     * SQL查询的通用
     *
     * @param p           属性值
     * @param sql         SQL语句
     * @param entityClass 实体类
     * @return
     */
    public static Object executeQuery(Properties p, String sql, Class entityClass) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = JdbcUtils.getConnetion(p);  //注意这里是用source 来调用 getConnetion ,不再是通过BaseDAO
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            int count = rs.getMetaData().getColumnCount();
            List<Object> list = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                for (int i = 0; i < count; i++) {
                    Object values = rs.getObject(i + 1);
                    String countName = rs.getMetaData().getColumnLabel(i + 1);
                    map.put(countName, values);
                }
                list.add(BeanUtil.mapToBean(map, entityClass, true));
            }
            return list;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
//            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * SQL查询总数的通用
     *
     * @param p   属性值
     * @param sql SQL语句
     * @return
     */
    public static int executeQueryNum(Properties p, String sql) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int totalNum = 0;
        try {
            con = JdbcUtils.getConnetion(p);  //注意这里是用source 来调用 getConnetion ,不再是通过BaseDAO
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                totalNum = rs.getInt(1);
            }
            return totalNum;
        } catch (SQLException e) {
            return totalNum;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
