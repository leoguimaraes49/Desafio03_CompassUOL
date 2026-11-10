# API de coleta de posts

Projeto de estudo em Java e Spring Boot que consulta posts e comentários da API pública JSONPlaceholder e os armazena em um banco H2 em memória.

## Funcionamento

```text
Cliente HTTP → PostController → PostService → JSONPlaceholder
                                     ↓
                              Spring Data JPA → H2
```

A coleta mantém os IDs da API externa, associa os comentários ao post e define seu estado como `ENABLED`. É possível desabilitar um post ou reprocessá-lo: nesse caso, a aplicação consulta novamente a API e atualiza os dados. Uma tarefa percorre os IDs de 1 a 100 a cada hora, com a primeira execução após uma hora.

O histórico de estados está modelado no projeto, mas ainda não é preenchido pelo fluxo atual.

## Tecnologias

- Java 11 e Spring Boot 2.6.6
- Spring Web, Spring Data JPA e H2
- Spring Security em configuração de demonstração
- Maven e JUnit

## Execução

Pré-requisitos: JDK 11 ou compatível, Maven e acesso à rede para baixar dependências e consultar a API externa. Os scripts `mvnw` e `mvnw.cmd` estão no repositório, mas a configuração `.mvn/wrapper` não está disponível; use uma instalação funcional do Maven.

```bash
git clone https://github.com/leoguimaraes49/Desafio03_CompassUOL.git
cd Desafio03_CompassUOL
mvn spring-boot:run
```

A aplicação usa a porta `8080`. O console H2 fica em `http://localhost:8080/h2-console`, com JDBC URL `jdbc:h2:mem:testdb`, usuário `sa` e senha vazia. O banco é temporário e é recriado ao iniciar a aplicação.

## Rotas

Os IDs aceitos nas rotas individuais vão de 1 a 100.

| Método e rota | Ação |
| --- | --- |
| `GET /posts` | Lista os posts e seus comentários. |
| `POST /posts/{id}` | Coleta o post e os comentários da API externa. |
| `DELETE /posts/{id}` | Desabilita um post habilitado, sem removê-lo. |
| `PUT /posts/{id}` | Reconsulta um post habilitado ou desabilitado e seus comentários. |

Exemplo:

```bash
curl -X POST http://localhost:8080/posts/1
curl http://localhost:8080/posts
```

IDs fora do intervalo retornam `400`; um post ausente retorna `404` nas operações de desabilitar e reprocessar. Uma mudança de estado incompatível retorna `409`, e uma falha de comunicação com a API externa retorna `502`.

## Testes

```bash
mvn test
```

Os testes de integração exercitam coleta, persistência, resposta JSON, desativação, reprocessamento e erros. A API externa é simulada nos testes, que não precisam fazer chamadas ao JSONPlaceholder.

## Limitações

- A configuração atual permite todas as requisições e desabilita CSRF; as credenciais locais não representam autenticação efetiva. O projeto é uma demonstração e essa configuração não deve ser exposta publicamente.
- O H2 em memória perde os dados quando a aplicação é encerrada. A coleta depende de um serviço externo e pode falhar por indisponibilidade de rede.
- O histórico de estados é uma possibilidade de evolução; o fluxo atual persiste apenas o estado corrente.

## Referências no repositório

- [Controlador](src/main/java/net/javaguides/springboot/controller/PostController.java)
- [Serviço](src/main/java/net/javaguides/springboot/service/impl/PostServiceImpl.java)
- [Cliente da API externa](src/main/java/net/javaguides/springboot/service/ExternalApiClient.java)
- [Configuração da aplicação](src/main/resources/application.properties)
