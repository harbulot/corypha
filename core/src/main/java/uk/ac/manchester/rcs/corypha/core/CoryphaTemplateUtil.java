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

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import org.restlet.Context;
import org.restlet.data.Reference;

import uk.ac.manchester.rcs.corypha.core.IMenuProvider.MenuItem;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateModelException;

/**
 * @author Bruno Harbulot
 */
public class CoryphaTemplateUtil {
    public static final String FREEMARKER_CONFIGURATION_ATTRIBUTE = "uk.ac.manchester.rcs.corypha.FREEMARKER_CONFIGURATION";

    public static Configuration getConfiguration(Context context) {
        try {
            Configuration cfg = (Configuration) context.getAttributes().get(
                    FREEMARKER_CONFIGURATION_ATTRIBUTE);
            if (cfg == null) {
                cfg = new Configuration();
                context.getAttributes().put(FREEMARKER_CONFIGURATION_ATTRIBUTE,
                        cfg);
            }

            String baseUrl = context.getParameters().getFirstValue(
                    CoryphaRootApplication.BASE_URL_CTX_PARAM);
            Reference baseRef = null;
            if (baseUrl != null) {
                baseRef = new Reference(baseUrl);
                cfg.setSharedVariable("baseurl", baseUrl);
            }

            @SuppressWarnings("unchecked")
            Collection<IMenuProvider> menuProviders = (Collection<IMenuProvider>) context
                    .getAttributes().get(
                            CoryphaRootApplication.MENU_PROVIDERS_CTX_ATTRIBUTE);

            CopyOnWriteArrayList<String> menuItemsHtml = new CopyOnWriteArrayList<String>();
            if (menuProviders != null) {
                for (IMenuProvider menuProvider : menuProviders) {
                    for (MenuItem menuItem : menuProvider.getMenuItems()) {
                        if (baseRef != null) {
                            menuItemsHtml.add(menuItem.toHtml(baseRef));
                        } else {
                            menuItemsHtml.add(menuItem.toHtml());
                        }
                    }
                }
            }

            cfg.setSharedVariable("topmenuitems", menuItemsHtml);

            return cfg;
        } catch (TemplateModelException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addTemplateLoader(Configuration cfg,
            TemplateLoader templateLoader) {
        synchronized (cfg) {
            TemplateLoader previousTemplateLoader = cfg.getTemplateLoader();
            if (previousTemplateLoader != null) {
                MultiTemplateLoader multiTemplateLoader = new MultiTemplateLoader(
                        new TemplateLoader[] { previousTemplateLoader,
                                templateLoader });
                cfg.setTemplateLoader(multiTemplateLoader);
            } else {
                cfg.setTemplateLoader(templateLoader);
            }
        }
    }
}
