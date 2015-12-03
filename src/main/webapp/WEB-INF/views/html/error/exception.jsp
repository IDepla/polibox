<%@page import="org.springframework.http.HttpStatus"%>
<%@page import="javax.ws.rs.core.MediaType"%>
<%@page import="javax.ws.rs.core.HttpHeaders"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="com.fasterxml.jackson.databind.ObjectMapper"%>
<%@page import="it.polito.ai.polibox.web.controllers.response.Response"%>
<%
ObjectMapper json = new ObjectMapper();
Response resp=new Response();
response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
resp.appendError(""+HttpServletResponse.SC_BAD_REQUEST, "parametri sbagliati");
json.writeValue(out, resp);
%>