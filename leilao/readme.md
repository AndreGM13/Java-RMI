# Sistema de Leilão Distribuído com Java RMI

Este projeto implementa um sistema de leilão concorrente e distribuído usando **Java RMI (Remote Method Invocation)**, combinando padrões de **Requisição/Resposta** (para envio de lances e consultas) e **Notificação Push** (para propagação assíncrona de novos lances e fim do leilão).

## Requisitos Atendidos
- **Topologia P2P / Distribuída:** 1 Servidor Leiloeiro e múltiplos Participantes executando em processos (ou máquinas) distintas.
- **Consulta de Estado:** Participantes podem consultar o item, maior lance atual, autor e tempo restante.
- **Regras de Lance:** O servidor valida se o lance é estritamente maior que o atual e retorna feedback imediato (Aceito/Recusado).
- **Notificações Push:** Todos os clientes conectados recebem alertas imediatos via callbacks remotos sempre que um novo lance maior é efetuado.
- **Encerramento Automático:** Após o tempo configurado, novos lances são bloqueados e o vencedor é anunciado simultaneamente a todos.

---

## Formato de Mensagens (Serialização RMI)

Embora o Java RMI gerencie a serialização de forma transparente em nível binário (via a interface `java.io.Serializable`), o contrato de dados transmitido entre os processos segue o formato lógico estruturado abaixo (documentado em formato **JSON** para fins de interoperabilidade e especificação):

### 1. Mensagem de Estado do Item (`ItemLeilao`)
Enviada nas requisições de consulta (`consultarEstado`).
```json
{
  "descricao": "Notebook Gamer",
  "lanceMinimo": 1000.00,
  "maiorLance": 1250.00,
  "maiorLicitante": "Participante_A",
  "tempoEncerramentoEpoch": 1718042400000
}
