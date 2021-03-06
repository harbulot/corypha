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

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.ClientInfo;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.Verifier;

/**
 * This is a {@link ChallengeAuthenticator} that is only activated if the
 * {@link ClientInfo} doesn't already have a user set.
 * 
 * @author Bruno Harbulot (Bruno.Harbulot@manchester.ac.uk)
 * 
 */
public class FallbackChallengeAuthenticator extends ChallengeAuthenticator {

    public FallbackChallengeAuthenticator(Context context, boolean optional,
            ChallengeScheme challengeScheme, String realm, Verifier verifier) {
        super(context, optional, challengeScheme, realm, verifier);
    }

    public FallbackChallengeAuthenticator(Context context, boolean optional,
            ChallengeScheme challengeScheme, String realm) {
        super(context, optional, challengeScheme, realm);
    }

    public FallbackChallengeAuthenticator(Context context,
            ChallengeScheme challengeScheme, String realm) {
        super(context, challengeScheme, realm);

    }

    @Override
    protected int beforeHandle(Request request, Response response) {
        if (request.getClientInfo() != null
                && request.getClientInfo().getUser() != null) {
            return CONTINUE;
        } else {
            return super.beforeHandle(request, response);
        }
    }
}
