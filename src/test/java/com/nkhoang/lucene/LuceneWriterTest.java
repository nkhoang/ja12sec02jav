package com.nkhoang.lucene;

import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.service.VocabularyService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext-service.xml", "/applicationContext-dao.xml"})
public class LuceneWriterTest {
	private static final String LUCENE_DIR = "src/test/resources/com/nkhoang/lucene/search/index/word/tmp";

	@Autowired
	private VocabularyService vocabularyService;

	@Test
	public void createWordIndex() throws Exception {
		Word w = vocabularyService.lookupVN("help");
		w.setId(1L);
		LuceneWriterUtil.openWordIndexWriter(LUCENE_DIR);
		LuceneWriterUtil.writeWordToIndex(w);
		LuceneWriterUtil.closeWordIndexWriter();
	}
}
