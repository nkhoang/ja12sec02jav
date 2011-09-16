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
        <style>
            .textboxlist {
                width: 350px;
            }
        </style>
        <script type="text/javascript">
            $(function(){
                var t = new $.TextboxList('#tag-list-box' ,{
                                              unique: true,
                                              plugins: {autocomplete: {
                                                  minLength: 1,
                                                  method: 'binary'
                                              }}
                                          });
                var autocomplete = t.plugins['autocomplete'];
                // get user tags.
                getUserTags(autocomplete);
            });

            function getUserTags(listener) {
                $.ajax({
                    url: '<c:url value="/user/getTags.html" />',
                    type: 'GET',
                    dataType: 'json',
                    success: function(response) {
                        var autoData = buildAutocompleteData(response.data);
                        listener.setValues(autoData);
                    },
                    error: function() {
                        showFailMessage('Error', 'An error occurred. Please try again later.');
                    }
                })
            }

            function buildAutocompleteData(data) {
                var autoData = [];
                for (var i in data) {
                    var row = new Array();
                    row.push(i);
                    row.push(data[i]);
                    autoData.push(row);
                }

                return autoData;
            }

            $('#tag-form').dialog({
                autoOpen: false,
                buttons: {
                    "Save" : function() {
                        $.ajax({
                            url: '<c:url value="/user/saveTag.html" />',
                            type: 'GET',
                            data: {
                                'tagName': $('#tag-name').val().trim(),
                                'wordId': wordId
                            },
                            dataType: 'json',
                            success: function(response) {
                                $('#tag-name').val('');
                                if (response.result) {
                                    $('#word-status').html('Tag Saved!!').hide().fadeIn(500, function() {
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
                        $('#tag-form').dialog('close');
                    }
                }
            });
            function addTag() {
                $('#tag-form').dialog('open');
            }
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
        <br>
        <a href="#" onclick="addTag(); return false;">Tag</a> : <input type="input" id="tag-list-box" style="width: 400px;"/>
        <div id="user-tag-list">
        </div>

        <div id="tag-form" title="Add a new tag">
            <table>
                <tr>
                    <td><label for="tag-name">Tag</label></td>
                    <td>:<input name="tag-name" type="input" id="tag-name"
                                class="text ui-widget-content ui-corner-all"/></td>
                </tr>
            </table>
        </div>
    </c:when>
    <c:otherwise>
        Are you misschara user ? If yes, you can <a href="#" onclick="openLoginDialog();">here</a>.
    </c:otherwise>
</c:choose>
