# captcha service

## TODO:
-1. Handle local, because for now captcha is like this "玁冧ἀ蓆"
0. Add logging and recovery on request crush
1. Add MNIST data 
    1. Loading and unpacking (.sh script)
    2. Pool of files in scala code
    3. Generator of clued images
2. Make /generate query work with images 
3. Add database
    1. Creation
    2. Connection
    3. Adding new instance
    4. Selecting and removing instance
4. Tests
5. v2 API
    1. Security (async)
    2. New endpoint 

## v1

Interaction with API looks like:
```
1. GET host/generate
2. GET host/check?answer=abc
```

## v2 

More secure API:
```
1. GET host/key.pub
2. GET host/generate?secret=123
3. GET host/check?answer=abc
4. decypt check response with key.pub and check if 
   it contains secret and what is result.
```