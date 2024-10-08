# Sistema de Consultas Médicas

Este repositório contém a implementação do Sistema de Consultas Médicas. O sistema é destinado a facilitar a gestão de consultas médicas, permitindo que médicos e pacientes realizem operações específicas de forma eficiente. Foi utilizado no desenvolvimento do projeto um sistema de interface gráfica (JavaFX) e banco de dados (MySQL).

## Funcionalidades Implementadas

1. *Login de Médico e Paciente:*
   - Médicos e pacientes podem realizar login para acessar o sistema.

2. *Alteração de Dados:*
   - Apenas médicos podem alterar seus dados, e o mesmo se aplica aos usuários.

3. *Agendamento de Consulta:*
   - Pacientes podem agendar consultas selecionando um médico e uma data disponível.

4. *Cancelamento de Agendamento:*
   - Pacientes podem cancelar um agendamento. Se houver pacientes na lista de espera, o próximo da lista é alocado.

5. *Realização de Consulta:*
   - Médicos podem registrar detalhes da consulta, incluindo sintomas, tratamento sugerido, medicamentos, exames, etc.

6. *Relatórios:*
   - Exibição de relatórios com:
      - Todas as consultas e agendamentos de um médico em um determinado período.
      - Todas as consultas de um paciente em um determinado período.

7. *Lista de Espera:*
   - Se um médico estiver lotado em um dia, o paciente pode ser adicionado a uma lista de espera.

8. *Pagamento por Consulta:*
   - Se o paciente não tem plano de saúde, é gerada uma conta de pagamento de acordo com o médico e a especialidade.

9. *Avaliação de Médicos:*
    - Após a consulta, os pacientes podem avaliar os médicos, fornecendo um texto e atribuindo de 1 a 5 estrelas.
