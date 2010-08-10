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
package uk.ac.manchester.rcs.corypha.startup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.restlet.Component;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class CoryphaStartupApplication {
    private static final Log LOGGER = LogFactory
            .getLog(CoryphaStartupApplication.class);

    public final static String COMPONENT_CONFIG_XML_URL_CTX_PARAM = "corypha_component_config_xml_url";

    public static void main(String[] args) throws Exception {
        SLF4JBridgeHandler.install();

        String authnConfigXmlUrl = System.getProperty(
                COMPONENT_CONFIG_XML_URL_CTX_PARAM,
                "clap://thread/corypha-component.cfg.xml");

        if (authnConfigXmlUrl != null) {
            try {
                ClientResource configResource = new ClientResource(
                        authnConfigXmlUrl);
                Representation entity = configResource.get();
                try {
                    if (configResource.getStatus().isSuccess()
                            && (entity != null)) {

                        XmlConfiguration xmlConfig = new XmlConfiguration(
                                entity.getStream());
                        Component component = (Component) xmlConfig.configure();
                        component.start();
                    } else {
                        LOGGER.error(String.format(
                                "Unable to load config file %s.",
                                authnConfigXmlUrl));
                    }
                } finally {
                    entity.release();
                }
            } catch (Exception e) {
                LOGGER.error(String.format(
                        "Error while loading config from %s.",
                        authnConfigXmlUrl), e);
            }
        }
    }
}
