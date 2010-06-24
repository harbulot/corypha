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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;

/**
 * @author Bruno Harbulot
 * 
 */
public class DefaultModule extends CoryphaModule implements
        IApplicationProvider, IMenuProvider {
    public static class WelcomePageResource extends ServerResource {
        @Get("html")
        public Representation toHtml() {
            @SuppressWarnings("unchecked")
            Collection<IMenuProvider> menuProviders = (Collection<IMenuProvider>) getContext()
                    .getAttributes()
                    .get(CoryphaRootApplication.MENU_PROVIDERS_CTX_ATTRIBUTE);

            CopyOnWriteArrayList<String> menuItemsHtml = new CopyOnWriteArrayList<String>();
            if (menuProviders != null) {
                for (IMenuProvider menuProvider : menuProviders) {
                    for (MenuItem menuItem : menuProvider.getMenuItems()) {
                        menuItemsHtml.add(menuItem.toHtml());
                    }
                }
            }

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("menuitems", menuItemsHtml);

            return CoryphaTemplateUtil.buildTemplateRepresentation(
                    getContext(), getRequest(), "welcome.ftl.html", data,
                    MediaType.TEXT_HTML);
        }
    }

    public static class RootApplication extends CoryphaApplication {
        @Override
        public String getAutoPrefix() {
            return "";
        }

        @Override
        public Restlet createInboundRoot() {
            Configuration cfg = CoryphaTemplateUtil
                    .getConfiguration(getContext());
            CoryphaTemplateUtil.addTemplateLoader(cfg, new ClassTemplateLoader(
                    CoryphaRootApplication.class, "templates"));

            Router router = new Router(getContext());
            router.attachDefault(WelcomePageResource.class);
            return router;
        }

        @Override
        public CoryphaApplication getApplication() {
            return this;
        }
    }

    private final RootApplication rootApplication = new RootApplication();
    private final List<MenuItem> menuItems = Collections
            .unmodifiableList(Arrays.asList(new MenuItem[] { new MenuItem(
                    "Home", "") }));

    @Override
    public CoryphaApplication getApplication() {
        return this.rootApplication;
    }

    @Override
    public List<MenuItem> getMenuItems() {
        return this.menuItems;
    }
}
