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

import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.Ignore;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.Protocol;

import uk.ac.manchester.rcs.corypha.core.CoryphaRootApplication;
import uk.ac.manchester.rcs.corypha.core.CoryphaTemplateUtil;

import freemarker.template.Configuration;
import freemarker.template.TemplateModelException;

/**
 * @author Bruno Harbulot
 * 
 */
@Ignore
public class CoryphaRootApplicationTest {
    public static void main(String[] args) throws Exception {
        Component component = new Component();
        component.getServers().add(Protocol.HTTP, 8182);
        component.getClients().add(Protocol.CLAP);

        CoryphaRootApplication cmsRootApplication = new CoryphaRootApplication();
        cmsRootApplication.setContext(component.getContext()
                .createChildContext());
        Context cmsRootAppContext = cmsRootApplication.getContext();
        cmsRootAppContext.getParameters().add(
                CoryphaRootApplication.MODULE_CLASSES_CTX_PARAM,
                "uk.ac.manchester.rcs.corypha.testapp1.Module1");
        cmsRootAppContext.getParameters().add(
                CoryphaRootApplication.MODULE_CLASSES_CTX_PARAM,
                "uk.ac.manchester.rcs.corypha.testapp1.Module2");
        cmsRootAppContext.getParameters().add(
                CoryphaRootApplication.MODULE_CLASSES_CTX_PARAM,
                "uk.ac.manchester.rcs.corypha.testapp1.Module3");

        fillSampleTemplate(cmsRootAppContext);

        component.getDefaultHost().attachDefault(cmsRootApplication);

        component.start();
    }

    /**
     * @param appContext
     * @throws TemplateModelException
     */
    private static void fillSampleTemplate(Context appContext)
            throws TemplateModelException {
        Configuration freemarkerConfig = CoryphaTemplateUtil
                .getConfiguration(appContext);
        freemarkerConfig.setSharedVariable("maintitle", "Corypha Test Page");

        CopyOnWriteArrayList<String> menuItemsHtml = new CopyOnWriteArrayList<String>();
        menuItemsHtml.add("Menu Item 1");
        menuItemsHtml.add("Menu Item 2");
        freemarkerConfig.setSharedVariable("sidemenuitems", menuItemsHtml);
        freemarkerConfig.setSharedVariable("sidemenutitle", "Left bar");
    }
}
