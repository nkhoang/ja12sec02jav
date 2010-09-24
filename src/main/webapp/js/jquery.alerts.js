// jQuery Alert Dialogs Plugin
//
// Version 1.1
//
// Cory S.N. LaViska
// A Beautiful Site (http://abeautifulsite.net/)
// 14 May 2009
//
// Visit http://abeautifulsite.net/notebook/87 for more information
//
// Usage:
//		jAlert( message, [title, callback] )
//		jConfirm( message, [title, callback] )
//		jPrompt( message, [value, title, callback] )
// 
// History:
//
//		1.00 - Released (29 December 2008)
//
//		1.01 - Fixed bug where unbinding would destroy all resize events
//
// License:
// 
// This plugin is dual-licensed under the GNU General Public License and the MIT License and
// is copyright 2008 A Beautiful Site, LLC. 
//
(function($) {
	
	$.alerts = {
		
		// These properties can be read/written by accessing $.alerts.propertyName from your scripts at any time
		
		verticalOffset: -75,                // vertical offset of the dialog from center screen, in pixels
		horizontalOffset: 0,                // horizontal offset of the dialog from center screen, in pixels/
		repositionOnResize: false,           // re-centers the dialog on window resize
		overlayOpacity: 0.6,                // transparency level of overlay
		overlayColor: '#000',               // base color of overlay
		draggable: false,                    // make the dialogs draggable (requires UI Draggables plugin)
		okButton: '&nbsp;OK&nbsp;',         // text for the OK button
		cancelButton: '&nbsp;Cancel&nbsp;', // text for the Cancel button
		dialogClass: null,                  // if specified, this class will be applied to all dialogs
		effectDelay: 700,
		
		// Public methods
		
		alert: function(message, title, callback) {
			if( title == null ) title = 'Alert';
			$.alerts._show(title, message, null, 'alert', function(result) {
				if( callback ) callback(result);
			});
		},
		
		confirm: function(message, title, callback) {
			if( title == null ) title = 'Confirm';
			$.alerts._show(title, message, null, 'confirm', function(result) {
				if( callback ) callback(result);
			});
		},
		// this function is to show login
		showURL: function(url, title, callback){
			if (title == null) title = 'Login';
			$.alerts._showURL(url, title, function(){
				if (callback) callback();
			});
		},
		
		// this function is to show login
		showTabURL: function(url, title){
			if (title == null) title = 'Login';
			$.alerts._showTabURL(url, title);
		},
			
		prompt: function(message, value, title, callback) {
			if( title == null ) title = 'Prompt';
			$.alerts._show(title, message, value, 'prompt', function(result) {
				if( callback ) callback(result);
			});
		},
		
		showLoading: function(isLoading){
			var $popup_content = $('#popup_content');			

			if (isLoading){
				$popup_content.empty();
				$popup_content.addClass('loading');	
			}else {
				$popup_content.removeClass('loading');
			}
			
		},
		
		updateContent: function(content){
			$('#popup_content').html(content);
		},
		
		close: function(){
			$.alerts._hide();
		},
		
		reposition: function(){
			$.alerts._reposition($('#container').outerWidth(), $('#container').outerHeight());
			$.alerts._maintainPosition(true);
			
		    existingLoadEvent();
		    NFInit();
		},
		
		_showURL: function(url, title, callback){
			$('body').css('overflow','hidden');

			$.alerts._hide();
			$.alerts._overlay('show');
			
			var type= "login";
			
			var $popup_container = $('<div id="popup_container" />');
			var $popup_title = $('<h1 id="popup_title"></h1>');
			var $popup_content = $('<div id="popup_content" />');
			
			$popup_container.append($popup_title, $popup_content).hide();
			
			$("BODY").append($popup_container);
			
			// IE6 Fix
			var pos = ($.browser.msie && parseInt($.browser.version) <= 6 ) ? 'absolute' : 'fixed'; 
			
			$("#popup_container").css({
				position: pos,
				zIndex: 99998,
				padding: 0,
				margin: 0
			});
			
			$popup_container.addClass(type);
			$popup_title.text(title).addClass(type);
			$popup_content.addClass(type);
			
			$.alerts._reposition();
			$.alerts._maintainPosition(true);
			
			$popup_container.show();
			$popup_content.addClass('loading');
			// load dynamic content
			$popup_content.load(url, function(){
				$.alerts._reposition($('#container').outerWidth(), $('#container').outerHeight());
				$.alerts._maintainPosition(true);
				// niceform
			    existingLoadEvent();
				NFInit();
				    
				$popup_content.removeClass('loading');
			});
			
			// handle enter keypress.
			$("#popup_ok").focus().keypress( function(e) {
				if( e.keyCode == 13 || e.keyCode == 27 ) $("#popup_ok").trigger('click');
			});
			
			$("#popup_ok").live('click', function() {
				$.alerts._hide();
				
				$('body').css('overflow','auto');

				callback(true);
			});
			
		},
		
		_showTabURL: function(url, title, callback){
			$('body').css('overflow','hidden');

			$.alerts._hide();
			$.alerts._overlay('show');
			
			var type= "login";
			
			var $popup_container = $('<div id="popup_container" />');
			var $popup_title = $('<h1 id="popup_title"></h1>');
			var $popup_content = $('<div id="popup_content"><div id="tabs"><ul><li><a href="' + url[0] + '">Item Registration</a></li><li><a href="'+ url[1] + '">Backup</a></li></ul></div></div>');
			
			$popup_container.append($popup_title, $popup_content).hide();
			
			$("BODY").append($popup_container);
			
			// IE6 Fix
			var pos = ($.browser.msie && parseInt($.browser.version) <= 6 ) ? 'absolute' : 'fixed'; 
			
			$("#popup_container").css({
				position: pos,
				zIndex: 99998,
				padding: 0,
				margin: 0
			});
			
			$popup_container.addClass(type);
			$popup_title.text(title).addClass(type);
			$popup_content.addClass(type);
			
			$("#tabs").tabs({
					ajaxOptions: {
						error: function(xhr, status, index, anchor) {
							$(anchor.hash).html("Couldn't load this tab. We'll try to fix this as soon as possible. If this wouldn't be a demo.");
						}
					},
					spinner: 'Loading...',
					load: function(event, ui){
						$.alerts._reposition($('#container').outerWidth(), $('#container').outerHeight());
						$.alerts._maintainPosition(true);
						// niceform
					    existingLoadEvent();
						NFInit();
						
						// replace the normal submit event.
				        $('#itemForm').live('submit' ,function(){
				            var $itemForm = $(this);
				            $.alerts._reposition($('#container').outerWidth(), $('#container').outerHeight());
				            if ($(this).valid()){
				                if($itemForm.hasClass('loading')){
				                	return false;
				                }else {
						           $itemForm.ajaxSubmit({
						                beforeSubmit: function(formData, jqForm, options){
						                	$itemForm.addClass('loading');
						                	$('#itemLoading').show();
						                },
										success: function(responseText, statusText, xhr, $form){
											// update message body.
						                	$itemForm.parents('div.ui-tabs-panel').html(responseText);
											// reposition content container.
											$.alerts.reposition();
						   				},
						   				complete: function(){
							   				$itemForm.removeClass('loading');
							   			},
										url: '/item/register.html',
						                type: 'post'
						            });
				                }
				            }
				            return false;
				        });
						
					}
			});
			
			$.alerts._reposition();
			$.alerts._maintainPosition(true);
			
			$popup_container.show();
			
		},
		// Private methods
		_show: function(title, msg, value, type, callback) {
			
			$.alerts._hide();
			$.alerts._overlay('show');
			
			var $popup_container = $('<div id="popup_container" />');
			var $popup_title = $('<h1 id="popup_title"></h1>');
			var $popup_content = $('<div id="popup_content" />');
			var $popup_message = $('<div id="popup_message"></div>');
			
			$popup_container.append($popup_title, $popup_content.append($popup_message)).hide();
			
			$("BODY").append($popup_container);
			
			if( $.alerts.dialogClass ) $("#popup_container").addClass($.alerts.dialogClass);
			
			// IE6 Fix
			var pos = ($.browser.msie && parseInt($.browser.version) <= 6 ) ? 'absolute' : 'fixed'; 
			
			$("#popup_container").css({
				position: pos,
				zIndex: 99999,
				padding: 0,
				margin: 0
			});
			
			$popup_title.text(title);
			$popup_content.addClass(type);
			$popup_message.text(msg);
			$popup_message.html( $popup_message.text().replace(/\n/g, '<br />') );
			
			$("#popup_container").css({
				minWidth: $popup_container.outerWidth(),
				maxWidth: $popup_container.outerWidth()
			});
			
			$.alerts._reposition();
			$.alerts._maintainPosition(true);
			
			$(document).oneTime($.alerts.effectDelay, function(){
				$popup_container.show();
			});
			
			
			switch( type ) {
				case 'alert':
					$("#popup_message").after('<div id="popup_panel"><input type="button" value="' + $.alerts.okButton + '" id="popup_ok" /></div>');
					$("#popup_ok").click( function() {
						$.alerts._hide();
						callback(true);
					});
					$("#popup_ok").focus().keypress( function(e) {
						if( e.keyCode == 13 || e.keyCode == 27 ) $("#popup_ok").trigger('click');
					});
				break;
				case 'confirm':
					$("#popup_message").after('<div id="popup_panel"><input type="button" value="' + $.alerts.okButton + '" id="popup_ok" /> <input type="button" value="' + $.alerts.cancelButton + '" id="popup_cancel" /></div>');
					$("#popup_ok").click( function() {
						$.alerts._hide();
						if( callback ) callback(true);
					});
					$("#popup_cancel").click( function() {
						$.alerts._hide();
						if( callback ) callback(false);
					});
					$("#popup_ok").focus();
					$("#popup_ok, #popup_cancel").keypress( function(e) {
						if( e.keyCode == 13 ) $("#popup_ok").trigger('click');
						if( e.keyCode == 27 ) $("#popup_cancel").trigger('click');
					});
				break;
				case 'prompt':
					$("#popup_message").append('<br /><input type="text" size="30" id="popup_prompt" />').after('<div id="popup_panel"><input type="button" value="' + $.alerts.okButton + '" id="popup_ok" /> <input type="button" value="' + $.alerts.cancelButton + '" id="popup_cancel" /></div>');
					$("#popup_prompt").width( $("#popup_message").width() );
					$("#popup_ok").click( function() {
						var val = $("#popup_prompt").val();
						$.alerts._hide();
						if( callback ) callback( val );
					});
					$("#popup_cancel").click( function() {
						$.alerts._hide();
						if( callback ) callback( null );
					});
					$("#popup_prompt, #popup_ok, #popup_cancel").keypress( function(e) {
						if( e.keyCode == 13 ) $("#popup_ok").trigger('click');
						if( e.keyCode == 27 ) $("#popup_cancel").trigger('click');
					});
					if( value ) $("#popup_prompt").val(value);
					$("#popup_prompt").focus().select();
				break;
			}
			
			// Make draggable
			if( $.alerts.draggable ) {
				try {
					$("#popup_container").draggable({ handle: $("#popup_title") });
					$("#popup_title").css({ cursor: 'move' });
				} catch(e) { /* requires jQuery UI draggables */ }
			}
		},
		
		_hide: function() {
			$("#popup_container").remove();
			$.alerts._overlay('hide');
			$.alerts._maintainPosition(false);
		},
		
		_overlay: function(status) {
			switch( status ) {
				case 'show':
					$.alerts._overlay('hide');
					var $overlay = $('<div id="popup_overlay"></div>').hide()
					$("BODY").append($overlay);
					$("#popup_overlay").css({
						position: 'absolute',
						zIndex: 99998,
						top: '0px',
						left: '0px',
						width: $(window).width(),
						height: $(window).height(),
						background: $.alerts.overlayColor,
						opacity: $.alerts.overlayOpacity
					});
					$overlay.show()
					break;
				case 'hide':
					$("#popup_overlay").remove();
				break;
			}
		},
		
		_reposition: function(w,h) {
			var containerH = $("#popup_container").outerHeight();
			var containerW = $("#popup_container").outerWidth();
			
			if (w > containerW){
	 			var left = (($(window).width() / 2) - (w / 2)) + $.alerts.horizontalOffset;
			}else {
	 			var left = (($(window).width() / 2) - (containerW / 2)) + $.alerts.horizontalOffset;

			}
			
			if (h > containerH){
 				var top = (($(window).height() / 2) - (h / 2)) + $.alerts.verticalOffset;

			}else {
				var top = (($(window).height() / 2) - (containerH / 2)) + $.alerts.verticalOffset;

			}
			
			
			if( top < 0 ) top = 0;
			if( left < 0 ) left = 0;
			
			// IE6 fix
			if( $.browser.msie && parseInt($.browser.version) <= 6 ) top = top + $(window).scrollTop();
			
			$("#popup_container").css({
				top: top + 'px',
				left: left + 'px'
			});
			$("#popup_overlay").height( $(document).height() );
		},
		
		_maintainPosition: function(status) {
			if( $.alerts.repositionOnResize ) {
				switch(status) {
					case true:
						$(window).bind('resize', $.alerts._reposition);
					break;
					case false:
						$(window).unbind('resize', $.alerts._reposition);
					break;
				}
			}
		}
		
	}
	
	// Shortuct functions
	jAlert = function(message, title, callback) {
		$.alerts.alert(message, title, callback);
	}
	
	jConfirm = function(message, title, callback) {
		$.alerts.confirm(message, title, callback);
	};
	
	jshowURL = function (url, title, callback){
		$.alerts.showURL(url, title, callback);
	};
	
	jshowTabURL = function (url, title){
		$.alerts.showTabURL(url, title);
	};
		
	jPrompt = function(message, value, title, callback) {
		$.alerts.prompt(message, value, title, callback);
	};
	
})(jQuery);