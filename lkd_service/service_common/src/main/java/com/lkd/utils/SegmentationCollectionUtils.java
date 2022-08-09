package com.lkd.utils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.Method;
import java.util.*;
/**
 * @author: liYuan
 * @Title: SegmentationCollectionUtils
 * @ProjectName: lkd_parent
 * @Description:
 * @date: 2022/8/9 7:39
 */
@Slf4j
//public class SegmentationCollectionUtils extends CollectionUtils{
public class SegmentationCollectionUtils{
    /**
     * 拆分集合
     *
     * @param <T> 泛型对象
     * @param resList 需要拆分的集合
     * @param subListLength 每个子集合的元素个数
     * @return 返回拆分后的各个集合组成的列表
     * 代码里面用到了guava和common的结合工具类
     **/
    public static <T> List<List<T>> splitLists(List<T> resList, int subListLength) {
        if (CollectionUtils.isEmpty(resList) || subListLength <= 0) {
            return new ArrayList<>();
        }
        List<List<T>> ret = new ArrayList<>();
        int size = resList.size();
        if (size <= subListLength) {
            // 数据量不足 subListLength 指定的大小
            ret.add(resList);
        } else {
            //被切割成了多少份。
            int pre = size / subListLength;
            int last = size % subListLength;
            // 前面pre个集合，每个大小都是 subListLength 个元素
            for (int i = 0; i < pre; i++) {
                List<T> itemList = new ArrayList<>();
                for (int j = 0; j < subListLength; j++) {
                    itemList.add(resList.get(i * subListLength + j));
                }
                ret.add(itemList);
            }
            // last的进行处理
            if (last > 0) {
                List<T> itemList = new ArrayList<>();
                for (int i = 0; i < last; i++) {
                    itemList.add(resList.get(pre * subListLength + i));
                }
                ret.add(itemList);
            }
        }
        return ret;
    }


    /**
         * 将一个List的集合分割成多个用于批量处理
         * @param source 总量集合
         * @param n 分成几份
         * @param <T> 泛型
         * @return 分割后的子集合
         */
        public static <T> List<List<T>> averageAssignList(List<T> source, int n) {
            List<List<T>> result = new ArrayList<List<T>>();
            int remainder = source.size() % n;  //(先计算出余数)，不够整除的部分。
            int number = source.size() / n;  //然后是商       每个子集合中多少个元素？
            int offset = 0;//偏移量
            for (int i = 0; i < n; i++) {
                List<T> value = null;
                if (remainder > 0) {
                    value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                    remainder--;
                    offset++;
                } else {
                    value = source.subList(i * number + offset, (i + 1) * number + offset);
                }
                result.add(value);
            }
            return result;
        }

        private static final int DEFAULT_SIZE = 1000;

        /**
         *
         * <p>拆分List为固定大小的多个集合</p>
         * <p>推荐使用</p>
         * <p>返回集合的size越小,此方法性能越高</p>
         * @param baseList
         * @param size
         * @return ArrayList
         */
        @SuppressWarnings("unchecked")
        public static <T> List<List<T>> fastSplitList(List<T> baseList, int size) {
            if (baseList == null || baseList.size() == 0) {
                return null;
            }
            if (size <= 0) {
                size = DEFAULT_SIZE;
            }
            int arrSize = baseList.size() % size == 0 ? baseList.size() / size : baseList.size() / size + 1;
            List<List<T>> resultList = new ArrayList<List<T>>();
            for (int i = 0; i < arrSize; i++) {
                if (arrSize - 1 == i) {
                    resultList.add((List<T>) new ArrayList<Object>( baseList.subList(i * size, baseList.size())));
                } else {
                    resultList.add((List<T>) new ArrayList<Object>( baseList.subList(i * size, size * (i + 1))));
                }
            }
            return resultList;
        }

        /**
         *
         * <p>拆分List为固定大小的多个集合</p>
         * <p>返回集合的size越大,此方法性能越高</p>
         * @param baseList
         * @param size
         * @return ArrayList
         */
        public static <T> List<List<T>> splitList(List<T> baseList, int size) {
            if (baseList == null || baseList.size() == 0) {
                return null;
            }
            if (size <= 0) {
                size = DEFAULT_SIZE;
            }
            List<List<T>> resultList = new ArrayList<>();
            for (int i = 0; i < baseList.size(); ++i) {
                if (i % size == 0) {
                    List<T> result = new ArrayList<T>();
                    resultList.add(result);
                }
                resultList.get(i / size).add(baseList.get(i));
            }
            return resultList;
        }

        /**
         *
         * <p>集合转Set</p>
         * @param coll 源集合
         * @param keyType 属性类型
         * @param keyMethodName 属性get方法
         * @return LinkedHashSet
         */
        public static <K, V> Set<K> asSet(final java.util.Collection<V> coll,final Class<K> keyType
                ,final String keyMethodName) {
            if (CollectionUtils.isEmpty(coll)) {
                return new HashSet<K>(0);
            }
            final Set<K> set = new LinkedHashSet<K>(coll.size());
            try {
                for (final V value : coll) {
                    Object object;
                    Method method = value.getClass().getMethod(keyMethodName);
                    object = method.invoke(value);
                    @SuppressWarnings("unchecked")
                    final K key = (K) object;
                    set.add(key);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new CollectionUtilsException("Collection conversion Set exceptions");
            }
            return set;
        }

        /**
         *
         * <p>集合转Map</p>
         * <p>比如:List<EmployeeEntity>,讲EmployeeEntity的name属性作为key,转换成Map</p>
         * @param coll 源集合
         * @param keyType 属性类型
         * @param valueType 源数据类型(实体类型)
         * @param keyMethodName 属性get方法
         * @return LinkedHashMap
         */
        public static <K, V> Map<K, V> asMap(final java.util.Collection<V> coll,final Class<K> keyType
                ,final Class<V> valueType,final String keyMethodName) {
            if (CollectionUtils.isEmpty(coll)) {
                return new LinkedHashMap<K, V>(0);
            }
            final Map<K, V> map = new LinkedHashMap<K, V>(coll.size());
            try {
                Method method = valueType.getMethod(keyMethodName);
                for (final V value : coll) {
                    Object object;
                    object = method.invoke(value);
                    @SuppressWarnings("unchecked")
                    final K key = (K) object;
                    map.put(key, value);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new CollectionUtilsException("Collection conversion Map exceptions");
            }
            return map;
        }

        /**
         * <p>集合转List</p>
         * @param coll
         * @return ArrayList
         */
        public static <V> List<V> asList(final java.util.Collection<V> coll) {
            if (CollectionUtils.isEmpty(coll)) {
                return new ArrayList<V>(0);
            }
            final List<V> list = new ArrayList<V>();
            for (final V value : coll) {
                if (value != null) {
                    list.add(value);
                }
            }
            return list;
        }

        /**
         * <p>集合<String>toString</p>
         * @param collection 泛型必须为String类型
         * @param split 比如连接符","
         * @return
         */
        public static String collToString(Collection<String> collection, String split) {
            StringBuilder sb = new StringBuilder();
            if (collection != null) {
                int i = 0, size = collection.size();
                for (Iterator<String> iterator = collection.iterator(); iterator.hasNext();) {
                    String str = iterator.next();
                    sb.append(str);
                    if (++i < size) {
                        sb.append(split);
                    }
                }
            }
            return sb.toString();
        }

        static class CollectionUtilsException extends RuntimeException{

            private static final long serialVersionUID = 1L;

            public CollectionUtilsException(String s) {
                super(s);
            }

            public CollectionUtilsException(String s, Throwable e) {
                super(s, e);
            }

            public CollectionUtilsException(Throwable e) {
                super(e);
            }

        }
}
