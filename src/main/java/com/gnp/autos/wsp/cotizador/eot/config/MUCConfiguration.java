package com.gnp.autos.wsp.cotizador.eot.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.transport.http.ClientHttpRequestMessageSender;

import com.gnp.autos.wsp.cotizador.eot.client.soap.FoliadorClient;
import com.gnp.autos.wsp.cotizador.eot.client.soap.MucClient;
import com.gnp.autos.wsp.cotizador.eot.client.soap.RulesClient;
import com.gnp.autos.wsp.cotizador.eot.client.soap.RulesProdClient;
import com.gnp.autos.wsp.errors.annotations.WSPErrorConfig;

/**
 * The Class MUCConfiguration.
 */
@Configuration
@EnableCaching
@WSPErrorConfig
public class MUCConfiguration {

    /** The url catalogo. */
    @Value("${wsp_timeout_soap}")
    private Integer timeSoap;

    /** The url rules. */
    @Value("${wsp_url_Rules}")
    private String urlRules;

    /** The url muc. */
    @Value("${wsp_url_MUC}")
    private String urlMuc;

    /** The url foliador. */
    @Value("${wsp_url_Foliador}")
    private String urlFoliador;

    /** The url rules. */
    @Value("${wsp_url_RulesProd}")
    private String urlRulesProd;

    /**
     * Http components message sender.
     *
     * @return the client http request message sender
     */
    @Bean
    public ClientHttpRequestMessageSender httpComponentsMessageSender() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeSoap);
        requestFactory.setReadTimeout(timeSoap);
        return new ClientHttpRequestMessageSender(requestFactory);
    }

    /**
     * Marshaller.
     *
     * @return the jaxb 2 marshaller
     */
    @Bean
    @Qualifier("muc")
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.gnp.autos.wsp.cotizador.eot.wsdl");
        return marshaller;
    }

    /**
     * Marshaller rules.
     *
     * @return the jaxb 2 marshaller
     */
    @Bean
    @Qualifier("rules")
    public Jaxb2Marshaller marshallerRules() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.gnp.autos.wsp.cotizador.eot.rules.wsdl");
        return marshaller;
    }

    /**
     * Marshaller fol.
     *
     * @return the jaxb 2 marshaller
     */
    @Bean
    @Qualifier("foliador")
    public Jaxb2Marshaller marshallerFol() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.gnp.autos.wsp.cotizador.eot.foliador.wsdl");
        return marshaller;
    }

    /**
     * Marshaller rules prod.
     *
     * @return the jaxb 2 marshaller
     */
    @Bean
    @Qualifier("rulesprod")
    public Jaxb2Marshaller marshallerRulesProd() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.gnp.autos.wsp.cotizador.eot.rulesprod.wsdl");
        return marshaller;
    }

    /**
     * Gets the calcular.
     *
     * @param marshaller                  the marshaller
     * @param httpComponentsMessageSender the http components message sender
     * @return the calcular
     */
    @Bean
    public MucClient getCalcular(@Qualifier("muc") final Jaxb2Marshaller marshaller,
            final ClientHttpRequestMessageSender httpComponentsMessageSender) {
        MucClient client = new MucClient();
        client.setDefaultUri(urlMuc);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        client.setMessageSender(httpComponentsMessageSender);
        return client;
    }

    /**
     * Gets the rules.
     *
     * @param marshaller                  the marshaller
     * @param httpComponentsMessageSender the http components message sender
     * @return the rules
     */
    @Bean
    public RulesClient getRules(@Qualifier("rules") final Jaxb2Marshaller marshaller,
            final ClientHttpRequestMessageSender httpComponentsMessageSender) {
        RulesClient clientR = new RulesClient();
        clientR.setDefaultUri(urlRules);
        clientR.setMarshaller(marshaller);
        clientR.setUnmarshaller(marshaller);
        clientR.setMessageSender(httpComponentsMessageSender);
        return clientR;
    }

    /**
     * Gets the folio.
     *
     * @param marshaller                  the marshaller
     * @param httpComponentsMessageSender the http components message sender
     * @return the folio
     */
    @Bean
    public FoliadorClient getFolio(@Qualifier("foliador") final Jaxb2Marshaller marshaller,
            final ClientHttpRequestMessageSender httpComponentsMessageSender) {
        FoliadorClient clientF = new FoliadorClient();
        clientF.setDefaultUri(urlFoliador);
        clientF.setMarshaller(marshaller);
        clientF.setUnmarshaller(marshaller);
        clientF.setMessageSender(httpComponentsMessageSender);
        return clientF;
    }

    /**
     * Gets the rules prod.
     *
     * @param marshaller                  the marshaller
     * @param httpComponentsMessageSender the http components message sender
     * @return the rules
     */
    @Bean
    public RulesProdClient getRulesProd(@Qualifier("rulesprod") final Jaxb2Marshaller marshaller,
            final ClientHttpRequestMessageSender httpComponentsMessageSender) {
        RulesProdClient clientRProd = new RulesProdClient();
        clientRProd.setDefaultUri(urlRulesProd);
        clientRProd.setMarshaller(marshaller);
        clientRProd.setUnmarshaller(marshaller);
        clientRProd.setMessageSender(httpComponentsMessageSender);
        return clientRProd;
    }

}
