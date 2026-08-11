function syncNavbarHeight() {
	var navbar = document.querySelector('nav.navbar');
	if (navbar) {
		document.documentElement.style.setProperty('--navbar-height', navbar.offsetHeight + 'px');
	}
}

$(document).ready(function() {
	syncNavbarHeight();
	$(window).on('resize', syncNavbarHeight);

    $('form').submit(function(e) {
		e.preventDefault();
		
		showLoading();
		
		var form = $(this);
		var fields = {};
		var array = form.serializeArray();
		for (var item of array) {
			parseData(fields, item.name, item.value);
		}
		var data = new FormData();
		data.append('encryptedData', encrypt(fields));
		$('input[type="file"]').each(function() {
			var name = $(this).attr('name');
			var files = $(this)[0].files;
			if (files.length > 0) {
				data.append(name, files[0]);
			}
		});
		$.ajax({
			url: form.attr('action'),
			cache: false,
			contentType: false,
			processData: false,
			method: 'POST',
			data: data,
			success: function(data, textStatus, response) {
				hideLoading();
				if (typeof processData === 'function' && data.data !== undefined && data.data != null) {
					processData(decrypt(data.data));
				}
				if (data.success) {
					switch (data.action) {
					case 0:
						successMessage(form, data.message);
						break;
					case 1:
						if (data.message !== undefined && data.message != null && data.message.length > 0) {
							successMessage(form, data.message, data.url, 3, data.data);
						} else {
							window.location.href = appCtx + data.url;
						}
						break;
					}
				} else {
					switch (data.action) {
					case 0:
						errorMessage(form, data.message);
						break;
					case 1:
						postError(data.url, data.message);
						break;
					}
				}
			},
			error: function(response, textStatus, errorThrown) {
				hideLoading();
				console.log('Error [' + errorThrown + ']: ' + response);
				errorMessage(form);
			}
		});
	});
	
	keepSessionAlive();

	$(document).on('click', '.password-toggle-btn', function() {
		var target = $('#' + $(this).data('target'));
		var icon = $(this).find('i');
		if (target.attr('type') === 'password') {
			target.attr('type', 'text');
			icon.removeClass('fa-eye').addClass('fa-eye-slash');
		} else {
			target.attr('type', 'password');
			icon.removeClass('fa-eye-slash').addClass('fa-eye');
		}
	});
});

function toggleSidebar() {
	document.getElementById('sidebarMenu').classList.toggle('show');
}

function parseData(container, name, value) {
	if (name.match(/\[\d+\]\./)) {
		var parentName = name.substring(0, name.indexOf('['));
		var childName = name.substring(name.indexOf('].') + 2);
		var index = parseInt(name.replace(parentName + '[', '').replace('].' + childName, ''));
		var newElement = false;
		var subContainer = container[parentName];
		if (subContainer === undefined || (!Array.isArray(subContainer) && index == 1)) {
			newElement = true;
			subContainer = {};
		} else if (Array.isArray(subContainer)) {
			subContainer = subContainer[index];
			if (subContainer === undefined) {
				newElement = true;
				subContainer = {};
			}
		}
		setData(subContainer, childName, value);
		if (newElement) {
			setData(container, parentName, subContainer);
		}
	} else {
		setData(container, name, value);
	}
}

function setData(container, name, value) {
	if (container[name] === undefined) {
		container[name] = value;
	} else {
		var list = container[name];
		if (Array.isArray(list)) {
			list.push(value);
		} else {
			list = [list, value];
		}
		container[name] = list;
	}
}

function postEncryptedData(url, encryptedData) {
	var form = $('<form method="POST"></from>');
	form.attr('action', appCtx + url);
	var input = $('<input type="hidden" name="encryptedData" />');
	input.val(encryptedData);
	form.append(input);
	$('body').append(form);
	form.submit();
}

function postError(url, error) {
	var form = $('<form method="POST"></from>');
	form.attr('action', appCtx + url);
	var input = $('<input type="hidden" name="error" />');
	input.val(error);
	form.append(input);
	$('body').append(form);
	form.submit();
}

function showLoading() {
	clearMessage();
	hideLoading();
	$('<div class="modal" id="loadingMask" data-backdrop="static" data-keyboard="false"><div class="spinner"></div></div>').modal();
}

function hideLoading() {
	$('#loadingMask').modal('hide');
	$('#loadingMask').remove();
}

function setCookie(name, value, hours) {
    var expires = '';
    
    if (hours) {
        var date = new Date();
		var time = date.getTime() + hours * 60 * 60 * 1000; // + hh * mm * ss * ms
        date.setTime(time);
        expires = '; expires=' + date.toUTCString();
    }
    
    name = 'nano.server.' + name;
    
    document.cookie = name + '=' + (value || '')  + expires + '; path=/';
}

function getCookie(name) {
    var nameEQ = 'nano.server.' + name + '=';
    var ca = document.cookie.split(';');
    
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
        	c = c.substring(1, c.length);
        }
        if (c.indexOf(nameEQ) == 0) {
        	return c.substring(nameEQ.length, c.length);
        }
    }
    
    return null;
}

function removeCookie(name) {   
	document.cookie = 'nano.server.' + name + '=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/';  
}

function clearMessage() {
	$('.alert').remove();
}

function errorMessage(form, message) {
	showMessage(form, 'alert-danger', false, message);
}

function successMessage(form, message, url, seconds, data) {
	if (url !== undefined && seconds !== undefined) {
		showMessage(form, 'alert-success', true, message);
		setTimeout(function() {
			if (data !== undefined && data != null) {
				postEncryptedData(url, data);
			} else {
				window.location.href = appCtx + url;
			}
		}, seconds * 1000);
	} else {
		showMessage(form, 'alert-success', false, message);
	}
}

function infoMessage(form, message) {
	showMessage(form, 'alert-info', false, message);
}

function warningMessage(form, message) {
	showMessage(form, 'alert-warning', false, message);
}

function showMessage(form, type, showSpinner, message) {
	if (message === undefined || message == null || message.length == 0) {
		message = (generalErrorMessage !== undefined) ? generalErrorMessage : $('#error_msg_general').val();
	}
	clearMessage();
	var button = '<button type="button" class="close" data-dismiss="alert" aria-label="Close">' +
		'<span aria-hidden="true">&times;</span></button>';
	if (showSpinner) {
		button = '<button type="button" class="close" disabled>' +
		    '<div class="spinner-border spinner-border-sm align-baseline"></div></button>';
	}
	var alert = $('<div class="alert ' + type + ' alert-dismissible fade show" role="alert">' +
			message + button + '</div>');
	form.before(alert);
	var modalId = form.attr('id').replace("form", "modal");
	$('#' + modalId).scrollTop(0);
}

function checkUserSession() {
	var token = getCookie('token');
	if (token !== undefined && token != null && token.length > 0) {
		showLoading();
		setTimeout(function() {
			window.location.href = appCtx + '/dashboard';
		}, 2000);
	}
}

function keepSessionAlive() {
	setInterval(updateSession, 300000);
}

function clearSession() {
	clearInterval(updateSession);
}

function updateSession() {
	var token = getCookie('token');
	if (token !== undefined && token != null && token.length > 0) {
		setCookie('token', token, 24);
	} else {
		clearSession();
	}
}

function loadUrlImage(imageUrl, imageId) {
	var canvas = document.getElementById(imageId + '_img');
	var img = new Image();
	img.onload = function() {
		canvas.width = img.width;
		canvas.height = img.height;
		
		var ctx = canvas.getContext('2d');
		ctx.drawImage(img, 0, 0, img.width, img.height);
		
		$('#' + imageId + '_img').parent().find('.fa-camera-retro').addClass('d-none');
	};
	img.src = imageUrl;
}

function loadImage(fileInput, imageId) {
	if (fileInput.files && fileInput.files[0]) {
		var name = fileInput.files[0].name;
		var canvas = document.getElementById(imageId + '_img');
		var realCanvas = document.getElementById(imageId + '_img_real');
		var reader = new FileReader();
		reader.onload = function(e) {
			var img = new Image();
			img.onload = function() {
				var width = parseFloat($('#' + imageId + '_img').css('width').replace('px', ''));
				var height = parseFloat($('#' + imageId + '_img').css('height').replace('px', ''));
				var realWidth = (realCanvas) ? parseFloat($('#' + imageId + '_img_real').parent().css('width').replace('px', '')) : width;
				var realHeight = (realCanvas) ? parseFloat($('#' + imageId + '_img_real').parent().css('height').replace('px', '')) : height;
				
				var imgWidth = img.width;
				var imgHeight = img.height;

				if (imgWidth <= realWidth && imgHeight <= realHeight && (name.endsWith('.png') || name.endsWith('jpg'))) {
					if (imgWidth > imgHeight) {
						imgWidth = height * imgWidth / imgHeight;
						imgHeight = height;
					} else {
						imgHeight = width * imgHeight / imgWidth;
						imgWidth = width;
					}

					canvas.width = width;
					canvas.height = height;
					
					var imgLeft = (width - imgWidth) / 2;
					var imgTop = (height - imgHeight) / 2;
					
					var ctx = canvas.getContext('2d');
					ctx.drawImage(img, imgLeft, imgTop, imgWidth, imgHeight);
					
					$('#' + imageId + '_img').parent().find('.fa-camera-retro').addClass('d-none');
				} else {
					$('#' + imageId).val('');
					var formId = imageId.substring(0, imageId.indexOf("_")) + "_form";
					var message = dataTableMsgImageFormat.replace("ImageWidth", realWidth).replace("ImageHeight", realHeight);
					errorMessage($('#' + formId), message);
				}
			};
			img.src = e.target.result;
		};
		reader.readAsDataURL(fileInput.files[0]);
	} else {
		clearImage(imageId);
	}
}

function setImageLegend(imageId) {
	var realCanvas = document.getElementById(imageId + '_img_real');

	var width = parseInt($('#' + imageId + '_img').css('width').replace('px', ''));
	var height = parseInt($('#' + imageId + '_img').css('height').replace('px', ''));
	
	if (realCanvas) {
		var width = parseFloat($('#' + imageId + '_img_real').parent().css('width').replace('px', ''));
		var height = parseFloat($('#' + imageId + '_img_real').parent().css('height').replace('px', ''));
	}
	
	var legend = $('<small class="text-muted d-block"></small>');
	legend.text(dataTableMaximumSize + ' ' + width + 'x' + height);
	
	$('#' + imageId).before(legend);
}

function clearImage(imageId) {
	$('#' + imageId).val('');
	var canvas = document.getElementById(imageId + '_img');
	var ctx = canvas.getContext('2d');
	ctx.clearRect(0, 0, canvas.width, canvas.height);

	$('#' + imageId + '_img').parent().find('.fa-camera-retro').removeClass('d-none');
}

function parseDate(timestamp, locale) {
	if (timestamp !== undefined && timestamp != null) {
		var date = new Date(timestamp);
		var dateString = date.toLocaleDateString(locale);
		var timeString = date.toLocaleTimeString(locale);
		
		return  dateString + " " + timeString; 
	} else {
		return '';
	}
}

function setupDatetimepicker(pickerId, format, locale, date, timepicker) {
	var lang = (locale.indexOf('-') >= 0) ? locale.split('-')[0] : locale;
	
	$.datetimepicker.setLocale(lang);
	
	$('#' + pickerId).datetimepicker({
		format: format,
		timepicker: (timepicker === undefined) ? true : timepicker,
		onChangeDateTime: function(dp) {
			if (dp != null) {
				$('#' + pickerId + '_value').val(dp.getTime());
			} else {
				$('#' + pickerId + '_value').val("");
			}
		}
	});
	
	if (date !== undefined && date != null) {
		var mDate = new Date(date);
		$('#' + pickerId).datetimepicker({
			value: mDate,
		});
		$('#' + pickerId + '_value').val(mDate.getTime());
	}
}

function setupAutocomplete(elementId) {
	$('#' + elementId).empty()
	var id = '#' + elementId + '_autocomplete';
	var input = $(id + ' input');
	input.val("");
	input.focus(function() {
		$(id + ' .autocomplete').collapse('show');
	});
	input.blur(function() {
		setTimeout(function() {
			$(id + ' .autocomplete').collapse('hide');
		}, 100);
	});
	input.keyup(function() {
		var text = $(this).val().toLowerCase();
		$(id + ' .autocomplete .list-group button').each(function(index, item) {
			var button = $(item);
			if (button.html().toLowerCase().includes(text)) {
				button.removeClass('d-none');
			} else {
				button.addClass('d-none');
			}
		});
	});
}

function clearAutocomplete(elementId) {
	$('#' + elementId + '_autocomplete input').val("");
	$('#' + elementId + '_autocomplete .autocomplete').collapse('hide');
	$('#' + elementId + '_autocomplete .autocomplete .list-group button').removeClass('d-none');
}

function addAutocompleteItem(elementId, value, name) {
	var exists = false;
	$('#' + elementId + ' option').each(function(index, item) {
		if (value == $(item).val()) {
			exists = true;
			return;
		}
	});
	if (!exists) {
		var option = $('<option value="' + value + '">' + name + '</option>');
		$('#' + elementId).append(option);
	}
	clearAutocomplete(elementId);
	$('#' + elementId + ' option').prop('selected', true);
}

function removeAutocompleteItems(elementId) {
	var selectedValues = $('#' + elementId).val();
	$('#' + elementId + ' option').each(function(inex, item) {
		var option = $(item);
		if (selectedValues.includes(option.val())) {
			option.remove();
		}
	});
	$('#' + elementId + ' option').prop('selected', true);
}
