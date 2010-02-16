var _backgroundColor;

function hideAll() {
	jQuery.noConflict();  
	jQuery(document).ready(function(){
		jQuery("#application").hide("slide");
		jQuery("#documentation").hide("slide");
		jQuery("#contact").hide("slide");
		jQuery("#tFile").hide("slide");
		jQuery("#tEdit").hide("slide");
		jQuery("#tAbout").hide("slide");
		jQuery("#cFile").hide("slide");
		jQuery("#cEdit").hide("slide");
		jQuery("#cAbout").hide("slide");
		jQuery("#iFile").hide("slide");
		jQuery("#rFile").hide("slide");
		jQuery("#dFile").hide("slide");
	});
}

function menuHover(element, menuID) {
	jQuery.noConflict();  
	jQuery(document).ready(function(){
		if(menuID != "#application"){
			jQuery("#application").hide("slide");
		} 
		if(menuID != "#documentation"){
			jQuery("#documentation").hide("slide");
		}
		if(menuID != "#contact"){
			jQuery("#contact").hide("slide");
		}
		if(menuID != "#tFile"){
			jQuery("#tFile").hide("slide");
		} 
		if(menuID != "#tEdit"){
			jQuery("#tEdit").hide("slide");
		}
		if(menuID != "#tAbout"){
			jQuery("#tAbout").hide("slide");
		}
		if(menuID != "#cFile"){
			jQuery("#cFile").hide("slide");
		} 
		if(menuID != "#cEdit"){
			jQuery("#cEdit").hide("slide");
		}
		if(menuID != "#cAbout"){
			jQuery("#cAbout").hide("slide");
		}
		if(menuID != "#iFile"){
			jQuery("#iFile").hide("slide");
		} 
		if(menuID != "#rFile"){
			jQuery("#rFile").hide("slide");
		} 
		if(menuID != "#dFile"){
			jQuery("#dFile").hide("slide");
		} 
		jQuery(menuID).show("slide");
	});

}

