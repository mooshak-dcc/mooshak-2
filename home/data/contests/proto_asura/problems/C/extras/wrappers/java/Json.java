package wrappers.java;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * JSON utilities for converting to/from JSON string
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Json {
    private final static Logger LOGGER = Logger.getLogger(Json.class.getSimpleName());

    private static GsonBuilder gsonBuilder = null;

    private Gson gson;

    private Json(Gson gson) {
        this.gson = gson;
    }

    public static Json get() {

        if (gsonBuilder == null) {
            gsonBuilder = new GsonBuilder();
            gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        }

        return new Json(gsonBuilder.create());
    }

    public static void registerTypeAdapter(Type type, Object typeAdapter) {
        gsonBuilder.registerTypeAdapter(type, typeAdapter);
    }

    public static void registerTypeAdapterFactory(TypeAdapterFactory factory) {
        gsonBuilder.registerTypeAdapterFactory(factory);
    }

    public static void registerTypeHierarchyAdapter(Class<?> baseType, Object typeAdapter) {
        gsonBuilder.registerTypeHierarchyAdapter(baseType, typeAdapter);
    }

    /***************************************************************************************
     *                             JSON String to Java Objects                             *
     ***************************************************************************************/

    public <T> T objectFromString(String json, Class<T> modelClass) {

        try {
            return gson.fromJson(json, modelClass);
        } catch (Exception e) {
            LOGGER.severe("objectFromString: " + e.getMessage());
        }

        return null;
    }

    public JsonElement jsonFromString(String json) {

        try {
            return gson.fromJson(json, JsonElement.class);
        } catch (Exception e) {
            LOGGER.severe("jsonFromString: " + e.getMessage());
        }

        return null;
    }

    public <T> Collection<T> collectionFromString(String json) {

        Type desiredType = new TypeToken<Collection<T>>() {
        }.getType();

        try {
            return gson.fromJson(json, desiredType);
        } catch (Exception e) {
            LOGGER.severe("collectionFromString: " + e.getMessage());
        }

        return null;
    }


    /***************************************************************************************
     *                                Java Objects to JSON                                 *
     ***************************************************************************************/

    public <T> JsonElement objectToJson(T object) {

        Type type = new TypeToken<T>() {
        }.getType();

        try {
            return gson.toJsonTree(object, type);
        } catch (Exception e) {
            LOGGER.severe("objectToString: " + e.getMessage());
        }

        return null;
    }

    public <T> JsonElement collectionToJson(List<T> list) {

        Type type = new TypeToken<List<T>>() {
        }.getType();

        try {
            return gson.toJsonTree(list, type);
        } catch (Exception e) {
            LOGGER.severe("collectionToJson: " + e.getMessage());
        }

        return null;
    }

    public <K, V> JsonElement mapToJson(Map<K, V> map) {

        Type type = new TypeToken<Map<K, V>>() {
        }.getType();

        try {
            return gson.toJsonTree(map, type);
        } catch (Exception e) {
            LOGGER.severe("mapToJson: " + e.getMessage());
        }

        return null;
    }

    public <T> JsonElement arrayToJson(T[] array) {

        Type type = new TypeToken<T[]>() {
        }.getType();

        try {
            return gson.toJsonTree(array, type);
        } catch (Exception e) {
            LOGGER.severe("arrayToJson: " + e.getMessage());
        }

        return null;
    }

    /***************************************************************************************
     *                             Java Objects to JSON String                             *
     ***************************************************************************************/

    public <T> String objectToString(T object) {

        Type type = new TypeToken<T>() {
        }.getType();

        try {
            return gson.toJson(object, type);
        } catch (Exception e) {
            LOGGER.severe("objectToString: " + e.getMessage());
        }

        return null;
    }

    public <T> String collectionToString(List<T> list) {

        Type type = new TypeToken<List<T>>() {
        }.getType();

        try {
            return gson.toJson(list, type);
        } catch (Exception e) {
            LOGGER.severe("collectionToString: " + e.getMessage());
        }

        return null;
    }

    public <K, V> String mapToString(Map<K, V> map) {

        Type type = new TypeToken<Map<K, V>>() {
        }.getType();

        try {
            return gson.toJson(map, type);
        } catch (Exception e) {
            LOGGER.severe("mapToString: " + e.getMessage());
        }

        return null;
    }

    public <T> String arrayToString(T[] array) {

        Type type = new TypeToken<T[]>() {
        }.getType();

        try {
            return gson.toJson(array, type);
        } catch (Exception e) {
            LOGGER.severe("arrayToString: " + e.getMessage());
        }

        return null;
    }
}
