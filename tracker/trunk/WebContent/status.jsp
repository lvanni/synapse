<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>myMed tracker home page</title>
<link rel="stylesheet" type="text/css" href="css/style.css" />
<link rel="stylesheet" type="text/css" href="css/cssverticalmenu.css" />
<script type="text/javascript" src="javascript/cssverticalmenu.js"></script>
<script type="text/javascript" src="javascript/jquery/dist/jquery.js"></script>
<script type="text/javascript" src="javascript/tools.js"></script>

<% /********************* JAVA DECLARATION *********************/ %>
<%@ page import="java.util.*" %>
<%@ page import="tracker.core.*" %>
<%@ page import="core.protocols.p2p.Node" %>
<%  
Tracker tracker = null;
if(Tracker.started){
	tracker = Tracker.getTracker();
}
// FORM COMMANDS
if(request.getParameter("command") != null) {
	tracker = Tracker.getTracker(); 
	if(request.getParameter("command").equals("stop")){
		tracker.kill(); 
		tracker = null;
	} else if(request.getParameter("command").equals("addInvitation")){
		tracker.addInvitation(request.getParameter("invitation"), request.getParameter("pass"));
	}
	System.out.println(request.getParameter("command"));
}
// HTTP REQUEST
if(request.getParameter("request") != null && request.getParameter("from") != null) {
	if(tracker != null){
		tracker.handleHttpRequest(request.getParameter("request"), request.getParameter("from"));
	}
}%> 

</head>

<% /********************* BODY HTML *********************/ %>
<body>
	<div align="center">
		<div class="container">
			
			<div style="position:relative; top:85px; left:450px;">
			<a href="index.html">Home</a> - 
			<a href="#">Synapse</a> - 
			<a href="status.jsp">Tracker</a>
			</div>
			
			<ul id="verticalmenu" class="glossymenu" style="position:relative; top:240px; left:50px; z-index: 10;">
				<li><a href="index.html">Home</a></li>
				<li><a href="" >Tracker</a>
				    <ul>
				    <li><a href="">Public access</a></li>
				    <li><a href="status.jsp">Private access</a></li>
				    </ul>
				</li>
				<li><a href="">Networks</a>
				    <ul>
					    <li><a href="">Enterprise</a></li>
					    <li><a href="">Student</a></li>
					    <li><a href="">Old versions</a>
					      <ul>
					      	<li><a href="">MyConcert</a></li>
					      	<li><a href="">MyFoot</a></li>
					      </ul>
					    </li>
				    </ul>
				</li>
				<li><a href="" >Documentation</a></li>
				<li><a href="">Download</a></li>
				<li><a href="">Contact</a></li>
			</ul>
			
			<div style="position:relative; top:-50px; left:280px; width: 536px; height: 634px; overflow: auto;">
				<h3>Tracker manage page:</h3>
				<% if(tracker != null){ %>
					Status: <b style="color:green;">Started, Synchronized</b><br />
					Tracker running on : http://cycloid.inria.fr:<%= tracker.getPort() %>
					<br /><br />
					<hr style="width: 515px; margin-left:0px;" />
					<% for (String key : tracker.getPeerSet().keySet()) { %>
						<br />
						<% if (key.equals("synapse")){ %>
							<b>Control Network</b> <a onclick="document.getElementById('controlTable').style.display = 'table'" > + </a>
							<a onclick="document.getElementById('controlTable').style.display = 'none'" > - </a>
							<br />
							<table id="controlTable" border="1"  cellpadding="10" style="display: none;">
						<% } else { %>
							<b><%= key + " Network" %></b>
							<br />
							<table border="1"  cellpadding="10">
						<% } %>
						<tr>
							<td>IP</td><td>Port</td><td>Node ID</td><td>Status</td>
						</tr>
						<% for (Node n : tracker.getPeerSet().get(key)) { %>
							<tr id="<%= n.getIp() + n.getPort() + n.getId()%>" onmouseover="onTR('<%= n.getIp() + n.getPort() + n.getId()%>');" onmouseout="outTR('<%= n.getIp() + n.getPort() + n.getId()%>');">
								<td><%= n.getIp() %></td>
								<td><%= n.getPort() %></td>
								<td><%= n.getId() %></td>
								<td><span style="color: green;">Running</span></td>
							</tr>
						<% } %>
						</table>
					<% } %>
					<br />
					<% if(tracker.getInvitations().size() != 0){ %>
						Invitation:<br />
						<table border="1"  cellpadding="10">
							<tr>
								<td>NetworkID</td><td>pass</td>
							</tr>
							<% for(Invitation i : tracker.getInvitations()){ %>
								<tr>
									<td><%= i.getNetworkID() %></td><td><%= i.getAccessPass() %></td>
								</tr>
							<% } %>
						</table>
					<% } %>
				<% } else { %>
					Status: <b style="color:red;">Stopped...</b><br />
				<% } %>
				<br />
				<hr style="width: 515px; margin-left:0px;" />
				<h3>Commands:</h3>
				<form name="formCommand">
				<input type="hidden" name="command">
				<table>
				<tr>
					<td>Start the tracker :</td><td><input type="submit" value="send" onclick="start();" /></td>
				</tr>
				<tr>
					<td>Stop the tracker :</td><td><input type="submit" value="send" onclick="stop();" /></td>
				</tr>
				<tr>
					<td>Add invitation to :</td><td><input type="text" name="invitation"></td>
				</tr>
				<tr>
					<td>password :</td><td><input type="text" name="pass"> <input type="submit" value="send" onclick="addInvitation()"></td>
				</tr>
				</table>
				<br />
				</form>
			</div>
		</div>
		<a><i style="font-size:12px; ">Supported by AEOLUS FP6-IST-15964-FET Proactive and DEUKS JEP-41099 TEMPUS.</i></a>
	</div>
	<i style="font-size:12px; position:absolute; right:100px; bottom:50px;" >designed by <a href="mailto:laurent.vanni@sophia.inria.fr">Laurent Vanni</a></i>
</body>
</html>

