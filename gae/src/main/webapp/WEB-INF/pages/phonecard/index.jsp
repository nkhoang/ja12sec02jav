<%@ include file="/common/taglibs.jsp" %>
<html>
<head><title><fmt:message key="phonecard.title"/></title>
    <style type="text/css">
        body {
            font: "Lucida Grande", "Lucida Sans Unicode", "Lucida Sans", Arial, sans-serif;
        }

        a {
            text-decoration: none;
        }

        a:hover {
            text-decoration: underline;
        }

            /* form style */
        input[type="text"], input[type="password"] {
            background: none repeat scroll 0 0 #FAFAFA;
            border-color: #C6C6C6 #DADADA #EAEAEA;
            border-radius: 4px 4px 4px 4px;
            color: #999999;
            font-family: inherit;
            font-size: 1.4em;
            display: block;
            margin-bottom: 7px;
        }

            /* form structure */
        input[type="password"], input[type="text"], input[type="button"] {
            -moz-box-sizing: border-box;
            border-style: solid;
            border-width: 1px;
            line-height: 1.1em;
            padding: 10px;
            vertical-align: middle;
        }

        input[type="button"] {
            background: -moz-linear-gradient(center top, #5393C2 40%, #3A77A4) repeat scroll 0 0 transparent;
            border-color: #3A77A4;
            border-radius: 8px 8px 8px 8px;
            box-shadow: 0 1px 0 0 rgba(255, 255, 255, 0.4) inset;
            color: #FFFFFF;
            font-size: 1.2em;
            font-weight: bolder;
            letter-spacing: -0.015em;
            line-height: 1.2em;
            cursor: pointer;
            text-transform: uppercase;
            margin-top: 5px;
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

        #logo-container {
            padding-left: 20px;
            padding-top: 20px;

        }

        #footer {
            color: #666;
            text-align: center;
            text-shadow: 0px 1px 1px #D3D3D3;

        }


    </style>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js"></script>
</head>
<body>
<div id="header">
    <div id="logo-container">
        <img src="<c:url value="/styles/images/thecao8888.png" />"/>
    </div>
</div>
<table style="height: 90%; width: 100%;">
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
                <input type="button" name="submit" value="Submit"/>
            </div>
        </td>
    </tr>
    </tbody>
</table>
<div id="footer">
    <fmt:message key="phonecard.copyright"/><a href="thecao8888.appspot.com.">
    thecao8888.appspot.com.</a>Copyright &copy; 2011.
</div>
</body>
</html>