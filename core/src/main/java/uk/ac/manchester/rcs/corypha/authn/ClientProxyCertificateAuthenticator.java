/*-----------------------------------------------------------------------
  
Copyright (c) 2010, The University of Manchester, United Kingdom.
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
package uk.ac.manchester.rcs.corypha.authn;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.restlet.Context;

/**
 * Authenticator based on the SSL client certificate, allowing for proxy
 * certificates. If a client certificate is presented, it adds the Principal of
 * its subject to the list of principals in the request's ClientInfo. It also
 * sets the user to be a new User based on this Principal.
 * 
 * It behaves simirly to {@link ClientCertificateAuthenticator}, but uses the DN
 * of the end-entity certificate (which is delegated by proxy) rather than the
 * DN of the proxy itself.
 * 
 * {@link #getPrincipal(List)} and {@link #getUser(Principal)} can be overridden
 * to change the default behaviour.
 * 
 * @author Bruno Harbulot (Bruno.Harbulot@manchester.ac.uk)
 */
public class ClientProxyCertificateAuthenticator extends
        ClientCertificateAuthenticator {
    public ClientProxyCertificateAuthenticator(Context context) {
        super(context);
    }

    public ClientProxyCertificateAuthenticator(Context context, boolean optional) {
        super(context, optional);
    }

    /**
     * Extracts the Principal of the subject to use from a chain of certificate.
     * By default, this is the X500Principal of the subject of the first
     * end-entity certificate in the chain (the first one signed by a CA, and
     * not a proxy).
     * 
     * @see X509Certificate
     * @see X500Principal
     * @param certificateChain
     *            chain of client certificates.
     * @return Principal of the client certificate or null if the chain is
     *         empty.
     */
    @Override
    protected List<Principal> getPrincipals(
            List<X509Certificate> certificateChain) {
        if ((certificateChain != null) && (certificateChain.size() > 0)) {
            ArrayList<Principal> principals = new ArrayList<Principal>();
            int i = 1;
            while (i < certificateChain.size()) {
                if (certificateChain.get(i).getBasicConstraints() != -1) {
                    break;
                }
                i++;
            }
            X509Certificate userCert = certificateChain.get(i - 1);
            principals.add(userCert.getSubjectX500Principal());
            return principals;
        } else {
            return null;
        }
    }
}
