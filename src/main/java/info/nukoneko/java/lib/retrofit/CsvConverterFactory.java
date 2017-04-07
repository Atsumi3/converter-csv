package info.nukoneko.java.lib.retrofit;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@SuppressWarnings("unused")
public final class CsvConverterFactory extends Converter.Factory {
    private static final MediaType MEDIA_TYPE = MediaType.parse("text/csv; charset=UTF-8");

    public static CsvConverterFactory create() {
        return new CsvConverterFactory();
    }

    private CsvConverterFactory() {
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations,
                                                          Retrofit retrofit) {
        if (type == String.class) {
            return new Converter<String, RequestBody>() {
                @Override
                public RequestBody convert(String value) throws IOException {
                    return RequestBody.create(MEDIA_TYPE, String.valueOf(value));
                }
            };
        }
        return null;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        try {
            return new CsvResponseBodyConverter<>(type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
