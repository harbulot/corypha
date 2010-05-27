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

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * @author Bruno Harbulot
 * 
 */
public class FreeMarkerTest {
    private Configuration config;

    @Before
    public void setUp() {
        config = new Configuration();
        config.setTemplateLoader(new ClassTemplateLoader(FreeMarkerTest.class,
                "templates"));
    }

    @Test
    public void testSimpleTemplate() throws Exception {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("username", "Bruno");

        StringWriter out = new StringWriter();
        Template template = config.getTemplate("test1.ftl.html");
        template.process(data, out);

        assertEquals("<html>\n<body>\nHello Bruno\n</body>\n</html>", out
                .getBuffer().toString());
    }

    @Test
    public void testTemplateRepresentation() throws Exception {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("username", "Bruno");

        Representation rep = new TemplateRepresentation("test1.ftl.html",
                config, data, MediaType.TEXT_HTML);

        StringWriter out = new StringWriter();
        rep.write(out);
        assertEquals("<html>\n<body>\nHello Bruno\n</body>\n</html>", out
                .getBuffer().toString());
    }

    @Test
    public void testMacro1() throws Exception {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("username", "Bruno");

        Representation rep = new TemplateRepresentation("test2.ftl.html",
                config, data, MediaType.TEXT_HTML);

        StringWriter out = new StringWriter();
        rep.write(out);
        assertEquals(
                "<html>\n"
                        + "<head>\n"
                        + "  <title>\n"
                        + "    Default title\n"
                        + "  </title>\n"
                        + "<link href=\"test.css\" rel=\"stylesheet\" type=\"text/css\" />\n"
                        + "</head>\n" + "<body>\n" + "<h1>Hello Bruno</h1>\n"
                        + "</body>\n" + "</html>", out.getBuffer().toString());
    }

    @Test
    public void testMacro2() throws Exception {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("username", "Bruno");

        Representation rep = new TemplateRepresentation("test3.ftl.html",
                config, data, MediaType.TEXT_HTML);

        StringWriter out = new StringWriter();
        rep.write(out);
        assertEquals(
                "<html>\n"
                        + "<head>\n"
                        + "  <title>\n"
                        + "    Test page\n"
                        + "  </title>\n"
                        + "<link href=\"test.css\" rel=\"stylesheet\" type=\"text/css\" />\n"
                        + "</head>\n" + "<body>\n" + "<h1>Hello Bruno</h1>\n"
                        + "</body>\n" + "</html>", out.getBuffer().toString());
    }
}
