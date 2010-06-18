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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ServerResource;

public class FileResource extends ServerResource {
    public final static String ID_ATTR = "fileid";
    private DataContainer dc;

    @Override
    public void doInit() {
        try {
            @SuppressWarnings("unchecked")
            ArrayList<DataContainer> dcList = (ArrayList<DataContainer>) getContext()
                    .getAttributes().get("list_of_things");

            int position = Integer.parseInt((String) getRequestAttributes()
                    .get(ID_ATTR));

            this.dc = dcList.get(position);
        } catch (Exception e) {
        }

        setExisting(this.dc != null);
    }

    @Override
    public Representation get() {
        if (dc.getTextContent() != null) {
            return new StringRepresentation(dc.getTextContent(), MediaType
                    .valueOf(dc.getContentType()));
        } else {
            if (dc.getBinaryContent() != null) {
                return new OutputRepresentation(MediaType.valueOf(dc
                        .getContentType())) {
                    @Override
                    public void write(OutputStream outputStream)
                            throws IOException {
                        outputStream.write(dc.getBinaryContent());
                    }
                };
            } else {
                return null;
            }
        }
    }
}