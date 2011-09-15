<%@ include file="/common/taglibs.jsp" %>

<c:set var="isUser" value="false"/>
<security:authorize access="hasRole('ROLE_USER')">
    <c:set var="isUser" value="true"/>
    <c:set var="userName">
        <security:authentication property="principal.username"/>
    </c:set>
</security:authorize>

<c:choose>
    <c:when test="${isUser}">
        <script type="text/javascript">
            function addNewWord() {
                if (wordId) {
                    $.ajax({
                        url: '<c:url value="/user/saveWord.html" />',
                        type: 'GET',
                        data: {
                            'wordId': wordId
                        },
                        dataType: 'json',
                        success: function(response) {
                            if (response.result) {
                                $('#word-status').html('Done.').hide().fadeIn(500, function() {
                                    $(this).fadeOut(3000);
                                })
                            } else {
                                showFailMessage('Info', response.error);
                            }
                        },
                        error: function() {
                            showFailMessage('Error', 'An error occurred. Please try again later.');
                        }
                    });
                } else {
                    showFailMessage('Warning', 'Please wait a moment.');
                }
            }
        </script>
        You are logged in as <b>${userName}</b>.
        <br>
        <a href="#" onclick="addNewWord(); return false;">Add</a> this to my dictionary.
        <br>

        <div id="word-status"></div>
    </c:when>
    <c:otherwise>
        Are you misschara user ? If yes, you can <a href="#" onclick="openLoginDialog();">here</a>.
    </c:otherwise>
</c:choose>
