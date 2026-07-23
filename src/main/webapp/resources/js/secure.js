var keys = [ 'ZrYd+p^kg4jpeKSb', '-hZJSdUtUJ2Ys_fK', 'MT?=Lt&p!P!bBG5^', '$sXjYC6vvU5NEF-@', 'mz5FUuA8%hqYPj6*', 'WHsHDQsthCTrw!9t', 'WLma58Jbq?cp5gXd', 'Vb66E+t2DDsE&TnB', 'cb$54xtc^WLA$zg' ];

function pickKeyIndex() {
	var date = new Date();
	var sDate = String(date.getFullYear());
	sDate += String(date.getMonth() + 1);
	sDate += String(date.getDate());
	sDate += String(date.getHours());
	sDate += String(date.getMinutes());
	
	while (sDate.length > 1) {
		var total = 0;
		for (var i = 0; i < sDate.length; i++) {
			total += parseInt(sDate[i]);
		}
		sDate = String(total);
	}
	
	var index = 9 - parseInt(sDate);
	
	return index;
}

function encrypt(object) {
	var k = 0;
	var keyIndex = pickKeyIndex();
	var key = keys[keyIndex];
	var value = JSON.stringify(object);
	var encrypted = new Array();
	for (var i = 0; i < value.length; i++) {
		if (k == key.length) {
			k = 0;
		}
		var valueChar = value.charCodeAt(i);
		var keyChar = key.charCodeAt(k);
		encrypted.push(valueChar ^ keyChar);
		k++;
	}
	var reversed = encrypted.reverse();
	return keyIndex + reversed.join(key.charAt(1));
}

function decrypt(value) {
	var object = null;
	if (value.length > 0) {
		var k = 0;
		var keyIndex = parseInt(value.substring(0, 1));
		value = value.substring(1);
		var key = keys[keyIndex];
		var splited = value.split(key.charAt(1));
		var array = new Array();
		for (var i = 0; i < splited.length; i++) {
			array.push(parseInt(splited[i]));
		}
		var encrypted = array.reverse();
		var decrypted = "";
		for (var i = 0; i < encrypted.length; i++) {
			if (k == key.length) {
				k = 0;
			}
			var encryptedChar = encrypted[i];
			var keyChar = key.charCodeAt(k);
			decrypted += String.fromCharCode(encryptedChar ^ keyChar);
			k++;
		}
		object = JSON.parse(decrypted)
	}
	return object;
}
