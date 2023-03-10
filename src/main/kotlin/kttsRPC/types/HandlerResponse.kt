package kttsRPC.types

import kttsRPC.exampleAPI.utils.ErrorFormat
import org.springframework.http.ResponseEntity

// If the API has a global error format it's more readable/concise to do it like this
typealias HandlerResponseType<S> = ResponseEntity<HandlerResponse<S, ErrorFormat>>

class HandlerResponse<S, E> private constructor(
    val data: S?,
    val error: E?
) {
    companion object {
        fun <S, E> success(data: S, statusCode: Int = 200) =
            ResponseEntity
                .status(statusCode)
                .body(HandlerResponse<S, E>(data, null))


        fun <S, E> error(statusCode: Int, error: E) =
            ResponseEntity
                .status(statusCode)
                .body(HandlerResponse<S, E>(null, error))
    }
}