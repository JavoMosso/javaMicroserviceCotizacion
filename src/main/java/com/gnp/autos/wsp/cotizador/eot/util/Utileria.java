package com.gnp.autos.wsp.cotizador.eot.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;

import com.gnp.autos.wsp.cotizador.eot.error.ExecutionError;
import com.gnp.autos.wsp.negocio.neg.model.ElementoNeg;
import com.google.gson.Gson;

/**
 * The Interface Utileria.
 */
public final class Utileria {
    /**
     * Constructor privado para evitar creacion de objetos.
     */
    private Utileria() {
    }

    /**
     * Checks if is numeric.
     *
     * @param s the s
     * @return true, if is numeric
     */
    public static boolean isNumeric(final String s) {
        if (!Optional.ofNullable(s).isPresent()) {
            return false;
        }
        return s.matches("[-+]?\\d*\\.?\\d+");
    }

    /**
     * Solo numeros.
     *
     * @param referencia the referencia
     * @return the string
     */
    public static String soloNumeros(final String referencia) {
        if (referencia == null) {
            return null;
        }
        return referencia.replaceAll("[^0-9.]", "");
    }

    /**
     * Solo letras.
     *
     * @param texto the texto
     * @return the string
     */
    public static String soloLetras(final String texto) {
        if (!Optional.ofNullable(texto).isPresent()) {
            return null;
        }
        return texto.replaceAll("[^A-Za-z]+", "");
    }

    /**
     * Solo alfa numerico.
     *
     * @param texto the texto
     * @return the string
     */
    public static String soloAlfaNumerico(final String texto) {
        if (!Optional.ofNullable(texto).isPresent()) {
            return null;
        }
        return texto.replaceAll("[^A-Za-z0-9]+", "");
    }

    /**
     * Gets the registra log time.
     *
     * @param registro the registro
     */
    public static void getRegistraLogTime(final String registro) {
        Timestamp tsReq = new Timestamp(new Date().getTime());
        Logger.getRootLogger().info("***********************" + registro + ":" + tsReq);

    }

    /**
     * Gets the XML date.
     *
     * @param date the date
     * @return the XML date
     */
    public static XMLGregorianCalendar getXMLDate(final String date) {
        Date fecha = null;
        SimpleDateFormat d = new SimpleDateFormat("yyyyMMdd");
        try {
            fecha = d.parse(date);
        } catch (ParseException e1) {
            throw new ExecutionError(Constantes.ERROR_6);
        }

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(fecha);
        XMLGregorianCalendar fechaXml;
        try {
            fechaXml = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        } catch (DatatypeConfigurationException e) {
            throw new ExecutionError(Constantes.ERROR_7, e);
        }

        return fechaXml;
    }

    /**
     * Gets the f 1 date F 2.
     *
     * @param date the date
     * @return the f 1 date F 2
     */
    public static String getF1DateF2(final String date) {
        Date fecha = null;
        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");
        try {

            fecha = d.parse(date);
            return getStrFecha(fecha);

        } catch (ParseException e1) {
            throw new ExecutionError(Constantes.ERROR_6);
        }
    }

    /**
     * Gets the XML date.
     *
     * @param fecha the fecha
     * @return the XML date
     */
    public static XMLGregorianCalendar getXMLDate(final Date fecha) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(fecha);
        XMLGregorianCalendar fechaXml;
        try {
            fechaXml = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        } catch (DatatypeConfigurationException e) {
            throw new ExecutionError(Constantes.ERROR_7, e);
        }

        return fechaXml;
    }

    /**
     * Gets the decimal format.
     *
     * @param val   the val
     * @param scale the scale
     * @return the decimal format
     */
    public static String getDecimalFormat(final Double val, final Integer scale) {
        BigDecimal bd = BigDecimal.valueOf(val);
        if (bd.scale() > scale) {
            bd = bd.setScale(scale, RoundingMode.HALF_UP);
        }
        return bd.toString();
    }

    /**
     * Gets the decimal format 1.
     *
     * @param valr  the valr
     * @param scale the scale
     * @return the decimal format 1
     */
    public static BigDecimal getDecimalFormat1(final Double valr, final Integer scale) {
        BigDecimal bdv = BigDecimal.valueOf(valr);
        if (bdv.scale() > scale) {
            bdv = bdv.setScale(scale, RoundingMode.HALF_UP);
        }
        return bdv;
    }

    /**
     * Gets the str fecha.
     *
     * @param fecha the fecha
     * @return the str fecha
     */
    public static String getStrFecha(final Date fecha) {
        String strFecha;
        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
            strFecha = format1.format(fecha);
        } catch (DateTimeParseException ex) {
            throw new ExecutionError(Constantes.ERROR_4, ex);
        }
        return strFecha;
    }

    /**
     * Pinta obj to json.
     *
     * @param <T>        the generic type
     * @param clazz      the clazz
     * @param isRegistra the is registra
     */
    public static <T> void pintaObjToJson(final T clazz, final Boolean isRegistra) {
        if (isRegistra) {
            Timestamp tsReq = new Timestamp(new Date().getTime());
            Gson gson = new Gson();
            Logger.getRootLogger().info(tsReq + "_" + gson.toJson(clazz));
        }
    }

    /**
     * Pinta objecto xml.
     *
     * @param <T>        the generic type
     * @param clazz      the clazz
     * @param isRegistra the is registra
     */
    public static <T> void pintaObjToXml(final T clazz, final Boolean isRegistra) {
        JAXBContext jx;
        if (isRegistra) {
            try {
                Timestamp tsReq = new Timestamp(new Date().getTime());
                jx = JAXBContext.newInstance(clazz.getClass());
                StringWriter sw = new StringWriter();
                jx.createMarshaller().marshal(clazz, sw);
                Logger.getRootLogger().info(tsReq + ":" + sw.toString());
            } catch (JAXBException e) {
                Logger.getRootLogger().error(e);
            }
        }
    }

    /**
     * Pinta object to XML.
     *
     * @param jaxbT      the jaxb T
     * @param isRegistra the is registra
     */
    public static void pintaJAXBToXML(final JAXBElement<?> jaxbT, final Boolean isRegistra) {
        JAXBContext jc;
        if (isRegistra) {
            try {
                jc = JAXBContext.newInstance(jaxbT.getValue().getClass());
                Marshaller m = jc.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                StringWriter sw = new StringWriter();
                m.marshal(jaxbT, sw);
                Logger.getRootLogger().info(sw.toString());
            } catch (JAXBException e) {
                Logger.getRootLogger().error(e);
            }
        }
    }

    /**
     * Object to string.
     *
     * @param obj the obj
     * @return the string
     */
    public static String objectToString(final Object obj) {
        if (Optional.ofNullable(obj).isPresent()) {
            return obj.toString();
        } else {
            return null;
        }
    }

    /**
     * Object to boolean.
     *
     * @param obj the obj
     * @return the string
     */
    public static String objectToBoolean(final Object obj) {
        if (Optional.ofNullable(obj).isPresent()) {
            return obj.toString();
        } else {
            return "0";
        }
    }

    /**
     * Object to boolean.
     *
     * @param cal the cal
     * @return the string
     */
    public static String calendarToString(final Calendar cal) {
        if (Optional.ofNullable(cal).isPresent()) {
            return Utileria.getStrFecha(cal.getTime());
        } else {
            return null;
        }
    }

    /**
     * String to boolean.
     *
     * @param obj the obj
     * @return the boolean
     */
    public static boolean stringToBoolean(final String obj) {
        return Optional.ofNullable(obj).isPresent() && obj.equalsIgnoreCase(Constantes.STR_UNO);

    }

    /**
     * Boolean to string.
     *
     * @param obj the obj
     * @return the string
     */
    public static String booleanToString(final Boolean obj) {
        if (Optional.ofNullable(obj).isPresent() && obj) {
            return "1";
        } else {
            return "0";
        }

    }

    /**
     * String to integer.
     *
     * @param obj the obj
     * @return the integer
     */
    public static Integer stringToInteger(final String obj) {
        if (Optional.ofNullable(obj).isPresent() && !obj.isEmpty()) {
            return (int) Double.parseDouble(obj);
        } else {
            return null;
        }
    }

    /**
     * String empty to string.
     *
     * @param obj the obj
     * @return the string
     */
    public static String stringEmptyToString(final String obj) {
        if (Optional.ofNullable(obj).isPresent() && !obj.isEmpty()) {
            return obj;
        } else {
            return null;
        }
    }

    /**
     * String to double porc.
     *
     * @param obj the obj
     * @return the double
     */
    public static Double stringToDoublePorc(final String obj) {
        if (Optional.ofNullable(obj).isPresent() && !obj.isEmpty()) {
            return Double.parseDouble(obj) / Constantes.CIEN;
        } else {
            return null;
        }
    }

    /**
     * String to double porc.
     *
     * @param obj the obj
     * @return the double
     */
    public static Double stringToDouble(final String obj) {
        if (Optional.ofNullable(obj).isPresent() && !obj.isEmpty()) {
            return Double.parseDouble(obj);
        } else {
            return 0.0;
        }
    }

    /**
     * String to integer porc.
     *
     * @param obj the obj
     * @return the integer
     */
    public static Integer stringToIntegerPorc(final String obj) {
        if (Optional.ofNullable(obj).isPresent() && !obj.isEmpty()) {
            return (int) (Double.parseDouble(obj) / Constantes.CIEN);
        } else {
            return null;
        }
    }

    /**
     * String to long.
     *
     * @param obj the obj
     * @return the long
     */
    public static Long stringToLong(final String obj) {
        if (Optional.ofNullable(obj).isPresent() && !obj.isEmpty()) {
            Double valDouble = Double.parseDouble(obj);
            return valDouble.longValue();
        } else {
            return null;
        }
    }

    /**
     * String to short.
     *
     * @param obj the obj
     * @return the short
     */
    public static Short stringToShort(final String obj) {
        if (Optional.ofNullable(obj).isPresent() && !obj.isEmpty()) {
            return Short.valueOf(obj);
        } else {
            return null;
        }
    }

    /**
     * Gets the valor double.
     *
     * @param obj the obj
     * @return the valor double
     */
    public static Double getValorDouble(final String obj) {
        if (obj != null && !obj.isEmpty()) {
            return Double.valueOf(obj);
        } else {
            return null;
        }
    }

    /**
     * Gets the valor double.
     *
     * @param obj the obj
     * @return the valor double
     */
    public static Double getValorDoubleZero(final String obj) {
        if (obj != null && !obj.isEmpty()) {
            return Double.valueOf(obj);
        } else {
            return 0.0;
        }
    }

    /**
     * Gets the valor string.
     *
     * @param obj the obj
     * @return the valor string
     */
    public static String getValorString(final String obj) {
        if (obj != null && !obj.isEmpty()) {
            return obj;
        } else {
            return null;
        }
    }

    /**
     * Existe valor.
     *
     * @param obj the obj
     * @return true, if successful
     */
    public static boolean existeValor(final Object obj) {
        if (obj instanceof String) {
            return !((String) obj).trim().isEmpty();
        }
        if (obj instanceof List) {
            return !((List<?>) obj).isEmpty();
        }
        return Optional.ofNullable(obj).isPresent();

    }

    /**
     * Gets the valor string.
     *
     * @param obj the obj
     * @return the valor string
     */
    public static String getValorStringVacio(final String obj) {
        if (obj != null && !obj.isEmpty()) {
            return obj;
        } else {
            return "";
        }
    }

    /**
     * Gets the valor double.
     *
     * @param obj the obj
     * @return the valor string
     */
    public static Integer getValorInt(final String obj) {
        if (obj != null && !obj.isEmpty()) {
            return Integer.parseInt(obj);
        } else {
            return null;
        }
    }

    /**
     * Rellenar cero.
     *
     * @param cadena   the cadena
     * @param longitud the longitud
     * @return the string
     */
    public static String rellenarCero(final String cadena, final String longitud) {
        if (!Optional.ofNullable(cadena).isPresent()) {
            return null;
        }
        String patron = "%0" + longitud + "d";
        String result;
        try {
            result = String.format(patron, new BigInteger(cadena));
        } catch (NumberFormatException ex) {
            throw new ExecutionError(Constantes.ERROR_37, "Dato incorrecto");
        }
        return result;
    }

    /**
     * Unmarshal xml.
     *
     * @param <T>       the generic type
     * @param clazz     the clazz
     * @param xmlString the xml string
     * @return the t
     */
    public static <T> T unmarshalXml(final Class<T> clazz, final String xmlString) {
        try {
            JAXBContext jc;
            T resultObject;
            jc = JAXBContext.newInstance(clazz);
            Unmarshaller u = jc.createUnmarshaller();
            resultObject = clazz.cast(u.unmarshal(new StringReader(xmlString)));
            return clazz.cast(resultObject);
        } catch (JAXBException | ClassCastException e) {
            Logger.getRootLogger().error(e);
        }

        return null;
    }

    /**
     * Unmarshal xml paq.
     *
     * @param <T>       the generic type
     * @param clazz     the clazz
     * @param xmlString the xml string
     * @return the t
     */
    public static <T> T unmarshalXmlPaq(final Class<T> clazz, final String xmlString) {

        try {
            JAXBContext jc;
            jc = JAXBContext.newInstance(clazz.getPackage().getName());
            Unmarshaller u = jc.createUnmarshaller();
            return clazz.cast(JAXBIntrospector.getValue(u.unmarshal(new StringReader(xmlString))));

        } catch (JAXBException | ClassCastException e) {
            Logger.getRootLogger().error(e);
        }
        return null;
    }

    /**
     * Gets the valor elemento neg.
     *
     * @param elementos   the elementos
     * @param cveElemento the cve elemento
     * @return the valor elemento neg
     */
    public static String getValorElementoNeg(final List<ElementoNeg> elementos, final String cveElemento) {
        if (!Utileria.existeValor(elementos)) {
            return "";
        }
        return elementos.parallelStream().filter(el -> el.getNombre().equalsIgnoreCase(cveElemento))
                .map(ElementoNeg::getValor).findFirst().orElse("");
    }

    /**
     * Formato moneda (dos posiciones despues del punto decimal).
     *
     * @param value the value
     * @return the string
     */
    public static String money(final double value) {
        return "$ " + new DecimalFormat("###,###.##").format(value);
    }

    /**
     * Formato moneda redondeado a entero (sin centavos).
     *
     * @param value the value
     * @return the string
     */
    public static String moneyWithoutCents(final String value) {
        return moneyWithoutCents(Double.parseDouble(value));
    }

    /**
     * Formato moneda redondeado a entero (sin centavos).
     *
     * @param value the value
     * @return the string
     */
    public static String moneyWithoutCents(final double value) {
        return "$ " + new DecimalFormat("###,###").format(value);
    }
}