<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html >
<head >
    <title ><fmt:message key="webapp.title" /></title >
    <style type="text/css">
        .w-k-t {
            font-size: 2em;
            font-weight: bold;
        }
        .w-k-m {
            padding-left: 20px;
        }
        .w-k-m-c {
            font-weight: bold;
        }
        .w-k-m-ex {
            padding-left: 15px;
        }
    </style>
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
            })
        });
        // submit new word.
        function submitNewWord() {
            $.ajax({
                url: '<c:url value="/vocabulary/lookup.html" />',
                type: 'GET',
                data: $('#aw-form').serialize(),
                dataType: 'json',
                beforeSend : function() {
                    $('#aw-b').prop('disabled', 'disabled');
                },
                success: function(word) {
                    processWord(word);
                    $('#aw-b').removeProp('disabled');
                },
                error: function() {
                    alert('Could not lookup requested word. Server error. Please try again later.')
                }
            });
        }
        function processWord(data) {
            var word = data.word;
            console.debug(word);
            // append description.
            var $word = $('<div class="w"></div>');
            // append title.
            var $title = $('<div class="w-t"></div>').html(word.description);
            $word.append($title);

            var $wordKinds = $('<div class="w-ks"></div>');
            // append meaning
            for (var i in word.meanings) {
                // append kind.
                var $kind = $('<div class="w-k"></div>');
                // append kind title.
                var $kindTitle = $('<div class="w-k-t"></div>').html(wordKind[i]);
                $kind.append($kindTitle);
                // loop through content.
                var meanings = word.meanings[i];
                for (var j in meanings) {
                    var $meaning = $('<div class="w-k-m"></div>');
                    console.debug(meanings[j]);
                    var $content = $('<div class="w-k-m-c"></div>').html(meanings[j].content);
                    $meaning.append($content);
                    var examples = meanings[j].examples;
                    for (var z in examples) {
                        var $example = $('<div class="w-k-m-ex"></div>').html(examples[z]);
                        $meaning.append($example);
                    }
                    $kind.append($meaning);
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
        <input name="word" type="input" />
        <input id="aw-b" type="button" value="Add" onclick="submitNewWord();" />
    </form >
</div >

<div id="w-dis" >

</div >
</body >
</html >