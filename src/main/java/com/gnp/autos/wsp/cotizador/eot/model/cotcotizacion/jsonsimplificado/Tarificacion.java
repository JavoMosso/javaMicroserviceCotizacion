package com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.jsonsimplificado;

import lombok.Data;

/**
 * The Class Tarificacion.
 */
@Data
public class Tarificacion {
    /** The mto primer recibo. */
    private double mtoPrimerRecibo;
    
    /** The mto recibo subsecuente. */
    private double mtoReciboSubsecuente;
    
    /** The num pagos. */
    private int numPagos;
    
    /** The num recibos subsecuentes. */
    private int numRecibosSubsecuentes;
    
    /** The num parcialidades. */
    private String numParcialidades;
}