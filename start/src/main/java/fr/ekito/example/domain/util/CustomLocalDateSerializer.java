package fr.ekito.example.domain.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

/**
 * Custom Jackson serializer for displaying Joda Time dates.
 */
public class CustomLocalDateSerializer extends JsonSerializer<LocalDate> {

    private static DateTimeFormatter formatter =
            DateTimeFormat.forPattern("yyyy-MM-dd");

    @Override
    public void serialize(LocalDate value, JsonGenerator generator,
                          SerializerProvider serializerProvider)
            throws IOException {

        generator.writeString(formatter.print(value));
    }
}
