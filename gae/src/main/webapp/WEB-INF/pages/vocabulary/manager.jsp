<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html >
<head >
    <title ><fmt:message key="webapp.title" /></title >
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js" ></script >
    <script >
        var timer;
        var stop_timer = true;
        function getVocabularyMessages() {
            $.ajax({
                        url: '<c:url value="/vocabulary/getMessage.html" />',
                        data: {
                            'interval': 1
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
            if (!stop_timer)
                timer = setTimeout("getVocabularyMessages()", 1000);
        }
        function requestUpdateWords(index, target) {
            if (index >= target) {
                stop_timer = true;
                return;
            }
            // set row
            $('#row').val(index);
            stop_timer = false;
            timer = setTimeout("getVocabularyMessages()", 1000);
            $.ajax({
                        url: '<c:url value="/vocabulary/updateViaGD.html" />',
                        data: $('#requestForm').serialize(),
                        type: 'GET',
                        dataType: 'json',
                        success: function(data) {
                            requestUpdateWords(parseInt(index) + 100, target);
                        },
                        error: function() {
                        }
                    });
        }
    </script >
</head >
<body >
<h3 >Welcome to Vocabulary manager.</h3 >

<div >
    <form id="requestForm" >
        <table >
            <tr >
                <td >Spreadsheet Name:</td >
                <td ><input type="input" name="spreadsheetName" /></td >
            </tr >
            <tr >
                <td >Worksheet Name:</td >
                <td ><input type="input" name="worksheetName" /></td >
            </tr >
            <tr >
                <td >Row Index:</td >
                <td ><input id="row" type="input" name="row" /></td >
            </tr >
            <tr >
                <td >Column Index:</td >
                <td ><input type="input" name="col" /></td >
            </tr >
            <tr >
                <td >Size:</td >
                <td ><input type="input" name="size" /></td >
            </tr >
        </table >
    </form >
    Target Index
    <input id="target" name="target" />

    <input id="aw-b" type="button" value="Post" onclick="requestUpdateWords($('#row').val(), $('#target').val());" />
    <textarea rows="30" cols="200" id="notification" style="font-size: 8pt;" >

    </textarea >
</div >
</body >
</html >