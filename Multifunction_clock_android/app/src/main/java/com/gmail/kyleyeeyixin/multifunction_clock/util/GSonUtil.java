package com.gmail.kyleyeeyixin.multifunction_clock.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;

/***
 * 
 * @author wangqingsong
 * 
 */
public class GSonUtil {
    static ExclusionStrategy strategy = new ExclusionStrategy() {

        @Override
        public boolean shouldSkipField(FieldAttributes attr) {
            if (attr != null && attr.getAnnotations() != null) {
                Collection<Annotation> annotations = attr.getAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation instanceof Expose) {
                        Expose temp = (Expose) annotation;
                        return !temp.serialize();
                    }
                }
            }

            return false;
        }

        @Override
        public boolean shouldSkipClass(Class<?> className) {
            return false;
        }
    };

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final Gson NAMING_GSON = new GsonBuilder()
            .setDateFormat(DATE_FORMAT)
            .addSerializationExclusionStrategy(strategy)
            .create();

    /***
     * 把Json转化为对象
     * 
     * @param <T>
     * @param json
     * @param classOfT
     * @return
     */
    public static <T> T getObjectFromJson(String json, Class<T> classOfT) {
        return NAMING_GSON.fromJson(json, classOfT);
    }

    public static <T> T getObjectFromJson(String json, Type classOfT) {
        return NAMING_GSON.fromJson(json, classOfT);
    }

    /**
     * 将对象实例转化到Gson字符串; 如果在此class中某些属性表明了@Expose; 则此属性不会被Gson实例化到json字符串中;
     * 
     * @param obj
     * @return
     */
    public static String getJson(Object obj) {
        return NAMING_GSON.toJson(obj);
    }
}
