package com.unir.books.controller;

import com.unir.books.model.ResponseDeleteDto;
import com.unir.books.model.db.Book;
import com.unir.books.model.request.CreateBookRequest;
import com.unir.books.model.response.BooksQueryResponse;
import com.unir.books.service.BooksServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

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
    public ResponseEntity<BooksQueryResponse> getProducts(
            @RequestHeader Map<String, String> headers,
            @Parameter(name = "param", description = "Parametro de busqueda", example = "Novela", required = false)
            @RequestParam(required = false) String param,
            @Parameter(name = "gender", description = "Genero del libro", example = "Novela", required = false)
            @RequestParam(required = false) String gender,
            @Parameter(name = "aggregate", description = "Variable en true en caso de tener un agregado en la busqueda")
            @RequestParam(required = false, defaultValue = "false") Boolean aggregate) {

        log.info("Headers: {}", headers);
        BooksQueryResponse books = service.getBooks(param,gender, aggregate);
        return ResponseEntity.ok(books);
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
            @RequestHeader Map<String, String> headers, @PathVariable String idBook) {

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
    @ApiResponse(
            responseCode = "500",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "Error interno del servidor interno.")
    public ResponseEntity<Book> patchBook(@PathVariable String idBook, @RequestBody String patchBody){
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
            responseCode = "400",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "Peticion erronea, id no encontrado")
    @ApiResponse(
            responseCode = "500",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "Error interno del servidor interno.")
    public ResponseEntity<ResponseDeleteDto> deleteBook(@PathVariable String idBook) {
        Boolean removed = service.removeBook(idBook);
        if (Boolean.TRUE.equals(removed)) {
            return ResponseEntity.ok(new ResponseDeleteDto("Registro eliminado correctamente"));
        } else {
            return ResponseEntity.badRequest().body(new ResponseDeleteDto("Peticion erronea, id no encontrado"));
        }
    } 

}
