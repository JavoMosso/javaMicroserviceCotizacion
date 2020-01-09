package com.gnp.autos.wsp.cotizador.eot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gnp.autos.wsp.errors.URLSolver;

/**
 * The Class ErrorDBSolver.
 */
@Component
public class ErrorDBSolver implements URLSolver {

    /** The url errores. */
    @Value("${wsp_url_Errores}")
    private String urlErrores;

    /**
     * Gets the url.
     *
     * @return the url
     */
    @Override
    public String getURL() {
        return urlErrores;
    }

}
