<html >
<head ></head >
<body >
Hi <b >boss</b >,
<br />


<#if wordList??>
<p >
    This is the list of deleted WordItem entity Ids:
</p >

<ul >
    <#list wordList as word>
        <li >${word.word} - [id: ${word.id}]</li >
    </#list>
</ul >
<#else >
    No record.
</#if>

<p >These are first 100 items in the list</p >
<#if sampleList??>
<ul >
    <#list sampleList as sample>
        <li >${sample.word} - ${sample.id}</li >
    </#list>
</ul >
</#if>
</body >
</html >