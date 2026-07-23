$(document).ready(function() {
	$('#newPass').keyup(checkNewPassStrength);
	$('#confirmPass').keyup(checkConfirmPassMatch);
	validatePass('', '');
});

function checkNewPassStrength() {
	var newPass = $('#newPass');
	var newPassStatus = $('#newPassStatus');

	if (newPass.val().length > 0) {
		switch (getPassStrength(newPass.val())) {
		case 2:
			newPassStatus.removeClass('weak');
			newPassStatus.removeClass('not-strong');
			newPassStatus.addClass('strong');
			break;
		case 1:
			newPassStatus.removeClass('weak');
			newPassStatus.addClass('not-strong');
			newPassStatus.removeClass('strong');
	        break;
		case 0:
		default:
			newPassStatus.addClass('weak');
			newPassStatus.removeClass('not-strong');
			newPassStatus.removeClass('strong');
		    break;
		}
	}
}

function checkConfirmPassMatch() {
	var newPass = $('#newPass');
	var confirmPass = $('#confirmPass');
	var confirmPassStatus = $('#confirmPassStatus');
	
	if (newPass.val().length > 0 && confirmPass.val().length > 0) {
		if (newPass.val() === confirmPass.val()) {
			confirmPassStatus.removeClass('weak');
			confirmPassStatus.addClass('strong');
	    } else {
	    	confirmPassStatus.addClass('weak');
	    	confirmPassStatus.removeClass('strong');
	    }
		
		validatePass(newPass.val(), confirmPass.val());
	}
}

function validatePass(newPass, confirmPass) {
	if (getPassStrength(newPass) >= 1 && newPass == confirmPass) {
		$('#submitPass').prop('disabled', false);
	} else {
		$('#submitPass').prop('disabled', true);
	}
}

function getPassStrength(pass) {
	var strongRegex = new RegExp('^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%\^&\*])(?=.{8,})');
	var mediumRegex = new RegExp('^(((?=.*[a-z])(?=.*[A-Z]))|((?=.*[a-z])(?=.*[0-9]))|((?=.*[A-Z])(?=.*[0-9])))(?=.{6,})');

	if (strongRegex.test(pass)) {
        return 2;
    } else if (mediumRegex.test(pass)) {
        return 1;
    } else {
        return 0;
    }
}