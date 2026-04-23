# SmartCampusAPI
Smart Campus REST API - JAX-RS Coursework Report

Part 1: Service Architecture & Setup

Answer for Question 1.1:

JAX-RS by default will generate a new instance of each resource class with each incoming request (request-scoped). This implies that instance variables within resource classes cannot be used to maintain shared data since each request is allocated a new object with no recollection of any past requests. All shared data structures have to be contained in a Singleton class in order to preserve persistent in-memory data between requests, in this implementation, the DataStore class. Moreover, as there can be several requests at the same time, the default HashMap and ArrayList are not thread-safe and may lead to race conditions. ConcurrentHashMap is therefore employed to make sure that concurrent reading and writing activities do not corrupt the shared data resulting in loss of data and consistency in all the requests.

Answer for Question 1.2:

The concept of HATEOAS (Hypermedia As The Engine Of Application State) is that API responses need to have links to other related actions and resources, and that clients can navigate the API dynamically without depending on fixed URLs or external documentation. This is highly beneficial to client developers since the API is automatically self-documenting - a client can begin with a single entry point and then find out what operations are available to it by following the links given in each response. Also, should server-side URLs evolve, clients that visit hypermedia links will automatically update themselves without needing software modifications, enhancing maintainability and decreasing the chance of integration failure when the URL structure evolves.

Part 2: Room Management

Answer for Question 2.1:

Sending back IDs only saves bandwidth, and cuts down the response payload size, but the clients are required to send one more HTTP request for each ID to get the details, which is known as the N+1 problem, and has an extreme impact on latency and server load. Full-object-returning removes the need to make subsequent requests and makes the processing on the client-side easier but thereby makes the payload size and bandwidth usage larger. The correct decision varies with circumstance. Returning lightweight summaries with pagination is the industry-standard way of doing it with large collections. In the case of small collections, full object return is more desirable since it will be easier to develop and since all required data will be instantly accessible in a single response.

Answer for Question 2.2:

Yes, the DELETE operation is idempotent here. Idempotency refers to the property of having the same server state when performing the same request multiple times. The room is deleted in the data store, and a 200 OK is sent back on the first DELETE request for a room. If there is any further identical DELETE request for the same room ID, the room does not exist anymore, thus the server sends a 404 Not Found response. The code of response varies, but the state of the server is the same after each call - the room does not exist. This meets the concept of idempotency, as stipulated by the REST architectural principles.

Part 3: Sensor Operations & Linking

Answer for Question 3.1:

The @Consumes(MediaType.APPLICATION_JSON) tells JAX-RS to only process incoming requests having the content-type header set to application/json. In the case that the request is not made using application/json but something else like text/plain or application/xml, the JAX-RS engine will be unable to find a matching MessageBodyReader to convert the incoming request to a Java representation of the same. As a result, the request will be automatically rejected by the framework, resulting in an HTTP status code 415 - “Unsupported Media Type” before invoking the resource method handler.

Answer for Question 3.2:

It is semantically accurate to use query parameters, such as GET /api/v1/sensors?type=CO2, for optional filtering since the primary resource that is being accessed in the request is still /sensors irrespective of the filter used. Using path parameters, for instance, /api/v1/sensors/type/CO2, will wrongly imply that CO2 is an entity on its own, which is against REST resource naming guidelines. Additionally, using query parameters provides more flexibility as multiple filters may be added to the same request without having to change the URL. For example, one could use ?type=CO2&status=ACTIVE.

Part 4: Deep Nesting with Sub - Resources

Answer for Question 4.1:

Sub-Resource Locator Pattern allows the delegation of the nested path processing to another specific class for a particular type of resource. Rather than specifying all possible endpoints, such as /sensors/{id}/readings and /sensors/{id}/readings/{rid}, in one massive controller, we separate concerns into different classes, where each class is responsible for its task. In our case, SensorResource will have methods for operations at the sensor level, whereas SensorReadingResource will handle only the reading history. It makes code much more maintainable since it follows the single responsibility principle, allowing each class to perform one job. Also, this pattern allows you to test your classes individually, improving testability.

Part 5: Advanced Error Handling, Exception Mapping & Logging

Answer for Question 5.1:

The 404 Not Found error message should be used when the endpoint or URL cannot be found by the server. As POST /api/v1/sensors can be accessed without problems, using a 404 status code would technically be inaccurate. Using HTTP 422 Unprocessable Entity would be semantically more correct, since it signals that the request was successfully processed but could not be fulfilled due to semantic errors in its content. The server received the request, understood it, accepted the content type, and verified that the JSON data is correctly structured, but could not execute it since it contains information about a room that does not exist.

Answer for Question 5.2:

Displaying unfiltered Java stack traces within API outputs can result in serious security implications. Java stack traces can show names of internal packages and classes, giving hackers insight into the layout of the program. Stack traces may also reveal names and version numbers of third-party plugins used, thus helping hackers find and exploit known CVEs in those particular versions. Server-side file pathnames can also be obtained through stack traces, which provide information about the directory tree on the hosting computer. Moreover, stack traces show logic flow paths taken, which is useful for hackers because it shows the exact paths in the code that have gone wrong and can help them target their attacks more precisely. 

Answer for Question 5.3:

The use of JAX-RS filters for handling cross-cutting concerns, such as logging, is in line with the principles of Separation of Concerns and DRY (Don't Repeat Yourself). In case Logger.info() invocations had to be included in each resource method, even the slightest change in the format or behavior of the logging would require modification of each and every method separately, which would not only be cumbersome but also prone to mistakes. All that is needed is to create one filter class that implements ContainerRequestFilter and ContainerResponseFilter interfaces, which will intercept all incoming requests and responses automatically without modifying any code of resources.

