<html >
<head ></head >
<body >
Hi <b>boss</b>,
<br />
I would like to report the status from the last run:
<ul >
    <li ><pre>Total number of words         : </pre><b >${totalWords}</b ></li >
    <li ><pre>Total number of failures      : </pre><b >${totalFailed}</b ></li >
    <li ><pre>Total number of found words   : </pre><b >${totalSuccess}</b ></li >
</ul >

<h3 >Word statistics:</h3 >
<h4> Found List </h4>
<#if foundList??>
<table cellpadding="1" style="border:  1px solid black;">
    <thead >
    <th >Word</th >

    </thead >
    <tbody >
        <#list foundList as word>
        <tr >${word}</tr >
        </#list>
    </tbody >
</#if>
</table >
<h4> Not Found List </h4>
<#if notFoundList??>
<table cellpadding="1" style="border:  1px solid black;">
    <thead >
    <th >Word</th >
    </thead >
    <tbody >
        <#list notFoundList as word>
        <tr >${word}</tr >
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
