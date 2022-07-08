package com.lkd.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.google.common.collect.Lists;
import com.lkd.entity.AbstractEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

@Slf4j
public class ExcelDataListener<T extends AbstractEntity, E> extends AnalysisEventListener<E> {

    private List<T>  list= Lists.newArrayList();

    private Class<T> clazz;

    private Function<Collection<T>,Boolean> saveFunc;

    private static final int BATCH_COUNT=500;

    /**
     * 提取数据
     * @param e
     * @param analysisContext
     */
    @Override
    public void invoke(E e, AnalysisContext analysisContext) {

        try {
            T t= clazz.getDeclaredConstructor(null).newInstance();
            BeanUtils.copyProperties( e,t );
            list.add(t);
            if( list.size()>=BATCH_COUNT ){
                doAfterAllAnalysed(null);
            }

        } catch (Exception ex) {
           log.error("读取excel出错");
        }


    }


    /**
     * 读取完成
     * @param analysisContext
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

        saveFunc.apply(list);
        list.clear();
    }


    public ExcelDataListener(Function<Collection<T>,Boolean> saveFunc,Class<T> clazz){
        this.saveFunc=saveFunc;
        this.clazz=clazz;
    }

}
