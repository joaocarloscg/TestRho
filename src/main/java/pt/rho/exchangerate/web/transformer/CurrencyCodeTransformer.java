package pt.rho.exchangerate.web.transformer;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CurrencyCodeTransformer implements Converter<String, String> {

    @Override
    public String convert(String source) {
        if (source == null) {
            return null;
        }

        return source.trim().toUpperCase();
    }
}