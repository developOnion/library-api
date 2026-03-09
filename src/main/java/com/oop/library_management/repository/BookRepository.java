package com.oop.library_management.repository;

import com.oop.library_management.model.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

	List<Book> findByTitleContainingIgnoreCase(String title);

	@Query("""
				SELECT DISTINCT b FROM Book b
				JOIN b.authors a
				WHERE LOWER(CONCAT(a.firstName, " ", a.lastName))
							LIKE LOWER(CONCAT("%", :fullName, "%"))
			""")
	List<Book> findByAuthorFullName(@Param("fullName") String fullName);

	@Query("""
			SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END
			FROM Book b
			WHERE b.isbn IS NULL
			AND LOWER(b.title) = LOWER(:title)
			AND SIZE(b.authors) = :authorCount
			AND NOT EXISTS (
			    SELECT 1 FROM Author a
			    WHERE a MEMBER OF b.authors
			    AND a.id NOT IN :authorIds
			)
			AND NOT EXISTS (
			    SELECT 1 FROM Author a
			    WHERE a.id IN :authorIds
			    AND a NOT MEMBER OF b.authors
			)
			""")
	boolean existsNullIsbnWithTitleAndExactAuthors(
			@Param("title") String title,
			@Param("authorIds") Set<Long> authorIds,
			@Param("authorCount") Integer authorCount
	);

	@Query("""
			SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END
			FROM Book b
			WHERE b.isbn IS NULL
			AND LOWER(b.title) = LOWER(:title)
			AND SIZE(b.authors) = :authorCount
			AND b.id != :excludeId
			AND NOT EXISTS (
			    SELECT 1 FROM Author a
			    WHERE a MEMBER OF b.authors
			    AND a.id NOT IN :authorIds
			)
			AND NOT EXISTS (
			    SELECT 1 FROM Author a
			    WHERE a.id IN :authorIds
			    AND a NOT MEMBER OF b.authors
			)
			""")
	boolean existsNullIsbnWithTitleAndExactAuthorsAndIdNot(
			@Param("title") String title,
			@Param("authorIds") Set<Long> authorIds,
			@Param("authorCount") Integer authorCount,
			@Param("excludeId") Long excludeId
	);

	boolean existsByIsbn(String isbn);

	boolean existsByIsbnAndIdNot(String isbn, Long id);

	boolean existsByIdAndIsbnIsNull(Long id);
	
}
