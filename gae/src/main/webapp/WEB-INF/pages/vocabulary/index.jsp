<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN">
<html>
<head>
    <title><fmt:message key="webapp.title"/></title>
    <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
    <script type="text/javascript" src="<c:url value='/js/jquery-ui-1.8.16.custom.min.js' />"></script>
    <script type="text/javascript" src="<c:url value='/js/GrowingInput.js' />"></script>
    <script type="text/javascript" src="<c:url value='/js/TextboxList.js' />"></script>
    <script type="text/javascript" src="<c:url value='/js/jquery.autocomplete.js' />"></script>
    <script type="text/javascript" src="<c:url value='/js/jquery.activity-indicator-1.0.0.min.js' />"></script>
    <script type="text/javascript" src="<c:url value='/js/jquery.jtemplate.js' />"></script>
    <script type="text/javascript"
            src="<c:url value='/js/vocabulary/playsound${appConfig["compressMode"]}' />"></script>
    <script type="text/javascript" src="<c:url value='/js/jquery.pagescroller.lite.js' />"></script>
    <script type="text/javascript" src="<c:url value='/js/waypoints${appConfig["compressMode"]}' />"></script>
    <script type="text/javascript"
            src="<c:url value='/js/vocabulary/vocabulary${appConfig["compressMode"]}' />"></script>
    <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/jquery-ui-1.8.16.custom.css' />"/>
    <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/TextboxList.css' />"/>
    <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/jquery.autocomplete.css' />"/>
    <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/layout.css' />"/>
    <script type="text/javascript">
        var global_pageManager = new VocabularyManager();
        var global_wordId;

        var initWord = '';
        <c:if test="${not empty param.word}">
        initWord = "<c:out value="${param.word}" />";
        </c:if>

        $(function () {
            displayUserPanel('#user-zone');
            // initialize the login-form dialog. It only be showed when we call 'open'.
            $("#login-form").dialog({
                autoOpen:false,
                height:220,
                width:330,
                modal:true,
                buttons:{
                    "Login":function () {
                        $.ajax({
                            url:'<c:url value="/user/authenticate.html" />',
                            type:'GET',
                            data:{
                                'userName':$('#login-userName').val().trim(),
                                'password':$('#login-password').val().trim()
                            },
                            dataType:'json',
                            beforeSend:function () {

                            },
                            success:function (response) {
                                $('#login-userName').val('');
                                $('#login-password').val('');

                                $("#login-form").dialog("close");
                                // create message
                                if (response.result) {
                                    showSuccessMessage(response, function () {
                                        displayUserPanel('#user-zone', '<c:url value="/user/userPanel.html" />');
                                    });
                                } else {
                                    showFailMessage('Login', 'Invalid username and password. Please try again.');
                                }
                            },
                            error:function () {
                                showFailMessage('Error', 'Please try again later.');
                            }
                        });

                    },
                    Cancel:function () {
                        $(this).dialog("close");
                    }
                },
                close:function () {

                }
            });

            // hit enter to submit the search.
            $('#search-input').focus().keypress(function (e) {
                if (e.which == 13) {
                    if ($('#search-input').val().trim != '') {
                        // start a new page, not ajax. (issue with Waypoints js, just cannot clear the old waypoints.)
                        window.location = "<c:url value="/vocabulary/index.html"/>?word=" + $('#search-input').val();
                    }
                }
            });
            // use url parameter to search for the requested word.
            if (initWord) {
                $('#search-input').val(initWord);
                performSearch();
            }

            // @see : http://docs.jquery.com/Plugins/Autocomplete/autocomplete#url_or_dataoptions
            $('#search-input').legacyautocomplete(
                    '<c:url value="/services/autocomplete/" />', {
                        width: 500,
                        max: 8,
                        height: 500,
                        autoFill: false,
                        matchContains: true,
                        formatItem: function(data, index, max, value, term) {
                            return data;
                        },
                        parse: function(data){
                            for (var i= 0 ;i < data.length; i++) {
                                var val = data[i];
                                data[i] = {
                                    data: val,
                                    value: val,
                                    result: val.trim()
                                }
                            }
                            return data;
                        },
                        formatMatch: function(row, i, max) { // match when typing.
                            return row;
                        },
                        scrollHeight: 300,
                        onEnter: function(inputVal) {
                            console.debug(inputval);

                        }
                    });
        });

        var performSearch = function () {
            // not sure it need to perform this task.
            global_pageManager.clearAll();
            // submit the request.
            submitNewWord($('#search-input').val().trim(),
                    $('#updateIfNeed').prop('checked'),
                    '<c:url value="/vocabulary/lookup.html" />',
                    {
                        wordTpl:'<c:url value="/js/template/word.tpl" />',
                        wordKindTpl:'<c:url value="/js/template/word_kind.tpl" />',
                        navHiderTpl:'<c:url value="/js/template/nav_hider.tpl" />',
                        navInfoTpl:'<c:url value="/js/template/word_info.tpl" />'
                    });
        }
    </script>
</head>
<body>

<div id="page-body">
    <div id="search-box">
        <div id="search-left-sec">
            <table>
                <td class="search-ico">
                </td>
                <td>
                    <input type="text" id="search-input"/>
                </td>
            </table>
        </div>
    </div>
    <div style="clear: both;"></div>
    <div id="search-result-body">
        <div id="pnl-left">
            <div id="nav-border-hider">
            </div>

            <div id="pnl-nav">
            </div>
            <div style="clear: both;"></div>
        </div>
        <div id="display-pnl">

        </div>
        <div style="clear: both;"></div>
    </div>
</div>

<div id="login-form" title="Login">
    <table>
        <tr>
            <td><label for="login-userName">Name :</label></td>
            <td><input name="userName" type="input" id="login-userName" class="text ui-widget-content ui-corner-all"/>
            </td>
        </tr>
        <tr>
            <td><label for="login-password">Password :</label></td>
            <td><input name="password" type="password" id="login-password"
                       class="text ui-widget-content ui-corner-all"/></td>
        </tr>
    </table>
</div>


</body>
</html>