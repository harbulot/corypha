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

import java.util.List;

import org.restlet.data.Reference;

/**
 * @author Bruno Harbulot
 * 
 */
public interface IMenuProvider {
    public static class MenuItem {
        private final String label;
        private final Reference href;

        /**
         * @param label
         * @param href
         */
        public MenuItem(String label, Reference href) {
            this.label = label;
            this.href = href;
        }

        /**
         * @param label
         * @param href
         */
        public MenuItem(String label, String href) {
            this.label = label;
            this.href = new Reference(href);
        }

        public String getLabel() {
            return this.label;
        }

        public Reference getHref() {
            return this.href;
        }

        public String toHtml(Reference baseRef) {
            Reference ref;
            if (baseRef.isAbsolute()) {
                ref = new Reference(baseRef, this.href).getTargetRef();
            } else {
                ref = this.href;
            }
            return String.format("<a href=\"%s\">%s</a>", ref, this.label);
        }

        public String toHtml() {
            return String
                    .format("<a href=\"%s\">%s</a>", this.href, this.label);
        }
    }

    public List<MenuItem> getMenuItems();
}
