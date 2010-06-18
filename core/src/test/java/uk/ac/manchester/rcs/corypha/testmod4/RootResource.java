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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.util.Streams;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import uk.ac.manchester.rcs.corypha.core.CoryphaTemplateUtil;

public class RootResource extends ServerResource {
    @Get("html")
    public Representation toHtml() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("items", getContext().getAttributes().get("list_of_things"));

        return new TemplateRepresentation("testapp4.ftl.html",
                CoryphaTemplateUtil.getConfiguration(getContext()), data,
                MediaType.TEXT_HTML);
    }

    @Post("html")
    public Representation postToHtml(Representation entity) {
        if (entity != null) {
            if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(),
                    true)) {
                RestletFileUpload upload = new RestletFileUpload();
                byte[] binaryContent = null;
                String textContent = "";
                String contentType = null;
                String title = null;
                String dataType = null;
                try {
                    FileItemIterator fileItemIterator = upload
                            .getItemIterator(entity);
                    while (fileItemIterator.hasNext()) {
                        FileItemStream fileItemStream = fileItemIterator.next();
                        if (fileItemStream.isFormField()) {
                            if ("contenttype".equals(fileItemStream
                                    .getFieldName())) {
                                contentType = Streams.asString(fileItemStream
                                        .openStream(), "UTF-8");
                            } else if ("datatype".equals(fileItemStream
                                    .getFieldName())) {
                                dataType = Streams.asString(fileItemStream
                                        .openStream(), "UTF-8");
                            } else if ("textcontent".equals(fileItemStream
                                    .getFieldName())) {
                                textContent = Streams.asString(fileItemStream
                                        .openStream(), "UTF-8");
                            } else if ("title".equals(fileItemStream
                                    .getFieldName())) {
                                title = Streams.asString(fileItemStream
                                        .openStream(), "UTF-8");
                            }
                        } else {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            Streams.copy(fileItemStream.openStream(), baos,
                                    true);
                            binaryContent = baos.toByteArray();
                            contentType = fileItemStream.getContentType();
                        }
                    }

                    DataContainer dc;
                    if ("text".equals(dataType)) {
                        dc = new DataContainer(null, textContent, contentType,
                                title);
                    } else {
                        dc = new DataContainer(binaryContent, null,
                                contentType, title);
                    }

                    @SuppressWarnings("unchecked")
                    ArrayList<DataContainer> dcList = (ArrayList<DataContainer>) getContext()
                            .getAttributes().get("list_of_things");
                    dcList.add(dc);
                } catch (Exception e) {
                    getResponse().setEntity(
                            new StringRepresentation(e.getMessage(),
                                    MediaType.TEXT_PLAIN));
                    getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                    e.printStackTrace();
                }

            } else if (MediaType.APPLICATION_WWW_FORM.equals(entity
                    .getMediaType(), true)) {
                Form formResults = new Form(entity);

                DataContainer dc = new DataContainer(null, formResults
                        .getFirstValue("textcontent"), formResults
                        .getFirstValue("contenttype", "text/plain"),
                        formResults.getFirstValue("title", ""));
                @SuppressWarnings("unchecked")
                ArrayList<DataContainer> dcList = (ArrayList<DataContainer>) getContext()
                        .getAttributes().get("list_of_things");
                dcList.add(dc);
            }
        } else {
            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        }

        return toHtml();
    }
}