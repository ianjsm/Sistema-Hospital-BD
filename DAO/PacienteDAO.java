package DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import org.mindrot.jbcrypt.BCrypt;

import gui.util.Alerts;
import guiControllers.TelaLoginPacienteController;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableView;
import model.entities.Consulta;

public class PacienteDAO {

	public boolean cpfExiste(String cpfDigitado) {
		String url = "jdbc:mysql://localhost:3306/hospital?useSSL=false";
		String username = "root";
		String password = "86779791";

		String selectQuery = "SELECT * FROM pacientes WHERE cpf = ?";

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

			preparedStatement.setString(1, cpfDigitado);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.next();
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean realizarCadastroPaciente(String nome, LocalDate data_nascimento, String senha, String cpf, String plano) {

		String url = "jdbc:mysql://localhost:3306/hospital";
		String username = "root";
		String password = "86779791";

		if (cpfExiste(cpf) == true) {
			Alerts.showAlert("ERRO!", "CPF JÁ EXISTENTE!", "Tente novamente!", AlertType.ERROR);
			return false;
		}
		
		String senhaCriptografada = BCrypt.hashpw(senha, BCrypt.gensalt());
		
		String insertQuery = "INSERT INTO pacientes (cpf, nome, data_nascimento, plano, senha) VALUES (?, ?, ?, ?, ?)";

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

			preparedStatement.setString(1, cpf);
			preparedStatement.setString(2, nome);
			preparedStatement.setDate(3, java.sql.Date.valueOf(data_nascimento));
			preparedStatement.setString(4, plano);
			preparedStatement.setString(5, senhaCriptografada);

			int rowsAffected = preparedStatement.executeUpdate();

			if (rowsAffected > 0) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean fazerLogin(String cpf, String senha) {
	    String url = "jdbc:mysql://localhost:3306/hospital";
	    String username = "root";
	    String password = "86779791";

	    String selectQuery = "SELECT * FROM pacientes WHERE cpf = ?";

	    try (Connection connection = DriverManager.getConnection(url, username, password);
	         PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

	        preparedStatement.setString(1, cpf);

	        try (ResultSet resultSet = preparedStatement.executeQuery()) {
	            if (resultSet.next()) {
	                String senhaHashed = resultSet.getString("senha");
	                if (BCrypt.checkpw(senha, senhaHashed)) {
	                    TelaLoginPacienteController.cpfLogado = cpf;
	                    TelaLoginPacienteController.planoLogado = resultSet.getString("plano");
	                    return true;
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    Alerts.showAlert("Login inválido", null, "Tente novamente!", AlertType.ERROR);
	    return false;
	}

	public void editarDado(String campoSelecionado, String dadoNovo, LocalDate data_nascimento_nova) {
		String url = "jdbc:mysql://localhost:3306/hospital";
		String username = "root";
		String password = "86779791";

		if (campoSelecionado == "nome") {
			String updateQuery = "UPDATE pacientes SET nome = ? WHERE cpf = ?";
			try (Connection connection = DriverManager.getConnection(url, username, password);
					PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

				preparedStatement.setString(1, dadoNovo);
				preparedStatement.setString(2, TelaLoginPacienteController.getcpfLogado());

				int rowsAffected = preparedStatement.executeUpdate();

				if (rowsAffected > 0) {
					Alerts.showAlert("Sucesso!", "Edição realizada com sucesso!", "", AlertType.CONFIRMATION);
					// labelMensagem.setText("Edição realizada com sucesso!");
				} else {
					Alerts.showAlert("Erro!", "Erro ao editar dados!", "Tente novamente!", AlertType.ERROR);
					// labelMensagem.setText("Falha ao editar.");
				}

			} catch (SQLException e) {
				e.printStackTrace();
				// labelMensagem.setText("Erro ao editar.");
			}
			return;
		}

		else if (campoSelecionado == "data_nascimento") {
			String updateQuery = "UPDATE pacientes SET data_nascimento = ? WHERE cpf = ?";
			try (Connection connection = DriverManager.getConnection(url, username, password);
					PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

				java.sql.Date dataNascimentoSQL = java.sql.Date.valueOf(data_nascimento_nova);
				preparedStatement.setDate(1, dataNascimentoSQL);
				preparedStatement.setString(2, TelaLoginPacienteController.getcpfLogado());

				int rowsAffected = preparedStatement.executeUpdate();

				if (rowsAffected > 0) {
					Alerts.showAlert("Sucesso!", "Edição realizada com sucesso!", "", AlertType.CONFIRMATION);
					// labelMensagem.setText("Edição realizada com sucesso!");
				} else {
					Alerts.showAlert("Erro!", "Erro ao editar dados!", "Tente novamente!", AlertType.ERROR);
					// labelMensagem.setText("Falha ao editar.");
				}

			} catch (SQLException e) {
				e.printStackTrace();
				// labelMensagem.setText("Erro ao editar.");
			}
			return;
		} else if (campoSelecionado == "plano") {
			String updateQuery = "UPDATE pacientes SET plano = ? WHERE cpf = ?";
			try (Connection connection = DriverManager.getConnection(url, username, password);
					PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

				preparedStatement.setString(1, dadoNovo);
				preparedStatement.setString(2, TelaLoginPacienteController.getcpfLogado());

				int rowsAffected = preparedStatement.executeUpdate();

				if (rowsAffected > 0) {
					Alerts.showAlert("Sucesso!", "Edição realizada com sucesso!", "", AlertType.CONFIRMATION);
					// labelMensagem.setText("Edição realizada com sucesso!");
				} else {
					Alerts.showAlert("Erro!", "Erro ao editar dados!", "Tente novamente!", AlertType.ERROR);
					// labelMensagem.setText("Falha ao editar.");
				}

			} catch (SQLException e) {
				e.printStackTrace();
				// labelMensagem.setText("Erro ao editar.");
			}
			return;
		} else if (campoSelecionado == "senha") {
			String senhaCriptografada = BCrypt.hashpw(dadoNovo, BCrypt.gensalt());
			String updateQuery = "UPDATE pacientes SET senha = ? WHERE cpf = ?";
			try (Connection connection = DriverManager.getConnection(url, username, password);
					PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

				preparedStatement.setString(1, senhaCriptografada);
				preparedStatement.setString(2, TelaLoginPacienteController.getcpfLogado());

				int rowsAffected = preparedStatement.executeUpdate();

				if (rowsAffected > 0) {
					Alerts.showAlert("Sucesso!", "Edição realizada com sucesso!", "", AlertType.CONFIRMATION);
					// labelMensagem.setText("Edição realizada com sucesso!");
				} else {
					Alerts.showAlert("Erro!", "Erro ao editar dados!", "Tente novamente!", AlertType.ERROR);
					// labelMensagem.setText("Falha ao editar.");
				}

			} catch (SQLException e) {
				e.printStackTrace();
				// labelMensagem.setText("Erro ao editar.");
			}
			return;
		} else if (campoSelecionado == "cpf") {
			boolean cpfCadastrado = cpfExiste(dadoNovo);
			if (cpfCadastrado == true) {
				Alerts.showAlert("Erro!", "Esse CRM já está cadastrado", "", AlertType.ERROR);
				return;
			} else {
				String updateQuery = "UPDATE pacientes SET cpf = ? WHERE cpf = ?";
				try (Connection connection = DriverManager.getConnection(url, username, password);
						PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

					preparedStatement.setString(1, dadoNovo);
					preparedStatement.setString(2, TelaLoginPacienteController.getcpfLogado());

					int rowsAffected = preparedStatement.executeUpdate();

					if (rowsAffected > 0) {
						Alerts.showAlert("Sucesso!", "Edição realizada com sucesso!", "", AlertType.CONFIRMATION);
						// labelMensagem.setText("Edição realizada com sucesso!");
					} else {
						Alerts.showAlert("Erro!", "Erro ao editar dados!", "Tente novamente!", AlertType.ERROR);
						// labelMensagem.setText("Falha ao editar.");
					}

				} catch (SQLException e) {
					e.printStackTrace();
					// labelMensagem.setText("Erro ao editar.");
				}
				return;
			}
		} else {
			Alerts.showAlert("Erro!", "Erro ao editar dados!", "Tente novamente!", AlertType.ERROR);
		}
	}

	public void cliqueBotaoGerarRelatorioConsultasRealizadas(Date inicio, Date fim, ObservableList<Consulta> lista,
			TableView<Consulta> tableViewRelatorio) {
		String url = "jdbc:mysql://localhost:3306/hospital";
		String username = "root";
		String password = "86779791";

		String selectQuery = "SELECT * FROM consultas WHERE cpf_paciente = ? AND dataConsulta BETWEEN ? AND ? ";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

			preparedStatement.setString(1, TelaLoginPacienteController.getcpfLogado());
			preparedStatement.setDate(2, inicio);
			preparedStatement.setDate(3, fim);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					int id = resultSet.getInt("id");
					String crm = resultSet.getString("crm_Medico");
					Date data = resultSet.getDate("dataConsulta");
					Consulta consulta = new Consulta(id, crm, null, data);
					lista.add(consulta);
				}
				tableViewRelatorio.setItems(lista);
			} catch (SQLException e) {
				e.getMessage();
			}

		} catch (SQLException e) {
			e.getMessage();
		}
	}

	public void cliqueBotaoGerarRelatorioAgendamentos(Date inicio, Date fim, ObservableList<Consulta> lista,
			TableView<Consulta> tableViewRelatorio) {
		String url = "jdbc:mysql://localhost:3306/hospital";
		String username = "root";
		String password = "86779791";

		String selectQuery = "SELECT id, crm_medico, data_consulta FROM agendamentos WHERE cpf_paciente = ? AND data_consulta BETWEEN ? AND ? ";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

			preparedStatement.setString(1, TelaLoginPacienteController.getcpfLogado());
			preparedStatement.setDate(2, inicio);
			preparedStatement.setDate(3, fim);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					int id = resultSet.getInt("id");
					String crm = resultSet.getString("crm_Medico");
					Date data = resultSet.getDate("data_consulta");
					Consulta consulta = new Consulta(id, crm, null, data);
					lista.add(consulta);
				}
				tableViewRelatorio.setItems(lista);
			} catch (SQLException e) {
				e.getMessage();
			}
		} catch (SQLException e) {
			e.getMessage();
		}
	}

	
	//FALTA FAZER FUNCIONAR
	/*
	public void procurarMedicosPorNome(String dado, ListView<String> listViewResultados, ChoiceBox<String> choiceBoxEscolha) {
		String selectQuery = null;

		String url = "jdbc:mysql://localhost:3306/hospital?useSSL=false";
		String username = "root";
		String password = "86779791";

		Connection connection = null;
		try {
			if (TelaLoginPacienteController.getPlanoLogado() == null) {
				System.out.println("NULO");
				selectQuery = "SELECT crm_Medico, AVG(estrelas) AS notaMedia, GROUP_CONCAT(textoAvaliacao) AS comentarios FROM consultas GROUP BY crm_Medico";

				connection = DriverManager.getConnection(url, username, password);

				Map<String, Double> notaMediaMap = new HashMap<>();
				Map<String, List<String>> comentariosMap = new HashMap<>();

				try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
						ResultSet resultSet = preparedStatement.executeQuery()) {

					while (resultSet.next()) {
						String crmMedico = resultSet.getString("crm_Medico");
						double notaMedia = resultSet.getDouble("notaMedia");
						String comentario = resultSet.getString("textoAvaliacao");

						List<String> listaComentarios = comentariosMap.computeIfAbsent(crmMedico,
								k -> new ArrayList<>());
						listaComentarios.add(comentario);

						notaMediaMap.put(crmMedico, notaMedia);
					}
				}

				String selectDetailsQuery = "SELECT * FROM medicoscadastrados WHERE nome = ?";
				try (PreparedStatement preparedStatementDetails = connection.prepareStatement(selectDetailsQuery)) {
					preparedStatementDetails.setString(1, dado);

					try (ResultSet resultSetDetails = preparedStatementDetails.executeQuery()) {
						List<String> listaMedicos = new ArrayList<>();
						while (resultSetDetails.next()) {
							String crm = resultSetDetails.getString("crm");
							String nomeMedico = resultSetDetails.getString("nome");
							String especialidadeMedico = resultSetDetails.getString("especialidade");

							double notaMedia = notaMediaMap.getOrDefault(crm, 0.0);
							List<String> listaComentarios = comentariosMap.getOrDefault(crm, new ArrayList<>());

							String StringNotaMedia = String.format("%.1f", notaMedia);
							String nomeEspecialidade = "Nome: " + nomeMedico + " - " + "Especialidade: "
									+ especialidadeMedico + " - " + "Nota média: " + StringNotaMedia + " - "
									+ "Comentários: " + String.join(", ", listaComentarios);

							listaMedicos.add(nomeEspecialidade);
						}
						listViewResultados.getItems().setAll(listaMedicos);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (choiceBoxEscolha.getValue().equals("Nome")) {
				selectQuery = "SELECT crm_Medico, AVG(estrelas) AS notaMedia, GROUP_CONCAT(textoAvaliacao) AS comentarios FROM consultas GROUP BY crm_Medico";

				connection = DriverManager.getConnection(url, username, password);

				Map<String, Double> notaMediaMap = new HashMap<>();
				Map<String, List<String>> comentariosMap1 = new HashMap<>();

				try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
						ResultSet resultSet = preparedStatement.executeQuery()) {

					while (resultSet.next()) {
						String crmMedico = resultSet.getString("crm_Medico");
						double notaMedia = resultSet.getDouble("notaMedia");
						String comentario = resultSet.getString("textoAvaliacao");
						List<String> listaComentarios1 = comentariosMap1.computeIfAbsent(crmMedico,
								k -> new ArrayList<>());
						listaComentarios1.add(comentario);

						notaMediaMap.put(crmMedico, notaMedia);
					}
				}

				String selectDetailsQuery = "SELECT * FROM medicoscadastrados WHERE plano_atendido = ? AND nome = ? ";
				try (PreparedStatement preparedStatementDetails = connection.prepareStatement(selectDetailsQuery)) {
					preparedStatementDetails.setString(1, TelaLoginPacienteController.getPlanoLogado());
					preparedStatementDetails.setString(2, dado);

					try (ResultSet resultSetDetails = preparedStatementDetails.executeQuery()) {
						List<String> listaMedicos = new ArrayList<>();
						while (resultSetDetails.next()) {
							String crm = resultSetDetails.getString("crm");
							String nomeMedico = resultSetDetails.getString("nome");
							String especialidadeMedico = resultSetDetails.getString("especialidade");

							double notaMedia = notaMediaMap.getOrDefault(crm, 0.0);
							List<String> listaComentarios1 = comentariosMap1.getOrDefault(crm, new ArrayList<>());
							String StringNotaMedia = String.format("%.1f", notaMedia);
							String nomeEspecialidade = "Nome: " + nomeMedico + " - " + "Especialidade: "
									+ especialidadeMedico + " - " + "Nota média: " + StringNotaMedia + " - "
									+ "Comentários: " + String.join(", ", listaComentarios1);

							listaMedicos.add(nomeEspecialidade);
						}
						listViewResultados.getItems().setAll(listaMedicos);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (choiceBoxEscolha.getValue().equals("Especialidade")) {
				selectQuery = "SELECT crm_Medico, AVG(estrelas) AS notaMedia, GROUP_CONCAT(textoAvaliacao) AS comentarios FROM consultas GROUP BY crm_Medico";

				connection = DriverManager.getConnection(url, username, password);

				Map<String, Double> notaMediaMap = new HashMap<>();
				Map<String, List<String>> comentariosMap2 = new HashMap<>();

				try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
						ResultSet resultSet = preparedStatement.executeQuery()) {

					while (resultSet.next()) {
						String crmMedico = resultSet.getString("crm_Medico");
						double notaMedia = resultSet.getDouble("notaMedia");
						String comentario = resultSet.getString("textoAvaliacao");
						List<String> listaComentarios2 = comentariosMap2.computeIfAbsent(crmMedico,
								k -> new ArrayList<>());
						listaComentarios2.add(comentario);

						notaMediaMap.put(crmMedico, notaMedia);
					}
				}

				String selectDetailsQuery = "SELECT * FROM medicoscadastrados WHERE plano_atendido = ? AND especialidade = ? ";
				try (PreparedStatement preparedStatementDetails = connection.prepareStatement(selectDetailsQuery)) {
					preparedStatementDetails.setString(1, TelaLoginPacienteController.getPlanoLogado());
					preparedStatementDetails.setString(2, dado);

					try (ResultSet resultSetDetails = preparedStatementDetails.executeQuery()) {
						List<String> listaMedicos = new ArrayList<>();
						while (resultSetDetails.next()) {
							String crm = resultSetDetails.getString("crm");
							String nomeMedico = resultSetDetails.getString("nome");
							String especialidadeMedico = resultSetDetails.getString("especialidade");

							double notaMedia = notaMediaMap.getOrDefault(crm, 0.0);
							List<String> listaComentarios2 = comentariosMap2.getOrDefault(crm, new ArrayList<>());
							String StringNotaMedia = String.format("%.1f", notaMedia);
							String nomeEspecialidade = "Nome: " + nomeMedico + " - " + "Especialidade: "
									+ especialidadeMedico + " - " + "Nota média: " + StringNotaMedia + " - "
									+ "Comentários: " + String.join(", ", listaComentarios2);

							listaMedicos.add(nomeEspecialidade);
						}
						listViewResultados.getItems().setAll(listaMedicos);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/*
	public void procurarMedicosPorEspecialidade(String dado, ListView<String> listViewResultados, ChoiceBox<String> choiceBoxEscolha) {
		String selectQuery = null;

		String url = "jdbc:mysql://localhost:3306/hospital?useSSL=false";
		String username = "root";
		String password = "86779791";

		Connection connection = null;
		
		selectQuery = "SELECT crm_Medico, AVG(estrelas) AS notaMedia, GROUP_CONCAT(textoAvaliacao) AS comentarios FROM consultas GROUP BY crm_Medico";

		connection = DriverManager.getConnection(url, username, password);

		Map<String, Double> notaMediaMap = new HashMap<>();
		Map<String, List<String>> comentariosMap2 = new HashMap<>();

		try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
				ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String crmMedico = resultSet.getString("crm_Medico");
				double notaMedia = resultSet.getDouble("notaMedia");
				String comentario = resultSet.getString("textoAvaliacao");
				List<String> listaComentarios2 = comentariosMap2.computeIfAbsent(crmMedico,
						k -> new ArrayList<>());
				listaComentarios2.add(comentario);

				notaMediaMap.put(crmMedico, notaMedia);
			}
		}

		String selectDetailsQuery = "SELECT * FROM medicoscadastrados WHERE plano_atendido = ? AND especialidade = ? ";
		try (PreparedStatement preparedStatementDetails = connection.prepareStatement(selectDetailsQuery)) {
			preparedStatementDetails.setString(1, TelaLoginPacienteController.getPlanoLogado());
			preparedStatementDetails.setString(2, dado);

			try (ResultSet resultSetDetails = preparedStatementDetails.executeQuery()) {
				List<String> listaMedicos = new ArrayList<>();
				while (resultSetDetails.next()) {
					String crm = resultSetDetails.getString("crm");
					String nomeMedico = resultSetDetails.getString("nome");
					String especialidadeMedico = resultSetDetails.getString("especialidade");

					double notaMedia = notaMediaMap.getOrDefault(crm, 0.0);
					List<String> listaComentarios2 = comentariosMap2.getOrDefault(crm, new ArrayList<>());
					String StringNotaMedia = String.format("%.1f", notaMedia);
					String nomeEspecialidade = "Nome: " + nomeMedico + " - " + "Especialidade: "
							+ especialidadeMedico + " - " + "Nota média: " + StringNotaMedia + " - "
							+ "Comentários: " + String.join(", ", listaComentarios2);

					listaMedicos.add(nomeEspecialidade);
				}
				listViewResultados.getItems().setAll(listaMedicos);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	*/
}