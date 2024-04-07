package com.kkorkunc.paybook.booker;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {
    Logger logger = LogManager.getLogger(getClass());

    Map<Long, Book>books=new HashMap<>(){
        {
            put(1L, new Book("book1", BigDecimal.ONE));
            put(2L, new Book("book2", BigDecimal.TEN));
        }
    };

    public static record Book(String name, BigDecimal amount) {
    }

    public Book get(Long id) throws Exception {
        var book = books.get(id);
        if (book == null) {
            logger.error("couldn't find any book with id:{}", id);
            throw new Exception("book.notFound");
        }

        return book;
    }
}
