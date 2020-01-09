package com.gnp.autos.wsp.cotizador.eot.model.rules;

import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.Cotizacion;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.ListaDescuentos;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.NegocioOperable;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.Poliza;
import com.gnp.autos.wsp.cotizador.eot.rules.wsdl.Vehiculo;

import lombok.Data;

/**
 * Instantiates a new rules req.
 */
@Data
public class RulesReq {

    /** The cotizacion. */
    private Cotizacion cotizacion;

    /** The vehiculo. */
    private Vehiculo vehiculo;

    /** The no. */
    private NegocioOperable no;

    /** The poliza. */
    private Poliza poliza;

    /** The lista desc. */
    private ListaDescuentos listaDesc;

}
