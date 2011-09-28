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

    #user-words, #vietnamese-search {
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

    #vietnamese-search .words-container {
        margin:  0 auto;
    }
    #vietnamese-input {
        margin: 0 auto;
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
                        // var response = {"data":[{"id":305016,"pron":"/əˈlɒt/","soundSource":"playSoundFromFlash(\u0027http://dictionary.cambridge.org/media/british/us_pron/a/all/allot/allot.mp3\u0027, this)","description":"allot","meaningMap":{"4":[{"id":307019,"content":"phân công, giao (phân việc cho ai); định dùng (một số tiền vào việc gì)","kindId":4,"examples":[]},{"id":304018,"content":"chia phần, phân phối, định phần","kindId":4,"examples":[]},{"id":305014,"content":"(quân sự) phiên chế","kindId":4,"examples":[]},{"id":305015,"content":"(quân sự) chuyển (một phần lương cho gia đình)","kindId":4,"examples":[]}],"9":[{"id":301014,"content":" to use a particular amount of time for something, or give a particular share of money, space etc to someone or something","kindId":9,"examples":["Try and allot 2 or 3 hours a day to revision.","Each school will be allotted twenty seats.","Everyone who works for the company has been allotted 10 shares."]},{"id":307020,"content":"allot something to something/somebody","kindId":9,"examples":["Try and allot 2 or 3 hours a day to revision.","Each school will be allotted twenty seats."],"type":"gram"},{"id":303019,"content":"allot somebody something","kindId":9,"examples":["Everyone who works for the company has been allotted 10 shares."],"type":"collo"}]},"kindIdList":[4,9]},{"id":310344,"pron":"/əˈpɑːt.mənt/","soundSource":"playSoundFromFlash(\u0027http://dictionary.cambridge.org/media/british/us_pron/a/apa/apart/apartment.mp3\u0027, this)","description":"apartment","meaningMap":{"8":[{"id":321266,"content":" a set of rooms on one floor of a large building, where someone lives","kindId":8,"examples":["She lives in a small apartment.","a holiday apartment"]},{"id":308415,"content":"[usually plural] a room or set of rooms used by an important person such as a president","kindId":8,"examples":["I had never been in the prince\u0027s private apartments before.","the presidential apartments"]}],"2":[{"id":310343,"content":"căn phòng, buồng","kindId":2,"examples":[]},{"id":309349,"content":"(số nhiều) (Mỹ số ít) dãy buồng ở một tầng (cho một gia đình)","kindId":2,"examples":["walk-up apartment"]}]},"kindIdList":[2,8]},{"id":309073,"pron":"/əˈlɒt.mənt/","soundSource":"playSoundFromFlash(\u0027http://dictionary.cambridge.org/media/british/us_pron/a/all/allot/allotment.mp3\u0027, this)","description":"allotment","meaningMap":{"8":[{"id":308135,"content":"[uncountable and countable] an amount or share of something such as money or time that is given to someone or something, or the process of doing this","kindId":8,"examples":["The budget allotment for each county is below what is needed.","the allotment of shares in the company"]},{"id":310059,"content":"[countable] a small area of land that people can rent for growing vegetables","kindId":8,"examples":[]}],"2":[{"id":313018,"content":"sự phân công, sự giao việc (phần việc); sự định dùng (một số tiền vào việc gì)","kindId":2,"examples":[]},{"id":299986,"content":"sự chia phần, sự phân phối; sự định phần","kindId":2,"examples":[]},{"id":308134,"content":"phần được chia, phần được phân phối","kindId":2,"examples":[]},{"id":310058,"content":"mảnh đất được phân phối để cày cấy","kindId":2,"examples":[]},{"id":309072,"content":"(quân sự) sự phiên chế","kindId":2,"examples":[]},{"id":299987,"content":"(quân sự) sự chuyển (một phần lương) cho gia đình","kindId":2,"examples":[]}]},"kindIdList":[2,8]},{"id":195801,"pron":"/həʊm/","soundSource":"playSoundFromFlash(\u0027http://dictionary.cambridge.org/media/british/us_pron/h/hom/home_/home.mp3\u0027, this)","description":"home","meaningMap":{"1":[{"id":198698,"content":"(thuộc) gia đình, ở gia đình, ở nhà","kindId":1,"examples":["home life","for home use"]},{"id":195777,"content":"(thuộc) nước nhà, ở trong nước, nội","kindId":1,"examples":["Home Office","home trade","home market"]},{"id":196767,"content":"địa phương","kindId":1,"examples":["a home team"]},{"id":195778,"content":"ở gần nhà","kindId":1,"examples":[]}],"2":[{"id":196768,"content":"nhà, chỗ ở","kindId":2,"examples":["to have neither hearth nor home","to be at home","not at home"]},{"id":197737,"content":"nhà, gia đình, tổ ấm","kindId":2,"examples":["there\u0027s no place like home","make yourself at home","the pleasures of home"]},{"id":198701,"content":"quê hương, tổ quốc, nơi chôn nhau cắt rún, nước nhà","kindId":2,"examples":["an exile from home"]},{"id":197738,"content":"chỗ sinh sống (sinh vật)","kindId":2,"examples":[]},{"id":198702,"content":"nhà (hộ sinh...), viện (cứu tế, dưỡng lão...), trại (mồ côi...)","kindId":2,"examples":["convalescent home","arphan\u0027s home","lying in home"]},{"id":197739,"content":"đích (của một số trò chơi)","kindId":2,"examples":[]}],"3":[{"id":198703,"content":"trở về nhà, trở về quê hương (người, chim bồ câu đưa thư...)","kindId":3,"examples":[]},{"id":196772,"content":"(từ Mỹ,nghĩa Mỹ) có nhà, có gia đình","kindId":3,"examples":[]}],"4":[{"id":195782,"content":"cho về nhà, cho hồi hương; đưa về nhà","kindId":4,"examples":[]},{"id":196773,"content":"tạo cho (ai...) một căn nhà, tạo một tổ ấm","kindId":4,"examples":[]}],"5":[{"id":197742,"content":"về nhà, trở về nhà, đến nhà, ở nhà","kindId":5,"examples":["to go home","to see somebody home","he is home"]},{"id":198706,"content":"về nước, hồi hương, về quê hương","kindId":5,"examples":["to send someone home"]},{"id":197743,"content":"trúng, trúng địch; (bóng) chạm nọc, trúng tim đen","kindId":5,"examples":["to strike home"]},{"id":198707,"content":"đến cùng","kindId":5,"examples":["to drive a nail home"]}],"6":[{"id":197744,"content":" to or at the place where you live","kindId":6,"examples":["Is Sue home from work yet?","They brought the baby home from the hospital on Friday.","We stayed home last night.","I\u0027m going home now. See you tomorrow.","It was midnight by the time we got home.","What time are you coming home?"]},{"id":198708,"content":"bring/take somebody/something home","kindId":6,"examples":["They brought the baby home from the hospital on Friday."],"type":"collo"},{"id":197745,"content":"come/get/reach etc home","kindId":6,"examples":["It was midnight by the time we got home.","What time are you coming home?"],"type":"collo"},{"id":198709,"content":" to earn a certain amount of money after tax has been taken off","kindId":6,"examples":["The average worker takes home around $300 a week."]},{"id":197746,"content":" to make sure that someone understands what you mean by saying it in an extremely direct and determined way","kindId":6,"examples":["We really need to drive this message home."]},{"id":198710,"content":" to make you realize how serious, difficult, or dangerous something is","kindId":6,"examples":["The episode has brought home to me the pointlessness of this war."]},{"id":196779,"content":" if a remark, situation, or experience hits home, it makes you realize how serious, difficult, or dangerous something is","kindId":6,"examples":["She could see that her remark had hit home."]},{"id":200075,"content":" to have succeeded in doing something","kindId":6,"examples":[]},{"id":196780,"content":" to have succeeded in doing the most difficult part of something","kindId":6,"examples":["If I last five years with no symptoms, I\u0027ll be home free."]}],"7":[{"id":200076,"content":" relating to or belonging to your home or family","kindId":7,"examples":["These children need a proper home life."]},{"id":196781,"content":"home address/number","kindId":7,"examples":[],"type":"collo"},{"id":197750,"content":" done at home or intended for use in a home","kindId":7,"examples":["good old-fashioned home cooking","a home computer"]},{"id":198714,"content":" played or playing at a team\u0027s own sports field, rather than an opponent\u0027s field","kindId":7,"examples":["The home team took the lead after 25 minutes."]},{"id":197751,"content":"home team/game/crowd/club etc","kindId":7,"examples":["The home team took the lead after 25 minutes."],"type":"collo"},{"id":198715,"content":" relating to a particular country, as opposed to foreign countries","kindId":7,"examples":["The meat was destined for the home market."]}],"8":[{"id":197752,"content":"[uncountable and countable] the house, apartment, or place where you live","kindId":8,"examples":["They have a beautiful home in California.","Good luck in your new home!","Last night we stayed at home and watched TV.","He was spending more and more time away from home.","A family of birds made their home (\u003dstarted living) under the roof."]},{"id":198716,"content":"at home","kindId":8,"examples":["Last night we stayed at home and watched TV."],"type":"gram"},{"id":197753,"content":"away from home","kindId":8,"examples":["He was spending more and more time away from home."],"type":"gram"},{"id":198717,"content":"work from/at home","kindId":8,"examples":[],"type":"collo"},{"id":196786,"content":"[uncountable and countable] the place where a child lived with his or her family","kindId":8,"examples":["Jack left home when he was 16.","Were you still living at home (\u003dwith your parents)?","Carrie moved out of the family home a year ago."]},{"id":195792,"content":"[uncountable and countable] the place where you came from or where you usually live, especially when this is the place where you feel happy and comfortable","kindId":8,"examples":["She was born in Italy, but she\u0027s made Charleston her home.","The folks back home don\u0027t really understand what life is like here."]},{"id":196787,"content":"back home","kindId":8,"examples":["The folks back home don\u0027t really understand what life is like here."],"type":"gram"},{"id":195793,"content":"[uncountable] the country where you live, as opposed to foreign countries","kindId":8,"examples":["auto sales at home and abroad","He\u0027s been travelling, but he\u0027s kept up with what\u0027s going on back home."]},{"id":195794,"content":"at home","kindId":8,"examples":["auto sales at home and abroad"],"type":"gram"},{"id":197756,"content":"back home","kindId":8,"examples":["He\u0027s been travelling, but he\u0027s kept up with what\u0027s going on back home."],"type":"gram"},{"id":195795,"content":" to feel comfortable in a place or with a person","kindId":8,"examples":["I\u0027m already feeling at home in the new apartment.","After a while we began to feel at home with each other.","Practise using the video until you feel quite at home with it."]},{"id":198722,"content":"[countable] a house, apartment etc considered as property which you can buy or sell","kindId":8,"examples":["Attractive, modern homes for sale."]},{"id":200077,"content":"[countable] a place where people who are very old or sick, or children who have no family are looked after","kindId":8,"examples":["an old people\u0027s home","I could never put Dad into a home."]},{"id":196791,"content":" used to tell someone who is visiting you that they should relax","kindId":8,"examples":["Sit down and make yourself at home."]},{"id":195796,"content":" to make someone feel relaxed by being friendly towards them","kindId":8,"examples":["We like to make our customers feel at home."]},{"id":196792,"content":" the place where something was first discovered, made, or developed","kindId":8,"examples":["America is the home of baseball.","India is the home of elephants and tigers."]},{"id":197760,"content":" if a sports team plays at home, they play at their own sports field","kindId":8,"examples":["Birmingham Bullets are at home to Kingston."]},{"id":198725,"content":" a place that you think is as pleasant and comfortable as your own house","kindId":8,"examples":[]},{"id":197761,"content":" used to say how nice it is to be in your own home","kindId":8,"examples":[]},{"id":198726,"content":" a place where animals with no owners are looked after","kindId":8,"examples":[]},{"id":197762,"content":" to find a place where something can be kept","kindId":8,"examples":["Can you find a home for the piano?"]},{"id":198727,"content":" used humorously to ask what a long or unusual word means","kindId":8,"examples":[]},{"id":197763,"content":"[uncountable] a place in some games or sports which a player must try to reach in order to win a point","kindId":8,"examples":[]}],"9":[{"id":198728,"content":" to aim exactly at an object or place and move directly to it","kindId":9,"examples":["The bat can home in on insects using a kind of \u0027radar\u0027."]},{"id":197764,"content":" to direct your efforts or attention towards a particular fault or problem","kindId":9,"examples":["He homed in on the one weak link in the argument."]}]},"kindIdList":[1,2,3,4,5,6,7,8,9]},{"id":272006,"pron":"/kiːp/","soundSource":"playSoundFromFlash(\u0027http://dictionary.cambridge.org/media/british/us_pron/k/kee/keep_/keep.mp3\u0027, this)","description":"keep","meaningMap":{"8":[{"id":267450,"content":" the cost of providing food and a home for someone","kindId":8,"examples":["It\u0027s time you got a job and started earning your keep."]},{"id":273008,"content":"earn your keep","kindId":8,"examples":["It\u0027s time you got a job and started earning your keep."],"type":"collo"},{"id":266481,"content":" for ever","kindId":8,"examples":["Marriage ought to be for keeps."]},{"id":265508,"content":"[countable] a large strong tower, usually in the centre of a castle","kindId":8,"examples":[]}],"9":[{"id":267451,"content":"[linking verb, transitive] to stay in a particular state, condition, or position, or to make someone or something do this","kindId":9,"examples":["We huddled around the fire to keep warm.","I was struggling to keep awake.","Keep your room tidy.","some toys to keep the kids amused","You won\u0027t be able to keep it secret for ever.","Peter cycles to work to keep fit.","Don\u0027t keep us in suspense any longer!","The police put up barriers to keep the crowds back.","If I were you, I\u0027d keep away from that area at night.","a sign saying \u0027Danger: Keep out\u0027","The little boy kept close to his mother.","Keep him out of trouble.","You keep out of this, Mother (\u003ddo not get involved). It\u0027s no concern of yours.","How can I cut your hair if you won\u0027t keep still!","Jane kept the engine running."]},{"id":273009,"content":"keep (somebody/something) away/back/off/out etc","kindId":9,"examples":["The police put up barriers to keep the crowds back.","If I were you, I\u0027d keep away from that area at night.","a sign saying \u0027Danger: Keep out\u0027","The little boy kept close to his mother."],"type":"gram"},{"id":266482,"content":"keep (somebody) out of something","kindId":9,"examples":["Keep him out of trouble.","You keep out of this, Mother (\u003ddo not get involved). It\u0027s no concern of yours."],"type":"gram"},{"id":265509,"content":"keep somebody/something doing something","kindId":9,"examples":["Jane kept the engine running."],"type":"gram"},{"id":267452,"content":"keep (somebody/something) warm/safe/dry etc","kindId":9,"examples":["We huddled around the fire to keep warm."],"type":"collo"},{"id":273010,"content":"keep calm/awake/sane etc","kindId":9,"examples":["I was struggling to keep awake."],"type":"collo"},{"id":266483,"content":"keep something clean/tidy","kindId":9,"examples":["Keep your room tidy."],"type":"collo"},{"id":265510,"content":"keep somebody busy/amused/occupied","kindId":9,"examples":["some toys to keep the kids amused"],"type":"collo"},{"id":267453,"content":"keep left/right","kindId":9,"examples":[],"type":"collo"},{"id":273011,"content":"[intransitive] to continue doing something or to do the same thing many times","kindId":9,"examples":["I keep thinking about Joe, all alone in that place.","I keep telling you, but you won\u0027t listen!","She pretended not to hear, and kept on walking."]},{"id":266484,"content":"keep (on) doing something","kindId":9,"examples":["I keep thinking about Joe, all alone in that place.","I keep telling you, but you won\u0027t listen!","She pretended not to hear, and kept on walking."],"type":"gram"},{"id":265511,"content":"[transitive] to have something and not give it back to the person who had it before","kindId":9,"examples":["You can keep it. I don\u0027t need it any more."]},{"id":267454,"content":"[transitive] to continue to have something and not lose it or get rid of it","kindId":9,"examples":["We decided to keep our old car instead of selling it.","I kept his letters for years.","In spite of everything, Robyn\u0027s managed to keep her sense of humor."]},{"id":273012,"content":"[transitive always + adverb/preposition] to leave something in one particular place so that you can find it easily","kindId":9,"examples":["Where do you keep your tea bags?","George kept a bottle of whiskey under his bed."]},{"id":266485,"content":"[transitive always + adverb preposition] to make someone stay in a place, especially a prison or hospital","kindId":9,"examples":["He was kept in prison for a week without charge."]},{"id":265512,"content":"[transitive] to delay someone","kindId":9,"examples":["He should be here by now. What\u0027s keeping him?"]},{"id":267455,"content":"[transitive] to do what you have promised or agreed to do","kindId":9,"examples":["How do I know you\u0027ll keep your word?","patients who fail to keep their appointments"]},{"id":273013,"content":"keep your word/promise","kindId":9,"examples":["How do I know you\u0027ll keep your word?"],"type":"collo"},{"id":266486,"content":" to not tell anyone about a secret that you know","kindId":9,"examples":["Can I trust you to keep a secret?"]},{"id":265513,"content":" to not say anything in order to avoid telling a secret or causing problems","kindId":9,"examples":[]},{"id":267456,"content":" to regularly record written information somewhere","kindId":9,"examples":[]},{"id":273014,"content":" to have or to give someone enough hope and emotional strength to continue living and doing things, in a bad situation","kindId":9,"examples":["That woman\u0027s been through such a lot - I don\u0027t know how she keeps going.","Her letters were the only thing that kept me going while I was in prison.","The library costs £5 million a year to run, and the council can\u0027t afford to keep it going.","Persevere and keep going until you reach your ideal weight.","I\u0027ll have a biscuit to keep me going until dinner time."]},{"id":266487,"content":"[intransitive] if food keeps, it stays fresh enough to be eaten","kindId":9,"examples":["Eat the salmon because it won\u0027t keep till tomorrow."]},{"id":265514,"content":"[transitive] to own and look after animals","kindId":9,"examples":["We keep chickens and a couple of pigs."]},{"id":267457,"content":"[transitive] to stop other people from using something, so that it is available for someone","kindId":9,"examples":["Will you keep a seat for me?"]},{"id":273015,"content":" to make someone wait before you meet them or see them","kindId":9,"examples":["Sorry to keep you waiting - I got stuck in a meeting."]},{"id":266488,"content":" to guard a place or watch around you all the time","kindId":9,"examples":[]},{"id":265515,"content":"[transitive] to own a small business and work in it","kindId":9,"examples":[]},{"id":267458,"content":"[transitive] to provide someone with money, food etc","kindId":9,"examples":["He did not earn enough to keep a wife and children.","There\u0027s enough money there to keep you in champagne for a year!"]},{"id":273016,"content":"keep somebody in something","kindId":9,"examples":["There\u0027s enough money there to keep you in champagne for a year!"],"type":"gram"},{"id":266489,"content":"[transitive] to guard or protect someone","kindId":9,"examples":["The Lord bless you and keep you.","His only thought was to keep the child from harm."]},{"id":265516,"content":" to be the player in a team whose job is to protect the goal or wicket","kindId":9,"examples":[]},{"id":267459,"content":" used to tell someone not to say anything or make any noise","kindId":9,"examples":["Keep quiet! I\u0027m trying to watch the game."]},{"id":273017,"content":" used to ask if someone is well","kindId":9,"examples":["\u0027Hi, Mark! How are you keeping?\u0027 \u0027Oh, not so bad.\u0027"]},{"id":266490,"content":" used to tell someone to be more calm, patient etc","kindId":9,"examples":[]},{"id":265517,"content":" used to say that you do not want or are not interested in something","kindId":9,"examples":["She can keep her wild parties and posh friends - I like the quiet life."]},{"id":267460,"content":" used to say that you can tell someone something or do something later","kindId":9,"examples":["\u0027I don\u0027t have time to listen now.\u0027 \u0027Don\u0027t worry, it\u0027ll keep.\u0027"]},{"id":273018,"content":" to continue to do something, although it is difficult or hard work","kindId":9,"examples":["I know it\u0027s hard, but keep at it! Don\u0027t give up!"]},{"id":266491,"content":" to force someone to continue to work hard and not let them stop","kindId":9,"examples":[]},{"id":265518,"content":" to deliberately not tell someone all that you know about something","kindId":9,"examples":["I got the feeling he was keeping something back."]},{"id":267461,"content":" to not show your feelings, even though you want to very much","kindId":9,"examples":["She was struggling to keep back the tears."]},{"id":273019,"content":" to prevent someone from being as successful as they could be","kindId":9,"examples":["Fear and stereotypes have kept women back for centuries."]},{"id":266492,"content":" to not give or pay something that you were going to give","kindId":9,"examples":["They kept back some of his wages to pay for the damage."]},{"id":265519,"content":" to prevent the size, cost, or quantity of something from increasing or being too great","kindId":9,"examples":["We need to keep costs down."]},{"id":255743,"content":" to succeed in keeping food in your stomach, instead of bringing it up again out of your mouth, when you are ill","kindId":9,"examples":["I could hardly keep anything down for about three days."]},{"id":272004,"content":" used to ask someone to make less noise","kindId":9,"examples":["Keep your voice down - she\u0027ll hear you!","Can you keep it down - I\u0027m trying to work."]},{"id":265520,"content":" to prevent a group of people from becoming as successful and powerful as the other people in a society","kindId":9,"examples":["Plantation owners kept slaves down by refusing them an education."]},{"id":267462,"content":" to prevent someone from doing something or prevent something from happening","kindId":9,"examples":["His ex-wife had kept him from seeing his children.","I hope I haven\u0027t kept you from your work.","Put the pizza in the bottom of the oven to keep the cheese from burning.","The play was so boring, I could hardly keep myself from falling asleep."]},{"id":273020,"content":"keep somebody from (doing) something","kindId":9,"examples":["His ex-wife had kept him from seeing his children.","I hope I haven\u0027t kept you from your work."],"type":"gram"},{"id":266493,"content":"keep something from doing something","kindId":9,"examples":["Put the pizza in the bottom of the oven to keep the cheese from burning."],"type":"gram"},{"id":265521,"content":"keep (yourself) from doing something","kindId":9,"examples":["The play was so boring, I could hardly keep myself from falling asleep."],"type":"gram"},{"id":267463,"content":" to prevent someone from knowing something, by deliberately not telling them about it","kindId":9,"examples":["The government had wanted to keep this information from the public."]},{"id":273021,"content":" to make someone stay in hospital because they are too ill to go home","kindId":9,"examples":["They kept her in overnight for observation."]},{"id":266494,"content":" to force someone to stay inside, especially as a punishment in school","kindId":9,"examples":[]},{"id":265522,"content":" to try to stay friendly with someone, especially because this helps you","kindId":9,"examples":["It\u0027s a good idea to keep in with the boss."]},{"id":267464,"content":" to prevent something from touching or harming something","kindId":9,"examples":["She held an old piece of cloth over them both to keep the rain off.","How are we going to keep the flies off this food?"]},{"id":273022,"content":"keep something off something","kindId":9,"examples":["How are we going to keep the flies off this food?"],"type":"gram"},{"id":266495,"content":" used to tell someone not to touch someone or something","kindId":9,"examples":["Keep your hands off me!"]},{"id":265523,"content":" to not eat, drink, or take something that is bad for you, or to stop someone else from eating, drinking, or taking it","kindId":9,"examples":["Keep off fatty foods.","a programme aimed at keeping teenagers off drugs"]},{"id":267465,"content":" to avoid talking about a particular subject, especially so that you do not upset someone","kindId":9,"examples":[]},{"id":273023,"content":" if you keep weight off, you do not get heavier again after you have lost weight","kindId":9,"examples":[]},{"id":266496,"content":" if rain keeps off, it does not fall","kindId":9,"examples":[]},{"id":265524,"content":" to continue doing something, or to do something many times","kindId":9,"examples":["You just have to keep on trying."]},{"id":267466,"content":"keep on doing something","kindId":9,"examples":["You just have to keep on trying."],"type":"gram"},{"id":273024,"content":" to continue to employ someone, especially for longer than you had planned","kindId":9,"examples":["If you\u0027re good they might keep you on after Christmas."]},{"id":266497,"content":" to talk continuously about something or repeat something many times, in a way that is annoying","kindId":9,"examples":["There\u0027s no need to keep on and on about it!","If I didn\u0027t keep on at the children, they\u0027d never do their homework."]},{"id":265525,"content":" to stay on a particular road, course, piece of ground etc","kindId":9,"examples":["It\u0027s best to keep to the paths."]},{"id":267467,"content":" to do what has been decided in an agreement or plan, or what is demanded by law","kindId":9,"examples":["Keep to the speed limits."]},{"id":273025,"content":" to talk or write only about the subject you are supposed to be talking about","kindId":9,"examples":[]},{"id":266498,"content":" to prevent an amount, degree, or level from becoming higher than it should","kindId":9,"examples":["Costs must be kept to a minimum."]},{"id":265526,"content":" to not tell anyone about something","kindId":9,"examples":["I\u0027d appreciate it if you kept it to yourself."]},{"id":267468,"content":" to live a very quiet private life and not do many things that involve other people","kindId":9,"examples":[]},{"id":273026,"content":" to continue doing something","kindId":9,"examples":["I don\u0027t think I can keep this up any longer."]},{"id":266499,"content":"keep up the good work!","kindId":9,"examples":[],"type":"collo"},{"id":265527,"content":" if a situation keeps up, it continues without stopping or changing","kindId":9,"examples":["How long can the economic boom keep up?"]},{"id":267469,"content":" to go as quickly as someone else","kindId":9,"examples":["I had to walk fast to keep up with him."]},{"id":273027,"content":" to manage to do as much or as well as other people","kindId":9,"examples":["Jack\u0027s having trouble keeping up with the rest of the class."]},{"id":266500,"content":"keep up with the Joneses","kindId":9,"examples":[],"type":"collo"},{"id":265528,"content":" to continue to read and learn about a particular subject, so that you always know about the most recent facts, products etc","kindId":9,"examples":["Employees need to keep up with the latest technical developments."]},{"id":255744,"content":" to make something continue at its present level or amount, instead of letting it decrease","kindId":9,"examples":["NATO kept up the pressure on the Serbs to get out of Kosovo."]},{"id":272005,"content":" if one process keeps up with another, it increases at the same speed and by the same amount","kindId":9,"examples":["Food production is not keeping up with population growth."]},{"id":265529,"content":" to continue to practise a skill so that you do not lose it","kindId":9,"examples":["I used to speak French, but I haven\u0027t kept it up."]},{"id":267470,"content":" to prevent someone from going to bed","kindId":9,"examples":["I hope I\u0027m not keeping you up."]},{"id":273028,"content":" to stay happy, strong, confident etc, by making an effort","kindId":9,"examples":["We sang as we marched, to keep our spirits up."]},{"id":266501,"content":" to pretend that everything in your life is normal and happy even though you are in trouble, especially financial trouble","kindId":9,"examples":[]},{"id":255745,"content":" to write to, telephone, or meet a friend regularly, so that you do not forget each other","kindId":9,"examples":[]}],"2":[{"id":266474,"content":"sự nuôi thân, sự nuôi nấng (gia đình...); cái để nuôi thân, cái để nuôi nấng (gia đình...)","kindId":2,"examples":["to earn one\u0027s keep"]},{"id":265501,"content":"(từ Mỹ,nghĩa Mỹ) người giữ","kindId":2,"examples":[]},{"id":267444,"content":"(từ Mỹ,nghĩa Mỹ) nhà tù, nhà giam","kindId":2,"examples":[]},{"id":273002,"content":"(sử học) tháp, pháo đài, thành luỹ","kindId":2,"examples":[]}],"3":[{"id":266475,"content":"vẫn cứ, cứ, vẫn ở tình trạng tiếp tục","kindId":3,"examples":["the weather will keep fine","to keep laughing","keep straight on for two miles"]},{"id":265502,"content":"(thông tục) ở","kindId":3,"examples":["where do you keep?"]},{"id":267445,"content":"đẻ được, giữ được, để dành được (không hỏng, không thổi...) (đồ ăn...)","kindId":3,"examples":["these apples do not keep"]},{"id":273003,"content":"(+ to) giữ lấy, bám lấy, cứ theo, không rời xa","kindId":3,"examples":["to keep to one\u0027s course","to keep to one\u0027s promise","keep to the right"]},{"id":266476,"content":"(nghĩa bóng) có thể để đấy, có thể đợi đấy","kindId":3,"examples":["that business can keep"]},{"id":265503,"content":"(+ from,  off) rời xa, tránh xa; nhịn","kindId":3,"examples":["keep off!","keep off the grass"]},{"id":267446,"content":"(+ at) làm kiên trì, làm bền bỉ (công việc gì...)","kindId":3,"examples":["to keep had at work for a week"]}],"4":[{"id":273004,"content":"giữ, giữ lại","kindId":4,"examples":["to keep something as a souvenir","to keep hold of something"]},{"id":266477,"content":"giữ, tuân theo, y theo, thi hành, đúng","kindId":4,"examples":["to keep one\u0027s promise (word)","to keep an appointment","to keep the laws"]},{"id":265504,"content":"giữ, canh phòng, bảo vệ; phù hộ","kindId":4,"examples":["to keep the town against the enemy","God keep you!","to keep the goal"]},{"id":267447,"content":"giữ gìn, giấu","kindId":4,"examples":["to keep a secret","to keep something from somebody"]},{"id":273005,"content":"giữ gìn (cho ngăn nắp gọn gàng), bảo quản; chăm sóc, trông nom; quản lý","kindId":4,"examples":["to keep the house for somebody","to keep the cash","to keep a shop"]},{"id":266478,"content":"giữ riêng, để ra, để riêng ra, để dành","kindId":4,"examples":["to keep something to onself","to keep something for future time"]},{"id":265505,"content":"giữ lại, giam giữ","kindId":4,"examples":["to keep somebody in prison"]},{"id":267448,"content":"((thường) + from) giữ cho khỏi, giữ đứng, ngăn lại, nhịn tránh","kindId":4,"examples":["to keep somebody from falling","to keep oneself from smoking"]},{"id":273006,"content":"nuôi, nuôi nấng; bao (gái)","kindId":4,"examples":["to keep a family","to keep bees","to keep a woman","a kept woman"]},{"id":266479,"content":"(thương nghiệp) có thường xuyên để bán","kindId":4,"examples":["do they keep postcards here?"]},{"id":265506,"content":"cứ, cứ để cho, bắt phải","kindId":4,"examples":["to keep silence","to keep someone waiting"]},{"id":267449,"content":"không rời, ở lỳ, vẫn cứ, ở trong tình trạng","kindId":4,"examples":["to keep one\u0027s room"]},{"id":273007,"content":"theo","kindId":4,"examples":["to keep a straight course"]},{"id":266480,"content":"(+ at) bắt làm kiên trì, bắt làm bền bỉ","kindId":4,"examples":["to keep sosmebody at some work"]},{"id":265507,"content":"làm (lễ...), tổ chức (lễ kỷ niện...)","kindId":4,"examples":["to keep one\u0027s birthday"]}]},"kindIdList":[2,3,4,8,9]}]};
                        // build data
                        var words = response.data;
                        $('#search-container').empty().append($('<table cellpadding="0" cellspacing="0"></table>'));
                        var tableWords = $('#search-container').find('table');
                        var index = 0;
                        for (var i = 0; i < words.length; i++) {
                            var row = $('<tr></tr>').attr('class', (index % 2 == 0 ? 'even' : 'odd') + ' word-row').html(words[i].description)
                                    .attr('onclick', 'submitNewWord("' + words[i].description + '", false); return false');

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
        <div>Vietnamese Search:</div>
        <div style="text-align: center"><input id="vietnamese-input" /></div>

        <div class="words-container" id="search-container">
        </div>

    </div>
</div>
</c:when>
<c:otherwise>
    Are you misschara user ? If yes, you can <a href="#" onclick="openLoginDialog();">here</a>.
</c:otherwise>
</c:choose>
