<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Vocabulary sourceLanguage="${sourceLang!"English"}" targetLanguage="${targetLang!"English"}">
    <Info>
        <Author>${author!"Nguyen Khanh Hoang"}</Author>
        <Comment>${documentComment!""}</Comment>
        <Date>${date}</Date>
        <Title>${documentTitle}</Title>
        <WordCount>${totalWordCount}</WordCount>
    </Info>
    <Root>
        <Chapter title="${chapterTitle}">
            <Page title="${pageTitle}">
<#list words as w>
                <Word sourceWord="${w.description + " " + w.pron!""}" targetWord="
	<#list w.meanings as m>
                    ${m.content}
	</#list>
                ">
                    <Comment>
	<#list w.meanings as m>
		<#if m.examples??>
			<#list m.examples as ex>
                        ${ex}
			</#list>
		</#if>
	</#list>
                    </Comment>						
                </Word>
</#list>
            </Page>
        </Chapter>
    </Root>
</Vocabulary>
