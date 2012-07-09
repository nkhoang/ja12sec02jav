function VocabularyManager() {
    var listener = new Array();
    var ajaxData;

    this.addListener = addListener;
    function addListener(fn) {
        var exists = false;
        $.each(listener, function (f) {
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
        $.each(listener, function (i, f) {
            f(ajaxData);
        });
    }

    this.clearAll = function () {
        $("#display-pnl").empty();
        $('#display-pnl').activity({segments:12, width:7.5, space:6, length:13, color:'#252525', speed:1.5});
        $('#nav-border-hider').empty();
        $('#pnl-left *').hide();
        $("#pnl-nav").empty();
    }

    /**
     *
     * @param response
     * @param templateData takes these properties
     * <ul>
     *     <li>wordTpl</li>
     *     <li>wordKindTpl</li>
     *     <li>navHiderTpl</li>
     * </ul>
     */
    this.processWord = function (response, templateData) {
        var currTime = new Date().getTime();
        var requestParam = "?ts=" + currTime;

        $("#display-pnl").empty();
        var $navInfo = $('<div></div>');
        $navInfo.setTemplateURL(templateData.navInfoTpl + requestParam);
        // set description to make sure that they all have description
        if (!response.data['oxford']) {
            response.data['oxford'] = {
                description: response.data['vdict'].description
            }
        }

        $navInfo.processTemplate(response.data['oxford']);

        $("#pnl-nav").append($navInfo);
        $('#nav-border-hider').append($('<div class="nav-border-hider-section"></div>'));

        for (var dictName in response.data) {
            var $dictWrapper = $('<div></div>').addClass(dictName);
            var $navWrapper = $('<div></div>');
            var $navHider = $('<div></div>');

            $dictWrapper.setTemplateURL(templateData.wordTpl + requestParam);
            $dictWrapper.processTemplate(response.data[dictName]);

            $navWrapper.setTemplateURL(templateData.wordKindTpl + requestParam);
            $navWrapper.processTemplate(response.data[dictName]);

            $navHider.setTemplateURL(templateData.navHiderTpl + requestParam);
            $navHider.processTemplate(response.data[dictName]);

            $("#display-pnl").append($dictWrapper.html());
            $("#pnl-left *").show();
            $("#pnl-nav").append($navWrapper);
            $('#nav-border-hider').append($navHider.html());

            // register waypoints   .
            $('.w-k-t').waypoint(function (event, direction) {
                var classes = $(this).parents('.section')[0].className;
                if (classes.indexOf('section_') != -1) {
                    var indexPos = classes.substring(classes.indexOf('section_') + 'section_'.length);
                    // remove other 'active' classes.
                    $('.cnt-nav-lnk').removeClass('active');
                    $($('.cnt-nav-lnk')[indexPos]).addClass('active');
                }
            }, {
                offset: 0
            });
        }
        var navLinks = $('#pnl-nav').find('.cnt-nav-lnk');
        $(navLinks[navLinks.length - 1]).addClass('last');
    }
}


/**
 * Function to display user panel.
 * @param zoneId the element id.
 * @param url the url to get the user panel html.
 */
function displayUserPanel(zoneId, url) {
    $.ajax({
        url:url,
        type:'GET',
        dataType:'html',
        success:function (response) {
            $(zoneId).html(response);
        },
        error:function () {
            var $failMessage = $('<div></div>').html('An error occurred. Please try again later.');
            $failMessage.dialog({
                autoOpen:true
            });
        }
    });
}


function showFailMessage(title, messageContent) {
    var $failMessage = $('<div title="' + title + '"></div>').html(messageContent);
    $failMessage.dialog({
        modal:true,
        buttons:{
            "Close":function () {
                $failMessage.dialog('close');
            }
        }
    });
}

function showSuccessMessage(response, callback) {
    var $successMessage = $('<div></div>').html('Welcome ' + response.userName + ', you are logged in successfully!!');
    $successMessage.dialog({
        modal:true,
        buttons:{
            "Close":function () {
                $successMessage.dialog('close');
            }
        }
    });
    callback();
}

function openLoginDialog() {
    $("#login-form").dialog("open");
}

function submitNewWord(lookupWord, updateIfNeed, url, templateData) {
    $.ajax({
        url:url,
        type:'GET',
        data:{
            'word':lookupWord,
            'updateIfNeed':updateIfNeed
        },
        dataType:'json',
        beforeSend:function () {
            $('#search-input').val($('#search-input').val().trim());
        },
        success:function (word) {
            global_pageManager.processWord(word, templateData);
            // preload sound
            $('#w-d-sound').click();
            $('#display-pnl').pageScroller({
                navigation:'#pnl-nav',
                scrollOffset: 0
            });
        },
        error:function () {
            alert('Could not lookup requested word. Server error. Please try again later.')
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


