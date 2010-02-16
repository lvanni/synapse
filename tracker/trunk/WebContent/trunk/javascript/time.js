function showTime(id) {
	date = new Date;
	year = date.getFullYear();
	month = date.getMonth();
	months = new Array('Jan', 'Feb', 'Mar', 'Apr', 'May',
			'Jun', 'Jul', 'Aug', 'Sep', 'Oct',
			'Nov', 'Dec');
	j = date.getDate();
	jour = date.getDay();
	jours = new Array('Sun', 'Mon', 'Tue', 'Wed', 'Thu',
			'Fri', 'Sat');
	h = date.getHours();
	if (h < 10) {
		h = "0" + h;
	}
	m = date.getMinutes();
	if (m < 10) {
		m = "0" + m;
	}
	s = date.getSeconds();
	if (s < 10) {
		s = "0" + s;
	}
	resultat = jours[jour] + ' ' + months[month] + ' ' + j
			+ ', ' + h + ':' + m + ':' + s;
	document.getElementById(id).innerHTML = resultat;
	setTimeout('showTime("' + id + '");', '1000');
	return true;
}