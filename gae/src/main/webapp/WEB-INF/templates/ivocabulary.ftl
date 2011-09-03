<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Vocabulary sourceLanguage="English" targetLanguage="English">
    <Info>
        <Author>Hoang Nguyen Khanh</Author>
        <Comment>Built by iVocabulary Web</Comment>
        <Date>2010-11-20T08:00:00.000+07:00</Date>
        <Title>Hoang Nguyen Khanh Word List</Title>
        <WordCount>44</WordCount>
    </Info>
    <Root>
        <Chapter title="${chapterTitle}">
			<Page title="${pageTitle}">
				<#list words as w>
					<Word sourceWord="${w.word.description w.word.pron}" targetWord="
					<#list w.meanings as meaning>
						${meaning}
					</#list>
					">
						<Comment>
						<#list w.comment as comment>
							${comment}
						</#list>
						</Comment>
					</Word>
				</#list>
			</Page>
		</Chapter>
    </Root>
</Vocabulary>
