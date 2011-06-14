package com.nkhoang.common.abtab;

public class RawDataParseException extends Exception {

	private static final long serialVersionUID = -5652343226346326613L;
	private Integer _column;
	private Integer _line;
	private String  _table;

	public RawDataParseException(String message) {
		super(message);
	}

	public RawDataParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public RawDataParseException(Throwable cause) {
		super(cause);
	}

	public RawDataParseException(Throwable cause, String tableName, Integer lineNumber, Integer columnNumber) {
		super(cause);
		_table = tableName;
		_line = lineNumber;
		_column = columnNumber;
	}

	public RawDataParseException(String message, String tableName, Integer lineNumber, Integer columnNumber) {
		super(message);
		_table = tableName;
		_line = lineNumber;
		_column = columnNumber;
	}

	public RawDataParseException(
		String message, Throwable cause, String tableName, Integer lineNumber, Integer columnNumber) {
		super(message, cause);
		_table = tableName;
		_line = lineNumber;
		_column = columnNumber;
	}

	public void setLineNumber(Integer line) {
		_line = line;
	}

	public Integer getLineNumber() {
		return _line;
	}

	public void setColumnNumber(Integer column) {
		_column = column;
	}

	public Integer getColumnNumber() {
		return _column;
	}

	public void setTableName(String table) {
		_table = table;
	}

	@Override
	public String getMessage() {
		String rtn = "";
		if (_table != null) {
			rtn = _table + ": ";
		}
		if (_line != null) {
			rtn += "Line " + _line + ": ";
		}
		if (_column != null) {
			rtn += "Column " + _column + ": ";
		}
		rtn += super.getMessage();
		return rtn;
	}

}
