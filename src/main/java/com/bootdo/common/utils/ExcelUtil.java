package com.bootdo.common.utils;

import com.bootdo.common.config.Constant;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * excel工具类
 */
public class ExcelUtil {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public HSSFWorkbook workbook = new HSSFWorkbook();
    public ExcelUtil(){
        workbook.createSheet();//创建sheet
    }

    /**
     * 搭建Title
     * @param t
     * @param <T>
     */
    public <T> void buildTitle(T t){
        Field[] fields = t.getClass().getDeclaredFields();
        int count = 0;
        HSSFRow title = workbook.getSheetAt(0).createRow(0);
        for (Field f:fields){
            /**
             * 获取表格title注解
             */
            Annotation annotation = f.getAnnotation(TableTitle.class);
            if (annotation != null){
                String name = ((TableTitle) annotation).name();
                HSSFCell cell = title.createCell(count);
                cell.setCellValue(name);
                count++;

            }
        }
    }

    /**
     * 搭建主体
     * @param t
     * @param list
     * @param <T>
     * @throws Exception
     */
    public <T> void buildData(T t,List<Map<String,Object>> list) throws Exception{
        Field[] fields = t.getClass().getDeclaredFields();
        int countRow = 1;
        for (int i = 0 ; i < list.size(); i++){
            Map map = list.get(i);
            T temp = covert(map,t.getClass());//map对象转化为泛型
            logger.info("obj:"+temp.toString());
            int countCell = 0;
            HSSFRow row = workbook.getSheetAt(0).createRow(countRow);
            /**
             * 根据属性插入数据
             */
            for (Field f:fields){
                Annotation annotation = f.getAnnotation(TableTitle.class);
                if (annotation != null){
                    HSSFCell cell = row.createCell(countCell);
                    countCell++;
                    f.setAccessible(true);
                    cell.setCellValue(f.get(temp)+"");
                    cell.getAddress().toString();
                    logger.info("cellAddress:"+cell.getAddress().toString());
                    logger.info("cell:"+f.get(temp));

                }
            }
            countRow++;
        }
    }

    /**
     * HashMap转化为T
     * @param map
     * @param calzz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T covert(Map map,Class calzz) throws Exception{
        Field[] fields = calzz.getDeclaredFields();
        T obj = (T)calzz.newInstance();
        //将查询结果转化为驼峰
        Map newMap = StringUtils.formatHumpName(map);
        for (Field f:fields){
            int mod = f.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isFinal(mod)){
                continue;
            }
            Annotation annotation = f.getAnnotation(TableTitle.class);
            if (annotation == null){
                continue;
            }
            f.setAccessible(true);
            logger.info("type:"+f.getType().getTypeName());
            Object value = newMap.get(f.getName());
            if (Constant.JAVA_DATA_TYPE_OF_STRING.equals(f.getType().getTypeName())){
                f.set(obj,newMap.get(f.getName())+"");
            }else if(Constant.JAVA_DATA_TYPE_OF_INTEGER.equals(f.getType().getTypeName())){
                if (value != null){
                    f.set(obj, Integer.parseInt(newMap.get(f.getName())+""));
                }else{
                    f.set(obj, 0);
                }
            }else if(Constant.JAVA_DATA_TYPE_OF_DOUBLE.equals(f.getType().getTypeName())){
                if (value != null){
                    f.set(obj, Double.parseDouble(newMap.get(f.getName())+""));
                }else {
                    f.set(obj, 0.0);
                }
            }
        }
        return obj;
    }

    /**
     * 创建excel
     * @param t
     * @param list
     * @param <T>
     * @throws Exception
     */
    public <T> void create(T t,List<Map<String,Object>> list) throws Exception{
        buildTitle(t);
        buildData(t,list);
    }
}
