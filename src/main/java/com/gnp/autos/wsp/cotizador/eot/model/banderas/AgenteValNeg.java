package com.gnp.autos.wsp.cotizador.eot.model.banderas;

import lombok.Data;

/**
 * Instantiates a new agente val neg.
 */
@Data
public class AgenteValNeg {

    /** The id participante. */
    private String idParticipante;

    /** The cod intermediario. */
    private String codIntermediario;

    /** The id da. */
    private String idDa;

    /** The pct participacion. */
    private Double pctParticipacion;

    /** The pct cesion comision. */
    private Double pctCesionComision;

    /** The esquema compensacion. */
    private String esquemaCompensacion;

    /** The ban intermediario principal. */
    private String banIntermediarioPrincipal;

    /** The folio. */
    private String folio;

    /** The cve clase intermediario. */
    private String cveClaseIntermediario;
}
