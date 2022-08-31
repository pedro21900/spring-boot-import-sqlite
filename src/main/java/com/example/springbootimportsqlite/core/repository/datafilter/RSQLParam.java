package com.example.springbootimportsqlite.core.repository.datafilter;

import com.github.tennaito.rsql.jpa.JpaPredicateVisitor;
import com.github.tennaito.rsql.misc.ArgumentFormatException;
import com.github.tennaito.rsql.misc.DefaultArgumentParser;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <pre>{@code
 * @RestController
 * @RequestMapping("/api/foos")
 * @AllArgsConstructor
 * public class FooRest {
 *
 *     private final FooRepository fooRepository;
 *
 *     @GetMapping
 *     public ResponseEntity<PageEx<?>> findAll(RSQLParam q, Pageable pageable) {
 *         return ResponseEntity.ok(
 *            fooRepository.findAll(q.getSpeciification(), pageable)
 *         );
 *     }
 *
 * }
 * }</pre>
 */
@AllArgsConstructor
public class RSQLParam {
    private final EntityManager entityManager;
    @Getter
    private final String q;

    public <T> Specification<T> getSpecification() {
        if (Objects.isNull(q)) return null;
        return (root, cq, cb) -> {
            JpaPredicateVisitor<T> predicateVisitor = new JpaPredicateVisitor<>();
            predicateVisitor.getBuilderTools().setArgumentParser(new AppArgumentParser());
            RSQLVisitor<Predicate, EntityManager> visitor = predicateVisitor.defineRoot(root);
            Node rootNode = new RSQLParser().parse(q);
            return rootNode.accept(visitor, entityManager);
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RSQLParam rsqlParam = (RSQLParam) o;
        return q.equals(rsqlParam.q);
    }

    @Override
    public int hashCode() {
        return Objects.hash(q);
    }

    static class AppArgumentParser extends DefaultArgumentParser {

        @SuppressWarnings("unchecked")
        @Override
        public <T> T parse(String argument, Class<T> type) throws ArgumentFormatException, IllegalArgumentException {
            if (type.equals(LocalDate.class)) {
                return (T) LocalDate.parse(argument);
            }
            if (type.equals(LocalDateTime.class)) {
                return (T) LocalDateTime.parse(argument);
            }
            if (type.equals(Short.class)) {
                return (T) Short.valueOf(argument);
            }
            if (type.equals(BigDecimal.class)) {
                return (T) new BigDecimal(argument);
            }
            return super.parse(argument, type);
        }

    }

}
