package com.nkhoang.gae.service.impl;

import com.nkhoang.gae.dao.MeaningDao;
import com.nkhoang.gae.dao.VocabularyDao;
import com.nkhoang.gae.dao.impl.VocabularyDaoImpl;
import com.nkhoang.gae.model.Meaning;
import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.service.VocabularyService;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VocabularyServiceImpl implements VocabularyService {
	private static final Log log = LogFactory.getLog(VocabularyDaoImpl.class);
	private static final String CONSTANT_MEANING_CONTAINER = "div";
	private static final String CONSTANT_CLASS_RESULT = "result";

	private MeaningDao meaningDao;
	private VocabularyDao vocabularyDao;

	/*public static void main(String[] args) {
		Word word = lookup("Pungent");
		lookupEN(word, "Pungent");
	}*/

	public List<Word> getAllWords() {
		List<Word> words = vocabularyDao.getAll();
		for (Word w : words) {
			populateWord(w);
		}
		return words;
	}

	public List<Word> getAllWordsFromUser(List<Long> wordIds) {
		List<Word> words = new ArrayList<Word>();
		for (Long id : wordIds) {
			Word word = get(id);
			if (word != null) {
				words.add(word);
			}
		}
		return words;
	}

	Word populateWord(Word w) {
		// populate word by Meaning
		List<Long> meaningIds = w.getMeaningIds();
		for (Long meaningId : meaningIds) {
			Meaning meaning = meaningDao.get(meaningId);
			w.addMeaning(meaning.getKindId(), meaning);
		}
		return w;
	}

	Word get(Long id) {
		Word word = vocabularyDao.get(id);

		return populateWord(word);
	}

	public Word save(String lookupWord) {
		log.info("Saving word : " + lookupWord);
		Word word = lookup(lookupWord);
		word = lookupEN(word, lookupWord);
		// build list of meaning
		for (int i = 0; i < Word.WORD_KINDS.length; i++) {			
			List<Meaning> meanings = word.getMeaning(Long.parseLong(i + ""));
			if (meanings != null && meanings.size() > 0) {
				log.info("found : " + meanings.size() + " meanings for this word");
				for (Meaning meaning : meanings) {
					// save
					Meaning savedMeaning = meaningDao.save(meaning);
					word.addMeaningId(savedMeaning.getId());
				}
			} else {
				// log.info(word.getMeanings());
			}
		}

		vocabularyDao.save(word);

		return word;
	}

	public Word lookupEN(Word aWord, String word) {
		log.info("looking up word EN : " + word);
		try {
			Source source = checkWordExistence(word.toLowerCase());
			int i = 1;
			if (source == null) {
				// check it again
				source = checkWordExistence(word.toLowerCase() + "_" + i);
			}
			while (source != null) {
				// process the content
				List<Element> contentEles = source.getAllElementsByClass("gwblock ");
				// should be one
				Element targetContent = contentEles.get(0);
				String kind = "";
				// get kind
				List<Element> headers = targetContent.getAllElementsByClass("header");
				if (headers != null && headers.size() > 0) {
					Element header = headers.get(0);

					List<Element> kinds = header.getAllElementsByClass("pos");
					if (kinds != null && kinds.size() > 0) {
						kind = kinds.get(0).getContent().toString().trim();
					}
				}
				// get Pron
				List<Element> additional_headers = targetContent.getAllElementsByClass("additional_header");
				if (additional_headers != null && additional_headers.size() > 0) {
					Element additional_header = additional_headers.get(0);
					List<Element> prons = additional_header.getAllElementsByClass("pron");
					if (prons != null && prons.size() > 0) {
						String pron = prons.get(0).getContent().toString();
						log.info("Pron: " + pron);
						aWord.setPron(pron);
					}

					// get mp3 file
					List<Element> sounds = additional_header.getAllElementsByClass("sound");
					// may have 2
					if (sounds != null && sounds.size() > 0) {
						Element sound = null;
						if (sounds.size() == 1) {
							sound = sounds.get(0);
						} else if (sounds.size() == 2) {
							sound = sounds.get(1);
						}

						// process
						String soundSource = sound.getAttributeValue("onclick");
						String soundSrc = soundSource.replace("/media", "http://dictionary.cambridge.org/media");
						log.info("Found a sound source: " + soundSrc);
						aWord.setSoundSource(soundSrc);
					}
				}
				// get meaning
				List<Element> meaningList = targetContent.getAllElementsByClass("gwblock_b");
				if (meaningList != null && meaningList.size() > 0) {
					Element meanings = meaningList.get(0);

					List<Element> meaningsChildren = meanings.getChildElements();
                    for (Element aMeaningsChildren : meaningsChildren) {
                        Meaning m = new Meaning();
                        // check the class name
                        if (aMeaningsChildren.getName().equals("div")
                                && aMeaningsChildren.getAttributeValue("class").trim().equals("sense")) {
                            // get meaning
                            List<Element> meaningContent = aMeaningsChildren.getAllElementsByClass("def");
                            if (meaningContent != null && meaningContent.size() > 0) {
                                m = new Meaning(meaningContent.get(0).getContent().toString(), aWord.getKindidmap()
                                        .get(kind));
                            }

                            // get examples
                            List<Element> examples = aMeaningsChildren.getAllElementsByClass("examp");
                            if (examples != null && examples.size() > 0) {
                                for (Element example : examples) {
                                    m.addExample(example.getChildElements().get(0).getContent().toString());
                                }
                            }
                            // add this meaning
                            aWord.addMeaning(aWord.getKindidmap().get(kind), m);
                        }
                    }
				}

				source = checkWordExistence(word + "_" + ++i);
				// log.info(aWord.getMeanings());
			}
		} catch (Exception e) {
			log.error(e);
		}
		return aWord;
	}

	private Source checkWordExistence(String word) {
		try {
			URL url = new URL("http://dictionary.cambridge.org/dictionary/british/" + word);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			// get inputStream
			InputStream is = connection.getInputStream();
			// create source HTML
			Source source = new Source(is);

			List<Element> contentEles = source.getAllElementsByClass("definition-title");

			if (contentEles == null || contentEles.size() == 0) {
				return null;
			} else {
				return source;
			}
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	public Word lookup(String word) {
		log.info("Looking up word : " + word);
		try {
			URL url = new URL("http://m.vdict.com/?word=" + word + "&dict=1&searchaction=Lookup");

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			// get inputStream
			InputStream is = connection.getInputStream();
			// create source HTML
			Source source = new Source(is);
			// System.out.println(source.toString());

			List<Element> contentEles = source.getAllElementsByClass(CONSTANT_CLASS_RESULT);

			if (contentEles == null || contentEles.size() == 0) {
				return null;
			}
			// expect only one exists
			Element targetContent = contentEles.get(0);
			Word aWord = new Word();
			aWord.setDescription(word);
			Meaning meaning = new Meaning();
			String kind = "";

			// System.out.println(targetContent.getContent());
			List<Element> eles = targetContent.getChildElements();
			for (Element ele : eles) {
				// System.out.println(ele);
				if (ele.getName().equals("div")) {
					kind = "";
				}
				// get the kind
				if (ele.getAttributeValue("class") != null && ele.getAttributeValue("class").equals("phanloai")) {
					// set the word kind.
					kind = ele.getContent().toString();
					if (kind != null) {
						String[] words = kind.split(" ");
						kind = "";
						int limit = words.length > 3 ? 3 : words.length;
						for (int i = 0; i < limit; i++) {
							kind += words[i] + " ";
						}
						kind = kind.trim();
						log.info(Arrays.toString(kind.getBytes("UTF-8")));
						kind = new String(kind.getBytes("UTF-8"));
						log.info("Kind : " + kind);
					}
				}
				if (ele.getName().equals("ul") && StringUtils.isNotEmpty(kind)) {
					// convert kind
					// log.info(Arrays.toString(kind.getBytes("UTF-8")));
					String className = ele.getAttributeValue("class");
					if (className != null && className.equals("list1")) {
						List<Element> meaningLis = ele.getChildElements();
						for (Element meaningLi : meaningLis) {
							if (meaningLi.getName().equals("li")) {
								List<Element> liContent = meaningLi.getChildElements();
								for (Element content : liContent) {
									if (content.getName().equals("b")) {
										String contentRaw = content.getContent().toString();
										log.info("content : " + contentRaw);
										meaning = new Meaning(contentRaw, aWord.getKindidmap().get(kind));
									}
									if (content.getName().equals("ul")) {
										meaning.addExample(content.getChildElements().get(0).getChildElements().get(0)
												.getContent().toString());
									}
								}
							}
							if (meaning != null) {
								// log.info(meaning.getContent());
								aWord.addMeaning(aWord.getKindidmap().get(kind), meaning);
							}
						}
					}
				}
			}
			// log.info(aWord.getKindidmap());
			return aWord;
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	public MeaningDao getMeaningDao() {
		return meaningDao;
	}

	public void setMeaningDao(MeaningDao meaningDao) {
		this.meaningDao = meaningDao;
	}

	public VocabularyDao getVocabularyDao() {
		return vocabularyDao;
	}

	public void setVocabularyDao(VocabularyDao vocabularyDao) {
		this.vocabularyDao = vocabularyDao;
	}
}
