function initEntry(B, A) {
    setupDoubleClick(B, A, false, "main-container", null, null, null);
}

// playSoundFromFlash('/media/british/uk_pron/u/ukr/ukref/ukrefre015.mp3', this)
// <embed type="application/x-shockwave-flash" src="http://dictionary.cambridge.org/external/flash/speaker.swf?song_url=http://dictionary.cambridge.org/media/british/uk_pron/u/ukr/ukref/ukrefre015.mp3&amp;autoplay=true" height="0" width="0">
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
    var A = document.getElementById("overflow_container");
    if (!A) {
        return;
    }
    var C;
    if (A.className == "hide_overflow_more") {
        A.className = "";
        C = "<< View Less";
    } else {
        A.className = "hide_overflow_more";
        C = "View More >>";
    }
    B.firstChild.nodeValue = C;
}