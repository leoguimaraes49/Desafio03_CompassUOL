# API de coleta de posts

Projeto de estudo em Java e Spring Boot que busca posts e comentários na API pública JSONPlaceholder e os armazena em um banco H2 em memória. O código também apresenta rotas para alterar estados de posts e uma rotina periódica de coleta.

> Estado do projeto: exercício de back-end. A documentação abaixo descreve o código existente; o build e o funcionamento das rotas não foram comprovados nesta revisão. Há limitações importantes na configuração e na lógica que devem ser resolvidas antes de usar a aplicação como serviço funcional.

## Arquitetura e tecnologias

```text
Cliente HTTP → PostController → PostService → ExternalApiClient → JSONPlaceholder
                                     ↓
                              Spring Data JPA → H2 em memória
```

- Java 11 e Spring Boot 2.6.6, conforme `pom.xml`.
- Spring Web, Spring Data JPA, Spring Security, cache e H2.
- A aplicação habilita agendamento e cache. A rotina `scheduledFetchPosts` percorre os IDs 1 a 100 a cada hora; as chamadas externas de posts e comentários têm anotações de cache.
- As entidades incluem post, comentário e histórico de estados. Consulte as limitações abaixo antes de presumir que todas as transições e relacionamentos funcionem como pretendido.

## Rotas implementadas

O controlador está mapeado para `/posts` — não para `/api/posts`.

| Método e rota | Comportamento previsto pelo código |
| --- | --- |
| `GET /posts` | Consulta os posts pelo repositório. |
| `POST /posts/{postId}` | Solicita coleta de um post e seus comentários; aceita IDs de 1 a 100. |
| `DELETE /posts/{postId}` | Tenta alterar o estado de um post habilitado para desabilitado; aceita IDs de 1 a 100. Não remove o registro. |
| `PUT /posts/{postId}` | Tenta alterar o estado de um post habilitado ou desabilitado para `UPDATING`; aceita IDs de 1 a 100. A reconsulta ainda não está implementada. |

As rotas de alteração retornam `400` para IDs fora do intervalo. A mensagem de sucesso, porém, não garante que uma mudança tenha ocorrido: o processamento captura exceções, e as operações de desabilitar e reprocessar podem não encontrar um post elegível.

## Como tentar executar

Pré-requisitos: JDK 11, Maven e acesso à rede para dependências e à API externa. O projeto contém scripts `mvnw` e `mvnw.cmd`, mas a configuração `.mvn/wrapper` não está no repositório; por isso, use uma instalação funcional do Maven.

```bash
git clone https://github.com/leoguimaraes49/Desafio03_CompassUOL.git
cd Desafio03_CompassUOL
mvn spring-boot:run
```

O arquivo `application.properties` configura a porta `8080` e habilita o console H2 em `http://localhost:8080/h2-console`, com JDBC URL `jdbc:h2:mem:testdb`. O banco é temporário e o esquema está configurado como `create-drop`. Essas instruções dependem de resolver a configuração de build; não se afirma que a aplicação inicia sem ajustes.

## Testes e limitações

- O único teste encontrado em `src/test` é `contextLoads`; não há no repositório as classes `PostServiceTests` e `PostControllerIntegrationTest` citadas anteriormente. O comando convencional seria `mvn test`, sujeito às mesmas limitações de build.
- O `pom.xml` declara `spring-boot-starter-task` e `spring-boot-starter-scheduling` sem versões explícitas. A resolução dessas dependências precisa ser verificada e corrigida para uma construção reproduzível.
- O build e os testes não foram validados nesta revisão.
- Apesar de configurar HTTP Basic e credenciais locais, `SecurityConfig` permite todas as requisições e desabilita CSRF. Não exponha essa configuração como autenticação efetiva.
- O getter de estado de `Post` consulta o histórico, enquanto o serviço atribui diretamente o campo de estado. A coerência dessas transições e o relacionamento dos comentários precisam de testes e correções.
- A rotina de coleta chama uma API externa, e a persistência em H2 é volátil; falhas de rede e reinicialização afetam o resultado.

## Referências no repositório

- [Controlador](src/main/java/net/javaguides/springboot/controller/PostController.java)
- [Serviço](src/main/java/net/javaguides/springboot/service/impl/PostServiceImpl.java)
- [Cliente da API externa](src/main/java/net/javaguides/springboot/service/ExternalApiClient.java)
- [Configuração da aplicação](src/main/resources/application.properties)
