package com.unir.books.data;

import com.unir.books.model.db.Book;
import com.unir.books.model.response.AggregationDetails;
import com.unir.books.model.response.BooksQueryResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DataAccessRepository {

    @Value("${server.fullAddress}")
    private String serverFullAddress;

    // Esta clase (y bean) es la unica que usan directamente los servicios para
    // acceder a los datos.
    private final BookRepository bookRepository;
    private final ElasticsearchOperations elasticClient;

    private final String[] descriptionSearchFields = {"Title", "Title._2gram", "Title._3gram","Author","Isbn","Age","Synapsis"};

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public Boolean deleteBook(Book book) {
        bookRepository.delete(book);
        return Boolean.TRUE;
    }

    public Optional<Book> getBook(String id) {
        return bookRepository.findById(id);
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @SneakyThrows
    public BooksQueryResponse findBooks(String param, String gender, Boolean aggregate) {
        BoolQueryBuilder querySpec = QueryBuilders.boolQuery();

        if (!StringUtils.isEmpty(gender)) {
            querySpec.must(QueryBuilders.termQuery("Gender",gender));
        }

        if (!StringUtils.isEmpty(param)) {
            querySpec.must(QueryBuilders.multiMatchQuery(param, descriptionSearchFields)
                    .type(MultiMatchQueryBuilder.Type.BOOL_PREFIX)
                    .operator(Operator.OR));

        }

        //Si no he recibido ningun parametro, busco todos los elementos.
        if (!querySpec.hasClauses()) {
            querySpec.must(QueryBuilders.matchAllQuery());
        }

        //Filtro implicito
        //No le pido al usuario que lo introduzca pero lo aplicamos proactivamente en todas las peticiones
        //En este caso, que los libros sean visibles si el stock es mayor a cero.
        querySpec.must(QueryBuilders.rangeQuery("Stock").gt(0));

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(querySpec);

        if (aggregate) {
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("Gender Aggregation").field("Gender").size(1000));
            nativeSearchQueryBuilder.withMaxResults(0);
        }

        //Opcionalmente, podemos paginar los resultados
        //nativeSearchQueryBuilder.withPageable(PageRequest.of(0, 10));

        Query query = nativeSearchQueryBuilder.build();
        SearchHits<Book> result = elasticClient.search(query, Book.class);

        List<AggregationDetails> responseAggs = new LinkedList<>();

        if (result.hasAggregations()) {
            Map<String, Aggregation> aggs = result.getAggregations().asMap();
            ParsedStringTerms countryAgg = (ParsedStringTerms) aggs.get("Gender Aggregation");

            countryAgg.getBuckets()
                    .forEach(
                            bucket -> responseAggs.add(
                                    new AggregationDetails(
                                            bucket.getKey().toString(),
                                            (int) bucket.getDocCount(),
                                            serverFullAddress + "/books?gender=" + bucket.getKey() )));
        }
        return new BooksQueryResponse(result.getSearchHits().stream().map(SearchHit::getContent).toList(), responseAggs);
    }



}
