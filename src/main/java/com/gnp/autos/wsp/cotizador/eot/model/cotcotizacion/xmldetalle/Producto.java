package com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * The Class Producto.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Productos")
public class Producto {
    /** The id producto. */
    @XmlElement(name = "ID_PRODUCTO")
    private String idProducto;
    
    /** The nombre producto. */
    @XmlElement(name = "NOMBRE_PRODUCTO")
    private String nombreProducto;

    /** The fch tarifa. */
    @XmlElement(name = "FCH_TARIFA")
    private String fchTarifa;

    /** The cve tarifa. */
    @XmlElement(name = "CVE_TARIFA")
    private String cveTarifa;

    /** The cve zonificacion. */
    @XmlElement(name = "CLAVE_ZONIFICACION")
    private String cveZonificacion;

    /** The version zonificacion. */
    @XmlElement(name = "VERSION_ZONIFICACION")
    private int versionZonificacion;

    /** The coberturas. */
    @XmlElement(name = "Coberturas")
    private List<Cobertura> coberturas;

    /** The totales. */
    @XmlElement(name = "TotalPrima")
    private List<TotalPrima> totales;
    
    /** The version recargo forma pago. */
    @XmlElement(name = "VERSION_RECARGO_FORMA_PAGO")
    private String versionRecargoFormaPago;

    /** The cotizacion. */
    @XmlElement(name = "Cotizacion")
    private Cotizacion cotizacion;

    /** The descuentos tarificador. */
    @XmlElement(name = "Descuentos")
    private List<DescuentoTarificador> descuentosTarificador;

    /** The sade. */
    @XmlElement(name = "SADE")
    private Sade sade;
}