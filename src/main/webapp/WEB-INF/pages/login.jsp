<%@ include file="/common/taglibs.jsp"%>
<div>
<!-- Change the var name so it will not conflict the same name in index.jsp page -->
<spring:eval expression='isAdmin == false' var="isClientMode"></spring:eval>
<!-- Defined css for login page - specific for each page - apply to niceform -->
<style type="text/css">
	dl {clear:both;}
	dt {float:left; text-align:right; width:70px; margin:0 10px 10px 0;}
	dd {float:left; width:170px; margin:0 0 10px 0;}
	fieldset {width: 250px;}
	#container {width:300px; margin:0 auto;}
</style>
<c:if test="${isClientMode}">
	<form id="loginForm" action="" class="niceform">
		<script type="text/javascript">
			var urlVal = '<c:url value="/login_check.html" />';
	
			$(function(){
				$('#loginForm').submit(function(){
					var $form = $(this);
					if ($form.hasClass('loading')){
					}else {
					
						$(this).ajaxSubmit({
							url: urlVal,
							type: 'post',
							beforeSend: function(){
								$.alerts.showLoading(true);
								// add class to make sure that only one kind of this request being made.
								$form.addClass('loading');
							},
							success: function(responseText, statusText, xhr, $form){
								$.alerts.updateContent(responseText);
								//$('#popup_content').html(responseText);
							
								$.alerts.reposition();
			 				    	
							   if (isAdmin){
								   $('a.login').html('<img src="http://docs.google.com/File?id=d5brrvd_1054hbrp6bd5_b" />');
								   $('#loginPanel').prepend($('<a class="addItem" href="#"><img src="images/simple/image_add.png" /></a>'));
								   $('#loginPanel').prepend($('<a class="clearItems" href="#"><img src="images/simple/clearAll.png" /></a>'));
							   }
							},
							complete: function(){
								$.alerts.showLoading(false);
								// remove  class when done.
								$form.removeClass('loading');
							}
						});		
					}
					return false;		
				});
			});
		</script>
		<fieldset>
			<dl>
				<dt><fmt:message key="login.loginForm.username" /></dt>
				<dd><input type="text" id="loginUsername" name="j_username" maxlength="10" /></dd>
			</dl>
			<dl>
				<dt><fmt:message key="login.loginForm.password" /> </dt>
				<dd><input type="password" id="loginPassword" name="j_password" maxlength="10" /></dd>
			</dl>
			<dl>
				<c:if test="${param.error == 'true'}">
					<fmt:message key="errors.password.mismatch" />
				</c:if>
			</dl>
			<dl>
				<dt></dt>
				<dd><input type="submit" value="<fmt:message key="login.loginForm.button.login" />" title="<fmt:message key="login.loginForm.button.login" />"
	      			  name="loginBtn" id="loginBtn" />	
	      			<input type="button" id="popup_ok" value="Close" />
			    </dd>
			</dl>
		</fieldset>
	</form>
</c:if>

<!-- Login successfully -->
<security:authorize url="/user/admin">
	<script type="text/javascript">
		$('#logoutForm').submit(function(){
			var $form = $(this);
			if ($form.hasClass('loading')){
			}else {
				$(this).ajaxSubmit({
					type: 'post',
					beforeSend: function(){
						$.alerts.showLoading(true);
						$form.addClass('loading');
					},
					success: function(responseText, statusText, xhr, $form){
						$.alerts.close();
						
						$('a.login').html('<img src="http://docs.google.com/File?id=d5brrvd_1053rsk33gdr_b" />');
						// remove addItem
						$('a.addItem').remove();
						$('a.clearItems').remove();
						// reload page.
						manager.loadItems();
						isAdmin = false;
						$('body').css('overflow', 'auto');
					},
					complete: function(){
						$.alerts.showLoading(false);
						$form.removeClass('loading');
					}
				});
			}
			return false;
		});
	</script>
	<form action="<c:url value='/logout.html' />" id="logoutForm" class="niceform">
		<fmt:message key="login.loginForm.welcome"></fmt:message>
		<b><c:out value="${currentUser.firstName}" /></b>
		<br/>
		<br/>
		<input type="button" id="popup_ok" value="Close" />&nbsp;
		<input type="submit" value="Logout Me" />
		<script type="text/javascript">
			 isAdmin = true;
		</script>
	</form>
</security:authorize>
</div>




