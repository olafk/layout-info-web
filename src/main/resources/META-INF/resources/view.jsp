<%@page import="de.olafkock.liferay.layoutinfo.prettyprint.XmlPrettyPrinter"%>
<%@page import="com.liferay.portal.kernel.util.HtmlUtil"%>
<%@page import="com.liferay.portal.kernel.model.PortletPreferences"%>
<%@page import="java.util.List"%>
<%@page import="javax.portlet.RenderRequest"%>
<%@ include file="init.jsp" %>

<h2>Meta-Information about this page</h2>
<ul>
<li>layoutName=<%=renderRequest.getAttribute("layoutName") %></li>
<li>layoutType=<%=renderRequest.getAttribute("layoutType") %></li>
<li>friendlyURL=<%=renderRequest.getAttribute("friendlyURL") %></li>
<li>stylebookEntryId=<%=renderRequest.getAttribute("styleBookEntryId") %></li>
<li>stylebookName=<%=renderRequest.getAttribute("styleBookEntryName") %></li>
<li>frontendTokens=<%=renderRequest.getAttribute("styleBookTokens") %></li>
<li>ancestorLayoutId=<%=renderRequest.getAttribute("ancestorLayoutId") %></li>
<li>ancestorPlid=<%=renderRequest.getAttribute("ancestorPlid") %></li>
<li>layoutPrototypeUuid=<%=renderRequest.getAttribute("layoutPrototypeUuid") %></li>
<li>masterLayoutPlid=<%=renderRequest.getAttribute("masterLayoutPlid") %></li>
<li>parentLayoutId=<%=renderRequest.getAttribute("parentLayoutId") %></li>
<li>parentLayoutPlid=<%=renderRequest.getAttribute("parentLayoutPlid") %></li>
<li>plid=<%=plid %></li>
</ul>

<h2>TypeSettingsProps</h2>
<%=renderRequest.getAttribute("props") %>

<h2>Description</h2>
<pre><%=renderRequest.getAttribute("desc") %></pre>

<h2>PortletPreferences</h2>
<%=renderRequest.getAttribute("portletPreferences") %>


