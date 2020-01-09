package com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * The Class DetalleCotizacionPadre.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "detalleCotizacion")
public class DetalleCotizacionPadre {
    /** The detalle cotizacion. */
    @XmlElement(name = "detalleCotizacion")
    private DetalleCotizacion detalleCotizacion;
}