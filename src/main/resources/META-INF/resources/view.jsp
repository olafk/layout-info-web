<%@page import="de.olafkock.liferay.layoutinfo.prettyprint.XmlPrettyPrinter"%>
<%@page import="com.liferay.portal.kernel.util.HtmlUtil"%>
<%@page import="com.liferay.portal.kernel.model.PortletPreferences"%>
<%@page import="java.util.List"%>
<%@page import="javax.portlet.RenderRequest"%>
<%@ include file="init.jsp" %>

<h2>TypeSettingsProps</h2>
<%=renderRequest.getAttribute("props") %>
<h2>Description</h2>
<pre><%=renderRequest.getAttribute("desc") %></pre>
<h2>PortletPreferences</h2>
<%List<PortletPreferences> pp = (List<PortletPreferences>)renderRequest.getAttribute("portletPreferences");
	for(PortletPreferences pref: pp) {
		out.write("<h3>" + pref.getPortletId() + "</h3><p><pre>");
		out.write(HtmlUtil.escape(XmlPrettyPrinter.toPrettyString(pref.getPreferences(), 2)));
		out.write("</pre></p>");			
	}
%>