<%@ include file="/common/taglibs.jsp" %>
<c:set var="isUser" value="false" />
<security:authorize access="hasRole('ROLE_USER')" >
    <c:set var="isUser" value="true" />
    <c:set var="userName" >
        <security:authentication property="principal.username" />
    </c:set >
</security:authorize >
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN">
<html >
<head >
    <title ><fmt:message key="webapp.title" /></title >
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js" ></script >
    <c:if test="${isUser}" >
    <script type="text/javascript" >
        function addNewDictionary(element) {
            if ($(element).data('loading')) {
                alert('aaa');
                return false;
            }
            $.ajax({
                        url: '<c:url value="/user/dictionary/addDictionary.html" />',
                        data: {
                            'dictName': $('#dictionary-name').val(),
                            'dictDescription': $('dictionary-des').val()
                        },
                        type: 'GET',
                        dataType: 'json',
                        beforeSend: function() {
                            $(element).data('loading', true);
                        },
                        success: function(response) {
                            if (response.data) {
                                if (response.data.error) {
                                    alert(response.data.error);
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

    </script >
    </c:if>
</head >
<body >
<c:choose >
    <c:when test="${isUser}" >
        <div class="panel" >
            <div class="panelTitle" >Dictionary Panel</div >
            <script type="text/javascript" >
            </script >
            <div >
                <div >Register Dictionary service.</div >
                <table >
                    <tbody >
                    <tr >
                        <td >
                            <label >Name</label >
                            <input id="dictionary-name" name="dictionary-name" />
                        </td >
                        <td >
                            <label >Description</label >
                            <input id="dictionary-des" name="dictionary-des" />
                        </td >
                    </tr >
                    <tr >
                        <td colspan="2" >
                            <input type="button" value="Submit" name="Submit" onclick="addNewDictionary(this);" />
                        </td >
                    </tr >
                    </tbody >
                </table >
            </div >
        </div >
    </c:when >
    <c:otherwise >
        Are you misschara user ? If yes, you can <a href="#" onclick="openLoginDialog();" >here</a >.
    </c:otherwise >
</c:choose >
</body >
</html >
