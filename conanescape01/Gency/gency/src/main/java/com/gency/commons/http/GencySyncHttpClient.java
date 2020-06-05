package com.gency.commons.http;

import android.content.Context;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

/**
 * Processes http requests in synchronous mode, so your caller thread will be blocked on each
 * request
 *
 * @see com.loopj.android.http.AsyncHttpClient
 */
public class GencySyncHttpClient extends GencyAsyncHttpClient {

    /**
     * Creates a new SyncHttpClient with default constructor arguments values
     */
    public GencySyncHttpClient() {
        super(80, 443);
    }

    /**
     * Creates a new SyncHttpClient.
     *
     * @param httpPort non-standard HTTP-only port
     */
    public GencySyncHttpClient(int httpPort) {
        super(httpPort, 443);
    }

    /**
     * Creates a new SyncHttpClient.
     *
     * @param httpPort  non-standard HTTP-only port
     * @param httpsPort non-standard HTTPS-only port
     */
    public GencySyncHttpClient(int httpPort, int httpsPort) {
        super(httpPort, httpsPort);
    }

    /**
     * Creates a new SyncHttpClient.
     *
     * @param schemeRegistry SchemeRegistry to be used
     */
    public GencySyncHttpClient(SchemeRegistry schemeRegistry) {
        super(schemeRegistry);
    }

    @Override
    protected GencyRequestHandle sendRequest(DefaultHttpClient client,
                                        HttpContext httpContext, HttpUriRequest uriRequest,
                                        String contentType, GencyResponseHandlerInterface responseHandler,
                                        Context context) {
        if (contentType != null) {
            uriRequest.addHeader("Content-Type", contentType);
        }

        responseHandler.setUseSynchronousMode(true);

		/*
         * will execute the request directly
		*/
        new GencyAsyncHttpRequest(client, httpContext, uriRequest, responseHandler).run();

        // Return a Request Handle that cannot be used to cancel the request
        // because it is already complete by the time this returns
        return new GencyRequestHandle(null);
    }
}
