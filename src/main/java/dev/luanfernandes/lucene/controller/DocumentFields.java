package dev.luanfernandes.lucene.controller;

public final class DocumentFields {

    private DocumentFields() {
        throw new AssertionError("Não há instâncias");
    }

    public static final String NAME_FIELD = "name";
    public static final String DATE_FIELD = "date";
    public static final String ITEM_FIELD = "item";
    public static final String FILE_NAME_FIELD = "fileName";
}
