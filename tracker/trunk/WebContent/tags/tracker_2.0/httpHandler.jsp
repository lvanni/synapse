<%@ page import="java.util.*"%>
<%@ page import="tracker.core.*"%>

<%  
if(Tracker.started){
	Tracker tracker = Tracker.getTracker();
	if(request.getParameter("request") != null) { %>
		<%= "*SAECO* " + tracker.handleRequest(request.getParameter("request")) %>
<% } } %>

