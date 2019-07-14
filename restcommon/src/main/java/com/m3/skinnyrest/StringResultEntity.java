package com.m3.skinnyrest;

public class StringResultEntity implements RestEntity {

    private final String _value;

	public StringResultEntity(String value) {
	    _value = value;
	}

    @Override
    public void toJson(StringBuilder sb) {
        if (_value != null && !_value.isBlank()) {
            sb.append("{ \"success_result\": \"");
            sb.append(_value);
            sb.append("\" }");
        }
    }

}
