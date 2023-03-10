# Typescript Client SDK Generator for Kotlin Spring APIs 

> This library uses kotlin reflection to build a Client SDK from a Spring Web API, helping the client to be keep
> up to date and catch typing errors before runtime. It also helps client-side developers to move faster
> if using a Typescript LSP in your IDE due to fields suggestions.

### On the Server (Kotlin)

* Define the handler response type

```kotlin
class HandlerResponse<S, E> private constructor(
    val data: S?,
    val error: E?
)

typealias HandlerResponseType<S, E> = ResponseEntity<HandlerResponse<S, E>>
```

* Define the handlers

```kotlin
@GetMapping("/api/users/{id}")
fun get(
    @PathVariable id: String
): HandlerResponseType<User, MyErrorFormat>
// You can use your own data type here. Ex: ResponseEntity<User>. See the example under `src/main/kotlin/c`
{
    val uuid = UUID.fromString(id)
        ?: return HandlerResponse.error(400, GlobalErrors.INVALID_UUID /* this is of type MyErrorFormat (ERROR) */)

    val user: User = USERS_DB[uuid]
        ?: return HandlerResponse.error(404, UserErrors.USER_NOT_FOUND_ERROR)

    return HandlerResponse.success(user) // this is of type User (SUCCESS)
}
```

* Call the generator function when you want to generate the SDK

```kotlin
@Component
class TypescriptSDKGenerator(requestMappingHandlerMapping: RequestMappingInfoHandlerMapping) {
    // * Remove when used as jar library
    init {
        generateTypescriptSDKFromSpringHandlers(
            // Needed to extract handlers metadata using reflection
            requestMappingHandlerMapping,
            // Directory where the typescript files will be located 
            buildDirectory = "./ts-client-example/api-sdk"
        )
    }
} 
```

### On the Client (Typescript)

* Type definition of a server response

```typescript
export type ServerResponse<S, E> = SuccessResponse<S> | ErrorResponse<E>;

type SuccessResponse<S> = {
  error: null;
  data: S;
};

type ErrorResponse<E> = {
  data: null;
  error: E;
};
```

* Using the SDK with type-safety and auto-complete

```typescript
import BuildSDK from "./api-sdk/sdk";
import { User, ErrorFormat } from "./api-sdk/UserTypes";

const sdk = BuildSDK("http://localhost:8080");

// You can pass query parameters, path variables and body 
const getUserResponse = await sdk.users.get({ id: userId });

if (getUserResponse.error) {
    const err: MyErrorType = getUserResponse.error
    // ... handle error...
   return;
}

const user: User = getUserResponse.data;
```

### Current Limitations

* **No** support for data classes with `generics` currently

* **Can't** use recursive types (ex: `Throwable(cause: Throwable?)`)
