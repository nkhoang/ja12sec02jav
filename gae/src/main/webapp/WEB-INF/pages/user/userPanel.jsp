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
            var global_textboxList;
            $(function(){
                global_textboxList = new $.TextboxList('#tag-list-box' ,{
                                          });
                global_textboxList.addEvent('bitBoxAdd', function(bit) {
                    if (bit.getValue()[2] == null) {
                        addNewTag(bit.getValue()[1], function(result, data) {
                            if (!result) {
                                bit.setValue([null,null, true]);
                                bit.remove();
                            } else {
                                var tagName = bit.getValue()[1];
                                bit.setValue([data, tagName]);
                            }
                        });
                    }
                });
                global_textboxList.addEvent('bitBoxRemove', function(bit) {
                    if (bit.getValue()[2]) {
                        return;
                    }
                    deleteTag(global_wordId, bit.getValue()[0], function(result){
                        if (!result) {
                            var bitData = bit.getValue();
                            // add it again.
                            bit.setValue([bitData[0], bitData[1], false])
                            global_textboxList.add(bit.getValue());
                        }
                    });

                });
                // get user tags.
                getUserTags();
                getWordTags(global_textboxList, global_wordId);
                global_pageManager.addListener(function(data){
                    if (data) {
                        global_textboxList.clearTextList();
                        getUserTags();
                        getWordTags(global_textboxList, data.word.id);
                        global_wordId = data.word.id;
                    }
                });
            });

             function getWordTags(listener, wid) {
                if (wid && listener) {
                    if (listener) {
                        $.ajax({
                            url: '<c:url value="/user/getTags.html" />',
                            type: 'GET',
                            data: {
                                'wordId': wid
                            },
                            dataType: 'json',
                            success: function(response) {
                                addTags(listener, response.data);
                            },
                            error: function() {
                                showFailMessage('Error', 'An error occurred. Please try again later.');
                            }
                        });
                    }
                }
            }

            function getUserTags() {
                $.ajax({
                    url: '<c:url value="/user/getTags.html" />',
                    type: 'GET',
                    dataType: 'json',
                    success: function(response) {
                    },
                    error: function() {
                        showFailMessage('Error', 'An error occurred. Please try again later.');
                    }
                });
            }



            function addTags(listener, data){
                for (var i in data) {
                    listener.add(data[i], i, false);
                }
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

            function deleteTag(wordId, userTagId, fn) {
                if (wordId == null || userTagId == null)
                    return;
                $.ajax({
                    url: '<c:url value="/user/deleteTag.html" />',
                    type: 'GET',
                    data: {
                        'userTagId': userTagId,
                        'wordId': wordId
                    },
                    dataType: 'json',
                    success: function(response) {
                        if (response.result) {
                            fn(true);
                        } else {
                            fn(false);
                        }
                    },
                    error: function() {
                        fn(false);
                        showFailMessage('Error', 'An error occurred. Please try again later.');
                    }
                })
            }

            function addNewTag(tagName, fn) {
                $.ajax({
                    url: '<c:url value="/user/saveTag.html" />',
                    type: 'GET',
                    data: {
                        'tagName': tagName,
                        'wordId': global_wordId
                    },
                    dataType: 'json',
                    success: function(response) {
                        $('#tag-name').val('');
                        if (response.result) {
                            $('#word-status').html('Tag Saved!!').hide().fadeIn(500, function() {
                                $(this).fadeOut(3000);
                                fn(true, response.data);
                            })
                        } else {
                            fn(false);
                            showFailMessage('Info', response.error);
                        }
                    },
                    error: function() {
                        fn(false);
                        showFailMessage('Error', 'An error occurred. Please try again later.');
                    }
                });
            }

            $('#tag-form').dialog({
                autoOpen: false,
                buttons: {
                    "Save" : function() {
                        addNewTag($('#tag-name').val().trim());;
                        $('#tag-form').dialog('close');
                    }
                }
            });
            function addTag() {
                $('#tag-form').dialog('open');
            }
            function addNewWord() {
                if (global_wordId) {
                    $.ajax({
                        url: '<c:url value="/user/saveWord.html" />',
                        type: 'GET',
                        data: {
                            'wordId': global_wordId
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
        Tags:
        <input type="input" id="tag-list-box" style="width: 400px;"/>
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
