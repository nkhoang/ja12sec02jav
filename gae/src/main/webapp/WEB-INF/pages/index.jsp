<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title><fmt:message key="webapp.title" /></title>
	<script type="text/javascript" src="<c:url value='/js/jquery-1.4.2.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/jquery.alerts.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/jquery.timer.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/captify.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/jquery.validate.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/jquery.form.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/jquery.tooltip.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/jquery.fancybox-1.3.1.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/jquery.easing-1.3.pack.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/jquery.mousewheel-3.0.2.pack.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/jquery-ui.min.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/jquery.autocomplete.js' />"></script>
	<%@ include file="ItemJS.jsp"%>
	<%@ include file="ItemManagerJS.jsp"%>
	<%@ include file="script.jsp"%>
	<script type="text/javascript" src="<c:url value='/js/structure.js'/>"></script>
	<link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/simple/theme.css'/>" />
	<link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/simple/jquery.alerts.css'/>" />
	<link href="<c:url value='/styles/simple/niceforms-default.css' />" rel='stylesheet' type="text/css" />
	<link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/simple/preloader.css'/>" />
	<link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/simple/jquery.tooltip.css' />" />
	<link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/simple/jquery.fancybox-1.3.1.css' />" />
	<link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/simple/jquery-ui.css' />" />
	<link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/simple/jquery.autocomplete.css' />" />
</head>
<body>
	<div id="body-wrapper">
		<div id="header"></div>
		<div id="menuContainer">
            	<div id="menuButtons">
            		<div class="menuButton home fleft">
            		</div>
					<div class="menuSeperator fleft"></div>
					<div class="menuButton about fleft">
            		</div>
            	</div>
            	<div id="menuIndicator">
            		<div class="mit fleft">
            			<div class="mil fleft"></div>
						<div class="mic fleft">
						</div>
						<div class="mir fleft"></div>
            		</div>
					<div class="mib fleft">
						<div id="indicator-content" class="indicatorTitle home"></div>
					</div>
            	</div>
                <div class="mnt fleft">
                    <div class='mnl fleft'>
                    </div>
                    <div class="mnc fleft">
                    </div>
                    <div class="mnr fleft">
                    </div>
                </div>
                <div class="mnm fleft">
                    <div class='mnl fleft'>
                    </div>
                    <div class="mnc fleft">
                    </div>
                    <div class="mnr fleft">
                    </div>
                </div>
                <div class="mnb fleft">
                    <div class='mnl fleft'>
                    </div>
                    <div class="mnc fleft">
                    </div>
                    <div class="mnr fleft">
                    </div>
                </div>
                <div id="login">
					<div id="loginPanel">
						<spring:eval expression='isAdmin == false' var="showLogin"></spring:eval>
						<c:if test="${showLogin}">
							<a class="login" href="#"><img alt="Login to Chara" title="Login to Chara" src='http://docs.google.com/File?id=d5brrvd_1053rsk33gdr_b' /></a>
						</c:if>
						<security:authorize url="/user/admin">
							<a class="addItem" href="#"><img title="Add a new item to Chara" alt="Add a new item to Chara" src='<c:url value="/images/simple/image_add.png" />' /></a>						
							<a class="clearItems" href="#"><img title="Clear all images" alt="Clear all images" src='<c:url value="/images/simple/clearAll.png" />' /></a>
							<a class="login" href="#"><img alt="Logout Chara" src='http://docs.google.com/File?id=d5brrvd_1054hbrp6bd5_b' /></a>
						</security:authorize>
					</div>
				</div>
            </div>
		<div id="loginContent"></div>
        <div id="leftContent">
            <div id="subNav">
            	<!-- 
                <div class="tab first short active">
                    <div class="snt">
                    </div>
                    <div class="snm">
                        <div class="snl">
                        </div>
                        <div class="snc">
                        </div>
                        <div class="snr">
                        </div>
                    </div>
                    <div class="snb">
                    </div>
                </div>
                 -->
                <div class="tab shirt first last active">
                    <div class="snt">
                    </div>
                    <div class="snm">
                        <div class="snl">
                        </div>
                        <div class="snc">
                        </div>
                        <div class="snr">
                        </div>
                    </div>
                    <div class="snb">
                    </div>
                </div>
            </div>
            <div id="contentContainer">
                <div class="ctt">
                	<div class="ctl"></div>
					<div class="ctc"></div>
					<div class="ctr"></div>
                </div>
				<div class="ctm">
					<div class="ctl"></div>
					<div class="ctc"><div id="itemsContainer"></div></div>
					<div class="ctr"></div>
				</div>
				<div class="ctb">
					<div class="ctl"></div>
					<div class="ctc"></div>
					<div class="ctr"></div>
				</div>
				<div id="marker">
                    	<div id="markerContent">
	                		<div class="mal fleft"></div>
							<div class="mam fleft"></div>
							<div class="mar fleft"></div></div>
							<div id="page_number">
	                        	<div id="number_container">
	                        		<div id="current_page_number"></div>
									<img src="http://lh3.ggpht.com/_4oj_ltkp9pc/S_XYVBipyXI/AAAAAAAAAHw/bzEvOwr9tMs/n_slash.gif" style="vertical-align: middle; margin-top: -7px;"/>
									<div id="total_page_number"></div>
	                        	</div>
	                        </div>
							<div id="pager">
								<div class="pal fleft"><div class="touchArea"></div></div>
								<div class="pam fleft"></div>
								<div class="par fleft"><div class="touchArea"></div>
								<div class="pagerInput"><input type="text" maxlength="2" size="1"/></div>
								<div class="currentPage"><img src="http://lh5.ggpht.com/_4oj_ltkp9pc/S_ZIeNTClPI/AAAAAAAAAIA/l0u2KVRJ-xY/n_2.gif"></div>
							</div>
						</div>
                    </div>
            </div>
        </div>
       </div>
       <div style="clear: both;">&nbsp;</div>
    </body>
</html>