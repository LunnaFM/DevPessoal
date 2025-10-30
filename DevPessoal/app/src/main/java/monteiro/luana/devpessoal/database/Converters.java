package monteiro.luana.devpessoal.database;

import androidx.room.TypeConverter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Converters para tipos customizados no Room Database
 * Converte LocalDate para String e vice-versa
 */
public class Converters {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Converte LocalDate para String para armazenar no banco
     */
    @TypeConverter
    public static String fromLocalDate(LocalDate date) {
        return date == null ? null : date.format(formatter);
    }

    /**
     * Converte String do banco para LocalDate
     */
    @TypeConverter
    public static LocalDate toLocalDate(String dateString) {
        return dateString == null ? null : LocalDate.parse(dateString, formatter);
    }
}