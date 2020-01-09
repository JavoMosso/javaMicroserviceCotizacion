package com.gnp.autos.wsp.cotizador.eot.util;

import static com.google.common.collect.ImmutableMap.of;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The Class Constantes.
 */
public final class Constantes {

    /** The Constant PERSONAMORAL. */
    public static final String TIPO_PERSONA_MORAL = "M";

    /** The Constant TIPO_PERSONA_FISICA. */
    public static final String TIPO_PERSONA_FISICA = "F";

    /** The Constant PERSONAMORAL. */
    public static final String PERSONA_FEMENINO = "F";

    /** The Constant TIPO_PERSONA_FISICA. */
    public static final String PERSONA_MASCULINO = "M";

    /** The Constant STRCONDUCTOR. */
    public static final String STR_CONDUCTOR = "CONDUCTOR";

    /** The Constant STRCONTRATANTE. */
    public static final String STR_CONTRATANTE = "CONTRATANTE";

    /** The Constant DESC_TECNICO. */
    public static final String DESC_TECNICO = "TEC";

    /** The Constant STRUNO. */
    public static final String STR_UNO = "1";

    /** The Constant CIEN. */
    public static final Double CIEN = 100.0;

    /** The Constant FACTOR_VALOR_CONVENIDO_10. */
    public static final Double FACTOR_VALOR_CONVENIDO_10 = 1.1;

    /** The Constant FACTOR_VALOR_CONVENIDO_MENOS_10. */
    public static final Double FACTOR_VALOR_CONVENIDO_MENOS_10 = 0.9;

    /** The Constant STR091606. */
    public static final String CVE_COBERTURA_ROBO = "0000000916";

    /** The Constant CVE_TIPO_NOMINA. */
    public static final String CVE_TIPO_NOMINA = "CVE_TIPO_NOMINA";

    /** The Constant STRVACIO. */
    public static final String STR_VACIO = "";

    /** The Constant HTTPCODEOK. */
    public static final int HTTP_CODE_OK = 200;

    /** The Constant HTTPBADREQUEST. */
    public static final int HTTP_BAD_REQUEST = 400;

    /** The Constant MOD_CVE_DEDUCIBLE. */
    public static final String MOD_CVE_DEDUCIBLE = "CDDEDUCI";

    /** The Constant MOD_CVE_SA. */
    public static final String MOD_CVE_SA = "CPASEGUR";

    /** The Constant MOD_UNIDAD_SA. */
    public static final String MOD_UNIDAD_SA = "TCUDMSAS";

    /** The Constant MOD_UNIDAD_DED. */
    public static final String MOD_UNIDAD_DED = "TCUDMDED";

    /** The Constant MOD_UNIDAD_DED. */
    public static final String UNIDAD_DESC_DEFAULT = "PORC";

    /** The Constant UMAS. */
    public static final String UMAS = "UMAS";

    /** The Constant RUNTIME. */
    public static final String RUNTIME = "RUNTIME";

    /** The Constant PRINT CAT REQ. */
    public static final Boolean PRINT_CAT_REQ = false;

    /** The Constant PRINT CAT RESP. */
    public static final Boolean PRINT_CAT_RESP = false;

    /** The Constant PRINT_CAMP_REQ. */
    public static final Boolean PRINT_CAMP_REQ = false;

    /** The Constant PRINT_CAMP_RESP. */
    public static final Boolean PRINT_CAMP_RESP = false;

    /** The Constant PRINT_BAN_REQ. */
    public static final Boolean PRINT_BAN_REQ = false;

    /** The Constant PRINT_BAN_RESP. */
    public static final Boolean PRINT_BAN_RESP = false;

    /** The Constant PRINT_FORM_REQ. */
    public static final Boolean PRINT_FORM_REQ = false;

    /** The Constant PRINT_FORM_RESP. */
    public static final Boolean PRINT_FORM_RESP = false;

    /** The Constant PRINT_MUC_REQ. */
    public static final Boolean PRINT_MUC_REQ = false;

    /** The Constant PRINT_MUC_RESP. */
    public static final Boolean PRINT_MUC_RESP = false;

    /** The Constant PRINT_RG_REQ. */
    public static final Boolean PRINT_RG_REQ = false;

    /** The Constant PRINT_RG_RESP. */
    public static final Boolean PRINT_RG_RESP = false;

    /** The Constant PRINT_TRANS_REQ. */
    public static final Boolean PRINT_TRANS_REQ = false;

    /** The Constant PRINT_TRANS_RESP. */
    public static final Boolean PRINT_TRANS_RESP = false;

    /** The Constant PRINT UMO RESP. */
    public static final Boolean PRINT_UMO_RESP = false;

    /** The Constant ESTADO_OFFLINE. */
    public static final String ESTADO_OFFLINE = "EI";

    /** The Constant COBERTURA_ROBO_TOTAL. */
    public static final String COBERTURA_ROBO_TOTAL = "0000000916";

    /** The Constant CA3. */
    public static final String CA3 = "CA3";

    /** The Constant CA4. */
    public static final String CA4 = "CA4";

    /** The Constant CA5. */
    public static final String CA5 = "CA5";

    /** The Constant TRC. */
    public static final String TRC = "TRC";

    /** The Constant TR2. */
    public static final String TR2 = "TR2";

    /** The Constant DERECHO_POLIZA. */
    public static final Double DERECHO_POLIZA = Double.valueOf("500");

    /** The Constant RT. */
    public static final String RT = "RT";

    /** The Constant ID_REGLA_ELITE. */
    public static final String ID_REGLA_ELITE = "3897_P1";

    /** The Constant MOD_ELITE. */
    public static final String MOD_ELITE = "CDAGRCOB";

    /** The Constant STRF. */
    public static final String STR_PRIMA_TECNICA = "PRIMA_TECNICA";

    /** The Constant STRF. */
    public static final String STR_PRIMA_NETA = "PRIMA_NETA";

    /** The Constant STRF. */
    public static final String STR_PRIMA_COMERCIAL = "PRIMA_COMERCIAL";

    /** The Constant STRF. */
    public static final String STR_DESCUENTO = "DESCUENTO";

    /** The Constant STRF. */
    public static final String STR_TOTAL_PAGAR = "TOTAL_PAGAR";

    /** The Constant STR_PRIMER_RECIBO. */
    public static final String STR_PRIMER_RECIBO = "PRIMER_RECIBO";

    /** The Constant STR_RECIBO_SUBSECUENTE. */
    public static final String STR_RECIBO_SUBSECUENTE = "RECIBO_SUBSECUENTE";

    /** The Constant STR_NUM_RECIBOS_SUBS. */
    public static final String STR_NUM_RECIBOS_SUBS = "NUM_RECIBOS_SUBS";

    /** The Constant STR_NUM_PAGOS. */
    public static final String STR_NUM_PAGOS = "NUM_PAGOS";

    /** The Constant STRF. */
    public static final String STR_CONTINUAR = "CONTINUAR";

    /** The Constant STRF. */
    public static final String COD_CALENDARIO_INFO = "codigoCalendarioInfo";

    /** The Constant STR_TIPO_PERSONA. */
    public static final String STR_TIPO_PERSONA = "TIPO_PERSONA";

    /** The Constant STR_TIPO_VEHICULO. */
    public static final String STR_TIPO_VEHICULO = "TIPO_VEHICULO";

    /** The Constant STR_MODELO. */
    public static final String STR_MODELO = "MODELO";

    /** The Constant STR_ARMADORA. */
    public static final String STR_ARMADORA = "ARMADORA";

    /** The Constant STR_CARROCERIA. */
    public static final String STR_CARROCERIA = "CARROCERIA";

    /** The Constant STR_SUB_RAMO. */
    public static final String STR_SUB_RAMO = "SUB_RAMO";

    /** The Constant STR_VIA_PAGO. */
    public static final String STR_VIA_PAGO = "VIA_PAGO";

    /** The Constant STR_VERSION. */
    public static final String STR_VERSION = "VERSION";

    /** The Constant STR_CODIGO_POSTAL. */
    public static final String STR_CODIGO_POSTAL = "CODIGO_POSTAL";

    /** The Constant STR_ID_UNIDAD_OPERABLE. */
    public static final String STR_ID_UNIDAD_OPERABLE = "ID_UNIDAD_OPERABLE";

    /** The Constant STR_FCH_INICIO_VIGENCIA. */
    public static final String STR_FCH_INICIO_VIGENCIA = "FCH_INICIO_VIGENCIA";

    /** The Constant STR_FCH_FIN_VIGENCIA. */
    public static final String STR_FCH_FIN_VIGENCIA = "FCH_FIN_VIGENCIA";

    /** The Constant CVE_MOTOS. */
    public static final String CVE_MOTOS = "MOT";

    /** The Constant STR_USO. */
    public static final String STR_USO = "USO";

    /** The Constant SUBRAMOS_VEHICULOS. */
    public static final List<String> SUBRAMOS_VEHICULOS = Arrays.asList("01", "05", "06");

    /** The Constant STR_PODESCAM. */
    public static final String STR_PODESCAM = "PODESCAM";

    /** The Constant STR_VADESVOL. */
    public static final String STR_VADESVOL = "VADESVOL";

    /** The Constant STR_VADESREN. */
    public static final String STR_VADESREN = "VADESREN";

    /** The Constant STR_RUNTIME. */
    public static final String STR_RUNTIME = "runtime";

    /** The Constant CVE_VALOR_COMERCIAL. */
    public static final String CVE_VALOR_COMERCIAL = "01";

    /** The Constant CVE_VALOR_CONVENIDO. */
    public static final String CVE_VALOR_CONVENIDO = "03";

    /** The Constant CVE_VALOR_CONVENIDO_10. */
    public static final String CVE_VALOR_CONVENIDO_10 = "04";

    /** The Constant CVE_VALOR_CONVENIDO_MENOS_10. */
    public static final String CVE_VALOR_CONVENIDO_MENOS_10 = "06";

    /** The Constant CVE_COBERTURA_EXC_CUB. */
    public static final String CVE_COBERTURA_EXC_CUB = "0000000894";

    /** The Constant STR_FORMA_INDEMNIZACION. */
    public static final String STR_FORMA_INDEMNIZACION = "FORMA_INDEMNIZACION";

    /** The Constant TIPO_VALOR_VEHICULO_COBERTURA. */
    public static final String TIPO_VALOR_VEHICULO_COBERTURA = "Valor Vehiculo";

    /** The Constant TIPO_FORMA INDEMNIZAICON COBERTURA. */
    public static final String TIPO_FORMA_INDEM_COBERTURA = "Forma Indemnizacion";

    /** The Constant ERROR_0. */
    public static final Integer ERROR_0 = 0;

    /** The Constant ERROR_1. */
    public static final Integer ERROR_1 = 1;

    /** The Constant ERROR_2. */
    public static final Integer ERROR_2 = 2;

    /** The Constant ERROR_3. */
    public static final Integer ERROR_3 = 3;

    /** The Constant ERROR_4. */
    public static final Integer ERROR_4 = 4;

    /** The Constant ERROR_5. */
    public static final Integer ERROR_5 = 5;

    /** The Constant ERROR_6. */
    public static final Integer ERROR_6 = 6;

    /** The Constant ERROR_7. */
    public static final Integer ERROR_7 = 7;

    /** The Constant ERROR_8. */
    public static final Integer ERROR_8 = 8;

    /** The Constant ERROR_9. */
    public static final Integer ERROR_9 = 9;

    /** The Constant ERROR_10. */
    public static final Integer ERROR_10 = 10;

    /** The Constant ERROR_11. */
    public static final Integer ERROR_11 = 11;

    /** The Constant ERROR_12. */
    public static final Integer ERROR_12 = 12;

    /** The Constant ERROR_13. */
    public static final Integer ERROR_13 = 13;

    /** The Constant ERROR_14. */
    public static final Integer ERROR_14 = 14;

    /** The Constant ERROR_15. */
    public static final Integer ERROR_15 = 15;

    /** The Constant ERROR_16. */
    public static final Integer ERROR_16 = 16;

    /** The Constant ERROR_17. */
    public static final Integer ERROR_17 = 17;

    /** The Constant ERROR_18. */
    public static final Integer ERROR_18 = 18;

    /** The Constant ERROR_19. */
    public static final Integer ERROR_19 = 19;

    /** The Constant ERROR_20. */
    public static final Integer ERROR_20 = 20;

    /** The Constant ERROR_21. */
    public static final Integer ERROR_21 = 21;

    /** The Constant ERROR_22. */
    public static final Integer ERROR_22 = 22;

    /** The Constant ERROR_23. */
    public static final Integer ERROR_23 = 23;

    /** The Constant ERROR_24. */
    public static final Integer ERROR_24 = 24;

    /** The Constant ERROR_25. */
    public static final Integer ERROR_25 = 25;

    /** The Constant ERROR_26. */
    public static final Integer ERROR_26 = 26;

    /** The Constant ERROR_27. */
    public static final Integer ERROR_27 = 27;

    /** The Constant ERROR_28. */
    public static final Integer ERROR_28 = 28;

    /** The Constant ERROR_29. */
    public static final Integer ERROR_29 = 29;

    /** The Constant ERROR_30. */
    public static final Integer ERROR_30 = 30;

    /** The Constant ERROR_31. */
    public static final Integer ERROR_31 = 31;

    /** The Constant ERROR_32. */
    public static final Integer ERROR_32 = 32;

    /** The Constant ERROR_33. */
    public static final Integer ERROR_33 = 33;

    /** The Constant ERROR_34. */
    public static final Integer ERROR_34 = 34;

    /** The Constant ERROR_35. */
    public static final Integer ERROR_35 = 35;

    /** The Constant ERROR_36. */
    public static final Integer ERROR_36 = 36;

    /** The Constant ERROR_37. */
    public static final Integer ERROR_37 = 37;

    /** The Constant ERROR_38. */
    public static final Integer ERROR_38 = 38;

    /** The Constant ERROR_39. */
    public static final Integer ERROR_39 = 39;

    /** The Constant ERROR_40. */
    public static final Integer ERROR_40 = 40;

    /** The Constant ERROR_41. */
    public static final Integer ERROR_41 = 41;

    /** The Constant ERROR_42. */
    public static final Integer ERROR_42 = 42;

    /** The Constant NUM_1. */
    public static final Integer NUM_1 = 1;

    /** The Constant NUM_2. */
    public static final Integer NUM_2 = 2;

    /** The Constant NUM_3. */
    public static final Integer NUM_3 = 3;

    /** The Constant NUM_4. */
    public static final Integer NUM_4 = 4;

    /** The Constant NUM_5. */
    public static final Integer NUM_5 = 5;

    /** The Constant NUM_8. */
    public static final Integer NUM_8 = 8;

    /** The Constant NUM_10. */
    public static final Integer NUM_10 = 10;

    /** The Constant DESC_SERVICE. */
    public static final String DESC_SERVICE = "Regresa una cotizacion utilizando los servicios de evolucion.";

    /** The Constant DESC_SERVICE_NEG. */
    public static final String DESC_SERVICE_NEG = "Regresa una cotizacion utilizando los servicios de evolucion. Entrada de negocio";

    /** The Constant FILTRO_TX_DESCUENTO_. */
    public static final String FILTRO_TX_DESCUENTO = "Emision";

    /** The Constant FILTRO_TX_RENOV_. */
    public static final String FILTRO_TX_DESC_RENOV = "Renovacion";

    /** The Constant CVE_DESC_NOMINA. */
    public static final String CVE_DESC_NOMINA = "DN";

    /** The Constant CVE_BANCARIO_NOMINA. */
    public static final String CVE_BANCARIO_NOMINA = "BN";

    /** The Constant TIMEOUT_REST_DEFAULT. */
    public static final Integer TIMEOUT_REST_DEFAULT = 6000;

    /** The Constant STR_VERSION. */
    public static final String STR_VERSION_NEGOCIO = "VERSION_NEGOCIO";

    /** The Constant STR_VERSION. */
    public static final String STR_CVE_TARIFA = "CVE_TARIFA";

    /** The Constant STR_VERSION. */
    public static final String STR_FCH_TARIFA = "FCH_TARIFA";

    /** The Constant STR_VERSION. */
    public static final String STR_DERECHO_POLIZA = "DERECHO_POLIZA";

    /** The Constant CVE_TX_UMO_SERVICE. */
    public static final String CVE_TX_UMO_SERVICE = "US";

    /** The Constant CVE_TX_CATALOGO. */
    public static final String CVE_TX_CATALOGO = "CA";

    /** The Constant CVE_TX_FORMATO_IMP. */
    public static final String CVE_TX_FORMATO_IMP = "FM";

    /** The Constant STR_PORC_CESION_COMISION. */
    public static final String STR_PORC_CESION_COMISION = "PORCENTAJE_CESION_COMISION";

    /** The Constant STR_PORC_COMISION. */
    public static final String STR_PORC_COMISION = "PORCENTAJE_COMISION";

    /** The Constant AGRUPADOR_ELITE_MAP. */
    public static final Map<String, String> AGRUPADOR_ELITE_MAP = of("GNP", "Auto Elite GNP", "AUT", "Auto Elite");

    /** The Constant STRCVEHERRAMIENTA_PORTAL. */
    public static final String CVE_HERRAMIENTA_PORTAL = "PGN";

    /** The Constant CVE_HERRAMIENTA. */
    public static final String CVE_HERRAMIENTA = "WSP";

    /** The Constant STR_BAN_RENOVACION. */
    public static final String STR_BAN_RENOVACION = "BAN_RENOVACION";

    /**
     * Instantiates a new constantes.
     */
    private Constantes() {
        throw new IllegalStateException("Constantes class");
    }
}