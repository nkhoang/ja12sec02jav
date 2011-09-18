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
    #user-word-list {
        font-size:  11px;
        position: fixed;
        right: 0px;
        top: 20px;
        border:  1px solid black;
        padding:  10px;
    }

    #user-words {
        font-size: 12pt;
    }
</style>
<script type="text/javascript">
var global_textboxList;
var $global_datepicker;
$(function() {
    // create datepicker
    $global_datepicker = $('#user-word-datepicker').datepicker({
        dateFormat: 'dd/mm/yy',
        onSelect: function(dateText, instance) {
             updateUserSelectedDate(dateText);
        }
    });
    // update selected date when datepicker successfully created.
    updateUserSelectedDate($.datepicker.formatDate('dd/mm/yy', $global_datepicker.datepicker('getDate')));
    // create 'textboxlist'.
    global_textboxList = new $.TextboxList('#tag-list-box', {});
    // add 'Add' event for 'textboxlist'
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
        deleteTag(global_wordId, bit.getValue()[0], function(result) {
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
    global_pageManager.addListener(function(data) {
        if (data) {
            global_textboxList.clearTextList();
            getUserTags();
            getWordTags(global_textboxList, data.word.id);
            global_wordId = data.word.id;
        }
    });
});
function updateUserSelectedDate(date) {
    $('#user-selected-date').html(date);
}


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

/**
 * Get the current user's tags which will be used later to build autocomplete.
 */
function getUserTags() {
    $.ajax({
        url: '<c:url value="/user/getTags.html" />',
        type: 'GET',
        dataType: 'json',
        success: function(response) {
            // build array object from the response.
            var tagArr = new Array();
            for (var i in response.data) {
                var tag = {};
                tag.id =  i;
                tag.name = response.data[i];

                tagArr.push(tag);
            }

            $('.textboxlist-bit-editable-input').legacyautocomplete(
                    tagArr, { // id of the target textbox.
                        width: 310,
                        minChars: 0,
                        max: 1000,
                        scrollHeight: 300,
                        matchContains: true,
                        formatItem: function(data, i, n, value) { // how item to be displayed.
                            return "<table><tr><td>" + data.name + "</td></tr></table>";
                        },
                        formatMatch: function(row, i, max) { // match when typing.
                            return row.name;
                        },
                        formatResult: function(row) { // returned result when hit enter.
                            return row.name;
                        },
                        onEnter: function(inputVal) {
                            alert('aaa');
                        }
                    });
        },
        error: function() {
            showFailMessage('Error', 'An error occurred. Please try again later.');
        }
    });
}


function addTags(listener, data) {
    for (var i in data) {
        listener.add(data[i], i, false);
    }
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

/**
 * Connect to the server to create a new tag with <i>tagName</i>
 * @param tagName the tag name.
 * @param fn callback function.
 */
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
            addNewTag($('#tag-name').val().trim());
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

<div id="user-word-list">
    <div id="user-word-datepicker"></div>

    <div id="user-words">
        Recent words of: <b><span id="user-selected-date"></span></b>
        <div id="user-words-widget"></div>
    </div>
</div>
</c:when>
<c:otherwise>
    Are you misschara user ? If yes, you can <a href="#" onclick="openLoginDialog();">here</a>.
</c:otherwise>
</c:choose>
