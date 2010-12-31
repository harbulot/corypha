Corypha is a small utility framework based on Restlet.

It was developed at the University of Manchester to support the NanoCMOS
project and the Access and Identity Management using Social Networking
Technologies project (FOAF+SSL/WebID).


It uses:
  - Restlet as a Web/REST framework,
  - Hibernate for its persistence layer,
  - FreeMarker for using templates,
  - jQuery, jQuery UI and jQuery DataTables to improve the rendered pages.
  
  
 The configuration also uses the Jetty XML syntax (although the framework is
 otherwise independent of Jetty: you can run Corypha componenent within Jetty
 or not, as you wish).
 http://docs.codehaus.org/display/JETTY/Jetty+Xml+Configuration+Syntax+Reference
 
 
 The 3 main files for the configuration are 'corypha-component.cfg.xml',
 'corypha-template.cfg.xml' and 'corypha-authn.cfg.xml'.
 There are examples in core/src/test/resources
 
 - corypha-component.cfg.xml
 Using the Jetty XML configuration engine, this configures a
 'org.restlet.Component' (it sets and configures its connectors).
 See the Restlet documentation for more details.
 This file is optional and is only used to start application via
uk.ac.manchester.rcs.corypha.startup.CoryphaStartupApplication

Applications may be started via a standalone application too, 
as demonstrated in
'uk.ac.manchester.rcs.corypha.core.CoryphaRootApplicationTest'

 - corypha-template.cfg.xml
 This sets the common 'freemarker.template.Configuration'.
 
 
 
 
 'uk.ac.manchester.rcs.corypha.core.CoryphaRootApplicationTest', in
 'core/src/test', demonstrates the use of 4 "modules", also located
 in this test directory:
 1. "Hello World" that uses the common menus and layout.
 2. "Hello World" that bypasses the common menus and layout (plain text here).
 3. Example using jQuery, jQuery UI and DataTables.
 4. Example to upload a file.
 
 