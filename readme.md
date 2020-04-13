# captcha service

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