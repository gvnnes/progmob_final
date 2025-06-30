# Trabalho Final - Programação para Dispositivos Móveis
## Grupo: Antonio Castro, Geovanne Castro, João Pedro da Cruz


## Visão Geral

Diário de Bolso é um aplicativo que permite aos usuários registrar momentos do seu dia com textos, fotos e localização. Ele funciona como um diário pessoal digital, oferecendo uma experiência intuitiva, segura e acessível.



## Alunos do Grupo

- Antonio G. Castro
- João Pedro da Cruz
- Geovanne S. de Castro

---

## Papéis de Usuário

- Usuário comum:
  - Cadastrar e acessar sua conta com foto e senha
  - Criar, visualizar, editar e apagar registros do diário
  - Anexar fotos a cada entrada
  - Registrar localização (opcional)
  - Receber notificações para registrar um novo momento

---

## Requisitos Funcionais

### Autenticação

- Cadastro de usuário com:
  - Nome
  - Foto de perfil
  - Senha (armazenada com hash)
- Login seguro
- Validação de campos obrigatórios

### Funcionalidades do Diário

- Criar nova entrada com:
  - Título
  - Texto do diário (opcional)
  - Foto(s) (opcional) (tirada com a câmera ou da galeria)
  - Localização (opcional)
  - Data e hora automáticas
- Editar e excluir entradas
- Visualizar entradas anteriores com pesquisa por data
- Receber lembretes diários para registrar uma nova entrada

---

## Recursos Utilizados

- Câmera para fotos nas entradas
- Localização geográfica
- Fragmentos e navegação entre telas
- Paleta de cores personalizada e imagens temáticas
- Notificações diárias
- Menu de navegação e alarmes
- Tratamento de erros e entradas inválidas
- Armazenamento de senhas com hash seguro
- Armazenamento local de dados

---

## Testes e Tratamento de Erros

- Testes de caixa preta
- Mensagens de erro para:
  - Campos obrigatórios não preenchidos
  - Usuário não cadastrado tentando logar
  - Tentativa de uso de texto em campos numéricos (onde aplicável)
  - Tentativa de criar entrada sem título ou texto
  - Falha ao acessar a câmera ou localização

---

## Acessibilidade e Usabilidade

- Uso de ícones intuitivos
- Texto alternativo para imagens
- Layout adaptado para acessibilidade motora e visual

---

## Segurança

- Senhas armazenadas com hash seguro (SHA-256)
- Controle de permissões do Android para câmera e localização
- Validação de autenticação para acesso ao conteúdo
- Dados armazenados localmente com acesso restrito

---
