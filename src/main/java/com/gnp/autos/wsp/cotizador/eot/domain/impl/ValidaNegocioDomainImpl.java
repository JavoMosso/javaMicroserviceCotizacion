package com.gnp.autos.wsp.cotizador.eot.domain.impl;

import static com.gnp.autos.wsp.cotizador.eot.util.Constantes.CVE_VALOR_CONVENIDO_10;
import static com.gnp.autos.wsp.cotizador.eot.util.Constantes.CVE_VALOR_CONVENIDO_MENOS_10;
import static com.gnp.autos.wsp.negocio.util.Utils.filterFirst;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.gnp.autos.wsp.cotizador.eot.domain.ValidaNegocioDomain;
import com.gnp.autos.wsp.cotizador.eot.error.ExecutionError;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.util.Constantes;
import com.gnp.autos.wsp.cotizador.eot.util.Utileria;
import com.gnp.autos.wsp.negocio.neg.model.CoberturaNeg;
import com.gnp.autos.wsp.negocio.neg.model.CotizaNegReq;
import com.gnp.autos.wsp.negocio.neg.model.DescuentoNeg;
import com.gnp.autos.wsp.negocio.neg.model.PaqueteNeg;
import com.gnp.autos.wsp.negocio.umoservice.model.CoberturaUmo;
import com.gnp.autos.wsp.negocio.umoservice.model.Descuento;
import com.gnp.autos.wsp.negocio.umoservice.model.Elemento;
import com.gnp.autos.wsp.negocio.umoservice.model.FormatoCobertura;
import com.gnp.autos.wsp.negocio.umoservice.model.Paquete;
import com.gnp.autos.wsp.negocio.umoservice.model.Transacciones;
import com.gnp.autos.wsp.negocio.umoservice.model.UmoServiceResp;
import com.gnp.autos.wsp.negocio.umoservice.model.ValorCobertura;
import com.gnp.autos.wsp.negocio.util.Utils;

/**
 * The Class ValidaNegocioDomainImpl.
 */
@Component
public class ValidaNegocioDomainImpl implements ValidaNegocioDomain {

    /**
     * Valida.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the cotizacion negocio
     */
    @Override
    public final CotizacionNegocio valida(final CotizacionNegocio cotizacionNegocio) {

        UmoServiceResp umoServ = cotizacionNegocio.getUmoService();
        if (!cotizacionNegocio.getUmoService().isEsUmo()) {
            throw new ExecutionError(Constantes.ERROR_37,
                    "Es necesario el codigo de promocion para este negocio operable");
        }
        cotizacionNegocio.getCotizaNegReq().setIdVersionNegocio(umoServ.getNegocio().getVersion());

        if (!Utileria.existeValor(umoServ.getDominios().getCombinaciones())
                || !Utileria.existeValor(umoServ.getDominios().getCombinaciones().get(0).getPaquetes())) {
            throw new ExecutionError(Constantes.ERROR_26);
        }

        validacionUmos(umoServ, cotizacionNegocio);

        cotizacionNegocio.getCotizaNegReq().getVehiculo()
                .setEstadoCirculacion(cotizacionNegocio.getCatalogoResp().getEstadoCirculacion());

        validaVehiculo(cotizacionNegocio);

        return validaPaquetes(cotizacionNegocio);

    }

    /**
     * Valida vehiculo.
     *
     * @param cotizacionNegocio the cotizacion negocio
     */
    private static void validaVehiculo(final CotizacionNegocio cotizacionNegocio) {
        CotizaNegReq negReq = cotizacionNegocio.getCotizaNegReq();
        String formaIndemnizacion = negReq.getVehiculo().getFormaIndemnizacion();
        BigDecimal valorVehiculoBd = cotizacionNegocio.getCatalogoResp().getValorVehiculo();
        boolean isSubramoValorBd = Constantes.SUBRAMOS_VEHICULOS.contains(negReq.getVehiculo().getSubRamo());

        if (isSubramoValorBd && (Constantes.CVE_VALOR_CONVENIDO.equals(formaIndemnizacion)
                || Constantes.CVE_VALOR_COMERCIAL.equals(formaIndemnizacion))) {
            negReq.getVehiculo().setValorVehiculo(String.valueOf(valorVehiculoBd));
        } else {
            if (!Utileria.existeValor(negReq.getVehiculo().getValorVehiculo())) {
                throw new ExecutionError(Constantes.ERROR_37, "Es necesario proporcionar el valor del vehiculo");
            }
        }

        if (Constantes.CVE_HERRAMIENTA_PORTAL.equals(negReq.getCveHerramienta())) {
            return;
        }

        if (CVE_VALOR_CONVENIDO_10.equals(formaIndemnizacion)
                || CVE_VALOR_CONVENIDO_MENOS_10.equals(formaIndemnizacion)) {
            double factor = CVE_VALOR_CONVENIDO_10.equals(formaIndemnizacion) ? Constantes.FACTOR_VALOR_CONVENIDO_10
                    : Constantes.FACTOR_VALOR_CONVENIDO_MENOS_10;
            Double valor = valorVehiculoBd.doubleValue() * factor;
            valorVehiculoBd = Utileria.getDecimalFormat1(valor, Constantes.NUM_4);
            negReq.getVehiculo().setValorVehiculo(valorVehiculoBd.toString());
        }
    }

    /**
     * Validacion umos.
     *
     * @param umoServ           the umo serv
     * @param cotizacionNegocio the cotizacion negocio
     */
    private void validacionUmos(final UmoServiceResp umoServ, final CotizacionNegocio cotizacionNegocio) {

        if (umoServ.getDominios().getCombinaciones().get(0).getPaquetes().parallelStream()
                .noneMatch(p -> p.getIndemnizaciones().parallelStream().anyMatch(i -> i.getClave().equalsIgnoreCase(
                        cotizacionNegocio.getCotizaNegReq().getVehiculo().getFormaIndemnizacion())))) {
            throw new ExecutionError(Constantes.ERROR_37, "No se encontro forma de indemnizacion");
        }

        if (umoServ.getDominios().getCobranzas().getCobranzas().parallelStream().noneMatch(t -> t.getConductoCobro()
                .getClave().equalsIgnoreCase(cotizacionNegocio.getCotizaNegReq().getViaPago()))) {
            throw new ExecutionError(Constantes.ERROR_37, "No se encontro la via de pago");
        }

        if (Utileria.existeValor(cotizacionNegocio.getCotizaNegReq().getPeriodicidad())
                && umoServ.getDominios().getCobranzas().getCobranzas().parallelStream()
                        .noneMatch(t -> t.getFormas().parallelStream().anyMatch(f -> f.getFormaPago().getClave()
                                .equalsIgnoreCase(cotizacionNegocio.getCotizaNegReq().getPeriodicidad())))) {
            throw new ExecutionError(Constantes.ERROR_37, "No se encontro la periodicidad");
        }
    }

    /**
     * Valida paquetes.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the cotizacion negocio
     */
    private static CotizacionNegocio validaPaquetes(final CotizacionNegocio cotizacionNegocio) {
        cotizacionNegocio.getCotizaNegReq().setPaquetes(getPaquetesNeg(cotizacionNegocio));
        cotizacionNegocio.getCotizaNegReq().setDescuentos(getDescuentosNeg(cotizacionNegocio));

        return cotizacionNegocio;
    }

    /**
     * Gets the paquetes neg.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the paquetes neg
     */
    private static List<PaqueteNeg> getPaquetesNeg(final CotizacionNegocio cotizacionNegocio) {
        List<PaqueteNeg> paquetesNeg = cotizacionNegocio.getCotizaNegReq().getPaquetes();

        List<Paquete> paquetesUmo = cotizacionNegocio.getUmoService().getDominios().getCombinaciones().get(0)
                .getPaquetes();

        List<FormatoCobertura> formatosCob = cotizacionNegocio.getUmoService().getDominios().getImpresiones()
                .getFormatos();
        String valVeh = cotizacionNegocio.getCotizaNegReq().getVehiculo().getValorVehiculo();
        String formaInd = cotizacionNegocio.getCotizaNegReq().getVehiculo().getFormaIndemnizacion();

        if (!Utileria.existeValor(paquetesNeg)) {

            return paquetesUmo.parallelStream().map(p -> {
                PaqueteNeg n = new PaqueteNeg();
                n.setCvePaquete(String.valueOf(p.getProductoPersonalizado()));
                n.setDescPaquete(Utils.trim(p.getAlias()));
                n.setCoberturas(getCoberturasNegocioUmo(p, valVeh, n, 1, formatosCob, formaInd));
                n.setPreferente(p.isPreferente());
                return n;
            }).collect(Collectors.toList());

        } else {

            paquetesNeg.parallelStream().forEach(n -> {
                Paquete p;

                if (Utileria.existeValor(n.getCvePaquete())) {
                    p = paquetesUmo.parallelStream()
                            .filter(u -> u.getProductoPersonalizado().equalsIgnoreCase(n.getCvePaquete())).findFirst()
                            .orElseThrow(() -> new ExecutionError(Constantes.ERROR_11, n.getCvePaquete()));
                } else {
                    p = paquetesUmo.parallelStream()
                            .filter(u -> u.getAlias().trim().equalsIgnoreCase(n.getDescPaquete().trim())).findFirst()
                            .orElseThrow(() -> new ExecutionError(Constantes.ERROR_11, n.getDescPaquete()));
                    n.setCvePaquete(p.getProductoPersonalizado());
                }
                n.setPreferente(p.isPreferente());

                if (!Utileria.existeValor(n.getDescPaquete())) {
                    n.setDescPaquete(Utileria.getValorStringVacio(Utils.trim(p.getAlias())));
                }

                if (n.getCoberturas().parallelStream().anyMatch(cN -> p.getCoberturas().parallelStream().noneMatch(
                        pC -> pC.getCobertura().getClave().trim().equalsIgnoreCase(cN.getCveCobertura().trim())))) {
                    throw new ExecutionError(Constantes.ERROR_12);
                }
                List<CoberturaNeg> lstCobBasicas = getCoberturasNegocioUmo(p, valVeh, n, 1, formatosCob, formaInd);
                List<CoberturaNeg> lstCobAdic = getCoberturasNegocioUmo(p, valVeh, n, 2, formatosCob, formaInd);

                n.setCoberturas(lstCobBasicas);
                n.getCoberturas().addAll(lstCobAdic);

            });

        }
        return paquetesNeg;
    }

    /**
     * Gets the coberturas negocio umo.
     *
     * @param p                  the p
     * @param valorVehiculo      the valor vehiculo
     * @param n                  the n
     * @param tipoCobertura      the tipo cobertura
     * @param formatosCobertura  the formatos cobertura
     * @param formaIndemnizacion the forma indemnizacion
     * @return the coberturas negocio umo
     */
    private static List<CoberturaNeg> getCoberturasNegocioUmo(final Paquete p, final String valorVehiculo,
            final PaqueteNeg n, final Integer tipoCobertura, final List<FormatoCobertura> formatosCobertura,
            final String formaIndemnizacion) {

        if (tipoCobertura == 1) {
            return p.getCoberturas().parallelStream().filter(cob -> cob.getTipoCobertura().getId() == 1)
                    .map(cob -> getCoberturaNegUmo(cob, valorVehiculo, n.getCoberturas(), formatosCobertura,
                            formaIndemnizacion))
                    .collect(Collectors.toList());
        } else {

            return p.getCoberturas().parallelStream()
                    .filter(cob -> cob.getTipoCobertura().getId() == 2 && n.getCoberturas().parallelStream()
                            .anyMatch(nc -> nc.getCveCobertura().equalsIgnoreCase(cob.getCobertura().getClave())))
                    .map(cob -> getCoberturaNegUmo(cob, valorVehiculo, n.getCoberturas(), formatosCobertura,
                            formaIndemnizacion))
                    .collect(Collectors.toList());
        }
    }

    /**
     * Gets the cobertura neg umo.
     *
     * @param cob                the cob
     * @param valVeh             the val veh
     * @param coberturasNeg      the coberturas neg
     * @param formatosCobertura  the formatos cobertura
     * @param formaIndemnizacion the forma indemnizacion
     * @return the cobertura neg umo
     */
    private static CoberturaNeg getCoberturaNegUmo(final CoberturaUmo cob, final String valVeh,
            final List<CoberturaNeg> coberturasNeg, final List<FormatoCobertura> formatosCobertura,
            final String formaIndemnizacion) {

        CoberturaNeg cobNeg = new CoberturaNeg();
        cobNeg.setCveCobertura(cob.getCobertura().getClave());
        cobNeg.setNombre(cob.getCobertura().getNombre());
        cobNeg.setImpresionOrden(String.valueOf(cob.getCobertura().getOrden()));
        if (Utileria.existeValor(coberturasNeg)) {
            coberturasNeg.parallelStream().filter(cN -> cN.getCveCobertura().equalsIgnoreCase(cobNeg.getCveCobertura()))
                    .findFirst().ifPresent(cN -> {
                        cobNeg.setSa(cN.getSa());
                        cobNeg.setDeducible(cN.getDeducible());
                        cobNeg.setUdDed(cN.getUdDed());
                        cobNeg.setUdSA(cN.getUdSA());
                    });
        }
        String fCobertura = formatosCobertura.parallelStream()
                .filter(fc -> fc.getCobertura().getClave().equalsIgnoreCase(cobNeg.getCveCobertura()))
                .map(fc -> fc.getFormasIndemnizacion().parallelStream()
                        .filter(fci -> fci.getFormaIndemnizacion().getClave().equals(formaIndemnizacion))
                        .map(fci -> fci.getElementos().parallelStream()
                                .filter(fcie -> fcie.getElemento().getCodigo().equalsIgnoreCase(Constantes.MOD_CVE_SA))
                                .map(fcie -> fcie.getFormaImpresion().getNombre().trim()).findFirst().orElse(""))
                        .findFirst().orElse(""))
                .findFirst().orElse("");

        if (fCobertura.equalsIgnoreCase(Constantes.TIPO_VALOR_VEHICULO_COBERTURA)
                || fCobertura.equalsIgnoreCase(Constantes.TIPO_FORMA_INDEM_COBERTURA)) {
            cobNeg.setSa(valVeh);
            cobNeg.setUdSA("IMPT");
        } else if (cobNeg.getCveCobertura().equalsIgnoreCase(Constantes.CVE_COBERTURA_EXC_CUB)) {
            if (!Utileria.existeValor(cobNeg.getSa())) {
                throw new ExecutionError(Constantes.ERROR_14, cobNeg.getCveCobertura());
            }
            try {
                Utileria.getValorDouble(cobNeg.getSa());
            } catch (Exception ne) {
                Logger.getRootLogger().debug(ne);
                throw new ExecutionError(Constantes.ERROR_14, cobNeg.getCveCobertura());
            }

        } else {
            cob.getElementos().parallelStream()
                    .filter(ct -> ct.getElemento().getCodigo().equalsIgnoreCase(Constantes.MOD_CVE_SA)).findFirst()
                    .ifPresent(c -> {

                        if (Utileria.existeValor(c.getVisualiza()) && (c.getVisualiza().equalsIgnoreCase("AMPARADA")
                                || c.getVisualiza().equalsIgnoreCase("NO_APLICA"))) {
                            cobNeg.setSa("999999");
                        } else {
                            cobNeg.setSa(getDeducibleSA(cobNeg.getCveCobertura(), cobNeg.getSa(), c.getValores(),
                                    Constantes.ERROR_14));
                        }

                        cobNeg.setUdSA(getUnidadDedSA(c.getUnidadMedida(), cobNeg.getUdSA()));
                    });
        }

        cob.getElementos().parallelStream()
                .filter(ct -> ct.getElemento().getCodigo().equalsIgnoreCase(Constantes.MOD_CVE_DEDUCIBLE)).findFirst()
                .ifPresent(c -> {
                    cobNeg.setDeducible(getDeducibleSA(cobNeg.getCveCobertura(), cobNeg.getDeducible(), c.getValores(),
                            Constantes.ERROR_13));
                    cobNeg.setUdDed(getUnidadDedSA(c.getUnidadMedida(), cobNeg.getUdDed()));

                });

        return cobNeg;

    }

    /**
     * Gets the unidad ded SA.
     *
     * @param unidadMedida the unidad medida
     * @param valDefault   the val default
     * @return the unidad ded SA
     */
    private static String getUnidadDedSA(final Elemento unidadMedida, final String valDefault) {
        if (Optional.ofNullable(unidadMedida).isPresent()) {
            return unidadMedida.getClave();
        }
        return valDefault;
    }

    /**
     * Gets the deducible SA.
     *
     * @param cveCoberturaNeg the cve cobertura neg
     * @param dedsa           the dedsa
     * @param valores         the valores
     * @param numError        the num error
     * @return the deducible SA
     */
    private static String getDeducibleSA(final String cveCoberturaNeg, final String dedsa,
            final List<ValorCobertura> valores, final Integer numError) {
        if (Utileria.existeValor(dedsa)) {
            return valores.parallelStream().filter(cV -> cV.getValor().trim().equalsIgnoreCase(dedsa.trim()))
                    .map(ValorCobertura::getValor).findFirst()
                    .orElseThrow(() -> new ExecutionError(numError, cveCoberturaNeg));
        } else {
            return valores.parallelStream().filter(ValorCobertura::isDefecto).map(ValorCobertura::getValor).findFirst()
                    .orElse(null);
        }

    }

    /**
     * Gets the descuentos neg.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the descuentos neg
     */
    private static List<DescuentoNeg> getDescuentosNeg(final CotizacionNegocio cotizacionNegocio) {
        List<DescuentoNeg> descuentosNeg = cotizacionNegocio.getCotizaNegReq().getDescuentos();
        List<Descuento> descuentosUmo = cotizacionNegocio.getUmoService().getDominios().getCobranzas().getDescuentos();

        // Validar los descuentos en el request contra los del umo service
        if (Utileria.existeValor(descuentosNeg)) {
            descuentosNeg.parallelStream().forEach(p -> {
                if (!p.getCveDescuento().equalsIgnoreCase(Constantes.STR_VADESREN)
                        && Utileria.existeValor(descuentosUmo)) {
                    Descuento dU = descuentosUmo.parallelStream()
                            .filter(d -> d.getDescuento().getCodigo().equalsIgnoreCase(p.getCveDescuento())).findFirst()
                            .orElseThrow(() -> new ExecutionError(Constantes.ERROR_37,
                                    "El descuento " + p.getCveDescuento() + " no es valido"));
                    p.setBanRecargo("0");
                    p.setDescripcion(dU.getDescuento().getNombre());
                    Double valorDesc = Utileria.getValorDouble(p.getValor());
                    if (dU.getDescuento().getTipo().getNombre().trim().equalsIgnoreCase(Constantes.DESC_TECNICO)
                            && dU.getTransacciones().parallelStream()
                                    .filter(ty -> ty.getTransaccion().getNombre().trim()
                                            .equalsIgnoreCase(Constantes.FILTRO_TX_DESCUENTO))
                                    .map(Transacciones::getValor).noneMatch(ty -> Double.compare(ty, valorDesc) >= 0)) {
                        throw new ExecutionError(Constantes.ERROR_37,
                                "El descuento " + p.getCveDescuento() + " no tiene un valor correcto");
                    }

                }
            });
        }

        // Si en el request no se especifico el descuento por volumen y el umo service
        // indica que si aplica, incluirlo
        if (Utils.noneMatch(descuentosNeg, d -> d.getCveDescuento().equalsIgnoreCase(Constantes.STR_VADESVOL))) {
            filterFirst(descuentosUmo, d -> d.getDescuento().getCodigo().equalsIgnoreCase(Constantes.STR_VADESVOL))
                    .ifPresent(d -> {
                        DescuentoNeg dN = new DescuentoNeg();
                        dN.setCveDescuento(d.getDescuento().getCodigo());
                        dN.setDescripcion(d.getDescuento().getNombre());
                        dN.setBanRecargo("0");
                        Double valorDesc = d.getTransacciones().parallelStream()
                                .filter(ty -> ty.getTransaccion().getNombre().trim()
                                        .equalsIgnoreCase(Constantes.FILTRO_TX_DESCUENTO))
                                .mapToDouble(Transacciones::getValor).findFirst().orElse(0.0);
                        dN.setValor(valorDesc.toString());
                        dN.setUnidadMedida(Constantes.UNIDAD_DESC_DEFAULT);
                        if (valorDesc > 0) {
                            descuentosNeg.add(dN);
                        }
                    });
        }
        return descuentosNeg;
    }
}
