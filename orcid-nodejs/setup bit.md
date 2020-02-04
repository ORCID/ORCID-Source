# Bit Setup

Orcid shares angular components between projects using [bit.dev](https://bit.dev), a list of these components can be found here [https://bit.dev/orcid](https://bit.dev/orcid). Because of this before being able to install the npm dependencies please follow these steps


1- Create a [bit account](https://bit.dev/)

2- Install bit on your environment 

```
npm install bit-bin --global
## or using yarn
yarn global add bit-bin 
```

3- Logging into your bit account 

```
bit login
```

Now you are ready to install all npm dependencies.

For more information about bit please [refer to the documentation](https://docs.bit.dev/docs/quick-start)
