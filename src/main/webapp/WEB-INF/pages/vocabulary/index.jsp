<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title><fmt:message key="webapp.title"/></title>
    <style type="text/css">
        .w-k-t {
            font-size: 2em;
            font-weight: bold;
        }

        .w-k-m {
        }

        .w-k-m-c {
            font-weight: bold;
        }

        .w-k-m-ex {
            padding-left: 15px;
            font-style: italic;
        }
    </style>
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
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js"></script>
    <script type="text/javascript">
        // empty array.
        var wordKind = [];
        $(function() {
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
        // submit new word.
        function submitNewWord() {
            $.ajax({
                        url: '<c:url value="/vocabulary/lookup.html" />',
                        type: 'GET',
                        data: $('#aw-form').serialize(),
                        dataType: 'json',
                        beforeSend : function() {
                            $('#w-input').val($('#w-input').val().trim());
                            $('#aw-b').prop('disabled', 'disabled');
                        },
                        success: function(word) {
                            processWord(word);
                            $('#aw-b').removeProp('disabled');
                        },
                        error: function() {
                            alert('Could not lookup requested word. Server error. Please try again later.')
                            $('#aw-b').removeProp('disabled');
                        }
                    });
        }
        function processWord(data) {
            var word = data.word;
            var $word = $('<div class="w"></div>');
            // append title.
            var $title = $('<div class="w-t"></div>').html(word.description);
            var wordDescription = word.description;
            if (word.pron != 'undefined' || word.pron != null) {
                wordDescription = wordDescription + ' (' + word.pron + ')';
            }
            $('#w-d').html(wordDescription);
            $('#w-d').append($('<img onclick="' + word.soundSource + '" style="cursor: pointer" class="sound" title="Click to hear the US pronunciation of this word" alt="Click to hear the US pronunciation of this word" src="http://dictionary.cambridge.org/external/images/pron-us.png">'));
            // clear all old data.
            $('#w-nav').empty();
            var $wordKinds = $('<div class="w-ks"></div>');
            // append meaning
            for (var i in word.meanings) {
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
                var meanings = word.meanings[i];
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

            $('#w-dis').html($word);
        }
    </script>
</head>
<body>
Welcome to Vocabulary index page.

<div>
    Total word count:
    <span id="wc-s"><c:out value="${totalCount}"></span>
    </c:out>
</div>

<div id="f-wr">
    <form name="aw-form" action="/" id="aw-form">
        <div>Add a new word</div>
        <input name="word" type="input" id="w-input"/>
        <input id="aw-b" type="button" value="Find" onclick="submitNewWord();"/>
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
</body>
</html>