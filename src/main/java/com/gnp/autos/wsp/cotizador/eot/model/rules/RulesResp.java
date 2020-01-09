package com.gnp.autos.wsp.cotizador.eot.model.rules;

import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.ReglaActiva;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.Valida;

import lombok.Data;

/**
 * Instantiates a new rules resp.
 */
@Data
public class RulesResp {

    /** The outputstring. */
    private String outputstring;

    /** The firedrulescount. */
    private String firedrulescount;

    /** The decisionid. */
    private String decisionid;

    /** The valida. */
    private Valida valida;

    /** The reglaactiva. */
    private ReglaActiva reglaactiva;

}
