<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html >
<head >
    <title ><fmt:message key="webapp.title" /></title >
    <style type="text/css" >
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
    </style >
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js" ></script >
    <script type="text/javascript" >
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
            $('#aw-form').submit(function(){
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
            console.debug(word);
            var $word = $('<div class="w"></div>');
            // append title.
            var $title = $('<div class="w-t"></div>').html(word.description);
            var wordDescription = word.description;
            if (word.pron) {
                wordDescription = wordDescription + ' (' + word.pron + ')';
            }
            $('#w-d').html(word.description);
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
                var kindAnchorId = '#'  + wordKind[i];
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
    </script >
</head >
<body >
Welcome to Vocabulary index page.

<div >
    Total word count:
    <span id="wc-s" ><c:out value="${totalCount}"></span >
    </c:out>
</div >

<div id="f-wr" >
    <form name="aw-form" action="/" id="aw-form" >
        <div >Add a new word</div >
        <input name="word" type="input" id="w-input"/>
        <input id="aw-b" type="button" value="Find" onclick="submitNewWord();" />
    </form >
</div >

<table >
    <thead >
        <th >Definition</th>
    <th >Navigation</th>
    </thead >
    <tbody>
        <tr>
            <td id="w-d"></td>
            <td id="w-nav"></td>
        </tr>
    </tbody>
</table >
<div id="w-dis" >

</div >
</body >
</html >