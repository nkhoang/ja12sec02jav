<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN">
<html>
<head>
<title><fmt:message key="webapp.title"/></title>
<style type="text/css">
   body {
   }

   ul.title, li.title {
      background-color: transparent;
      border-bottom-width: 0;
      border-left-width: 0;
      border-right-width: 0;
      border-top-width: 0;
      font-size: 100%;
      list-style-type: none;
      margin-bottom: 0;
      margin-left: 0;
      margin-right: 0;
      margin-top: 0;
      outline-width: 0;
      padding-bottom: 0;
      padding-left: 0;
      padding-right: 0;
      padding-top: 0;
      vertical-align: baseline;
   }

   #aw-form ul li.title {
      clear: both;
      overflow-x: hidden;
      overflow-y: hidden;
      padding-top: 10px;

   }

   #aw-form li input.input {
      background: none repeat scroll 0 0 transparent;
      border-color: -moz-use-text-color -moz-use-text-color #989895;
      border-style: none none dashed;
      border-width: medium medium 1px;
      bottom: 13px;
      color: #4F4F4F;
      font: 16px "SeanRegular", Courier New, Courier New, Courier6, monospace;
      letter-spacing: 1px;
      outline: medium none;
      text-align: center;
   }

   #aw-form li input {
      vertical-align: middle;
   }

   div.title, span.title {
      color: #525250;
      float: left;
      font: 13px "ClarendonRoman", Georgia, Times, serif;
      letter-spacing: 2px;
      position: relative;
      top: 4px;
      font-weight: bold;
   }


   #w-dis {
      width: 600px;
      float: left;
       font-family: Georgia, Palatino, "Palatino Linotype", Times, "Times New Roman", serif;
       margin-left: 40px;
       line-height: 18px;
       font-size: 12pt;
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

   .w-phrase {
       color: #000080;
   }

   div.w-k > ol > li {
    color: #A8397A;
   font-weight: bold;
   }

   div.w-k-m-container {
       color: black;
       font-weight: normal;;
   }

   li.w-k-m-sub {
   }

   .grammarGroup, .languageGroup {
       font-style: italic;
       font-weight: bold;
       font-size: 11px;
   }

   .w-k-m-c {
      font-weight: bold;
      font-size: 14px;
   }

   .w-k-m-ex {
      padding-left: 15px;
      font-style: italic;
      color: #808080;
      font-size: 90%;
   }


   #recent-w {
      width: 300px;
      float: left;
   }

   div.w-k-m-examples {
       font-size: 12px;
       font-style: italic;
   }

   ol li.w-k-m:hover {
       border: #CCCCCC 1px solid;
       background-color: #FFF;
       padding:  0 0 0 3px;
   }

   ol li.w-k-m {
      padding : 1px 0 1px 4px;
   }

   ul li.w-k-m-sub:hover {
       border: #CCCCCC 1px solid;
       padding:  0 0 0 39px;
       background-color: #E8F2FB;
       background-position: 30px 7px;
   }

   ul li.w-k-m-sub {
       list-style: none outside none;
       background: url("<c:url value='/styles/images/bullet_gray.png' />") no-repeat scroll 31px 8px transparent;
       font-size: 13px;
       margin-left: -40px;
       padding: 1px 0px 1px 40px;
   }

   ul.w-k-m-sub-example {
       list-style: none;
       font-style: italic;
       font-size: 12px;
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
<script type="text/javascript" src="<c:url value='/js/jquery.jtemplate.js' />"></script>
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
var wordKind = [];
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
}// submit new word.
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
      },
      success: function(word) {
         global_pageManager.processWord(word);
         // preload sound
         $('#w-d-sound').click();
      },
      error: function() {
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

   this.processWord = function(response) {
      for (var dictName in response.data) {
         $('#w-dis').setTemplateURL('<c:url value="/js/template/word.tpl" />');
         $("#w-dis").setParam('wordKind', wordKind);
         $("#w-dis").processTemplate(response.data[dictName]);
      }
      for (var dictName in response.data) {
         var word = response.data[dictName];
         if (word) {
            // clear all old data.
            $('#w-nav').empty();
             $('#w-d').html(word.description);
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
            }
             $('#w-phrase').html('Phrase');
            $('#w-phrase-nav').empty();
            for (var i = 0; i < word.phraseList.length ; i++) {
                var $phraseAnchor = $('<div><a href="#' + word.phraseList[i].description + '" /></div>');
                $phraseAnchor.find('a').html(word.phraseList[i].description);
                $('#w-phrase-nav').append($phraseAnchor);
            }
           fireListener();
            // in case user search before login.
            global_wordId = word.id;
         }
      }
   }
}


</script>

</head>
<body>
<div id="f-wr">

   <div id="lookup-w-c"></div>

   <form name="aw-form" action="/" id="aw-form">
      <ul class="title">
         <li class="title">
            <div class="title">Lookup</div>
            <input name="word" type="input" id="w-input" class="input"/>
         </li>
      </ul>
      <br/>

      <input id="aw-b" type="button" value="Find"
             onclick="submitNewWord($('#w-input').val().trim(), $('#updateIfNeed').prop('checked'));"/>
      <br>
      <span class="title">Update if need ?</span> <input type="checkbox" name="updateIfNeed" id="updateIfNeed"/>
      <br>

      <div id="user-zone">
      </div>
   </form>
</div>

<table>
   <tbody>
       <tr>
          <td id="w-d"></td>
          <td id="w-nav"></td>
       </tr>
   </tbody>
</table>
<table>
       <tbody>
           <tr>
              <td id="w-phrase"></td>
              <td id="w-phrase-nav"></td>
           </tr>
   </tbody>
</table>
<div id="w-container">

</div>
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