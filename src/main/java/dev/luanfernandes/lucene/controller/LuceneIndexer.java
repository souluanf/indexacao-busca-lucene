package dev.luanfernandes.lucene.controller;

import static dev.luanfernandes.lucene.util.DateUtils.toDate;
import dev.luanfernandes.lucene.model.ShoppingList;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;

@ApplicationScoped
public class LuceneIndexer {

    @Inject
    private ObjectMapper mapper;

    public void index(Directory index) throws IOException {

        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(index, config);
        for (int i = 0; i < 5; i++) {

            String fileName = "/data/shopping-list-" + (i + 1) + ".json";
            InputStream stream = this.getClass().getResourceAsStream(fileName);
            ShoppingList shoppingList = mapper.readValue(stream, ShoppingList.class);

            shoppingList.setFileName(fileName);
            Document document = toDocument(shoppingList);

            indexWriter.addDocument(document);
            indexWriter.commit();
        }
        indexWriter.close();
    }

    private Document toDocument(ShoppingList shoppingList) {

        Document document = new Document();

        document.add(new StringField(DocumentFields.NAME_FIELD, shoppingList.getName(), Field.Store.YES));
        document.add(new SortedDocValuesField(DocumentFields.NAME_FIELD, new BytesRef(shoppingList.getName())));

        document.add(new StringField(DocumentFields.DATE_FIELD,
                DateTools.dateToString(toDate(shoppingList.getDate()), DateTools.Resolution.DAY), Field.Store.YES));

        shoppingList.getItems().forEach(item -> document.add(new StringField(DocumentFields.ITEM_FIELD, item, Field.Store.YES)));

        document.add(new StringField(DocumentFields.FILE_NAME_FIELD, shoppingList.getFileName(), Field.Store.YES));

        return document;
    }
}
