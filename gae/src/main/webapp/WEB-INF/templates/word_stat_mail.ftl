<html >
<head ></head >
<body >
Hi <b>boss</b>,
<br />
I would like to report the status from the last run:
<ul >
    <li ><pre>Total number of failures      : </pre><b >${wordStat.failedCount}</b ></li >
    <li ><pre>Total number of found words   : </pre><b >${wordStat.successCount}</b ></li >
    <li ><pre>Starting index                : </pre><b >${wordStat.index}</b ></li >
    <li ><pre>Process time                  : </pre><b >${wordStat.processTime} seconds</b ></li >
</ul >

<h3 >Word statistics:</h3 >

<#if wordList??>
<table cellpadding="1" style="border:  1px solid black;">
    <thead >
    <th >Word</th >
    <th >English</th >
    <th >Vietnamese</th >
    </thead >
    <tbody >
        <#list wordList as word>
        <tr >${word.word}</tr >
        <tr >${word.haveEnglish}</tr >
        <tr >${word.haveVietnamese}</tr >
        </#list>
    </tbody >
</#if>
</table >

<#if error?? >
<h3 >Error:</h3 >

<p >
${error}
</p >
</#if>
</body >
</html >
