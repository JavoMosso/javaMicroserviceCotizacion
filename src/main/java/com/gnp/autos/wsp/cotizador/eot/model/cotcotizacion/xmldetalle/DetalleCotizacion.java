package com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * The Class DetalleCotizacion.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "detalleCotizacion")
public class DetalleCotizacion {
    /** The origen. */
    @XmlElement(name = "origen")
    private String origen;

    /** The dias vigencia. */
    @XmlElement(name = "diasVigencia")
    private Integer diasVigencia;

    /** The cve herramienta. */
    @XmlElement(name = "cveHerramienta")
    private String cveHerramienta;

    /** The ubicacion trabajo. */
    @XmlElement(name = "ubicacionTrabajo")
    private String ubicacionTrabajo;

    /** The id modelo negocio. */
    @XmlElement(name = "idModeloNegocio")
    private String idModeloNegocio;

    /** The modelo negocio. */
    @XmlElement(name = "modeloNegocio")
    private String modeloNegocio;

    /** The json pintador. */
    @XmlElement(name = "jsonPintador")
    private String jsonPintador;

    /** The id negocio operable. */
    @XmlElement(name = "idNegocioOperable")
    private String idNegocioOperable;

    /** The version negocio. */
    @XmlElement(name = "versionNegocio")
    private Integer versionNegocio;

    /** The id negocio comercial. */
    @XmlElement(name = "idNegocioComercial")
    private String idNegocioComercial;

    /** The cve tipo intermediario. */
    @XmlElement(name = "cveTipoIntermediario")
    private String cveTipoIntermediario;

    /** The id producto personalizado. */
    @XmlElement(name = "idProductoPersonalizado")
    private String idProductoPersonalizado;

    /** The detalle tarificador. */
    @XmlElement(name = "detalleTarificador")
    private DetalleTarificador detalleTarificador;

    /** The detalle vehiculo. */
    @XmlElement(name = "detalleVehiculo")
    private DetalleVehiculo detalleVehiculo;

    /** The detalle etiquetas. */
    @XmlElement(name = "detalleEtiquetas")
    private DetalleEtiqueta detalleEtiquetas;

    /** The detalle cobranza. */
    @XmlElement(name = "detalleCobranza")
    private DetalleCobranza detalleCobranza;

    /** The detalle descuentos. */
    @XmlElement(name = "detalleDescuentos")
    private DetalleDescuento detalleDescuentos;

    /** The detalle campanias. */
    @XmlElement(name = "detalleCampanias")
    private DetalleCampania detalleCampanias;

    /** The detalle contratante. */
    @XmlElement(name = "detalleContratante")
    private DetalleContratante detalleContratante;

    /** The detalle conductor. */
    @XmlElement(name = "detalleConductor")
    private DetalleConductor detalleConductor;

    /** The cve empresa dependencia. */
    @XmlElement(name = "cveEmpresaDependencia")
    private String cveEmpresaDependencia;

    /** The cve filial subdependencia. */
    @XmlElement(name = "cveFilialSubdependencia")
    private String cveFilialSubdependencia;

    /** The id actor. */
    @XmlElement(name = "idActor")
    private String idActor;
}