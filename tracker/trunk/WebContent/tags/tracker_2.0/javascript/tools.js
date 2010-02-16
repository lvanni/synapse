function onTR(elementId){
	var tr = document.getElementById(elementId);
	tr.style.background = "#4b71ff";
}

function outTR(elementId){
	var tr = document.getElementById(elementId);
	tr.style.background = "white";
}

function start(){
	document.formCommand.command.value = "start";
}

function stop(){
	document.formCommand.command.value = "stop";
}

function addInvitation(){
	document.formCommand.command.value = "addInvitation";
}