package com.perso.config;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * <p>
 * In the pre-authenticated authentication case (unlike CAS, for example) the user will already have been identified through some external mechanism
 * and a secure context established by the time the security-enforcement filter is invoked.
 * <p>
 * Therefore this class isn't actually responsible for the commencement of authentication, as it is in the case of other providers. It will be called
 * if the user is rejected by the AbstractPreAuthenticatedProcessingFilter, resulting in a null authentication.
 * <p>
 * The <code>commence</code> method will always return an <code>HttpServletResponse.SC_UNAUTHORIZED</code> (403 error).
 *
 * @see org.springframework.security.web.access.ExceptionTranslationFilter
 *
 * @author Luke Taylor
 * @author Ruud Senden
 * @since 2.0
 */
public class Http401SessionTimeoutEntryPoint implements AuthenticationEntryPoint {
    /**
     * Reference sur l'objet logger de la classe
     */
    private static final Logger LOG = LogManager.getLogger(Http401SessionTimeoutEntryPoint.class);

    /**
     * Always returns a 401 error code to the client.
     */
    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException authException)
            throws IOException,
            ServletException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Pre-authenticated entry point called. Rejecting access");
        }
        response.setHeader("session-timeout", "true"); // header personalisé qui indique que la 401 est liée à un timeout de session
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access unauthorized");
    }

}