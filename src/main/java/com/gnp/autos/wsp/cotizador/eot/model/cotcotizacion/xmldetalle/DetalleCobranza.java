package com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * The Class DetalleCobranza.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "detalleCobranza")
public class DetalleCobranza {
    /** The tipo nomina. */
    @XmlElement(name = "tipoNomina")
    private String tipoNomina;

    /** The periodicidad. */
    @XmlElement(name = "periodicidad")
    private String periodicidad;

    /** The via pago. */
    @XmlElement(name = "viaPago")
    private String viaPago;
}