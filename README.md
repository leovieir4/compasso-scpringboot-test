# Challenge-compasso-uol-product-ms

compassoms é uma api rest desenvolvida com base nas tecnologias Java e Springboot e com banco relacional MYSQL e com base nos princios de TDD (ulizando junit para unit tests), Clean Architecture, SOLID e seus design patterns. Criada para cumprir o [desafio](https://bitbucket.org/RecrutamentoDesafios/desafio-java-springboot/src/master/) da compassouol.

## Tech
#### compassoms usa:

* [Java]
* [Spring boot]
* [Junit]
* [Lombok]
* [Maven]
* [Swagger]
* [JPA] 

Código publicado no GitHub

## Endpoints:
  - POST /products/ : adiciona um novo produto ao banco de dados
  - PUT /products/ : busca um produto por id e o atualiza na base de de dados
  - DELETE /products/{id} : remove um produto do banco de dados
  - GET /products/{id} : busca um produto por id no banco de dados
  - GET /products/ : retorna uma lista com todos os produtos cadastrados no banco de dados
  - GET /products/search?min_price=10&max_price=40&q=PALAVRA_CHAVE : Recebe como parâmetro max_price, min_price e q. Realiza uma busca no banco de dados por todos os produtos que estão com o valor price entre min_price e max price e bate o valor de q contra os campos name e description e retornando uma lista de produtos filtrada por esses parâmetro.
 
## Documentação
Para saber mais sobre o funcionamento da API basta acessar a documentação pelo swagger no [LINK](https://compassoms.herokuapp.com/swagger-ui.html#/product-controller) e ver como funciona cada endpoint juntamente com seus parâmetro e respostas.

![image swagger](https://i.imgur.com/7eRfp3W.png)

### Instalação e Build

compassoms precisa [Java](https://www.java.com/pt-BR/download/) 8+ para rodar.
maven para o gerenciamento e download das dependencias
MYSQ databaseL.

 Antes de realizar o build da aplicação, caso estaja rodando ela localmente precisamos configurar a url de conexão com o banco, para isso basta acessar o arquivo application.propertir dentro do diretório /resources e substitur os parâmetros de acesso ao banco de dados (senha e usuário) para os da sua máquina e substituir a url de conexão com banco para seu banco local.
 
 Por padrão o projeto está configurado para apontar para um banco de dadeos externo que está hospedado no [cloudclusters](https://clients.cloudclusters.io)

Com essas dependências instaladas e as configurações realizadas, basta clonar o repositório do git, navegar até a pasta raiz da aplicação e rodar os seguintes comandos:

```sh
$ mvn clean install //limpa o diretório target com o comando clean e realiza o download das dependências com install, além disso realiza todos os testes unitarios.
$ mvn clean package spring-boot:run //executa a aplicação
```

### Tests:
A aplicação possui 12 testes unitários para validar o retorno dos endpoints (status e objetos) e seus parâmetros de entrada.
Para saber mais basta acessas a documentação java (javadoc) no diretório /doc e executar o arquivo index.html.

![javadoc](https://i.imgur.com/OckqkI9.png)
```sh
mvn clean install //executa os testes
```
Após a execução desse proccesso a API estara rodando no endereço: http://localhost:9999/products/

### Hospedagem heroku:

Para facilitar o processo de testes a API foi hospeda no heroku, caso queira pode testar as requisições apontando elas para a url base:
https://compassoms.herokuapp.com/products/

OBS: A versão utilizada é a free dyno, por isso caso a api fique muito tempo sem ser utilizada o heroku a desliga, por isso pode ser que a primeira requisição feita demore um pouco.

### Links externos:

Documentação swagger: https://compassoms.herokuapp.com/swagger-ui.html#/product-controller

API no Heroku: https://compassoms.herokuapp.com/products/


