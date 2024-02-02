package com.unir.products.controller;

import com.unir.products.model.pojo.Book;
import com.unir.products.model.request.CreateBookRequest;
import com.unir.products.service.BooksServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Books Controller", description = "Microservicio encargado de exponer operaciones CRUD sobre productos alojados en la base de datos en memoria de la biblioteca.")
public class BooksController {


    private final BooksServiceImpl service;

    @GetMapping("/books")
    @Operation(
            operationId = "Obtener libros",
            description = "Operacion de lectura",
            summary = "Se devuelve una lista de todos los libros almacenados en la base de datos.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class)))
    @ApiResponse(
            responseCode = "404",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "No se ha encontrado el producto con el identificador indicado.")
    @ApiResponse(
            responseCode = "500",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "Error interno del servidor interno.")
    public ResponseEntity<List<Book>> getProducts(
            @RequestHeader Map<String, String> headers,
            @Parameter(name = "author", description = "Nombre del autor del libro. No tiene por que ser exacto", example = "Gabriel", required = false)
            @RequestParam(required = false) String author,
            @Parameter(name = "title", description = "Nombre del libro. No tiene por que ser exacto", example = "Cien años de soledad", required = false)
            @RequestParam(required = false) String title,
            @Parameter(name = "isbn", description = "Código isbn del del libro. Debe ser exacto", example = "ISBN 0-7645-2641-3", required = false)
            @RequestParam(required = false) String isbn,
            @Parameter(name = "age", description = "Año de publicación del libro. Debe ser exacto", example = "2023", required = false)
            @RequestParam(required = false) Short age,
            @Parameter(name = "synapsis", description = "Sinopsis del libro. No tiene que ser exacto, por ejemplo una palabra clave", example = "Soledad", required = false)
            @RequestParam(required = false) String synapsis,
            @Parameter(name = "stock", description = "Stock del libro. Debe ser exacto", example = "5", required = false)
            @RequestParam(required = false) Short stock) {

        log.info("Headers: {}", headers);
        List<Book> books = service.getBooks(author,title,isbn,age,synapsis,stock);
        return ResponseEntity.ok(Objects.requireNonNullElse(books, Collections.emptyList()));
    }

    @GetMapping("/books/{idBook}")
    @Operation(
            operationId = "Obtener libros por identificador",
            description = "Operacion de lectura",
            summary = "Se devuelve un libro correspondiente a partir del identificador")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class)))
    @ApiResponse(
            responseCode = "404",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "No se ha encontrado el libro con el identificador indicado.")
    @ApiResponse(
            responseCode = "500",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "Error interno del servidor interno.")
    public ResponseEntity<Book> getProduct(
            @RequestHeader Map<String, String> headers, @PathVariable Long idBook) {

        log.info("Headers: {}", headers);
        log.info("Request received for book: {}", idBook);

        Book book = service.getBook(idBook);
        if(book != null){
            return ResponseEntity.ok(book);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/books")
    @Operation(
            operationId = "Insertar un libro",
            description = "Operacion de escritura",
            summary = "Se crea un libro a partir de sus datos.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del libro a crear.",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateBookRequest.class))))
    @ApiResponse(
            responseCode = "201",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class)))
    @ApiResponse(
            responseCode = "400",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "Datos incorrectos introducidos.")
    @ApiResponse(
            responseCode = "500",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "Error interno del servidor interno.")
    public ResponseEntity<Book> addBook(@RequestBody CreateBookRequest request) {

        Book createdProduct = service.createBook(request);
        if (createdProduct != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/books/{idBook}")
    @Operation(
            operationId = "Modificar parcialmente un libro",
            description = "RFC 7386. Operacion de escritura",
            summary = "RFC 7386. Se modifica parcialmente un libro.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del libro a crear.",
                    required = true,
                    content = @Content(mediaType = "application/merge-patch+json", schema = @Schema(implementation = String.class))))
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class)))
    @ApiResponse(
            responseCode = "400",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "Producto inválido o datos incorrectos introducidos.")
    public ResponseEntity<Book> patchBook(@PathVariable Long idBook, @RequestBody String patchBody){
        log.info("init patch");
        Book bookPatched = service.updateBook(idBook,patchBody);
        if (bookPatched != null){
            return ResponseEntity.ok(bookPatched);
        }else{
            return ResponseEntity.badRequest().build();
        }
    }


    @DeleteMapping("/books/{idBook}")
    @Operation(
            operationId = "Eliminar un libro",
            description = "Operacion de escritura",
            summary = "Se elimina un libro a partir de su identificador.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)))
    @ApiResponse(
            responseCode = "404",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "No se ha encontrado el producto con el identificador indicado.")
    public ResponseEntity<Void> deleteBook(@PathVariable Long idBook) {
        Boolean removed = service.removeBook(idBook);
        if (Boolean.TRUE.equals(removed)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    } 

}
