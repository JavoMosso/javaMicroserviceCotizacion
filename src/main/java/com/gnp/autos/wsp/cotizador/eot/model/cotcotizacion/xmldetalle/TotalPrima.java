package com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * The Class TotalPrima.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "TotalPrima")
public class TotalPrima {
    /** The prima. */
    @XmlElement(name = "Prima")
    private List<Prima> prima;

    /** The concepto economico. */
    @XmlElement(name = "ConceptoEconomico")
    private List<ConceptoEconomico> conceptoEconomico;
}