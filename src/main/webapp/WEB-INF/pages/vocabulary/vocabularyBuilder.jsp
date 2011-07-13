<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html >
<head >
    <title ><fmt:message key="webapp.title" /></title >
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js" ></script >

    <style >
        .table-lookup-status {
            border: 1px solid black;
        }

        .w-k {
            border-bottom: 1px solid #C0C0C0;
            margin-bottom: 15px;
        }

        .w-k-t {
            font-size: 180%;
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

    </style >

    <script type="text/javascript" >
        var wordKind = [];
        function processWord(data) {
            var word = data.word;
            var $word = $('<div class="w"></div>');
            // append title.
            var $title = $('<div class="w-t"></div>').html(word.description);
            var wordDescription = word.description;
            if (word.pron != undefined) {
                wordDescription = wordDescription + ' (' + word.pron + ')';
            }
            // $('#w-d').html(wordDescription);
            if (word.soundSource != undefined)
            $('#w-d').append($('<img onclick="' + word.soundSource
                   + '" style="cursor: pointer" class="sound" title="Click to hear the US pronunciation of this word" alt="Click to hear the US pronunciation of this word" src="http://dictionary.cambridge.org/external/images/pron-us.png">'));
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
                        var $content = $('<div class="w-k-m-c"></div>');
                        $content.append($('<input type="checkbox" name="meaning" />').prop('value', meanings[j].id));
                        $content.append(meanings[j].content);

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

            return $word;
        }


        function WordTracker() {
            var table = $('.table-lookup-status > tbody');

            var wordList = new Array();
            this.registerWord = registerWord;
            this.addWordStatus = addWordStatus;
            this.reportDone = reportDone;

            function registerWord(word) {
                var index = wordList.length;
                addWordStatus(word, index)
                lookupWord(word, index);
            }

            function reportDone(index) {
                wordList[index].find('td.word-status').html("DONE");
            }

            function addWordStatus(word, index){
                var line = $('<tr><td class="word-description"></td><td class="word-status"></td></tr>');
                line.find('td.word-description').html(word);
                line.find('td.word-status').html("LOADING");

                table.append(line);
                // keep track of the line so we can update the status later.
                wordList[index] = line;
            }
        }

        $(function(){
            tracker = new WordTracker();

            $('#input-lookup').keypress(function(e){
                if(e.which == 13){
                    tracker.registerWord($(this).val());
                }
            });
        });
        function lookupWord(word, index) {
            $.ajax(
                    {
                        url: "<c:url value='/vocabulary/lookup.html' />",
                        dataType: "json",
                        type: "GET",
                        data: {
                            'word' : word
                        },
                        success: function(data) {
                            tracker.reportDone(index);
                            $('.word-select').append(processWord(data));
                        }
                    });
        }
    </script >
</head >
<body >
<h1 >Welcome to iVocabulary builder.</h1 >

<div class="form-container" >
    <table >
        <tr >
            <td >Lookup:</td >
            <td ><input type="input" name="col" id="input-lookup" /></td >
        </tr >
        <tr >
            <td ><input type="button" value="Add" id="submit-word" onclick="" /></td >
        </tr >
    </table >

    <div class="lookup-list" >
        <table class="table-lookup-status" >
            <thead >
            <th >Word</th >
            <th >Status</th >
            </thead >
            <tbody >

            </tbody >
        </table >
    </div >

    <div class="word-select"></div>
</div >
</body >
</html >