/*-----------------------------------------------------------------------
  
Copyright (c) 2007-2010, The University of Manchester, United Kingdom.
All rights reserved.

Redistribution and use in source and binary forms, with or without 
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, 
      this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright 
      notice, this list of conditions and the following disclaimer in the 
      documentation and/or other materials provided with the distribution.
 * Neither the name of The University of Manchester nor the names of 
      its contributors may be used to endorse or promote products derived 
      from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
POSSIBILITY OF SUCH DAMAGE.

-----------------------------------------------------------------------*/
package uk.ac.manchester.rcs.corypha.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.routing.Filter;

/**
 * This class is a Restlet Filter that starts and closes a database transaction
 * per request. The Hibernate Session is stored into a Request attribute:
 * HIBERNATE_SESSION_ATTRIBUTE.
 * 
 * @author Bruno Harbulot (Bruno.Harbulot@manchester.ac.uk)
 * 
 */
public class HibernateFilter extends Filter {

    private static final Log LOGGER = LogFactory.getLog(HibernateFilter.class);

    /**
     * Name of the attribute into which the Hibernate session is stored.
     */
    public final static String HIBERNATE_FACTORY_ATTRIBUTE = "uk.ac.manchester.rcs.corypha.hibernate.FACTORY";
    public final static String HIBERNATE_SESSION_ATTRIBUTE = "uk.ac.manchester.rcs.corypha.hibernate.SESSION";

    public HibernateFilter(Context context, Restlet next) {
        super(context, next);
    }

    public HibernateFilter() {
        this(null, null);
    }

    public HibernateFilter(Context context) {
        this(context, null);
    }

    /**
     * Filters the requests. Starts a transaction before passing on the request.
     * Commits if it returns normally. Rolls back if an exception has been
     * thrown.
     */
    @Override
    protected int doHandle(Request request, Response response) {
        int filterResponse = Filter.STOP;

        SessionFactory sessionFactory = (SessionFactory) getContext()
                .getAttributes().get(HIBERNATE_FACTORY_ATTRIBUTE);
        if (sessionFactory == null) {
            sessionFactory = createSessionFactory();
            getContext().getAttributes().put(HIBERNATE_FACTORY_ATTRIBUTE,
                    sessionFactory);
        }

        Transaction hibernateTransaction = null;
        try {
            filterResponse = super.doHandle(request, response);

            Throwable t = response.getStatus().getThrowable();
            if (t != null) {
                throw t;
            }
            Session hibernateSession = (Session) request.getAttributes().get(
                    HIBERNATE_SESSION_ATTRIBUTE);

            if (hibernateSession != null) {
                hibernateTransaction = hibernateSession.getTransaction();
                if ((hibernateTransaction != null)
                        && hibernateTransaction.isActive()) {
                    LOGGER.debug("Committing.");
                    hibernateTransaction.commit();
                    if (!hibernateTransaction.wasCommitted()) {
                        LOGGER.warn("Transaction not committed!");
                    }
                }
            }
        } catch (Throwable e) {
            if ((hibernateTransaction != null)
                    && hibernateTransaction.isActive()) {
                LOGGER.debug("Rolling back.", e);
                hibernateTransaction.rollback();
                if (!hibernateTransaction.wasRolledBack()) {
                    LOGGER.error("Transaction not rolled back!");
                }
            }
            if (e instanceof TransactionException) {
                throw (TransactionException) e;
            } else {
                throw new TransactionException(e);
            }
        } finally {
            request.getAttributes().remove(HIBERNATE_SESSION_ATTRIBUTE);
        }

        return filterResponse;
    }

    private static SessionFactory createSessionFactory() {
        org.hibernate.cfg.Configuration config = new org.hibernate.cfg.Configuration()
                .configure();
        SessionFactory sf = config.buildSessionFactory();
        return sf;
    }

    public static Session getSession(Context context, Request request) {
        return getSession(context, request, true);
    }

    public static Session getSession(Context context, Request request,
            boolean beginTransaction) {
        Session hibernateSession = (Session) request.getAttributes().get(
                HIBERNATE_SESSION_ATTRIBUTE);
        if (hibernateSession == null) {
            SessionFactory sessionFactory = (SessionFactory) context
                    .getAttributes().get(HIBERNATE_FACTORY_ATTRIBUTE);
            if (sessionFactory != null) {
                hibernateSession = sessionFactory.openSession();
                request.getAttributes().put(HIBERNATE_SESSION_ATTRIBUTE,
                        hibernateSession);
                Transaction transaction = hibernateSession.getTransaction();
                transaction.setTimeout(5);
                if (beginTransaction) {
                    LOGGER.debug("Beginning transaction...");
                    transaction.begin();
                }
            }
        }
        return hibernateSession;
    }
}
