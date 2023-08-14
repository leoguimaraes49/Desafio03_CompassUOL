
# API Post Collector

Este projeto foi desenvolvido para coletar posts de uma API externa e armazená-los em um banco de dados H2. Ele permite a criação, manipulação e consulta dos posts coletados.

## 🚀 Tecnologias

- **Backend**:
  - Java com Spring Boot
  - Spring Data JPA
  - Spring Web
  
- **Banco de Dados**:
  - H2-console

## 📌 Descrição

### Sobre o projeto
O objetivo principal deste projeto é fornecer uma maneira eficiente e automatizada de coletar e armazenar posts de uma API externa para consultas futuras.

### Entidade Post
A tabela a seguir mostra a estrutura da entidade Post:

| Parâmetro         | Tipo    | Descrição                                  |
|-------------------|---------|--------------------------------------------|
| id                | int     | Identificador único do post                |
| title             | string  | Título do post                             |
| content           | string  | Conteúdo do post                           |
| publishDate       | date    | Data de publicação do post                 |
| postHistories     | list    | Histórico de estados do post               |

## 🛠️ Instalação e Execução

1. Clone o repositório para sua máquina.
2. Abra o projeto em sua IDE preferida.
3. Execute o projeto usando o comando específico de sua IDE ou via linha de comando com `mvn spring-boot:run`.
4. O servidor será iniciado na porta 8080.

## 🔍 Endpoints

Após executar o projeto, os seguintes endpoints estarão disponíveis:

- **GET** `http://localhost:8080/api/posts`: Lista todos os posts.
- **GET** `http://localhost:8080/api/posts/{id}`: Retorna detalhes de um post específico.
- **POST** `http://localhost:8080/api/posts`: Adiciona um novo post.
- **PUT** `http://localhost:8080/api/posts/{id}`: Atualiza um post existente.
- **DELETE** `http://localhost:8080/api/posts/{id}`: Deleta um post específico.

## ⚙️ Configurações

Para acessar o banco de dados H2 em memória, conecte-se ao H2-Console pelo endereço:

```
http://localhost:8080/h2-console
```

## 🧪 Testes

- **Classe PostServiceTests**: Responsável por testar a funcionalidade do `PostService`.
- **Classe PostControllerIntegrationTest**: Contém testes de integração para o `PostController`.

Para executar os testes, é necessário ter o framework de teste JUnit 5 configurado no projeto.


