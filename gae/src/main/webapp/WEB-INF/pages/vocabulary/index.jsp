<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN">
<html>
<head>
<title><fmt:message key="webapp.title"/></title>
<style type="text/css">
    body {
    }

    .w-k {
        border-bottom: 1px solid #C0C0C0;
        margin-bottom: 15px;
    }

    .w-k-t {
        font-size: 120%;
        font-weight: bold;
        color: #000080;
    }

    .w-k-m {
    }

    .w-k-m-c {
        font-weight: bold;
        color: #A8397A;
        font-size: 90%;
    }

    .w-k-m-ex {
        padding-left: 15px;
        font-style: italic;
        color: #808080;
        font-size: 90%;
    }

    #w-dis {
        width: 600px;
        float: left;
        margin-left: 40px;
    }

    #recent-w {
        width: 300px;
        float: left;
    }

    .w-time {
        font-size: 9px;
    }
</style>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js"></script>
<script type="text/javascript" src="<c:url value='/js/jquery-ui-1.8.16.custom.min.js' />"></script>
<script type="text/javascript" src="<c:url value='/js/GrowingInput.js' />"></script>
<script type="text/javascript" src="<c:url value='/js/TextboxList.js' />"></script>
<script type="text/javascript" src="<c:url value='/js/jquery.autocomplete.js' />"></script>
<link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/jquery-ui-1.8.16.custom.css' />"/>
<link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/TextboxList.css' />"/>
<link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/jquery.autocomplete.css' />"/>


<script type="text/javascript">
    //playSoundFromFlash('/media/british/us_pron/a/agr/agric/agriculture.mp3', this)
    function playSoundFromFlash(B) {
        var C = "http://dictionary.cambridge.org/dictionary/british/".replace("http://", "");
        var D = C.split("/")[0];
        D = "http://" + D;
        C = "http://dictionary.cambridge.org/external/flash/speaker.swf?song_url=" + B;
        var E = document.getElementById("playSoundFromFlash");
        if (!E) {
            E = document.createElement("span");
            E.setAttribute("id", "playSoundFromFlash");
            document.body.appendChild(E);
        }
        $(E).html("");
        var A = "speakerCache";
        playFlash(C, E, A);
    }
    function playFlash(B, D, A) {
        if (D.firsChild) {
            return;
        }
        B += "&autoplay=true";
        var C;
        if (navigator.plugins && navigator.mimeTypes && navigator.mimeTypes.length) {
            C = "<embed type='application/x-shockwave-flash' src='" + B + "' width='0' height='0'></embed>";
        } else {
            C = "<object type='application/x-shockwave-flash' width='0' height='0' codebase='http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,40,0' data='" + B + "'><param name='wmode' value='transparent'/><param name='movie' value='" + B + "'/><embed src='" + B + "' width='0' height='0' ></embed></object>";
        }
        if (!A) {
            A = "speakerActive";
        }
        D.className = A;
        $(D).html(C);
    }
    function toggleCloudOverflow(B) {
        var A = document.getElementById("other_cloud_container");
        if (!A) {
            return;
        }
        var C;
        if (A.className == "hide_other_cloud") {
            A.className = "";
            C = "<< View Less";
        } else {
            A.className = "hide_other_cloud";
            C = "View More >>";
        }
        B.firstChild.nodeValue = C;
    }
</script>

<script type="text/javascript">
// empty array.
var wordKind = [];


/**
 * Get the 'userpanel' page and then add it to <i>zoneId</i>
 *
 * @param zoneId the id of the HTML element that the content will be appended to.
 */
function displayUserPanel(zoneId) {
    $.ajax({
        url: '<c:url value="/user/userPanel.html" />',
        type: 'GET',
        dataType: 'html',
        success: function(response) {
            $(zoneId).html(response);
        },
        error: function() {
            var $failMessage = $('<div></div>').html('An error occurred. Please try again later.');
            $failMessage.dialog({
                autoOpen: true
            });
        }
    });
}

/**
 * Show error message in an dialog. This dialog contains only one button named 'close'.
 *
 * @param title the title of the dialog.
 * @param messageContent message content.
 */
function showFailMessage(title, messageContent) {
    var $failMessage = $('<div title="' + title + '"></div>').html(messageContent);
    $failMessage.dialog({
        modal: true,
        buttons: {
            "Close": function() {
                $failMessage.dialog('close');
            }
        }
    });
}

function showSuccessMessage(response) {
    var $successMessage = $('<div></div>').html('Welcome ' + response.userName + ', you are logged in successfully!!');
    $successMessage.dialog({
        modal: true,
        buttons: {
            "Close": function() {
                $successMessage.dialog('close');
            }
        }
    });
    displayUserPanel('#user-zone');
}

/**
 * Show login dialog.
 */
function openLoginDialog() {
    $("#login-form").dialog("open");
}


$(function() {
    displayUserPanel('#user-zone');
    // initialize the login-form dialog. It only be showed when we call 'open'.
    $("#login-form").dialog({
        autoOpen: false,
        height: 220,
        width: 330,
        modal: true,
        buttons: {
            "Login": function() {
                $.ajax({
                    url: '<c:url value="/user/authenticate.html" />',
                    type: 'GET',
                    data: {
                        'userName': $('#login-userName').val().trim(),
                        'password': $('#login-password').val().trim()
                    },
                    dataType: 'json',
                    beforeSend : function() {

                    },
                    success: function(response) {
                        $('#login-userName').val('');
                        $('#login-password').val('');

                        $("#login-form").dialog("close");
                        // create message
                        if (response.result) {
                            showSuccessMessage(response);
                        } else {
                            showFailMessage('Login', 'Invalid username and password. Please try again.');
                        }
                    },
                    error: function() {
                        showFailMessage('Error', 'Please try again later.');
                    }
                });

            },
            Cancel: function() {
                $(this).dialog("close");
            }
        },
        close: function() {

        }
    });

    // get a list of word kinds.
    $.ajax({
        url: '<c:url value="/vocabulary/wordKind.html" />',
        type: 'POST',
        dataType: 'json',
        success: function(data) {
            wordKind = data.wordKind;
        },
        error: function() {
            alert("Failed to retrieve content from server. Cannot display data correctly. Please try again.");
        }
    });
    // prevent the form submitting.
    $('#aw-form').submit(function() {
        $('#aw-b').click();
        return false;
    });
});

// This is the main action of the page. Request a lookup for input word.
function lookupWord(word) {
    $('#w-input').val(word);
    $('#aw-b').click();
}
// submit new word.
function submitNewWord(lookupWord, updateIfNeed) {
    $.ajax({
        url: '<c:url value="/vocabulary/lookup.html" />',
        type: 'GET',
        data: {
            'word': lookupWord,
            'updateIfNeed': updateIfNeed
        },
        dataType: 'json',
        beforeSend : function() {
            $('#w-input').val($('#w-input').val().trim());
            $('#aw-b').prop('disabled', 'disabled');
        },
        success: function(word) {
            global_pageManager.processWord(word);
            $('#aw-b').removeProp('disabled');
            // preload sound
            $('#w-d-sound').click();
        },
        error: function() {
            alert('Could not lookup requested word. Server error. Please try again later.')
            $('#aw-b').removeProp('disabled');
        }
    });
}
function processRecentWords(data) {
    var words = data.words;
    $('.recent-ws').empty();
    for (var i = 0; i < words.length; i++) {
        var word = words[i];
        var $wordDiv = $('<div class="w-recent"></div>');
        var $timeSpan = $('<span class="w-time"></span>').html(' (' + word.currentTime + ')')
        var $wordContent = $('<a href="#" />').append(word.description);
        $wordContent.append($timeSpan);
        $wordContent.attr('onclick', 'lookupWord("' + word.description + '"); return false;');
        $wordDiv.append($wordContent);
        $('.recent-ws').append($wordDiv);
    }
}

var global_pageManager = new VocabularyManager();
var global_wordId;
function VocabularyManager() {
    var listener = new Array();
    var ajaxData;

    this.addListener = addListener;
    function addListener(fn) {
        var exists = false;
        $.each(listener, function(f) {
            if (f === fn) {
                exists = true;
                return;
            }
        });

        if (!exists) {
            listener.push(fn);
        }
    }

    this.fireListener = fireListener;
    function fireListener() {
        $.each(listener, function(i, f) {
            f(ajaxData);
        });
    }

    this.processWord = function(data) {
        var word = data.word;
        if (word) {
            // set global word id.
            ajaxData = data;
            var $word = $('<div class="w"></div>');
            $('#w-dis').html($word);
            // append title.
            var $title = $('<div class="w-t"></div>').html(word.description);
            var wordDescription = word.description;
            if (word.pron != undefined) {
                wordDescription = wordDescription + ' (' + word.pron + ')';
            }
            $('#w-d').html(wordDescription);
            if (word.soundSource != undefined) {
                $('#w-d').append($('<img id="w-d-sound" onclick="' + word.soundSource
                        + '" style="cursor: pointer" class="sound" title="Click to hear the US pronunciation of this word" alt="Click to hear the US pronunciation of this word" src="http://dictionary.cambridge.org/external/images/pron-us.png">'));
            }
            // clear all old data.
            $('#w-nav').empty();
            var $wordKinds = $('<div class="w-ks"></div>');
            // append meaning
            for (var i in word.meaningMap) {
                // append kind.
                var $kind = $('<div class="w-k"></div>');
                // append anchor
                var $anchor = $('<a name="' + wordKind[i] + '" />');
                // append kind title.
                var $kindTitle = $('<div class="w-k-t"></div>').html(wordKind[i]);
                $kind.append($anchor);
                $kind.append($kindTitle);
                // append to navigation table.
                var kindAnchorId = '#' + wordKind[i];
                var $kindAnchor = $('<div><a href="' + kindAnchorId + '" /></div>');
                $kindAnchor.find('a').html(wordKind[i]);
                $('#w-nav').append($kindAnchor);
                // loop through content.
                var meanings = word.meaningMap[i];
                if (meanings.length > 0) {
                    var $meaningWrapper = $('<ul></ul>');
                    for (var j in meanings) {
                        var $meaning = $('<li class="w-k-m"></li>');
                        var $content = $('<div class="w-k-m-c"></div>').html(meanings[j].content);
                        $meaning.append($content);
                        var examples = meanings[j].examples;
                        if (examples.length > 0) {
                            for (var z in examples) {
                                var $example = $('<div class="w-k-m-ex"></div>').html(examples[z]);
                                $meaning.append($example);
                            }
                        }
                        $meaningWrapper.append($meaning);
                    }
                    $kind.append($meaningWrapper);
                }
                $wordKinds.append($kind);
            }

            $word.append($wordKinds);

            fireListener();
            // in case user search before login.
            global_wordId = word.id;
        }
    }
}


</script>

<script type="text/javascript">
    /*
     Ext.onReady(function() {
     Ext.namespace("Vocabulary");

     Ext.define('Vocabulary.Search', {
     extend: 'Ext.form.field.Base',
     inputType: 'text',
     initComponent: function() {
     this.callParent();
     this.on('specialkey', this.checkEnterKey, this);
     },
     checkEnterKey: function(field, e) {
     var value = this.getValue();
     if (e.getKey() === e.ENTER && !Ext.isEmpty(value)) {
     Ext.MessageBox.alert("Key Entered", value);
     }
     },
     alias: 'widget.searchfield'
     });

     Ext.create('Ext.form.Panel', {
     title:"Lookup your new word",
     layout: 'anchor',
     bodyPadding: 5,
     width: 320,
     items: {
     xtype: 'searchfield',
     fieldLabel: 'Word',
     name: 'query'
     },
     renderTo: Ext.get('lookup-w-c')
     });
     });
     */
</script>
</head>
<body>

Welcome to Vocabulary index page.

<div id="f-wr">

    <div id="lookup-w-c"></div>

    <form name="aw-form" action="/" id="aw-form">
        <div>Add a new word</div>
        <input name="word" type="input" id="w-input"/>
        <input id="aw-b" type="button" value="Find"
               onclick="submitNewWord($('#w-input').val().trim(), $('#updateIfNeed').prop('checked'));"/>
        <br>
        Update if need ? <input type="checkbox" name="updateIfNeed" id="updateIfNeed"/>
        <br>

        <div id="user-zone">
        </div>
    </form>
</div>

<table>
    <thead>
    <th>Definition</th>
    <th>Navigation</th>
    </thead>
    <tbody>
    <tr>
        <td id="w-d"></td>
        <td id="w-nav"></td>
    </tr>
    </tbody>
</table>
<div id="w-dis">
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