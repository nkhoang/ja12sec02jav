<%@ include file="/common/taglibs.jsp" %>
<html>
<head><title><fmt:message key="phonecard.title"/></title>
    <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/phonecard-form.css' />"/>
    <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/phonecard-layout.css' />"/>
    <style type="text/css">
        body {
            margin: 0;
        }
        body p {
            text-align: center;
        }

        #login-form {
            height: auto;
            width: 300px;
            margin: 0 auto;
            padding: 30px 50px;
            -webkit-box-shadow: 0 0 15px #7CBDFF;
            -moz-box-shadow: 0 0 15px #7CBDFF;
            box-shadow: 0 0 15px #7CBDFF;
            border-radius: 15px 20px 15px 20px / 20px 15px 20px 15px;
            background-color: #FFF;
        }

        #login-form label {
            color: #666666;
            font-size: 120%;
            text-shadow: 0px 1px 2px #D3D3D3;
        }

        #content table {
            height: 70%;
        }


    </style>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js"></script>
</head>
<body>
<div id="content">
    <jsp:include page="header.jsp"/>
    <div class="main">
        <div class="m">
            <p><fmt:message key="phonecard.welcomeMessage">
                <fmt:param><a href="#"><fmt:message key="webapp.domain"/></a></fmt:param>
            </fmt:message>
            </p>
        </div>

        <table class="m">
            <tbody>
            <tr>
                <td>
                    <div id="login-form">
                        <label for="username" title="<fmt:message key="phonecard.login.username" />"><fmt:message
                                key="phonecard.login.username"/>:</label>
                        <input type="text" name="username" id="username" size="25"/>
                        <label for="username" title="<fmt:message key="phonecard.login.password" />"><fmt:message
                                key="phonecard.login.password"/>:</label>
                        <input type="password" name="password" id="password" size="25"/>
                        <input type="button" name="submit" value="<fmt:message key="phonecard.submit" />"/>
                        <br />
                        <div style="padding-top: 5px;">
                            <fmt:message key="phonecard.register" >
                                <fmt:param><a href="<c:url value="/user/register.html"/>">&#273;&#226;y</a></fmt:param>
                            </fmt:message>
                        </div>
                    </div>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
</div>
<jsp:include page="footer.jsp"/>
</body>
</html>