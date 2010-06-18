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
package uk.ac.manchester.rcs.corypha.testmod4;

import java.util.ArrayList;

import org.restlet.Restlet;
import org.restlet.routing.Router;

import uk.ac.manchester.rcs.corypha.core.CoryphaApplication;
import uk.ac.manchester.rcs.corypha.core.CoryphaTemplateUtil;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;

public class Application4 extends CoryphaApplication {
    @Override
    public String getAutoPrefix() {
        return "application4/";
    }

    @Override
    public Restlet createInboundRoot() {
        Configuration cfg = CoryphaTemplateUtil.getConfiguration(getContext());
        CoryphaTemplateUtil.addTemplateLoader(cfg, new ClassTemplateLoader(
                Module4.class, "templates"));

        ArrayList<DataContainer> dcList = new ArrayList<DataContainer>();
        dcList.add(new DataContainer(null, "Hello World!", "text/plain",
                "Hello"));
        getContext().getAttributes().put("list_of_things", dcList);

        Router router = new Router(getContext());
        router.attach("{" + FileResource.ID_ATTR + "}", FileResource.class);
        router.attachDefault(RootResource.class);
        return router;
    }

    @Override
    public CoryphaApplication getApplication() {
        return this;
    }
}