package com.gnp.autos.wsp.cotizador.eot.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StreamUtils;

import com.gnp.autos.wsp.cotizador.eot.error.ExecutionError;
import com.gnp.autos.wsp.cotizador.eot.wsdl.CalcularPrimaAutoRequest;
import com.gnp.autos.wsp.cotizador.eot.wsdl.ObjectFactory;
import com.gnp.autos.wsp.negocio.catalogo.model.CatalogoWSPResp;
import com.gnp.autos.wsp.negocio.cotizacion.model.req.TraductorReq;
import com.gnp.autos.wsp.negocio.cotizacion.model.resp.SolicitudResp;
import com.gnp.autos.wsp.negocio.cotizacion.model.resp.TraductorResp;

@RunWith(SpringRunner.class)
public class UtileriaTest {

    private String strBanderaReq;
    private String strMucReq;

    @Value("classpath:/cotizacionWSPReq.xml")
    public void setFile(Resource myRes) throws IOException {
        try (InputStream is = myRes.getInputStream()) {
            this.strBanderaReq = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }
    }

    @Value("classpath:/calcularPrimaReq.xml")
    public void setFileMuc(Resource myRes) throws IOException {
        try (InputStream is = myRes.getInputStream()) {
            this.strMucReq = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }
    }

    @Test
    public void getStrFecha_A$Date() throws Exception {
        //
        Date fecha = mock(Date.class);
        String actual = Utileria.getStrFecha(fecha);
        assertNotNull(actual);

    }

    @Test
    public void isNumeric_A$String() throws Exception {
        String s = null;
        boolean actual = Utileria.isNumeric(s);
        assertThat(actual, is(equalTo(false)));
        s = "10";
        actual = Utileria.isNumeric(s);
        assertTrue(actual);

    }

    @Test
    public void getXMLDateTest() {
        XMLGregorianCalendar actual = Utileria.getXMLDate("20180303");
        assertNotNull(actual);
    }

    @Test(expected = ExecutionError.class)
    public void getXMLDate2Test() {
        Utileria.getXMLDate("2018ssf3");

    }

    @Test(expected = ExecutionError.class)
    public void getgetF1DateF2Test() {
        String actual = Utileria.getF1DateF2("2018-03-03");
        assertNotNull(actual);
        assertEquals("20180303", actual);
        Utileria.getF1DateF2("20180303");

    }

    @Test
    public void getDecimalFormatTest() {
        String val = Utileria.getDecimalFormat(1.5, 1);
        assertNotNull(val);
        val = Utileria.getDecimalFormat(1.5334, 1);
        assertNotNull(val);
    }

    @Test
    public void getDecimalFormat2Test() {
        BigDecimal val = Utileria.getDecimalFormat1(1.5, 1);
        assertNotNull(val);
        val = Utileria.getDecimalFormat1(1.54353, 1);
        assertNotNull(val);
    }

    @Test
    public void getXMLDate3Test() {
        XMLGregorianCalendar actual = Utileria.getXMLDate(new Date());
        assertNotNull(actual);

    }

    @Test
    public void soloNumeros_A$String() throws Exception {
        String referencia = null;
        String actual = Utileria.soloNumeros(referencia);
        String expected = null;
        assertThat(actual, is(equalTo(expected)));

        referencia = "GNP000001";
        actual = Utileria.soloNumeros(referencia);
        expected = "000001";
        assertThat(actual, is(equalTo(expected)));

    }

    @Test
    public void soloLetras_A$String() throws Exception {
        String texto = null;
        String actual = Utileria.soloLetras(texto);
        String expected = null;
        assertThat(actual, is(equalTo(expected)));

        texto = "kjdskkjhk112344";
        actual = Utileria.soloLetras(texto);
        expected = "kjdskkjhk";
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void soloAlfaNumerico_A$String() throws Exception {
        String texto = null;
        String actual = Utileria.soloAlfaNumerico(texto);
        String expected = null;
        assertThat(actual, is(equalTo(expected)));

        texto = "***sdas214";
        actual = Utileria.soloAlfaNumerico(texto);
        expected = "sdas214";
        assertThat(actual, is(equalTo(expected)));

    }

    /**
     * Rellenar cero a$ string$ string.
     *
     * @throws Exception the exception
     */
    @Test(expected = ExecutionError.class)
    public void rellenarCero_A$String$String() throws Exception {
        String cadena = null;
        String longitud = null;
        String actual = Utileria.rellenarCero(cadena, longitud);
        String expected = null;
        assertThat(actual, is(equalTo(expected)));

        cadena = "1234";
        longitud = "10";
        actual = Utileria.rellenarCero(cadena, longitud);
        expected = "0000001234";
        assertThat(actual, is(equalTo(expected)));

        cadena = "1234dhkasjhkdjfsakf";
        longitud = "10";
        actual = Utileria.rellenarCero(cadena, longitud);
        expected = "0000001234";
        assertThat(actual, is(equalTo(expected)));
    }

    @Test(expected = ExecutionError.class)
    public void rellenarCero_A$Exe$String() throws Exception {

        String cadena = "123423.0002";
        String longitud = "10";
        String actual = Utileria.rellenarCero(cadena, longitud);
        String expected = "0000001234";
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void getRegistraLogTime_A$String() throws Exception {
        String registro = null;
        Utileria.getRegistraLogTime(registro);
        assertNull(registro);
    }

    @Test
    public void unmarshalXml_A$Class$String() throws Exception {

        TraductorReq actual = Utileria.unmarshalXml(TraductorReq.class, this.strBanderaReq);
        assertNotNull(actual);

        actual = Utileria.unmarshalXml(TraductorReq.class, "");
        assertNull(actual);
    }

    @Test
    public void unmarshalXmlPaq_A$Class$String() throws Exception {
        CalcularPrimaAutoRequest actual = Utileria.unmarshalXmlPaq(CalcularPrimaAutoRequest.class, this.strMucReq);
        assertNotNull(actual);

        actual = Utileria.unmarshalXmlPaq(CalcularPrimaAutoRequest.class, "");
        assertNull(actual);

    }

    @Test
    public void pintaObjectoJson_A$Object() throws Exception {
        TraductorResp clazz = new TraductorResp();
        SolicitudResp solicitud = new SolicitudResp();
        solicitud.setIdCotizacion("CIANNE29187398");
        clazz.setSolicitud(solicitud);
        Utileria.pintaObjToJson(clazz, true);

        Utileria.pintaObjToJson(new Object(), false);
        assertNotNull(solicitud);
    }

    @Test
    public void objectToString_A$Object() throws Exception {
        Object obj = null;
        String actual = Utileria.objectToString(obj);
        String expected = null;
        assertThat(actual, is(equalTo(expected)));

        String strObj = "prueba";
        actual = Utileria.objectToString(strObj);
        expected = "prueba";
        assertThat(actual, is(equalTo(expected)));

    }

    @Test
    public void objectToBoolean_A$Object() throws Exception {
        Object obj = null;
        String actual = Utileria.objectToBoolean(obj);
        String expected = "0";
        assertThat(actual, is(equalTo(expected)));

        String strObj = "1";
        actual = Utileria.objectToBoolean(strObj);
        expected = "1";
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void calendarToString_A$Calendar() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        String actual = Utileria.calendarToString(cal);
        assertNotNull(actual);

        actual = Utileria.calendarToString(null);
        assertNull(actual);

    }

    @Test
    public void stringToBoolean_A$String() throws Exception {
        String obj = null;
        Boolean actual = Utileria.stringToBoolean(obj);
        Boolean expected = false;
        assertThat(actual, is(equalTo(expected)));

        obj = "0";
        actual = Utileria.stringToBoolean(obj);
        expected = false;
        assertThat(actual, is(equalTo(expected)));

        actual = Utileria.stringToBoolean("1");
        expected = true;
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void booleanToString_A$Boolean() throws Exception {
        Boolean obj = null;
        String actual = Utileria.booleanToString(obj);
        String expected = "0";
        assertThat(actual, is(equalTo(expected)));

        obj = false;
        actual = Utileria.booleanToString(obj);
        expected = "0";
        assertThat(actual, is(equalTo(expected)));

        actual = Utileria.booleanToString(true);
        expected = "1";
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void stringToInteger_A$String() throws Exception {
        String obj = null;
        Integer actual = Utileria.stringToInteger(obj);
        Integer expected = null;
        assertThat(actual, is(equalTo(expected)));

        obj = "";
        actual = Utileria.stringToInteger(obj);
        expected = null;
        assertThat(actual, is(equalTo(expected)));

        obj = "12.0";
        actual = Utileria.stringToInteger(obj);
        expected = 12;
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void stringEmptyToString_A$String() throws Exception {
        String obj = null;
        String actual = Utileria.stringEmptyToString(obj);
        String expected = null;
        assertThat(actual, is(equalTo(expected)));

        obj = "";
        actual = Utileria.stringEmptyToString(obj);
        expected = null;
        assertThat(actual, is(equalTo(expected)));

        obj = "asda";
        actual = Utileria.stringEmptyToString(obj);
        expected = "asda";
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void stringToDoublePorc_A$String() throws Exception {
        String obj = null;
        Double actual = Utileria.stringToDoublePorc(obj);
        Double expected = null;
        assertThat(actual, is(equalTo(expected)));

        obj = "";
        actual = Utileria.stringToDoublePorc(obj);
        expected = null;
        assertThat(actual, is(equalTo(expected)));

        obj = "100";
        actual = Utileria.stringToDoublePorc(obj);
        expected = 1.0;
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void stringToDouble_A$String() throws Exception {
        String obj = null;
        Double actual = Utileria.stringToDouble(obj);
        Double expected = 0.0;
        assertThat(actual, is(equalTo(expected)));

        obj = "";
        actual = Utileria.stringToDouble(obj);
        expected = 0.0;
        assertThat(actual, is(equalTo(expected)));

        obj = "10";
        actual = Utileria.stringToDouble(obj);
        expected = 10.0;
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void stringToIntegerPorc_A$String() throws Exception {
        String obj = null;
        Integer actual = Utileria.stringToIntegerPorc(obj);
        Integer expected = null;
        assertThat(actual, is(equalTo(expected)));

        obj = "";
        actual = Utileria.stringToIntegerPorc(obj);
        expected = null;
        assertThat(actual, is(equalTo(expected)));

        obj = "100";
        actual = Utileria.stringToIntegerPorc(obj);
        expected = 1;
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void stringToLong_A$String() throws Exception {
        String obj = null;
        Long actual = Utileria.stringToLong(obj);
        Long expected = null;
        assertThat(actual, is(equalTo(expected)));

        obj = "";
        actual = Utileria.stringToLong(obj);
        expected = null;
        assertThat(actual, is(equalTo(expected)));

        obj = "10";
        actual = Utileria.stringToLong(obj);
        expected = 10L;
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void stringToShort_A$String() throws Exception {
        String obj = null;
        Short actual = Utileria.stringToShort(obj);
        Short expected = null;
        assertThat(actual, is(equalTo(expected)));

        obj = "";
        actual = Utileria.stringToShort(obj);
        expected = null;
        assertThat(actual, is(equalTo(expected)));

        obj = "2";
        actual = Utileria.stringToShort(obj);
        expected = 2;
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void getValorDouble_A$String() throws Exception {
        String obj = null;
        Double actual = Utileria.getValorDouble(obj);
        Double expected = null;
        assertThat(actual, is(equalTo(expected)));

        obj = "";
        actual = Utileria.getValorDouble(obj);
        expected = null;
        assertThat(actual, is(equalTo(expected)));

        obj = "13.2";
        actual = Utileria.getValorDouble(obj);
        expected = 13.2;
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void getValorDoubleZero_A$String() throws Exception {
        String obj = null;
        Double actual = Utileria.getValorDoubleZero(obj);
        Double expected = 0.0;
        assertThat(actual, is(equalTo(expected)));

        obj = "";
        actual = Utileria.getValorDoubleZero(obj);
        expected = 0.0;
        assertThat(actual, is(equalTo(expected)));

        obj = "12.2";
        actual = Utileria.getValorDoubleZero(obj);
        expected = 12.2;
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void getValorString_A$String() throws Exception {
        String obj = null;
        String actual = Utileria.getValorString(obj);
        String expected = null;
        assertThat(actual, is(equalTo(expected)));

        obj = "";
        actual = Utileria.getValorString(obj);
        expected = null;
        assertThat(actual, is(equalTo(expected)));

        obj = "sss";
        actual = Utileria.getValorString(obj);
        expected = "sss";
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void getValorInt_A$String() throws Exception {
        String obj = null;
        Integer actual = Utileria.getValorInt(obj);
        Integer expected = null;
        assertThat(actual, is(equalTo(expected)));

        obj = "";
        actual = Utileria.getValorInt(obj);
        expected = null;
        assertThat(actual, is(equalTo(expected)));

        obj = "1";
        actual = Utileria.getValorInt(obj);
        expected = 1;
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void existeValorTest() {
        assertTrue(Utileria.existeValor("Algo"));
        assertFalse(Utileria.existeValor(""));
        List<String> lstStr = new ArrayList<>();
        assertFalse(Utileria.existeValor(lstStr));
        lstStr.add("Algo");
        assertTrue(Utileria.existeValor(lstStr));

        assertTrue(Utileria.existeValor(1));
    }

    @Test
    public void getValorStringVacioTest() {
        assertEquals("", Utileria.getValorStringVacio(null));
        assertEquals("", Utileria.getValorStringVacio(""));
        assertEquals("algo", Utileria.getValorStringVacio("algo"));
    }

    @Test
    public void getJax() {
        CatalogoWSPResp objReq = new CatalogoWSPResp();
        Utileria.pintaObjToXml(objReq, true);
        Utileria.pintaObjToXml(objReq, false);
        ObjectFactory objF = new ObjectFactory();
        JAXBElement<CalcularPrimaAutoRequest> objCalc = null;
        CalcularPrimaAutoRequest objMuc = new CalcularPrimaAutoRequest();
        objCalc = objF.createCalcularPrimaAutoRequest(objMuc);

        Utileria.pintaJAXBToXML(objCalc, true);
        Utileria.pintaJAXBToXML(objCalc, false);
        assertNotNull(objCalc);
    }
    
    @Test
    public void money() {
        assertEquals("$ 4", Utileria.moneyWithoutCents("3.9"));
        assertEquals("$ 5", Utileria.moneyWithoutCents(4.9));
        assertEquals("$ 5.09", Utileria.money(5.09));
    }
}
