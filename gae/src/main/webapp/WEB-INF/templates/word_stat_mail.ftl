<html >
<head ></head >
<body >
Hi boss,

I would like to report the status from the last run:
<ul >
    <li >Total number of failures      : <b >${wordStat.failedCount}</b ></li >
    <li >Total number of found words   : <b >${wordStat.successCount}</b ></li >
    <li >Starting index                : <b >${wordStat.index}</b ></li >
    <li >Process time                  : <b >${wordStat.processTime} seconds</b ></li >
</ul >

<h3 >Word statistics:</h3 >

<table >
    <thead >
    <th >Word</th >
    <th >English</th >
    <th >Vietnamese</th >
    </thead >
    <tbody >
    <#if wordList??>
        <#list wordList as word>
        <tr >${word.word}</tr >
        <tr >${word.haveEnglish}</tr >
        <tr >${word.haveVietnamese}</tr >
        </#list>
    </#if>
    </tbody >
</table >

<#if error?? >
<h3 >Error:</h3 >

<p >
${error}
</p >
</#if>
</body >
</html >
