package com.serch.server.repositories.company;

import com.serch.server.enums.account.SerchCategory;
import com.serch.server.models.company.SpecialtyKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;

public interface SpecialtyKeywordRepository extends JpaRepository<SpecialtyKeyword, Long> {
    List<SpecialtyKeyword> findByCategory(@NonNull SerchCategory category);
    @Query(
            value = "SELECT * FROM " +
                    "(SELECT *, " +
                    "ts_rank_cd(to_tsvector('english', category), plainto_tsquery(:query)) as category_rank, " +
                    "ts_rank_cd(to_tsvector('english', keyword), plainto_tsquery(:query)) as keyword_rank " +
                    "FROM company.service_keywords " +
                    "WHERE to_tsvector('english', category) @@ plainto_tsquery(:query) " +
                    "OR to_tsvector('english', keyword) @@ plainto_tsquery(:query)) as ranked_keywords " +
                    "ORDER BY GREATEST(category_rank, keyword_rank) DESC",
            nativeQuery = true
    )
    List<SpecialtyKeyword> fullTextSearch(@Param("query") String query);

//    @Query(value = """
//    select   q.id
//    ,        q.type
//    ,        q.url
//    ,        fts doc
//    --,        ts_headline(title, websearch_to_tsquery('simple', :q), 'startSel=<em> stopSel=</em>') as title
//    ,        ts_headline(title, websearch_to_tsquery(:q), 'startSel=<em> stopSel=</em>') as title
//    ,        ts_headline(description, websearch_to_tsquery('simple', :q), 'startSel=<em> stopSel=</em>') as description
//    ,        ts_headline(meta, websearch_to_tsquery(:q), 'startSel=<em> stopSel=</em>') as meta
//    ,        greatest(ts_rank_cd(fts, websearch_to_tsquery(:q)), ts_rank_cd(fts, websearch_to_tsquery('simple', :q))) ranking
//    from     fts_documents q
//    where    fts@@ websearch_to_tsquery(:q) or fts@@ websearch_to_tsquery('simple', :q)
//    union all
//    -- match beginning of the word id query does not contain string
//    select   q.id
//    ,        q.type
//    ,        q.url
//    ,        fts doc
//    ,        ts_headline(title, to_tsquery(regexp_substr(:q,  '([[:word:]]|[[:digit:]])*')||':*'), 'startSel=<em> stopSel=</em>') as title
//    ,        ts_headline(description, to_tsquery(regexp_substr(:q,  '([[:word:]]|[[:digit:]])*')||':*'), 'startSel=<em> stopSel=</em>') as description
//    ,        ts_headline(meta, to_tsquery(regexp_substr(:q,  '([[:word:]]|[[:digit:]])*')||':*'), 'startSel=<em> stopSel=</em>') as meta
//    ,        ts_rank_cd(fts, to_tsquery(regexp_substr(:q,  '([[:word:]]|[[:digit:]])*')||':*')) ranking
//    from     fts_documents q
//    where    fts@@ to_tsquery(regexp_substr(:q,  '([[:word:]]|[[:digit:]])*') ||':*')
//    and      :q ~* '^([[:word:]]|[[:digit:]])*$'
//    order by ranking desc limit :limit
//
//    """ , nativeQuery = true)
}