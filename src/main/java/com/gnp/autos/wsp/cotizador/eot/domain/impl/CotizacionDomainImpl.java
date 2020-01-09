package com.gnp.autos.wsp.cotizador.eot.domain.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.gnp.autos.wsp.cotizador.eot.domain.BanderaDomain;
import com.gnp.autos.wsp.cotizador.eot.domain.CampaniasDomain;
import com.gnp.autos.wsp.cotizador.eot.domain.CatalogoDomain;
import com.gnp.autos.wsp.cotizador.eot.domain.CotCotizacionDomain;
import com.gnp.autos.wsp.cotizador.eot.domain.CotizacionDomain;
import com.gnp.autos.wsp.cotizador.eot.domain.FoliadorDomain;
import com.gnp.autos.wsp.cotizador.eot.domain.FormatoImpresionDomain;
import com.gnp.autos.wsp.cotizador.eot.domain.MucDomain;
import com.gnp.autos.wsp.cotizador.eot.domain.RulesDomain;
import com.gnp.autos.wsp.cotizador.eot.domain.TransformacionDomain;
import com.gnp.autos.wsp.cotizador.eot.domain.UmoServiceDomain;
import com.gnp.autos.wsp.cotizador.eot.domain.ValidaDomain;
import com.gnp.autos.wsp.cotizador.eot.domain.ValidaNegocioDomain;
import com.gnp.autos.wsp.cotizador.eot.error.ExecutionError;
import com.gnp.autos.wsp.cotizador.eot.model.CotizacionNegocio;
import com.gnp.autos.wsp.cotizador.eot.model.banderas.BanderasAutosDecisionService;
import com.gnp.autos.wsp.cotizador.eot.model.campain.CampaniaResp;
import com.gnp.autos.wsp.cotizador.eot.model.transformacion.TransformacionNeg;
import com.gnp.autos.wsp.cotizador.eot.util.Constantes;
import com.gnp.autos.wsp.cotizador.eot.util.Utileria;
import com.gnp.autos.wsp.errors.exceptions.WSPXmlExceptionWrapper;
import com.gnp.autos.wsp.negocio.catalogo.model.CatalogoWSPResp;
import com.gnp.autos.wsp.negocio.neg.model.CoberturaNeg;
import com.gnp.autos.wsp.negocio.neg.model.CotizaNegReq;
import com.gnp.autos.wsp.negocio.neg.model.DescuentoNeg;
import com.gnp.autos.wsp.negocio.neg.model.VehiculoNeg;
import com.gnp.autos.wsp.negocio.umoservice.model.UmoServiceResp;

/**
 * The Class CotizacionDomainImpl.
 */
@Service
public class CotizacionDomainImpl implements CotizacionDomain {

    /** The umo service domain. */
    @Autowired
    private UmoServiceDomain umoServiceDomain;

    /** The valida data. */
    @Autowired
    private ValidaDomain validaData;

    /** The valida negocio. */
    @Autowired
    private ValidaNegocioDomain validaNegocio;

    /** The bandera domain. */
    @Autowired
    private BanderaDomain banderaDomain;

    /** The rules client. */
    @Autowired
    @Qualifier("rules")
    private RulesDomain rulesClient;

    /** The rules client. */
    @Autowired
    @Qualifier("rulesProd")
    private RulesDomain rulesProdClient;

    /** The folio cot. */
    @Autowired
    private FoliadorDomain folioCot;

    /** The muc domain. */
    @Autowired
    private MucDomain mucDomain;

    /** The formato impresion domain. */
    @Autowired
    private FormatoImpresionDomain formatoImpresionDomain;

    /** The catalogo domain. */
    @Autowired
    private CatalogoDomain catalogoDomain;

    /** The campania domain. */
    @Autowired
    private CampaniasDomain campaniaDomain;

    /** The transformacion domain. */
    @Autowired
    private TransformacionDomain transformacionDomain;
    
    /** The cot cotizacion domain. */
    @Autowired
    private CotCotizacionDomain cotCotizacionDomain;

    /**
     * Gets the cotizacion.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the cotizacion
     */
    @Override
    public final CotizacionNegocio getCotizacion(final CotizacionNegocio cotizacionNegocio) {
        Utileria.getRegistraLogTime("Inicia Cotizacion");
        CotizacionNegocio cotizacionNegocioData = validaData.valida(cotizacionNegocio);
        try {
            CotizacionNegocio cotizacionNegocioConf = completaCataUmo(futureCatalogo(cotizacionNegocioData),
                    futureUmoService(cotizacionNegocioData), cotizacionNegocioData);

            CotizacionNegocio cotizacionNegocioVal = validaNegocio.valida(cotizacionNegocioConf);

            cotizacionNegocioData = completaTransBanFol(futureCampania(cotizacionNegocioVal),
                    futureTrasformacion(cotizacionNegocioVal), futureBanderas(cotizacionNegocioVal),
                    futureFolio(cotizacionNegocioVal), cotizacionNegocioVal);
        } catch (CompletionException e) {
            manejaError(e);
        }
        cotizacionNegocioData = rulesProdClient.getRules(cotizacionNegocioData);
        cotizacionNegocioData = rulesClient.getRules(cotizacionNegocioData);
        cotizacionNegocioData = mucDomain.getCalculoMuc(cotizacionNegocioData);
        cotizacionNegocioData = formatoImpresionDomain.getFormatoImp(cotizacionNegocioData);
        Utileria.getRegistraLogTime("Fin Cotizacion");
        guardarCotCotizacion(cotizacionNegocioData);
        return cotizacionNegocioData;
    }
    
    /**
     * Guardar cot cotizacion.
     *
     * @param cotNeg the cot neg
     */
    private void guardarCotCotizacion(final CotizacionNegocio cotNeg) {
        CompletableFuture.runAsync(() -> {
            try {
                cotCotizacionDomain.guardar(cotNeg);
            } catch (ExecutionError e) {
                Logger.getRootLogger().error(String.format("Error al guardar al cotizacion %s en COT_COTIZACION", 
                        cotNeg.getCotizaNegReq().getIdCotizacion()));
            }
        });
    }

    /**
     * Maneja error.
     *
     * @param e the e
     */
    private static void manejaError(final CompletionException e) {
        Logger.getRootLogger().error(e);
        if (e.getCause() instanceof ExecutionError) {
            throw (ExecutionError) e.getCause();
        } else if (e.getCause() instanceof WSPXmlExceptionWrapper) {
            WSPXmlExceptionWrapper eWrWSP = (WSPXmlExceptionWrapper) e.getCause();
            throw new WSPXmlExceptionWrapper(eWrWSP.getEx());
        } else {
            throw new ExecutionError(Constantes.ERROR_37, e.getMessage());
        }
    }

    /**
     * Completa cata umo.
     *
     * @param futCatalogoWSP the fut catalogo WSP
     * @param futUmoService  the fut umo service
     * @param cotNeg         the cot neg
     * @return the cotizacion negocio
     */
    private CotizacionNegocio completaCataUmo(final CompletableFuture<CatalogoWSPResp> futCatalogoWSP,
            final CompletableFuture<UmoServiceResp> futUmoService, final CotizacionNegocio cotNeg) {

        CompletableFuture<List<Object>> lstResultado = myAllOf(futCatalogoWSP, futUmoService);

        lstResultado.join().parallelStream().filter(p -> p instanceof CatalogoWSPResp).findFirst()
                .ifPresent(p -> cotNeg.setCatalogoResp((CatalogoWSPResp) p));

        lstResultado.join().parallelStream().filter(p -> p instanceof UmoServiceResp).findFirst()
                .ifPresent(p -> cotNeg.setUmoService((UmoServiceResp) p));

        return cotNeg;
    }

    /**
     * My all of.
     *
     * @param futures the futures
     * @return the completable future
     */
    public static CompletableFuture<List<Object>> myAllOf(final CompletableFuture<?>... futures) {
        return CompletableFuture.allOf(futures)
                .thenApply(x -> Arrays.stream(futures).map(f -> (Object) f.join()).collect(Collectors.toList()));
    }

    /**
     * Completa trans ban fol.
     *
     * @param futCampania       the fut campania
     * @param futTransformacion the fut transformacion
     * @param futBandera        the fut bandera
     * @param futFolio          the fut folio
     * @param cotNeg            the cot neg
     * @return the cotizacion negocio
     */
    private CotizacionNegocio completaTransBanFol(final CompletableFuture<CampaniaResp> futCampania,
            final CompletableFuture<TransformacionNeg> futTransformacion,
            final CompletableFuture<BanderasAutosDecisionService> futBandera, final CompletableFuture<String> futFolio,
            final CotizacionNegocio cotNeg) {

        CompletableFuture<List<Object>> lstResultado = myAllOf(futCampania, futTransformacion, futBandera, futFolio);

        lstResultado.join().parallelStream().filter(p -> p instanceof CampaniaResp).findFirst().ifPresent(p -> cotNeg
                .setCotizaNegReq(validaPodescam(cotNeg.getCotizaNegReq(), ((CampaniaResp) p).getDescuentoTotal())));

        lstResultado.join().parallelStream().filter(p -> p instanceof TransformacionNeg).findFirst()
                .ifPresent(p -> cotNeg.setTransformacionNegocio((TransformacionNeg) p));

        lstResultado.join().parallelStream().filter(p -> p instanceof String).findFirst()
                .ifPresent(p -> cotNeg.getCotizaNegReq().setIdCotizacion((String) p));

        if (!Utileria.existeValor(cotNeg.getCotizaNegReq().getIdCotizacion())) {
            throw new ExecutionError(Constantes.ERROR_37, "No se pudo generar el id de cotizacion");
        }

        lstResultado.join().parallelStream().filter(p -> p instanceof BanderasAutosDecisionService).findFirst()
                .ifPresent(p -> asignaValoresRequest((BanderasAutosDecisionService) p, cotNeg));

        return cotNeg;

    }

    /**
     * Future umo service.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the completable future
     */
    private CompletableFuture<UmoServiceResp> futureUmoService(final CotizacionNegocio cotizacionNegocio) {
        return CompletableFuture.supplyAsync(() -> umoServiceDomain.getDatosNegocio(cotizacionNegocio));
    }

    /**
     * Future catalogo.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the completable future
     */
    private CompletableFuture<CatalogoWSPResp> futureCatalogo(final CotizacionNegocio cotizacionNegocio) {
        return CompletableFuture.supplyAsync(() -> catalogoDomain.getCatalogoWSP(cotizacionNegocio));
    }

    /**
     * Future campania.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the completable future
     */
    private CompletableFuture<CampaniaResp> futureCampania(final CotizacionNegocio cotizacionNegocio) {
        return CompletableFuture.supplyAsync(() -> campaniaDomain.getCampana(cotizacionNegocio));
    }

    /**
     * Future trasformacion.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the completable future
     */
    private CompletableFuture<TransformacionNeg> futureTrasformacion(final CotizacionNegocio cotizacionNegocio) {
        return CompletableFuture.supplyAsync(() -> transformacionDomain.getVariablesTransformacion(cotizacionNegocio));
    }

    /**
     * Future banderas.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the completable future
     */
    private CompletableFuture<BanderasAutosDecisionService> futureBanderas(final CotizacionNegocio cotizacionNegocio) {
        return CompletableFuture.supplyAsync(() -> banderaDomain.actualizaBandera(cotizacionNegocio));
    }

    /**
     * Future folio.
     *
     * @param cotizacionNegocio the cotizacion negocio
     * @return the completable future
     */
    private CompletableFuture<String> futureFolio(final CotizacionNegocio cotizacionNegocio) {
        return CompletableFuture.supplyAsync(() -> folioCot.getFolio(cotizacionNegocio));
    }

    /**
     * Asigna valores request.
     *
     * @param respuesta         the respuesta
     * @param cotizacionNegocio the cotizacion negocio
     */
    private static void asignaValoresRequest(final BanderasAutosDecisionService respuesta,
            final CotizacionNegocio cotizacionNegocio) {
        VehiculoNeg vehiculo = respuesta.getVehiculo();

        Boolean isAltoRiesgo = Boolean.valueOf(vehiculo.getAltoRiesgo());
        if (isAltoRiesgo) {
            Double pctDeducible = Double.valueOf(respuesta.getListCoberturas().get(0).getMtoDeducible());
            cotizacionNegocio.getCotizaNegReq().getVehiculo()
                    .setPctDedAltoRiesgo(respuesta.getListCoberturas().get(0).getMtoDeducibleAltoRiesgo());
            cotizacionNegocio.getCotizaNegReq().getVehiculo().setAltoRiesgo(Constantes.STR_UNO);
            cotizacionNegocio.getCotizaNegReq().setAfectaBono(respuesta.getIndAfectaBono());

            cotizacionNegocio.getCotizaNegReq().getPaquetes().parallelStream()
                    .forEach(p -> recorreCoberturasProducto(p.getCoberturas(), pctDeducible));

        }
    }

    /**
     * Recorre coberturas producto.
     *
     * @param coberturas   the coberturas
     * @param pctDeducible the pct deducible
     */
    private static void recorreCoberturasProducto(final List<CoberturaNeg> coberturas, final Double pctDeducible) {
        coberturas.parallelStream().filter(p -> p.getCveCobertura().equalsIgnoreCase(Constantes.COBERTURA_ROBO_TOTAL))
                .forEach(p -> p.setDeducible(pctDeducible.toString()));

    }

    /**
     * Valida podescam.
     *
     * @param cotizaNeg the cotiza neg
     * @param podescam  the podescam
     * @return the cotiza neg req
     */
    private CotizaNegReq validaPodescam(final CotizaNegReq cotizaNeg, final String podescam) {
        if (Double.parseDouble(podescam) > 0) {
            if (!Utileria.existeValor(cotizaNeg.getDescuentos())) {
                cotizaNeg.setDescuentos(new ArrayList<DescuentoNeg>());
            }
            DescuentoNeg desc = new DescuentoNeg();
            desc.setBanRecargo("0");
            desc.setCveDescuento(Constantes.STR_PODESCAM);
            desc.setDescripcion("POR DESCUENTO DE CAMPANIA");
            desc.setUnidadMedida("PORC");
            desc.setValor(String.valueOf(podescam));
            Optional<DescuentoNeg> desN = cotizaNeg.getDescuentos().parallelStream()
                    .filter(p -> p.getCveDescuento().equals(Constantes.STR_PODESCAM)).findFirst();
            if (desN.isPresent()) {
                desN.get().setValor(String.valueOf(podescam));
            } else {
                cotizaNeg.getDescuentos().add(desc);
            }
        }
        return cotizaNeg;
    }
}
