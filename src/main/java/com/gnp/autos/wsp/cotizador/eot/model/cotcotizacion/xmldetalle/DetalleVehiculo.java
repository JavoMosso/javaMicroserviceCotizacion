package com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * The Class DetalleVehiculo.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "detalleVehiculo")
public class DetalleVehiculo {
    /** The fch cotizacion. */
    @XmlElement(name = "FechaCotizacion")
    private String fchCotizacion;

    /** The id participante contratante. */
    @XmlElement(name = "IdParticipanteContratante")
    private String idParticipanteContratante;

    /** The id participante conductor. */
    @XmlElement(name = "IdParticipanteConductor")
    private String idParticipanteConductor;

    /** The id sub ramo. */
    @XmlElement(name = "IdSubRamo")
    private String idSubRamo;

    /** The id uso vehiculo. */
    @XmlElement(name = "IdUsoVehiculo")
    private String idUsoVehiculo;

    /** The tipo vehiculo. */
    @XmlElement(name = "TipoVehiculo")
    private String tipoVehiculo;

    /** The armadora. */
    @XmlElement(name = "Armadora")
    private String armadora;

    /** The carroceria. */
    @XmlElement(name = "Carroceria")
    private String carroceria;

    /** The marca. */
    @XmlElement(name = "Marca")
    private String marca;

    /** The modelo. */
    @XmlElement(name = "Modelo")
    private String modelo;

    /** The descripcion vehiculo. */
    @XmlElement(name = "DescripcionVehiculo")
    private String descripcionVehiculo;

    /** The valor vehiculo. */
    @XmlElement(name = "ValorVehiculo")
    private String valorVehiculo;

    /** The codigo postal. */
    @XmlElement(name = "CodigoPostal")
    private String codigoPostal;

    /** The id estado circulacion. */
    @XmlElement(name = "IdEstadoCirculacion")
    private String idEstadoCirculacion;

    /** The id valor vehiculo. */
    @XmlElement(name = "IdValorVehiculo")
    private String idValorVehiculo;

    /** The descripcion vehiculo factura. */
    @XmlElement(name = "DescripcionVehiculoFactura")
    private String descripcionVehiculoFactura;

    /** The num serie. */
    @XmlElement(name = "Numero_Serie")
    private String numSerie;

    /** The fch blindaje. */
    @XmlElement(name = "FechaBlindaje")
    private String fchBlindaje;

    /** The valor blindaje. */
    @XmlElement(name = "ValorBlindaje")
    private String valorBlindaje;

    /** The valor blindaje depreciado. */
    @XmlElement(name = "ValorBlindajeDepreciado")
    private String valorBlindajeDepreciado;

    /** The ban vehiculo blindado fabrica. */
    @XmlElement(name = "BanVehiculoBlindadoFabrica")
    private String banVehiculoBlindadoFabrica;

    /** The version FLP. */
    @XmlElement(name = "versionFLP")
    private String versionFLP;
    
    /** The tipo carga. */
    @XmlElement(name = "TipoCarga")
    private String tipoCarga;
}