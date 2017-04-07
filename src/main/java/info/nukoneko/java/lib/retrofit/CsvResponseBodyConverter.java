package info.nukoneko.java.lib.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings({"Duplicates", "unchecked", "WeakerAccess"})
public final class CsvResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private Class<T> mClazz;
    private boolean isList;

    @SuppressWarnings("unchecked")
    public CsvResponseBodyConverter(Type type) throws Exception {
        if (type instanceof Class) {
            mClazz = (Class<T>) type;
            isList = false;
        } else if (type instanceof ParameterizedTypeImpl) {
            final ParameterizedTypeImpl genericParameterType = (ParameterizedTypeImpl) type;
            Class<T> genericRawTypeClass = (Class<T>) genericParameterType.getRawType();
            isList = genericRawTypeClass != null && genericRawTypeClass.isInstance(new ArrayList<>());
            if (!isList) throw new CsvParseException(type.getClass() + "is not list.");
            mClazz = (Class<T>) genericParameterType.getActualTypeArguments()[0];
        } else {
            throw new CsvParseException(type.getClass() + "is not parsable.");
        }
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        T retObj = null;
        final String csvText = value.string();
        if (isValidCsv(csvText)) {
            if (isList) {
                retObj = convertCSVToObjectList(csvText);
            } else {
                retObj = convertCSVToObject(csvText);
            }
        }
        value.close();
        return retObj;
    }

    private boolean isValidCsv(final String csvText) {
        final String[] csvRows = csvText.split("\n");
        if (2 > csvRows.length) return false;
        final String[] csvTitleRow = csvRows[0].split(",");
        if (2 > csvTitleRow.length) return false;
        for (int i = 1; i < csvRows.length; i++) {
            final String[] csvContentRow = csvRows[i].split(",");
            if (csvContentRow.length != csvTitleRow.length) return false;
        }
        return true;
    }

    private T convertCSVToObject(final String csvText) throws IOException {
        final String[] csvRows = csvText.split("\n");
        final String[] csvTitleRow = csvRows[0].split(",");
        final String[] csvContentRow = csvRows[1].split(",");

        final Map<String, String> valueMap = new HashMap<>();
        for (int j = 0; j < csvContentRow.length; j++) valueMap.put(csvTitleRow[j], csvContentRow[j]);
        return convertMapToObject(valueMap);
    }

    private T convertCSVToObjectList(final String csvText) throws IOException {
        final List<T> valueList = new ArrayList<>();
        final String[] csvRows = csvText.split("\n");
        final String[] csvTitleRow = csvRows[0].split(",");
        for (int i = 1; i < csvRows.length; i++) {
            Map<String, String> valueMap = new HashMap<>();
            final String[] csvContentRow = csvRows[i].split(",");
            for (int j = 0; j < csvContentRow.length; j++) valueMap.put(csvTitleRow[j], csvContentRow[j]);
            valueList.add(convertMapToObject(valueMap));
        }
        return (T) valueList;
    }

    @SuppressWarnings({"TryWithIdenticalCatches", "unchecked"})
    private T convertMapToObject(Map<String, String> propertyMap) {
        try {
            T t = mClazz.newInstance();
            try {
                for (Map.Entry<String, String> entry : propertyMap.entrySet()) {
                    final String targetValue = entry.getValue().replace("\"", "");
                    final Field targetField = mClazz.getDeclaredField(entry.getKey());
                    boolean fieldAccessible = targetField.isAccessible();

                    targetField.setAccessible(true);

                    final Type type = targetField.getType();
                    if (type == String.class) {
                        targetField.set(t, targetValue);
                    } else if (type == Date.class) {
                        targetField.set(t, dateFormat.parse(targetValue));
                    } else if (type == boolean.class || type == Boolean.class) {
                        targetField.setBoolean(t, Boolean.parseBoolean(targetValue));
                    } else if (type == byte.class || type == Byte.class) {
                        targetField.setByte(t, Byte.parseByte(targetValue));
                    } else if (type == char.class || type == Character.class) {
                        if (targetValue.length() == 1)
                            targetField.setChar(t, targetValue.charAt(0));
                    } else if (type == double.class || type == Double.class) {
                        targetField.setDouble(t, Double.parseDouble(targetValue));
                    } else if (type == float.class || type == Float.class) {
                        targetField.setFloat(t, Float.parseFloat(targetValue));
                    } else if (type == long.class || type == Long.class) {
                        targetField.setFloat(t, Long.parseLong(targetValue));
                    } else if (type == int.class || type == Integer.class) {
                        targetField.setInt(t, Integer.parseInt(targetValue));
                    } else if (type == short.class || type == Short.class) {
                        targetField.setShort(t, Short.parseShort(targetValue));
                    } else {
                        System.out.println(String.format("%s Can't parse this Converter.", type.getClass().getSimpleName()));
                    }

                    targetField.setAccessible(fieldAccessible);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return t;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
