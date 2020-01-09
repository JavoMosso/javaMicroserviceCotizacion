package com.gnp.autos.wsp.cotizador.eot.domain.impl;

import static com.gnp.autos.wsp.cotizador.eot.fillingvalidation.GeneralValidation.instantValidate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.gnp.autos.wsp.cotizador.eot.domain.ValidaDomain;
import com.gnp.autos.wsp.cotizador.eot.error.ExecutionError;
import com.gnp.autos.wsp.cotizador.eot.fillingvalidation.GeneralValidation;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.util.Constantes;
import com.gnp.autos.wsp.cotizador.eot.util.Utileria;
import com.gnp.autos.wsp.negocio.neg.model.CotizaNegReq;
import com.gnp.autos.wsp.negocio.neg.model.PaqueteNeg;
import com.gnp.autos.wsp.negocio.neg.model.PersonaNeg;
import com.gnp.autos.wsp.negocio.neg.model.VehiculoNeg;
import com.gnp.autos.wsp.negocio.util.Utils;

/**
 * The Class ValidaDataImpl.
 */
@Component
public class ValidaDataImpl implements ValidaDomain {

    /**
     * Valida.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the cotizacion negocio
     */
    @Override
    public final CotizacionNegocio valida(final CotizacionNegocio cotizacionNegocio) {
        CotizaNegReq cotizaNeg = cotizacionNegocio.getCotizaNegReq();
        if (!Utileria.existeValor(cotizaNeg.getPersonas())) {
            throw new ExecutionError(Constantes.ERROR_34);
        }
        traduccirTipoPersona(cotizaNeg);
        cotizacionNegocio.setCotizaNegReq(cotizaNeg);

        GeneralValidation.instantValidate(cotizaNeg.getIdUMO(), Constantes.STR_ID_UNIDAD_OPERABLE);
        GeneralValidation.instantValidate(cotizaNeg.getVehiculo(), "VEHICULO");

        PersonaNeg cont = cotizaNeg.getPersonas().parallelStream()
                .filter(e -> Constantes.STR_CONTRATANTE.equalsIgnoreCase(e.getTipo())).findFirst()
                .orElseThrow(() -> new ExecutionError(1, Constantes.STR_CONTRATANTE));

        GeneralValidation.instantValidate(cont, Constantes.STR_CONTRATANTE);

        List<PaqueteNeg> paquetesNeg = cotizacionNegocio.getCotizaNegReq().getPaquetes();
        instantValidate(paquetesNeg, "PRODUCTOS");

        GeneralValidation v = new GeneralValidation();
        v.validate(cotizaNeg.getIniVig(), Constantes.STR_FCH_INICIO_VIGENCIA, Constantes.NUM_8);
        v.validate(cotizaNeg.getFinVig(), Constantes.STR_FCH_FIN_VIGENCIA, Constantes.NUM_8);
        v.validate(cotizaNeg.getIdUMO(), Constantes.STR_ID_UNIDAD_OPERABLE, Constantes.NUM_10);
        v.validate(cotizaNeg.getViaPago(), Constantes.STR_VIA_PAGO, Constantes.NUM_2);
        v.validate(cont.getTipoPersona(), Constantes.STR_TIPO_PERSONA, Constantes.NUM_1);
        v.validate(cotizaNeg.getVehiculo().getSubRamo(), Constantes.STR_SUB_RAMO, Constantes.NUM_2);
        v.validate(cotizaNeg.getVehiculo().getTipoVehiculo(), Constantes.STR_TIPO_VEHICULO, Constantes.NUM_3);
        v.validate(cotizaNeg.getVehiculo().getModelo(), Constantes.STR_MODELO, Constantes.NUM_4);
        v.validate(cotizaNeg.getVehiculo().getArmadora(), Constantes.STR_ARMADORA, Constantes.NUM_2);
        v.validate(cotizaNeg.getVehiculo().getCarroceria(), Constantes.STR_CARROCERIA, Constantes.NUM_2);
        v.validate(cotizaNeg.getVehiculo().getVersion(), Constantes.STR_VERSION, Constantes.NUM_2);
        v.validate(cotizaNeg.getVehiculo().getUso(), Constantes.STR_USO, Constantes.NUM_2);
        v.validate(cotizaNeg.getVehiculo().getFormaIndemnizacion(), Constantes.STR_FORMA_INDEMNIZACION,
                Constantes.NUM_2);

        PersonaNeg conductor = cotizaNeg.getPersonas().parallelStream()
                .filter(e -> Constantes.STR_CONDUCTOR.equalsIgnoreCase(e.getTipo())).findFirst()
                .orElseThrow(() -> new ExecutionError(Constantes.ERROR_34));

        v.validate(conductor.getDomicilio().getCodigoPostal(), Constantes.STR_CODIGO_POSTAL, Constantes.NUM_5);

        v.throwIfErros();

        try {
            Integer.parseInt(cotizaNeg.getVehiculo().getModelo());
        } catch (NumberFormatException e) {
            throw new ExecutionError(Constantes.ERROR_9, Constantes.STR_MODELO);
        }

        cotizaNeg.setVehiculo(getValFinal(cotizaNeg.getVehiculo()));

        validateDurationVigencia(cotizacionNegocio);

        String versionNegocioTmp = Utileria.getValorElementoNeg(cotizaNeg.getElementos(),
                Constantes.STR_VERSION_NEGOCIO);
        String cveTarifaTmp = Utileria.getValorElementoNeg(cotizaNeg.getElementos(), Constantes.STR_CVE_TARIFA);
        String fchTarifaTmp = Utileria.getValorElementoNeg(cotizaNeg.getElementos(), Constantes.STR_FCH_TARIFA);
        String derPolTmp = Utileria.getValorElementoNeg(cotizaNeg.getElementos(), Constantes.STR_DERECHO_POLIZA);

        String porcComisionTmp = Utileria.getValorElementoNeg(cotizaNeg.getElementos(), Constantes.STR_PORC_COMISION);
        String porcCesionComTmp = Utileria.getValorElementoNeg(cotizaNeg.getElementos(),
                Constantes.STR_PORC_CESION_COMISION);

        GeneralValidation.instantValidateNumeric(versionNegocioTmp, Constantes.STR_VERSION_NEGOCIO);
        GeneralValidation.instantValidateNumeric(cveTarifaTmp, Constantes.STR_FCH_TARIFA);
        GeneralValidation.instantValidateNumeric(derPolTmp, Constantes.STR_DERECHO_POLIZA);
        GeneralValidation.instantValidateFecha(fchTarifaTmp);

        GeneralValidation.instantValidateNumeric(porcComisionTmp, Constantes.STR_PORC_COMISION);
        GeneralValidation.instantValidateNumeric(porcCesionComTmp, Constantes.STR_PORC_CESION_COMISION);

        cotizacionNegocio.getCotizaNegReq()
                .setCveHerramienta(Utils.ifNull(cotizaNeg.getCveHerramienta(), Constantes.CVE_HERRAMIENTA));
        return cotizacionNegocio;

    }

    /**
     * Traduccir tipo persona.
     *
     * @param cotizaNeg the cotiza neg
     */
    private static void traduccirTipoPersona(final CotizaNegReq cotizaNeg) {
        cotizaNeg.getPersonas().parallelStream().forEach(p -> {
            p.setTipoPersona(tipoPersona(p.getTipoPersona()));
            if (Utileria.existeValor(p.getSexo())) {
                p.setSexo(getSexoMUC(p.getSexo()));
            }
        });

    }

    /**
     * Validate duration vigencia.
     *
     * @param cotizacioNegocio the cotizacio negocio
     */
    private static void validateDurationVigencia(final CotizacionNegocio cotizacioNegocio) {

        String iniVig = cotizacioNegocio.getCotizaNegReq().getIniVig();
        String finVig = cotizacioNegocio.getCotizaNegReq().getFinVig();

        SimpleDateFormat d = new SimpleDateFormat("yyyyMMdd");
        Date ini;
        Date fin;
        try {
            ini = d.parse(iniVig);
            fin = d.parse(finVig);
        } catch (ParseException e) {
            throw new ExecutionError(Constantes.ERROR_6);
        }

        if (ini.compareTo(fin) > 0) {
            throw new ExecutionError(Constantes.ERROR_29);
        }

    }

    /**
     * Tipo persona.
     *
     * @param tipoPersona the tipo persona
     * @return the string
     */
    private static String tipoPersona(final String tipoPersona) {

        if (Utileria.existeValor(tipoPersona) && !(tipoPersona.equalsIgnoreCase(Constantes.TIPO_PERSONA_MORAL)
                || tipoPersona.equalsIgnoreCase(Constantes.TIPO_PERSONA_FISICA))) {
            throw new ExecutionError(Constantes.ERROR_25, "tipo de persona");
        }

        if (Constantes.TIPO_PERSONA_MORAL.equalsIgnoreCase(tipoPersona)) {
            return "J";
        }
        if (Constantes.TIPO_PERSONA_FISICA.equalsIgnoreCase(tipoPersona)) {
            return "F";
        }
        return tipoPersona;
    }

    /**
     * Gets the sexo MUC.
     *
     * @param sexo the sexo
     * @return the sexo MUC
     */
    private static String getSexoMUC(final String sexo) {
        if (Constantes.PERSONA_FEMENINO.equalsIgnoreCase(sexo) || Constantes.PERSONA_MASCULINO.equalsIgnoreCase(sexo)) {
            if (Constantes.PERSONA_FEMENINO.equalsIgnoreCase(sexo)) {
                return "M";
            } else {
                return "V";
            }
        } else {
            throw new ExecutionError(Constantes.ERROR_25, "sexo");
        }
    }

    /**
     * Gets the val final.
     *
     * @param veh the veh
     * @return the val final
     */
    private static VehiculoNeg getValFinal(final VehiculoNeg veh) {
        String valorFinal = null;
        valorFinal = actualizatValorFinal(valorFinal, veh.getValorFactura());
        valorFinal = actualizatValorFinal(valorFinal, veh.getValorConvenido());
        valorFinal = actualizatValorFinal(valorFinal, veh.getValorVehiculo());
        veh.setValorVehiculo(valorFinal);
        veh.setValorConvenido(Utileria.soloNumeros(veh.getValorConvenido()));
        veh.setValorVehiculo(Utileria.soloNumeros(veh.getValorVehiculo()));
        veh.setValorFactura(Utileria.soloNumeros(veh.getValorFactura()));

        return veh;

    }

    /**
     * Actualizat valor final.
     *
     * @param valorFinal   the valor final
     * @param valorCompara the valor compara
     * @return the string
     */
    private static String actualizatValorFinal(final String valorFinal, final String valorCompara) {
        if (Optional.ofNullable(valorCompara).isPresent() && !valorCompara.isEmpty()) {
            return valorCompara;
        }
        return valorFinal;

    }
}
