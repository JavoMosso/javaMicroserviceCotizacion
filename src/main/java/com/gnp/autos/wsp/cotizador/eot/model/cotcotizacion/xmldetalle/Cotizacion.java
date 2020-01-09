package com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * The Class Cotizacion.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Cotizacion")
public class Cotizacion {
    /** The cve forma pago. */
    @XmlElement(name = "CVE_FORMA_PAGO")
    private String cveFormaPago;

    /** The num pago. */
    @XmlElement(name = "NUMERO_PAGOS")
    private String numPago;

    /** The mto primer rec. */
    @XmlElement(name = "MONTO_PRIMER_RECIBO")
    private String mtoPrimerRec;

    /** The mto rec sub. */
    @XmlElement(name = "MONTO_RECIBO_SUBSECUENTE")
    private String mtoRecSub;

    /** The mto total. */
    @XmlElement(name = "MONTO_TOTAL")
    private String mtoTotal;
}