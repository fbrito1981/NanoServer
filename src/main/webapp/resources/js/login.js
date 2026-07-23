$(document).ready(function() {
	$('.localizer').each(function() {
		$(this).on('click', function() {
			var element = $(this);
			if (!element.hasClass('active')) {
				showLoading();
				$('.localizer').each(function() {
					$(this).removeClass('active')
				});
				element.addClass('active');
				var locale = element.attr('id');
				var data = new FormData();
				data.append('encryptedData', encrypt(locale));
				$.ajax({
					url: appCtx + '/localize',
					cache: false,
					contentType: false,
					processData: false,
					method: 'POST',
					data: data,
					success: function(data, textStatus, response) {
						hideLoading();
						if (data.success) {
							var dictionary = decrypt(data.data);
							$('#error_msg_general').val(dictionary['error_msg_general']);
							$('#login_ph_email').attr('placeholder', dictionary['login_ph_email']);
							$('#login_ph_pass').attr('placeholder', dictionary['login_ph_pass']);
							$('#login_btn_enter').val(dictionary['login_btn_enter']);
							$('#login_btn_forgot').html(dictionary['login_btn_forgot']);
						} else {
							console.log(data.message);
							errorMessage();
						}
					},
					error: function(response, textStatus, errorThrown) {
						hideLoading();
						console.log('Error [' + errorThrown + ']: ' + response);
						errorMessage();
					}
				});
			}
		});
	});
	
	checkUserSession();
});

processData = function(data) {
	setCookie('token', data, 24);
};