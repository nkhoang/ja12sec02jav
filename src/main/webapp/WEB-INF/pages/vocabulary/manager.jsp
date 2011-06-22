<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title><fmt:message key="webapp.title"/></title>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js"></script>
    <script>
        $(function() {
            setTimeout("getVocabularyMessages()", 2000);
        });
        function getVocabularyMessages() {
            $.ajax({
                        url: '<c:url value="/vocabulary/getMessage.html" />',
                        data: {
                            'interval': 2
                        },
                        type: 'GET',
                        dataType: 'json',
                        success: function(data) {
                            if (data) {
                                for (var i = data.length - 1; i >= 0; i--) {
                                    var textMessage = data[i].message + "\n";
                                    $('#notification').val(textMessage + $('#notification').val());
                                }
                            }
                        },
                        error: function() {
                        }
                    });
            setTimeout("getVocabularyMessages()", 2000);
        }
        function requestUpdateWords() {
            $.ajax({
                        url: '<c:url value="/vocabulary/updateViaGD.html" />',
                        data: $('#requestForm').serialize(),
                        type: 'GET',
                        dataType: 'json',
                        success: function(data) {
                            if (data);

                        },
                        error: function() {
                        }
                    });
        }
    </script>
</head>
<body>
<h3>Welcome to Vocabulary manager.</h3>

<div>
    <form id="requestForm">
        <table>
            <tr>
                <td>Spreadsheet Name:</td>
                <td><input type="input" name="spreadsheetName"/></td>
            </tr>
            <tr>
                <td>Worksheet Name:</td>
                <td><input type="input" name="worksheetName"/></td>
            </tr>
            <tr>
                <td>Row Index:</td>
                <td><input type="input" name="row"/></td>
            </tr>
            <tr>
                <td>Column Index:</td>
                <td><input type="input" name="col"/></td>
            </tr>
            <tr>
                <td>Size:</td>
                <td><input type="input" name="size"/></td>
            </tr>
        </table>
    </form>

    <input id="aw-b" type="button" value="Post" onclick="requestUpdateWords();"/>
    <textarea rows="30" cols="200" id="notification">

    </textarea>
</div>
</body>
</html>