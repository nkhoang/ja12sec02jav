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
        font-size: 11px;
        position: fixed;
        right: 0px;
        top: 20px;
        border: 1px solid black;
        padding: 12px;
    }

    #user-words {
        font-size: 12pt;
    }

    #user-words-widget table {
        margin: 0 auto;
    }

    .words-container {
        color: #6f6f6f;
        height: 201px;
        width: 160px;
    }

    #user-words-widget .nav-left, #user-words-widget .nav-right {
        width: 20px;
        text-align: center;
        border: 1px solid #f6f6f6;
    }

    #user-words-widget .nav-left:hover, #user-words-widget .nav-right:hover {
        background-color: #DEE7F8;
        cursor: pointer;
    }

    .words-container tr.word-row {
        cursor: pointer;
    }

    .words-container tr.word-row:hover {
        background-color: #DEE7F8;
    }

    .words-container tr.odd {
        background-color: #f3f3f3;
    }

    .words-container table {
        width: 100%;
        text-align: center;
        border-top: 1px solid #f4f4f4;
    }

    .words-container tr {
        border-bottom: 1px solid #f4f4f4;
    }

</style>
<script type="text/javascript">
var global_textboxList;
var $global_datepicker;
var total_word_per_page = 10;
var global_current_word_offset = 0;
var global_next_word_offset = global_current_word_offset + total_word_per_page;


$(function() {
    $('#vietnamese-input').keydown(function(event) {
        if (event.keyCode == '13') {
            var value = $('#vietnamese-input').val();
            if (value.length > 0) {
                $.ajax({
                    url: '<c:url value="/user/search.html" />',
                    type: 'POST',
                    data: {
                        'word': value
                    },
                    dataType: 'json',
                    success: function(response) {
                        // build data
                        var words = response.data;
                        $('#search-container').empty().append($('<table cellpadding="0" cellspacing="0"></table>'));
                        var tableWords = $('#search-container').find('table');
                        var index = 0;
                        for (var i in words) {
                            var row = $('<tr></tr>').attr('class', (index % 2 == 0 ? 'even' : 'odd') + ' word-row').html(words[i])
                                    .attr('onclick', 'submitNewWord("' + words[i] + '", false); return false');

                            tableWords.append(row);
                            index++;
                            if (index == total_word_per_page) break;
                        }
                    }
                });
            }
        }
    });

    // create datepicker
    $global_datepicker = $('#user-word-datepicker').datepicker({
        dateFormat: 'dd/mm/yy',
        onSelect: function(dateText, instance) {
            updateUserSelectedDate(dateText);
            updateUserWordList(dateText, null, total_word_per_page);
        }
    });
    // update selected date when datepicker successfully created.
    updateUserSelectedDate($.datepicker.formatDate('dd/mm/yy', $global_datepicker.datepicker('getDate')));
    updateUserWordList($.datepicker.formatDate('dd/mm/yy', $global_datepicker.datepicker('getDate')), null, total_word_per_page);
    initializeNav();
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
    // update the user selected date.
    $('#user-selected-date').html(date);
}

function updateUserWordList(date, offset, size) {
    // build data.
    var ajaxData = {};
    ajaxData.size = size;
    ajaxData.date = date;
    if (offset != null) {
        ajaxData.offset = offset;
    }
    $.ajax({
        url: '<c:url value="/user/getWords.html" />',
        type: 'GET',
        data: ajaxData,
        dataType: 'json',
        success: function(response) {
            // build data
            var words = response.data;
            $('#words-container').empty().append($('<table cellpadding="0" cellspacing="0"></table>'));
            var tableWords = $('#words-container').find('table');
            var index = 0;
            for (var i in words) {
                var row = $('<tr></tr>').attr('class', (index % 2 == 0 ? 'even' : 'odd') + ' word-row').html(words[i])
                        .attr('onclick', 'submitNewWord("' + words[i] + '", false); return false');

                tableWords.append(row);
                index++;
                if (index == total_word_per_page) break;
            }

            // then update the offset base on the returned value.
            global_current_word_offset = response.offset;
            global_next_word_offset = response.nextOffset;

        },
        error: function() {
            showFailMessage('Error', 'An error occurred. Please try again later.');
        }
    });
}

function initializeNav() {
    $('#user-words-widget .nav-right').click(function() {
        if ((global_current_word_offset + total_word_per_page) <= global_next_word_offset) {
            updateUserWordList($.datepicker.formatDate('dd/mm/yy', $global_datepicker.datepicker('getDate')), global_next_word_offset, total_word_per_page);
        }
    });

    $('#user-words-widget .nav-left').click(function() {
        if ((global_current_word_offset - total_word_per_page) >= 0) {
            updateUserWordList($.datepicker.formatDate('dd/mm/yy', $global_datepicker.datepicker('getDate')), global_current_word_offset - total_word_per_page, total_word_per_page);
        }
    });
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
                tag.id = i;
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

        <div id="user-words-widget">
            <table cellpadding="0" cellspacing="0">
                <tr>
                    <td class="nav-left"><</td>
                    <td>
                        <div id="words-container" class="words-container">

                        </div>
                    </td>
                    <td class="nav-right">></td>
                </tr>
            </table>
        </div>
    </div>

    <div id="vietnamese-search">
        <div>Vietnamese Search: </div>
        <input id="vietnamese-input" />
        <div class="words-container" id="search-container">
        </div>

    </div>
</div>
</c:when>
<c:otherwise>
    Are you misschara user ? If yes, you can <a href="#" onclick="openLoginDialog();">here</a>.
</c:otherwise>
</c:choose>
