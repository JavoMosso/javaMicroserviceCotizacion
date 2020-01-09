package com.gnp.autos.wsp.cotizador.eot.model.banderas;

import java.util.List;

import lombok.Data;

/**
 * Instantiates a new vehiculo val neg.
 */
@Data
public class VehiculoValNeg {

    /** The sub ramo. */
    private String subRamo;

    /** The tipo vehiculo. */
    private String tipoVehiculo;

    /** The clave marca. */
    private String claveMarca;

    /** The modelo. */
    private String modelo;

    /** The armadora. */
    private String armadora;

    /** The carroceria. */
    private String carroceria;

    /** The uso. */
    private String uso;

    /** The forma indemnizacion. */
    private String formaIndemnizacion;

    /** The valor vehiculo. */
    private String valorVehiculo;

    /** The placas. */
    private String placas;

    /** The alto riesgo. */
    private String altoRiesgo;

    /** The tipo valor vehiculo. */
    private String tipoValorVehiculo;

    /** The tipo carga. */
    private String tipoCarga;

    /** The estado circulacion. */
    private String estadoCirculacion;

    /** The serie. */
    private String serie;

    /** The ban legalizado. */
    private Boolean banLegalizado;

    /** The adaptaciones. */
    private List<AdaptacionVehValNeg> adaptaciones;
}
