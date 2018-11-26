# sedimenter

sedimenter is a zero-configuration plug & play source map exception deobfuscator service written with Spring Boot in Kotlin

## Usage

A typical use case for JavaScript is to [globally catch exceptions](https://developer.mozilla.org/en-US/docs/Web/API/GlobalEventHandlers/onerror)
 and then do a request with your preferred client-server communication framework.
 
Start up the service and if you want logging capability, forward the JSON output to your favorite backend log collection framework. 
If your source maps are very large or your network is slow, you can extends the timeout (for both requests) by setting the `SEDIMENTER_TIMEOUT` env (in ms, 
default is 40 000).  

### REST

```
POST /[deobfuscate|log]
{
    "message": "string",
    "source": "string",
    "lineno": number,
    "colno": number,
    "error": "string",
    // optional:
    "additionalFields": {
        "string": "string", ...
    }
}
```

`deobfuscate` will return exactly the same data format as put in. Both endpoints return status 200 on success.

## Features

- Just get back a deobfuscated exception or log directly in JSON format
- Endpoints for REST
- Detection of surce maps in referenced files via end of file comment
- Resilient requesting done with Retry and Hystrix
- Caching with Caffeine 

## Todo-List

- Proper testing
- Endpoints for GraphQL and gRPC
- Authentication for endpoints

