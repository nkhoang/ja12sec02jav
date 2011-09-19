package com.nkhoang.search;

import com.nkhoang.search.analyzer.LowerCaseAnalyzer;
import generated.Word;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;


public class LuceneWriterUtil {
	private static String INDEX_DIR = "/src/main/resources/com/nkhoang/search/index";
	//thread safe
	private static IndexWriter _indexWriter;


	public static void openIndexWriter() throws IOException {
		openPractIndexWriter(INDEX_DIR);
	}


	public static void openPractIndexWriter(String luceneDir) throws IOException {
		if (_indexWriter == null) {
			synchronized (LuceneWriterUtil.class) {
				if (_indexWriter == null) {
					File indexDir = new File(luceneDir);
					//NIOFS is supposed to be faster on unix based system
					Directory dir = NIOFSDirectory.open(indexDir);
					//RAMDirectory dir = new RAMDirectory(NIOFSDirectory.open(indexDir));
					IndexWriterConfig indexConfig = new IndexWriterConfig(Version.LUCENE_34, null);
					_indexWriter = new IndexWriter(
						dir, getAnalyzer(), IndexWriter.MaxFieldLength.LIMITED);
				}
			}
		}
	}


	public static IndexWriter getIndexWriter() {
		return _indexWriter;
	}


	public static void closePractIndexWriter() throws IOException {
		if (_indexWriter != null) {
			synchronized (LuceneWriterUtil.class) {
				if (_indexWriter != null) {
					try {
						_indexWriter.commit();
						_indexWriter.optimize();
					}
					finally {
						//We want to make sure to close it even if commit or optimize fail.
						_indexWriter.close();
						_indexWriter = null;
					}
				}
			}
		}
	}


	private static Analyzer getAnalyzer() {
		PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(
			new LowerCaseAnalyzer());
		return analyzer;
	}

	public static void writeWordToIndex(
		Word word) throws LuceneWriterNotOpenedException, CorruptIndexException, IOException {
		IndexWriter writer = LuceneWriterUtil.getIndexWriter();

		if (writer == null) {
			throw new LuceneWriterNotOpenedException(
				"Please open the Index Writer " + "by calling openIndexWriter() before trying to write to it.");
		}

		Term key = new Term(LuceneSearchFields.ID, Long.toString(word.getId()));

		Document doc = new Document();
		doc.add(
			new Field(
				LuceneSearchFields.ID, Long.toString(word.getId()), Field.Store.YES, Field.Index.ANALYZED));

		addField(
			doc, LuceneSearchFields.FIRST_NAME, word.getFirstName(), Field.Store.NO, Field.Index.ANALYZED);

		addField(
			doc, LuceneSearchFields.MIDDLE_NAME, word.getMiddleName(), Field.Store.NO, Field.Index.ANALYZED);

		addField(
			doc, LuceneSearchFields.LAST_NAME, word.getLastName(), Field.Store.NO, Field.Index.ANALYZED);

		//Add the addresses to the Index
		for (PractAddress address : word.getAddresses()) {
			PractLocation loc = address.getAddress();
			if (loc != null) {
				if (StringUtils.isNotBlank(loc.getZip5())) {
					doc.add(
						new Field(
							LuceneSearchFields.ZIP_CODE, loc.getZip5(), Field.Store.NO, Field.Index.ANALYZED));
				}

				if (StringUtils.isNotBlank(loc.getState())) {
					doc.add(
						new Field(
							LuceneSearchFields.STATE_CITY_ADDR1,
							loc.getState() + LuceneSearchFields.LUCENE_DELIM + loc.getCity() +
							LuceneSearchFields.LUCENE_DELIM + loc.getStreet1(), Field.Store.NO, Field.Index.ANALYZED));
				}
			}

			for (PractPhone phone : address.getAddressPhones()) {
				if (phone.getContactNumber().length() == 10) {
					doc.add(
						new Field(
							LuceneSearchFields.CONTACT_NUMBER, phone.getContactNumber().substring(3, 10),
							Field.Store.NO, Field.Index.ANALYZED));
				}
				doc.add(
					new Field(
						LuceneSearchFields.CONTACT_NUMBER, phone.getContactNumber(), Field.Store.NO,
						Field.Index.ANALYZED));
			}
			for (PractFax fax : address.getAddressFaxes()) {
				if (fax.getContactNumber().length() == 10) {
					doc.add(
						new Field(
							LuceneSearchFields.CONTACT_NUMBER, fax.getContactNumber().substring(3, 10), Field.Store.NO,
							Field.Index.ANALYZED));
				}
				doc.add(
					new Field(
						LuceneSearchFields.CONTACT_NUMBER, fax.getContactNumber(), Field.Store.NO,
						Field.Index.ANALYZED));
			}
		}

		//Add all of the various ID types to the index
		for (PractDeaId id : word.getDeaIds()) {
			doc.add(
				new Field(
					LuceneSearchFields.DEA, id.getIdValue(), Field.Store.NO, Field.Index.ANALYZED));
		}

		// Add DPS identifier to the index.
		for (PractDpsId id : word.getDpsIds()) {
			doc.add(
				new Field(
					PractDpsId.ID_TYPE, id.getIdValue(), Field.Store.NO, Field.Index.ANALYZED));
		}

		for (PractNpiId id : word.getNpiIds()) {
			doc.add(
				new Field(
					LuceneSearchFields.NPI, id.getIdValue(), Field.Store.NO, Field.Index.ANALYZED));
		}
		for (PractTaxId id : word.getTaxIds()) {
			doc.add(
				new Field(
					LuceneSearchFields.TIN, id.getIdValue(), Field.Store.NO, Field.Index.ANALYZED));
		}
		for (PractStateLicense id : word.getStateLicenseIds()) {
			doc.add(
				new Field(
					LuceneSearchFields.STLIC, id.getIdValue() + LuceneSearchFields.LUCENE_DELIM + id.getStateCode(),
					Field.Store.NO, Field.Index.ANALYZED));
		}
		for (PractUpinId id : word.getUpinIds()) {
			doc.add(
				new Field(
					LuceneSearchFields.UPIN, id.getIdValue(), Field.Store.NO, Field.Index.ANALYZED));
		}
		for (PractSsnId id : word.getSsnIds()) {
			if (id.getIdValue().length() == 9) {
				doc.add(
					new Field(
						LuceneSearchFields.SSN, id.getIdValue().substring(5, 9), Field.Store.NO, Field.Index.ANALYZED));
			}
			doc.add(
				new Field(
					LuceneSearchFields.SSN, id.getIdValue(), Field.Store.NO, Field.Index.ANALYZED));
		}
		for (PractSurescriptsId id : word.getSurescriptsIds()) {
			doc.add(
				new Field(
					LuceneSearchFields.SURESCRIPTS_ID, id.getIdValue(), Field.Store.NO, Field.Index.ANALYZED));
		}

		//Add the practitioner Date of Birth to the index
		if (!StringUtils.isEmpty(word.getDob())) {
			addField(
				doc, LuceneSearchFields.DOB, BirthDateParser.getSplitDobString(word.getDob()), Field.Store.NO,
				Field.Index.ANALYZED);
		}

		//Add the Practitioner Type to the Index
		addField(
			doc, LuceneSearchFields.PRACT_TYPE, word.getPractType(), Field.Store.NO, Field.Index.ANALYZED);

		//Add the Specialties to the Index
		for (PractSpecialty specialty : word.getSpecialties()) {
			doc.add(
				new Field(
					LuceneSearchFields.SPECIALTY, specialty.getSpecialtyCode(), Field.Store.NO, Field.Index.ANALYZED));
		}

		// Add the migrated piids to the Index
		for (String migratedPiid : word.getMigratedPiids()) {
			doc.add(
				new Field(
					LuceneSearchFields.MIGRATED_PIID, migratedPiid, Field.Store.NO, Field.Index.ANALYZED));
		}

		for (PractTaxonomy taxonomy : word.getTaxonomies()) {
			doc.add(
				new Field(
					LuceneSearchFields.TAXONOMY, taxonomy.getTaxonomyCode(), Field.Store.NO, Field.Index.ANALYZED));
		}

		addField(
			doc, LuceneSearchFields.IS_VENDIBLE, Boolean.toString(word.isVendible()), Field.Store.NO,
			Field.Index.ANALYZED);

		writer.updateDocument(key, doc);
	}

	public static void deletePractitionerFromIndex(
		String piid) throws LuceneWriterNotOpenedException, CorruptIndexException, IOException {
		IndexWriter writer = LuceneWriterUtil.getIndexWriter();

		if (writer == null) {
			throw new LuceneWriterNotOpenedException(
				"Please open the Index Writer " + "by calling openIndexWriter() before trying to write to it.");
		}

		Term key = new Term(LuceneSearchFields.GUID, piid.toLowerCase());
		writer.deleteDocuments(key);
	}


	//Helper method to check for a null/empty in the value field
	private static void addField(Document doc, String field, String value, Field.Store store, Field.Index analyze) {
		if (StringUtils.isNotBlank(value)) {
			doc.add(new Field(field, value, store, analyze));
		}
	}
}
