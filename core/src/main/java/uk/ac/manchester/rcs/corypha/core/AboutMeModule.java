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
package uk.ac.manchester.rcs.corypha.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;

/**
 * This is a small module that displays the information about the user who is
 * currently using the system (username, roles and principals).
 * 
 * @author Bruno Harbulot
 * 
 */
public class AboutMeModule extends CoryphaModule implements
        IApplicationProvider, IMenuProvider {
    public static class AboutMeResource extends ServerResource {
        @Get("html")
        public Representation toHtml() {
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("user", getClientInfo().getUser());
            data.put("roles", getClientInfo().getRoles());
            data.put("principals", getClientInfo().getPrincipals());
            return CoryphaTemplateUtil.buildTemplateRepresentation(
                    getContext(), getRequest(), "aboutme.ftl.html", data,
                    MediaType.TEXT_HTML);
        }
    }

    public static class AboutMeApplication extends CoryphaApplication {
        @Override
        public String getAutoPrefix() {
            return "aboutme/";
        }

        @Override
        public Restlet createInboundRoot() {
            Configuration cfg = CoryphaTemplateUtil
                    .getConfiguration(getContext());
            CoryphaTemplateUtil.addTemplateLoader(cfg, new ClassTemplateLoader(
                    AboutMeModule.class, "templates"));

            Router router = new Router(getContext());
            router.attachDefault(AboutMeResource.class);

            HibernateFilter hibernateFilter = new HibernateFilter(getContext(),
                    router);
            return hibernateFilter;
        }

        @Override
        public CoryphaApplication getApplication() {
            return this;
        }
    }

    private final AboutMeApplication application = new AboutMeApplication();
    private final List<MenuItem> menuItems = Collections
            .unmodifiableList(Arrays.asList(new MenuItem[] { new MenuItem(
                    "About me", "/aboutme/") }));

    @Override
    public CoryphaApplication getApplication() {
        return application;
    }

    @Override
    public List<MenuItem> getMenuItems() {
        return menuItems;
    }
}
