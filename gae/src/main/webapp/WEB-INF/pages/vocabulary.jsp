<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title><fmt:message key="webapp.title"/></title>
    <script type="text/javascript"
            src="<c:url value='/js/jquery-1.4.2.js'/>"></script>
    <script type="text/javascript"
            src="<c:url value='/js/jquery.validate.js' />"></script>
    <script type="text/javascript"
            src="<c:url value='/js/jquery.form.js' />"></script>
    <script type="text/javascript"
            src="<c:url value='/js/jquery.tooltip.js' />"></script>
    <script language="javascript" type="text/javascript"
            src="<c:url value='/js/jquery.autocomplete.js' />"></script>
    <link rel="stylesheet" type="text/css" media="all"
          href="<c:url value='/styles/simple/jquery.tooltip.css' />"/>
    <link rel="stylesheet" type="text/css" media="all"
          href="<c:url value='/styles/simple/jquery.autocomplete.css' />"/>
    <style type="text/css">
        a img {
            border: none;
        }
    </style>
    <script type="text/javascript">
        function playSoundFromFlash(B) {
            var C = window.location.href.replace("http://", "");
            var D = C.split("/")[0];
            var F = window.location.href.replace(/dictionary\/.*$/, "");
            F = F.replace(/wordoftheday\//, "");
            F = F.replace(/topics\/.*$/, "");
            D = "http://" + D;
            C = F + "external/flash/speaker.swf?song_url=" + D + B;
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
                C = "<embed type='application/x-shockwave-flash' src='" + B
                        + "' width='0' height='0'></embed>";
            } else {
                C = "<object type='application/x-shockwave-flash' width='0' height='0' codebase='http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,40,0' data='"
                        + B
                        + "'><param name='wmode' value='transparent'/><param name='movie' value='"
                        + B
                        + "'/><embed src='"
                        + B
                        + "' width='0' height='0' ></embed></object>";
            }
            if (!A) {
                A = "speakerActive";
            }
            D.className = A;
            $(D).html(C);
        }

        $(function() {
            $('#wordForm').validate({
                rules: {
                    word: {
                        required: true,
                        minlength: 1
                    }
                },
                messages: {
                    word: {
                        required: "Look up nothing ?",
                        minlength: "Should be longer than 1 character."
                    }
                }
            });

            $('#addWordBtn').click(function() {
                var result = $('#wordForm').valid();
                if (result) {
                    $.ajax({
                        url: "<c:url value='/vocabulary/addWord.html' />",
                        type: 'post',
                        data: {
                            'word': $('#wordInput').val()
                        },
                        beforeSend: function() {
                            $('#itemLoading').show();
                        },
                        success: function() {

                        },
                        error: function() {
                        },
                        complete: function() {
                            $('#itemLoading').hide();
                        }
                    });
                }
                return false;
            });

            $('#loadWordImg').click(function() {
                if ($(this).hasClass('loading')) {
                    return;
                }
                loadWords();
            });

            $('div.wd').live('click', function(){
                populateWord(this);
            })
        });

        function populateWord(ele) {

            var id = $(ele).data('id');

            $.ajax({
                url: "<c:url value='/vocabulary/populateWord.html' />",
                type: 'post',
                data: {
                    id: id
                },
                beforeSend: function() {
                    $('#wordLoading').show();
                    $('#loadWordImg').addClass('loading');
                },
                success: function(data) {
                    if (data.word) {
                        renderDef(data.word.meanings, $('#wordDef'));
                    }
                },
                error: function() {
                    $('#wordLoading').hide();
                    $('#loadWordImg').removeClass('loading');
                },
                complete: function() {
                    $('#wordLoading').hide();
                    $('#loadWordImg').removeClass('loading');
                }
            });
        }

        var loadWords = function() {
            $.ajax({
                url: "<c:url value='/vocabulary/listWordsInRange.html' />",
                type: 'post',
                data: {
                    size: 40
                },
                beforeSend: function() {
                    $('#wordLoading').show();
                    $('#loadWordImg').addClass('loading');
                },
                success: function(data) {
                    $('#wordArea').empty();
                    if (data.words.length > 0 ) {
                        processWord(data.words, 'wordArea');
                    }
                },
                error: function() {
                    $('#wordLoading').hide();
                    $('#loadWordImg').removeClass('loading');
                },
                complete: function() {
                    $('#wordLoading').hide();
                    $('#loadWordImg').removeClass('loading');
                }
            });
        }

        function processWord(wordData, id) {
            var $targetC = $('#' + id);
            for (var i = 0 ;i < wordData.length; i++) {
                var w = wordData[i];
                // build description
                var wc = $("<div class='wc'></div>");
                var wd = $("<div class='wd'></div>").html(w.description);
                $(wd).data('id', w.id)                
                wc.append(wd);
                $targetC.append(wc);
            }
        }


        function renderDef(data, target) { // render word data to meaning section
            for (var p in data) {
                var ms = data[p];

                var wdc = $('<div class="wdc" ></div>');
                var wdt = $('<div class="wdt"></div>');
                wdt.append(p);
                var wda = $('<div class="wda"></div>');

                for (var i = 0 ;i <ms.length; i++) {
                    // build meaning
                    var wdm = $('<div class="wd-m"></div>');
                    wdm.html(ms[i].content);

                    wda.append(wdm);
                    for (var j = 0 ; j < ms[i].examples.length; j++) {
                        var wdme = $('<div class="wd-me"></div>')
                        wdme.html(ms[i].examples[j]);
                        wda.append(wdme);
                    }
                }

                wdc.append(wdt);
                wdc.append(wda);

                target.html(wdc);
            }

        }

        function processWordDef(ele, id) {
            var $targetC = $('#' + id);
            
            var data = $(ele).data();
            renderDef(data, $targetC);
        }

    </script>
</head>
<body>
<div id="addWordContainer">
    <form id="wordForm" action="" method="post">What's new today ? <br/>

        <div><label for="wordInput"></label> <input id="wordInput" type="text"
                                                    size="25" name="word"/></div>
        <a id="addWordBtn" href="#"><img
                src="<c:url value='/images/simple/Add.png' />"
                style="vertical-align: middle"/></a> <img id="itemLoading"
                                                          src="<c:url value='/images/simple/loading.gif' />" width='24'
                                                          height="24" style="display: none; vertical-align: middle;"/>
    </form>
</div>
<div id="wordContainer">
    <div id="wordController"><img id="loadWordImg" alt="Reload Words"
                                  title="Reload Words"
                                  src="http://lh5.ggpht.com/_4oj_ltkp9pc/S_VoFkL9MXI/AAAAAAAAAGc/mkWFeTSUVFc/arrow_rotate_anticlockwise.png"/>
        <img id="wordLoading" src="<c:url value='/images/simple/loading.gif' />"
             width='24' height="24" style="display: none; vertical-align: middle;"/>
    </div>
    <div id="wordArea"></div>
    <div id="wordDef">
        
    </div>
</div>
</body>
</html>