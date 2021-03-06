<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html >
<head >
    <title ><fmt:message key="webapp.title" /></title >
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js" ></script >
    <script type="text/javascript" src="<c:url value='/js/jquery-ui-1.8.16.custom.min.js' />"></script>
    <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/jquery-ui-1.8.16.custom.css' />"/>

    <style >
        .table-lookup-status {
            border: 1px solid black;
        }

        .w-k {
            border-bottom: 1px solid #C0C0C0;
            margin-bottom: 15px;
            cursor: pointer;
        }

        .w-k-t {
            font-size: 120%;
            font-weight: bold;
            color: #000080;
            cursor: pointer;
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
        // word kind list get from the server.
        var wordKind = [];
        // english word kind ids.
        var EN_ids = [6,7,8,9];

        // check if a the word kind id is in allowance list.
        function checkENWordKind(number, array) {
            for (i = 0; i < array.length; i++) {
                if (array[i] == number) {
                    return true;
                }
            }
            return false;
        }

        function submitForm() {
             $.ajax(
                    {
                        url: "<c:url value='/vocabulary/constructIVocabulary.html?' />" + $('#submit-form').serialize(),
                        dataType: "html",
                        type: "GET",
                        data: {
                           'ids': tracker.getWordIds().join(',')
                        },
                        success: function(data) {

                            $('#output').val('').val(data);
                        }
                    });
        }

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
            // flag to detect if a word have any required meanings
            var haveMeaning = false;
            // append meaning
            for (var i in word.meaningMap) {
                if (checkENWordKind(i, EN_ids)) {
                    haveMeaning = true;
                    // append kind.
                    var $kind = $('<div class="w-k exp"></div>');
                    // append anchor
                    var $anchor = $('<a name="' + wordKind[i] + '" />');
                    // append kind title.
                    var $kindTitle = $('<div class="w-k-t"></div>').html(wordKind[i]).click(function(){
                        var $ul = $(this).siblings('ul');
                        if ($ul.hasClass('exp')) {
                            $ul.removeClass('exp').addClass('colps');
                            $ul.slideUp();
                        } else if ($ul.hasClass('colps')) {
                            $ul.removeClass('colps').addClass('exp');
                            $ul.slideDown();
                        }
                    });
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
                        var $meaningWrapper = $('<ul class="exp"></ul>');
                        for (var j in meanings) {
                            var $meaning = $('<li class="w-k-m"></li>');
                            var $content = $('<div class="w-k-m-c"></div>');
                            // append check box to know which meaning need to be included.
                            $content.append($('<input type="checkbox" name="meaningIds" />').click(function(){
                                if (!$(this).prop('checked')) {
                                    $(this).parents('li.w-k-m').find('input[type=checkbox]').each(function(){
                                        $(this).prop('checked', false);
                                    });
                                }
                            }).prop('value', meanings[j].id));
                            $content.append($('<span></span>').html(meanings[j].content));

                            $meaning.append($content);
                            var examples = meanings[j].examples;
                            if (examples.length > 0) {
                                exampleIndex = 0;
                                for (var z in examples) {
                                    var $example = $('<div class="w-k-m-ex"></div>');
                                    // append check box to know which meaning to be included.
                                    $example.append($('<input type="checkbox" name="exampleIds" />').prop('value', meanings[j].id + '-' + exampleIndex + '-').click(function(){
                                        if ($(this).prop('checked')) {
                                            $(this).parents('li.w-k-m').find('div.w-k-m-c > input').prop('checked', true);
                                        }
                                    }));
                                    $example.append($('<span></span>').html(examples[z]));
                                    $meaning.append($example);
                                    exampleIndex++;
                                }
                            }
                            $meaningWrapper.append($meaning);
                        }
                        $kind.append($meaningWrapper);
                    }
                    $wordKinds.append($kind);
                }
            }

            $word.append($wordKinds);
            if (haveMeaning) {
                return $word;
            }
            return null;

        }


        function WordTracker() {
            var table = $('.table-lookup-status > tbody');

            var wordList = new Array();
            this.registerWord = registerWord;
            this.addWordStatus = addWordStatus;
            this.reportDone = reportDone;
            this.registerId = registerId;
            this.getWordIds = getWordIds;
            this.disableWord = disableWord;
            this.reportStatus = reportStatus;

            function registerId(word, id) {
                wordList[word].id = id;
                wordList[word].disable = false;
            }

            function disableWord(word) {
                wordList[word].disable = true;
            }

            function getWordIds() {
                var ids = new Array();
                for (var w in wordList) {
                    if (!wordList[w].disable) {
                        ids.push(wordList[w].id);
                    }
                }
                return ids;
            }

            function registerWord(word) {
                if (wordList[word] == undefined) {
                    var index = wordList.length;
                    addWordStatus(word)
                    lookupWord(word);
                }
            }

            function reportDone(word) {
                wordList[word].find('td.word-status').html("DONE");
            }

            function reportStatus(word, status) {
                wordList[word].find('td.word-status').html(status);
            }


            function addWordStatus(word){
                var line = $('<tr><td class="word-description"></td><td class="word-status"></td></tr>');
                line.find('td.word-description').html(word);
                line.find('td.word-status').html("LOADING");

                table.append(line);
                // keep track of the line so we can update the status later.
                wordList[word] = line;
            }
        }

        function loadUserWordsByDate(date) {
            // build data.
            var ajaxData = {};
            ajaxData.offset = 0;
            ajaxData.date = date;

            $.ajax({
                url: '<c:url value="/user/getWords.html" />',
                type: 'GET',
                data: ajaxData,
                dataType: 'json',
                success: function(response) {
                    console.debug(response);
                },
                error: function() {
                    showFailMessage('Error', 'An error occurred. Please try again later.');
                }
            });
        }

        $(function(){

            // load datepicker
            $('#user-word-datepicker').datepicker({
                dateFormat: 'dd/mm/yy',
                onSelect: function(dateText, instance) {
                    loadUserWordsByDate(dateText);
                }
            });


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


            tracker = new WordTracker();

            $('#input-lookup').keypress(function(e){
                if(e.which == 13){
                    tracker.registerWord($(this).val());
                    $(this).val('');
                }
            });
        });
        function lookupWord(word) {
            $.ajax(
                    {
                        url: "<c:url value='/vocabulary/lookup.html' />",
                        dataType: "json",
                        type: "GET",
                        data: {
                            'word' : word
                        },
                        success: function(data) {
                            tracker.reportDone(word);

                            var $container = $('<div class="word-container"><h3></h3></div>');
                            $container.find('h3').html(word);
                            var wordHtml = processWord(data);
                            tracker.registerId(word, data.word.id);
                            if (wordHtml != null) {
                                $container.append(wordHtml);
                                $('.word-select').append($container);
                            } else {
                                tracker.reportStatus(word, "NO MEANING");
                                tracker.disableWord(word);
                            }
                        }
                    });
        }
    </script >
<style type="text/css">
    #user-word-list {
        font-size: 11px;
        position: fixed;
        right: 0px;
        top: 20px;
        border: 1px solid black;
        padding: 12px;
    }

</style>
</head >
<body >
<h1 >Welcome to iVocabulary builder.</h1 >

<div id="user-word-list">
    <div id="user-word-datepicker"></div>
</div>

<div class="form-container" >

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
    <form id="submit-form">
        <table >
        <tr >
            <td >Date Time:</td >
            <td ><input type="input" name="dateTime"  /></td >
        </tr >
        <tr >
            <td >Chapter Title:</td >
            <td ><input type="input" name="chapterTitle" /></td >
        </tr >
            <tr >
            <td >Page Title:</td >
            <td ><input type="input" name="pageTitle" /></td >
        </tr >
    </table >
        <div class="word-select"></div>
        <input type="button" value="Submit" onclick="submitForm()" />
    </form>
    <table >
        <tr >
            <td >Lookup:</td >
            <td ><input type="input" name="col" id="input-lookup" /></td >
        </tr >
        <tr >
            <td ><input type="button" value="Add" id="submit-word" onclick="" /></td >
        </tr >
    </table >

    <textarea rows="100" cols="200" id="output">

    </textarea>

</div >
</body >
</html >
