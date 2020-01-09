package com.gnp.autos.wsp.cotizador.eot.model.cotcotizacion.xmldetalle;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * The Class Cobertura.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Coberturas")
public class Cobertura {
    /** The cve cobertura. */
    @XmlElement(name = "CLAVE_COBERTURA")
    private String cveCobertura;

    /** The primas. */
    @XmlElement(name = "Prima")
    private List<Prima> primas;

    /** The concepto economico. */
    @XmlElement(name = "ConceptoEconomico")
    private List<ConceptoEconomico> conceptoEconomico;
}