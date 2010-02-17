<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>myMed tracker home page</title>
<link rel="stylesheet" type="text/css" href="css/style.css" />
<link rel="stylesheet" type="text/css" href="css/menu.css" />
<link rel="stylesheet" type="text/css" href="css/tracker.css" />
<script type="text/javascript" src="javascript/jquery/dist/jquery.js"></script>
<script type="text/javascript" src="javascript/processing-js-0.4/processing-0.4.js"></script>
<script type="text/javascript" src="javascript/drag.js"></script>
<script type="text/javascript" src="javascript/time.js"></script>
<script type="text/javascript" src="javascript/menu.js"></script>
<script type="text/javascript" src="javascript/tracker.js"></script>

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
	} else if(request.getParameter("command").equals("removeInvitation")){
		tracker.removeInvitation(request.getParameter("invitation"), request.getParameter("pass"));
	}
	System.out.println(request.getParameter("command"));
}
%> 

</head>

<body onload="showTime('time');" onclick="hideAll();">
	<div align="center">
		<div id="container">
			
			<!-- PANEL TOP -->
			<div id="panel_top" class="panel"></div>
			
			<div style="position:absolute; text-align: left; color:white;">
			<span class="menu" onmouseover="menuHover(this, '#application');" ><img alt="" src="img/mymed.png" style="position: relative; top:3px;"> <span>Application</span></span> 
			<span class="menu" onmouseover="menuHover(this, '#documentation');" ><span> Documentation </span></span> 
			<span class="menu" onmouseover="menuHover(this, '#contact');" ><span> Contact </span></span>
			</div>
			<ul id="application" class="menuContent" >
			<li><img alt="" src="img/mymed.png" style="position: relative; top:3px;">&nbsp;&nbsp;<span onclick="displayWindow('#tracker');">Tracker</span></li>
			<li><img alt="" src="img/mymed.png" style="position: relative; top:3px;">&nbsp;&nbsp;<span onclick="displayWindow('#carpal');">CarPal</span></li>
			<li><img alt="" src="img/mymed.png" style="position: relative; top:3px;">&nbsp;&nbsp;<span onclick="displayWindow('#download');">Download</span></li>
			</ul>
			<ul id="documentation" class="menuContent" style="left:105px;">
			<li><img alt="" src="img/mymed.png" style="position: relative; top:3px;">&nbsp;&nbsp;<span onclick="location.href='http://www-sop.inria.fr/lognet/synapse/synapse.pdf'">Synapse</span></li>
			<li><img alt="" src="img/mymed.png" style="position: relative; top:3px;">&nbsp;&nbsp;<span onclick="location.href='http://www-sop.inria.fr/lognet/synapse/synapse-appendix.pdf'">White Vs Black Box</span></li>
			<li><img alt="" src="img/mymed.png" style="position: relative; top:3px;">&nbsp;&nbsp;<span onclick="location.href='http://www-sop.inria.fr/lognet/MYMED'">myMed</span></li>
			<li><img alt="" src="img/mymed.png" style="position: relative; top:3px;">&nbsp;&nbsp;<span onclick="location.href='http://www-sop.inria.fr/teams/lognet/LOGV4/lognet/uid1.html'">LogNet</span></li>
			</ul>
			<ul id="contact" class="menuContent" style="left:219px;">
			<li><img alt="" src="img/mymed.png" style="position: relative; top:3px;">&nbsp;&nbsp;<a href="mailto:luigi.liquori@sophia.inria.fr">Luigi Liquori</a></li>
			<li><img alt="" src="img/mymed.png" style="position: relative; top:3px;">&nbsp;&nbsp;<a href="mailto:vincenzo.ciancaglini@sophia.inria.fr">Vincenzo Ciancaglini</a></li>
			<li><img alt="" src="img/mymed.png" style="position: relative; top:3px;">&nbsp;&nbsp;<a href="mailto:laurent.vanni@sophia.inria.fr">Laurent Vanni</a></li>
			</ul>
			<span id="time" style="color: #fff6ca;"></span>
			
			<!--TRACKER ICON-->
			<div class="drag" style="background-image: url('img/tracker.png'); text-align: center; width: 80px; height: 100px; margin-left:10px; top:60px;" ondblclick="displayWindow('#tracker');">
			<br /><br /><br /><br /><br />
			<b>Tracker</b>
			</div>
			
			<!--TRACKER ICON-->
			<div class="drag" style="background-image: url('img/carPal.png'); text-align: center; width: 80px; height: 100px; margin-left:10px; top:170px;" ondblclick="displayWindow('#carpal');">
			<br /><br /><br /><br /><br />
			<b>CarPal</b>
			</div>
			
			<!-- PANEL BOTTOM -->
			<div id="panel_bottom" class="panel"></div>
			<div style="position:absolute; bottom:1px; left:0px;">
			<img alt="" src="img/mymedBlack.png" height="22">
			</div>
			<div style="position:absolute; bottom:1px; right:0px;">
			<img alt="" src="img/inria.png" height="22">
			</div>
			
			
			<!--TRACKER IMPL-->
			<div id="tracker" class="drag" <%= request.getParameter("showTracker") != null ? "style='display:block;'" : "" %>>
				<div style="position:absolute; text-align: left;">
					<span class="menu" onmouseover="menuHover(this, '#tFile');" ><img alt="" src="img/mymed.png" style="position: relative; top:3px;"><span> File </span></span> 
					<span class="menu" onmouseover="menuHover(this, '#tEdit');" ><span> Edition </span></span> 
					<span class="menu" onmouseover="menuHover(this, '#tAbout');" ><span> About </span></span>
				</div>
				<ul id="tFile" class="menuContent" >
				<li onclick="location.href='index.jsp?command=start&showTracker=1';"><img alt="" src="img/mymed.png" style="position: relative; top:3px;">&nbsp;&nbsp;<span>Start</span></li>
				<li onclick="location.href='index.jsp?command=stop&showTracker=1';"><img alt="" src="img/mymed.png" style="position: relative; top:3px;">&nbsp;&nbsp;<span>Stop</span></li>
				<li onclick="hideWindow('#tracker');"><img alt="" src="img/mymed.png" style="position: relative; top:3px;">&nbsp;&nbsp;<span>Quit</span></li>
				</ul>
				<ul id="tEdit" class="menuContent" style="left:58px;">
				<li onclick="displayWindow('#addInvitation');"><img alt="" src="img/mymed.png" style="position: relative; top:3px;">&nbsp;&nbsp;<span>add invitations</span></li>
				<li onclick="displayWindow('#removeInvitation');"><img alt="" src="img/mymed.png" style="position: relative; top:3px;">&nbsp;&nbsp;<span>remove invitations</span></li>
				</ul>
				<ul id="tAbout" class="menuContent" style="left:117px;">
				<li onclick="document.getElementById('trackerInfo').style.display='block'; document.getElementById('invitationInfo').style.display='none';"><img alt="" src="img/mymed.png" style="position: relative; top:3px;">&nbsp;&nbsp;<span>tracker</span></li>
				<li onclick="document.getElementById('invitationInfo').style.display='block'; document.getElementById('trackerInfo').style.display='none';"><img alt="" src="img/mymed.png" style="position: relative; top:3px;">&nbsp;&nbsp;<span>invitations</span></li>
				</ul>
				
				<div class="trackerContent">
				<h3>Tracker manage page:</h3>
				<% if(tracker != null){ %>
					Status: <b style="color:green;">Started, Synchronized</b><br />
					Tracker running on : http://cycloid.inria.fr:<%= tracker.getPort() %>
					<br /><br />
					<hr style="width: 515px; margin-left:0px;" />
					<br />
					<div id="trackerInfo">
						<% for (String key : tracker.getPeerSet().keySet()) { %>
							<% if (key.equals("synapse")){ %>
								<b>Control Network</b>
								<br />
								<table id="controlTable" border="1"  cellpadding="10">
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
						<% } %>
					</div>
					<div id="invitationInfo" style="display: none;">
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
					</div>
				<% } else { %>
					Status: <b style="color:red;">Stopped...</b><br />
				<% } %>
				<br />
				</div>
			</div>
			
			<!--CarPal IMPL-->
			<div id="carpal" class="drag" <%= request.getParameter("showCarPal") == "1" ? "style='display:block;'" : "" %>>
				<div style="position:absolute; text-align: left;">
					<span class="menu" onmouseover="menuHover(this, '#cFile');" ><img alt="" src="img/mymed.png" style="position: relative; top:3px;"><span> File </span></span> 
					<span class="menu" onmouseover="menuHover(this, '#cEdit');" ><span> Edition </span></span> 
					<span class="menu" onmouseover="menuHover(this, '#cAbout');" ><span> About </span></span>
				</div>
				<ul id="cFile" class="menuContent" >
				<li onclick="hideWindow('#carpal');"><img alt="" src="img/mymed.png" style="position: relative; top:3px;">&nbsp;&nbsp;<span>Quit</span></li>
				</ul>
				<ul id="cEdit" class="menuContent" style="left:58px;">
				<li onclick="displayWindow('#addInvitation');"><img alt="" src="img/mymed.png" style="position: relative; top:3px;">&nbsp;&nbsp;<span>add invitations</span></li>
				<li onclick="displayWindow('#removeInvitation');"><img alt="" src="img/mymed.png" style="position: relative; top:3px;">&nbsp;&nbsp;<span>remove invitations</span></li>
				</ul>
				<ul id="cAbout" class="menuContent" style="left:117px;">
				<li onclick="document.getElementById('carpalInfo').style.display='block'; document.getElementById('invitationInfo2').style.display='none';"><img alt="" src="img/mymed.png" style="position: relative; top:3px;">&nbsp;&nbsp;<span>CarPal</span></li>
				<li onclick="document.getElementById('invitationInfo2').style.display='block'; document.getElementById('carpalInfo').style.display='none';"><img alt="" src="img/mymed.png" style="position: relative; top:3px;">&nbsp;&nbsp;<span>invitations</span></li>
				</ul>
				
				<div class="trackerContent">
				<h3>CarPal manage page:</h3>
				<% if(tracker != null && tracker.getPeerSet().keySet().size() != 0){ %>
					CarPal is deployed on <%= tracker.getPeerSet().keySet().size() - 1 %> networks<br />
					whith <%= tracker.getPeerSet().get("synapse").size() %> peers.
					<br /><br />
					<hr style="width: 515px; margin-left:0px;" />
					<br />
					<div id="carpalInfo">
					<% for (String key : tracker.getPeerSet().keySet()) { %>
						<br />
						<% if (!key.equals("synapse")){ %>
							<b><%= key %> Network</b>
							<br />
							<table id="controlTable" border="1"  cellpadding="10">
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
					<% } %>
					</div>
					<div id="invitationInfo2" style="display: none;">
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
					</div>
				<% } else { %>
					Status: <b style="color:red;">no user connected...</b><br />
				<% } %>
				<br />
				</div>
			</div>
			
			<!--Download -->
			<div id="download" class="drag">
				<div style="position:absolute; text-align: left;">
					<span class="menu" onmouseover="menuHover(this, '#dFile');" ><img alt="" src="img/mymed.png" style="position: relative; top:3px;"><span> File </span></span> 
				</div>
				<ul id="dFile" class="menuContent" >
					<li onclick="hideWindow('#download');"><img alt="" src="img/mymed.png" style="position: relative; top:3px;">&nbsp;&nbsp;<span>Quit</span></li>
				</ul>
				
				<div class="trackerContent">
				<h4>Download Current version</h4>
				<table border=1 cellpadding="10">
					<tr>
						<td>Name</td><td>x86</td><td>x64</td><td>maemo</td><td>macosx</td>
					</tr>
					<tr>
						<td>EnterpriseNetwork</td>
						<td><a href="dl/enterpriseNetwork_x86.jar"><img src="img/dl.png" width="20" height="20"/></a></td>
						<td><a href="dl/enterpriseNetwork_x64.jar"><img src="img/dl.png" width="20" height="20"/></a></td>
						<td><a href="dl/enterpriseNetwork_maemo.jar"><img src="img/dl.png" width="20" height="20"/></a></td>
						<td><a href="dl/enterpriseNetwork_macosx.jar"><img src="img/dl.png" width="20" height="20"/></a></td>
					</tr>
					<tr>
						<td>StudentNetwork</td>
						<td><a href="dl/studentNetwork_x86.jar"><img src="img/dl.png" width="20" height="20"/></a></td>
						<td><a href="dl/studentNetwork_x64.jar"><img src="img/dl.png" width="20" height="20"/></a></td>
						<td><a href="dl/studentNetwork_maemo.jar"><img src="img/dl.png" width="20" height="20"/></a></td>
						<td><a href="dl/studentNetwork_macosx.jar"><img src="img/dl.png" width="20" height="20"/></a></td>
					</tr>
				</table>
				<br />
				<br />
				<h4>Download Old version</h4>
					<table border=1 cellpadding="10">
						<tr>
							<td>Name</td><td>x86</td><td>x64</td><td>maemo</td><td>macosx</td>
						</tr>
						<tr>
							<td>myConcert</td>
							<td><a href="dl/old/myConcert_x86.jar"><img src="img/dl.png" width="20" height="20"/></a></td>
							<td><a href="dl/old/myConcert_x64.jar"><img src="img/dl.png" width="20" height="20"/></a></td>
							<td><a href="dl/old/myConcert_maemo.jar"><img src="img/dl.png" width="20" height="20"/></a></td>
							<td><a href="dl/old/myConcert_macosx.jar"><img src="img/dl.png" width="20" height="20"/></a></td>
						</tr>
						<tr>
							<td>myFoot</td>
							<td><a href="dl/old/myFoot_x86.jar"><img src="img/dl.png" width="20" height="20"/></a></td>
							<td><a href="dl/old/myFoot_x64.jar"><img src="img/dl.png" width="20" height="20"/></a></td>
							<td><a href="dl/old/myFoot_maemo.jar"><img src="img/dl.png" width="20" height="20"/></a></td>
							<td><a href="dl/old/myFoot_macosx.jar"><img src="img/dl.png" width="20" height="20"/></a></td>
						</tr>
						<tr>
							<td>myTransport</td>
							<td><a href="dl/old/myTransport_x86.jar"><img src="img/dl.png" width="20" height="20"/></a></td>
							<td><a href="dl/old/myTransport_x64.jar"><img src="img/dl.png" width="20" height="20"/></a></td>
							<td><a href="dl/old/myTransport_maemo.jar"><img src="img/dl.png" width="20" height="20"/></a></td>
							<td><a href="dl/old/myTransport_macosx.jar"><img src="img/dl.png" width="20" height="20"/></a></td>
						</tr>
					</table>
					<br />
				</div>
			</div>
			
			<!--AddInvitations IMPL-->
			<div id="addInvitation" class="drag">
				<div style="position:absolute; text-align: left;">
					<span class="menu" onmouseover="menuHover(this, '#iFile');" ><img alt="" src="img/mymed.png" style="position: relative; top:3px;"><span> File </span></span> 
				</div>
				<ul id="iFile" class="menuContent" >
				<li onclick="hideWindow('#addInvitation');"><img alt="" src="img/mymed.png" style="position: relative; top:3px;">&nbsp;&nbsp;<span>Quit</span></li>
				</ul>
				
				<div class="invitationContent">
				<br />
				<form name="formCommand">
					<input type="hidden" name="command" value="addInvitation">
					<table>
					<tr>
						<td>Add invitation to :</td><td><input type="text" name="invitation"></td>
					</tr>
					<tr>
						<td>password :</td><td><input type="text" name="pass"> <input type="submit" value="send"></td>
					</tr>
					</table>
					<br />
				</form>
				</div>
			</div>
			
			<!--removeInvitations IMPL-->
			<div id="removeInvitation" class="drag">
				<div style="position:absolute; text-align: left;">
					<span class="menu" onmouseover="menuHover(this, '#rFile');" ><img alt="" src="img/mymed.png" style="position: relative; top:3px;"><span> File </span></span> 
				</div>
				<ul id="rFile" class="menuContent" >
					<li onclick="hideWindow('#removeInvitation');"><img alt="" src="img/mymed.png" style="position: relative; top:3px;">&nbsp;&nbsp;<span>Quit</span></li>
				</ul>
				
				<div class="invitationContent">
				<br />
				<form name="formCommand">
					<input type="hidden" name="command" value="removeInvitation">
					<table>
					<tr>
						<td>Invitation to remove :</td><td><input type="text" name="invitation"></td>
					</tr>
					<tr>
						<td>password :</td><td><input type="text" name="pass"> <input type="submit" value="send"></td>
					</tr>
					</table>
					<br />
				</form>
				</div>
			</div>

		</div>
	</div>
	<i style="font-size:12px; position:absolute; right:100px; bottom:50px; color: white;" >designed by <a href="mailto:laurent.vanni@sophia.inria.fr">Laurent Vanni</a></i>
</body>

</html>