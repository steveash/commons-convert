package org.apache.commons.convert;

/**
 * Thrown when this converter is unable to convert the type given or no converter exists that
 * can convert this type
 */
public class UnconvertableException extends ConversionException {

    public static UnconvertableException makeNoConverterExists(Class<?> sourceType, Class<?> targetType) {
        return new UnconvertableException("No converter exists to convert from type " + sourceType +
                " to type " + targetType);
    }

    public UnconvertableException(String message) {
        super(message);
    }

    public UnconvertableException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnconvertableException(Throwable cause) {
        super(cause);
    }
}
