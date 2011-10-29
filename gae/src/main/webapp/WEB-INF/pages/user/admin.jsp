<%@ include file="/common/taglibs.jsp" %>
<c:set var="isUser" value="false"/>
<security:authorize access="hasRole('ROLE_USER')">
    <c:set var="isUser" value="true"/>
    <c:set var="userName">
        <security:authentication property="principal.username"/>
    </c:set>
</security:authorize>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN">
<html>
<head>
    <title><fmt:message key="webapp.title"/></title>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1/jquery-ui.js" type="text/javascript"></script>

    <link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/redmond/jquery-ui.css" rel="stylesheet"
          type="text/css">

    <c:if test="${isUser}">
        <script type="text/javascript">

            function listAllDicts() {
                $.ajax({
                    url : '<c:url value="/user/getAllDicts.html" />',
                    dataType: 'json',
                    type: 'POST',
                    beforeSend: function() {
                    },
                    success: function(response) {
                        if (response && response.data) {

                            $content = $('#dict-table-view .content');
                            $content.empty();
                            if ($content) {
                                for (i = 0; i < response.data.length; i++) {
                                    var $tr = $('<tr></tr>');
                                    var dict = response.data[i];
                                    $tr.append($('<td></td>').html(dict.name));
                                    $tr.append($('<td></td>').html(dict.description));
                                    $content.append($tr);
                                }
                            }
                        }
                    }
                });
            }

            function addNewDictionary(element) {
                if ($(element).data('loading')) {
                    showMessage({ title:'Dictionary', text: 'Working... please wait!!!'});
                    return false;
                }
                $.ajax({
                    url: '<c:url value="/user/addDictionary.html" />',
                    data: {
                        'dictName': $('#dictionary-name').val(),
                        'dictDescription': $('#dictionary-des').val()
                    },
                    type: 'POST',
                    dataType: 'json',
                    beforeSend: function() {
                        $(element).data('loading', true);
                    },
                    success: function(response) {
                        if (response.data) {
                            if (response.data.error) {
                                showMessage({ title:'Dictionary', text: response.data.error});
                            } else {
                                showMessage({ title:'Dictionary', text: 'Added successfully!'});
                                listAllDicts();
                            }
                        }
                    },
                    error: function() {
                    },
                    complete: function() {
                        $(element).data('loading', false);
                    }
                });
            }
        </script>
    </c:if>
</head>
<body>
<c:choose>
    <c:when test="${isUser}">
        <div class="panel">
            <div class="panelTitle">Dictionary Panel</div>
            <script type="text/javascript">
            </script>
            <div>
                <div>Register Dictionary service.</div>
                <table>
                    <tbody>
                    <tr>
                        <td>
                            <label>Name</label>
                            <input id="dictionary-name" name="dictionary-name"/>
                        </td>
                        <td>
                            <label>Description</label>
                            <input id="dictionary-des" name="dictionary-des"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <input type="button" value="Submit" name="Submit" onclick="addNewDictionary(this);"/>
                        </td>
                    </tr>
                    </tbody>
                </table>
                <div>Dictionary View</div>
                <div id="dict-table-view">
                    <table cellpadding="4" cellspacing="0" border="1" >
                        <thead>
                        <th>Name</th>
                        <th>Description</th>
                        </thead>
                        <tbody class="content">
                        </tbody>

                    </table>
                </div>
            </div>
        </div>
        <%@ include file="/common/notify-template.jsp" %>
    </c:when>
    <c:otherwise>
        Are you misschara user ? If yes, you can <a href="#" onclick="openLoginDialog();">here</a>.
    </c:otherwise>
</c:choose>
</body>
</html>
