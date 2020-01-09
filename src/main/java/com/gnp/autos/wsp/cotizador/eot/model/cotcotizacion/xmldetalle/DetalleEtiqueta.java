package com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * The Class DetalleEtiqueta.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "detalleEtiquetas")
public class DetalleEtiqueta {
    /** The codigo promocion. */
    @XmlElement(name = "codigoPromocion")
    private String codigoPromocion;
}