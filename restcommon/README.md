# Skinny Rest

This library provides a minimal framework to support classic annotations 
for defining a REST service. It may not be as rich as what is supported by 
frameworks like Jersey, but it is a whole lot lighter in footprint and 
call depth with minimal dependencies, making it quicker to deploy 
applications based on it and much easier to debug those applications.

It provides basic facilities like:
- configuration
- monitoring (via Prometheus Exporter API)
- security
- unit and integration testing
while including a minimum set of libraries.
In that it is a little more prescriptive than flexible, but 
it does not lack in extensibility either.

In its goal towards *lightweight simplicity*, it makes 
available a full web application framework without 
relying on JEE, and instead using only JSE.
It also attempts to avoid classic libraries like 
Apache, Jakarta, and Google as much as possible, as 
these libraries have largely filled gaps in older 
versions of JSE which today are mostly closed anyway.

The main program is just an example and the user should 
write their own main program in their package and project.
The library is delivered as a fat jar thereby, like Spring Boot, 
makes it simple for the user to only import this single jar.

TODO
- Complete RestUtil
- Complete SkinnyRestSampleService and StoreResource
- Enhance SkinnyResource to handle remaining


