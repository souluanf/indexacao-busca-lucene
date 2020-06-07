package dev.luanfernandes.lucene;

import dev.luanfernandes.lucene.controller.LuceneIndexer;
import dev.luanfernandes.lucene.controller.LuceneSearcher;
import dev.luanfernandes.lucene.model.ShoppingList;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.status.StatusLogger;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class Application {

    public static void main(String[] args) throws IOException {

        StatusLogger.getLogger().setLevel(Level.OFF);

        Weld weld = new Weld();
        WeldContainer container = weld.initialize();

        Directory index = new RAMDirectory();
        LuceneIndexer indexer = container.select(LuceneIndexer.class).get();
        LuceneSearcher searcher = container.select(LuceneSearcher.class).get();

        indexer.index(index);

        System.out.println("\nTODAS AS LISTAS: ");
        System.out.println("----------------------------------------------");
        searcher.findAll(index).forEach(Application::printShoppingList);

        System.out.println("\nPOR NOME DA PESSOA");
        System.out.println("----------------------------------------------");
        searcher.findByPersonName(index, "Camila Alvarenga").forEach(Application::printShoppingList);

        System.out.println("\nPOR NOME DE ITEM CONTIDO NA LISTA");
        System.out.println("----------------------------------------------");
        searcher.findByItem(index, "Mussarela").forEach(Application::printShoppingList);

        System.out.println("\nPOR INTERVALO DE TEMPO");
        System.out.println("----------------------------------------------");
        searcher.findByDateRange(index, LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 2)).forEach(Application::printShoppingList);

        container.shutdown();
    }

    private static void printShoppingList(ShoppingList shoppingList) {
        System.out.println("Arquivo: " + shoppingList.getFileName());
        System.out.println("Nome: " + shoppingList.getName());
        System.out.println("Data: " + shoppingList.getDate().format(DateTimeFormatter.ISO_DATE));
        System.out.println("Items: " + shoppingList.getItems().stream().collect(Collectors.joining(", ")));
        System.out.println();
    }
}
