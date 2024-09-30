package DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import gui.util.Alerts;
import guiControllers.TelaLoginMedicoController;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import model.entities.Consulta;

public class MedicoDAO {

	public boolean verificarCrmExiste(String crmDigitado) {
		if (crmDigitado == null || crmDigitado.trim().isEmpty()) {
			System.out.println("CRM inválido: vazio ou nulo."); // Log para verificar CRM inválido
			return false;
		}

		String url = "jdbc:mysql://localhost:3306/hospital?useSSL=false";
		String username = "root";
		String password = "86779791";

		String selectQuery = "SELECT * FROM medicoscadastrados WHERE crm = ?";

		System.out.println("Verificando CRM: " + crmDigitado);

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

			preparedStatement.setString(1, crmDigitado);
			ResultSet resultSet = preparedStatement.executeQuery();

			boolean crmExiste = resultSet.next();
			System.out.println("CRM existe: " + crmExiste);
			return crmExiste;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean realizarCadastroMedico(String nome, String especialidade, String planoAtendido, String senha,
			String crm) {

		String url = "jdbc:mysql://localhost:3306/hospital";
		String username = "root";
		String password = "86779791";

		if (verificarCrmExiste(crm) == true) {
			Alerts.showAlert("ERRO!", "CRM JÁ CADASTRADO!", "Tente novamente!", AlertType.ERROR);
			return false;
		}

		String senhaCriptografada = BCrypt.hashpw(senha, BCrypt.gensalt());

		String insertQuery = "INSERT INTO medicoscadastrados (nome, especialidade, plano_atendido, senha, crm)"
				+ " VALUES (?, ?, ?, ?, ?)";

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

			preparedStatement.setString(1, nome);
			preparedStatement.setString(2, especialidade);
			preparedStatement.setString(3, planoAtendido);
			preparedStatement.setString(4, senhaCriptografada);
			preparedStatement.setString(5, crm);

			int rowsAffected = preparedStatement.executeUpdate();

			if (rowsAffected > 0) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException | NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean fazerLogin(String crm, String senha) {
		String url = "jdbc:mysql://localhost:3306/hospital";
		String username = "root";
		String password = "86779791";

		String selectQuery = "SELECT * FROM medicoscadastrados WHERE crm = ?";

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

			preparedStatement.setString(1, crm);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					String senhaCriptografada = resultSet.getString("senha");

					if (BCrypt.checkpw(senha, senhaCriptografada)) {
						TelaLoginMedicoController.crmLogado = crm;
						return true;
					} else {
						Alerts.showAlert("Login inválido", null, "Senha incorreta", AlertType.ERROR);
						return false;
					}
				} else {
					Alerts.showAlert("Login inválido", null, "Usuário não encontrado", AlertType.ERROR);
					return false;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Alerts.showAlert("Erro", null, "Erro ao realizar o login", AlertType.ERROR);
			return false;
		}
	}

	public void editarDado(String campoSelecionado, String dadoNovo) {
		String url = "jdbc:mysql://localhost:3306/hospital";
		String username = "root";
		String password = "86779791";

		if (campoSelecionado == "nome") {
			String updateQuery = "UPDATE medicoscadastrados SET nome = ? WHERE crm = ?";
			try (Connection connection = DriverManager.getConnection(url, username, password);
					PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

				preparedStatement.setString(1, dadoNovo);
				preparedStatement.setString(2, TelaLoginMedicoController.getcrmLogado());

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
		} else if (campoSelecionado == "especialidade") {
			String updateQuery = "UPDATE medicoscadastrados SET especialidade = ? WHERE crm = ?";
			try (Connection connection = DriverManager.getConnection(url, username, password);
					PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

				preparedStatement.setString(1, dadoNovo);
				preparedStatement.setString(2, TelaLoginMedicoController.getcrmLogado());

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
			String updateQuery = "UPDATE medicoscadastrados SET plano_atendido = ? WHERE crm = ?";
			try (Connection connection = DriverManager.getConnection(url, username, password);
					PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

				preparedStatement.setString(1, dadoNovo);
				preparedStatement.setString(2, TelaLoginMedicoController.getcrmLogado());

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
		} else if (campoSelecionado == "crm") {
			boolean crmExiste = verificarCrmExiste(dadoNovo);
			if (crmExiste == true) {
				Alerts.showAlert("Erro!", "Esse CRM já está cadastrado", "", AlertType.ERROR);
				return;
			} else {
				String updateQuery = "UPDATE medicoscadastrados SET crm = ? WHERE crm = ?";
				try (Connection connection = DriverManager.getConnection(url, username, password);
						PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

					preparedStatement.setString(1, dadoNovo);
					preparedStatement.setString(2, TelaLoginMedicoController.getcrmLogado());

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
			}
			return;
		} else if (campoSelecionado == "senha") {
			String senhaCriptografada = BCrypt.hashpw(dadoNovo, BCrypt.gensalt());
			String updateQuery = "UPDATE medicoscadastrados SET senha = ? WHERE crm = ?";
			try (Connection connection = DriverManager.getConnection(url, username, password);
					PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

				preparedStatement.setString(1, senhaCriptografada);
				preparedStatement.setString(2, TelaLoginMedicoController.getcrmLogado());

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
		} else {
			return;
		}
	}

	public void cliqueBotaoGerarRelatorioConsultasRealizadas(Date inicio, Date fim, ObservableList<Consulta> lista,
			TableView<Consulta> tableViewRelatorio) {
		String url = "jdbc:mysql://localhost:3306/hospital";
		String username = "root";
		String password = "86779791";

		String selectQuery = "SELECT * FROM consultas WHERE crm_Medico = ? AND dataConsulta BETWEEN ? AND ? ";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

			preparedStatement.setString(1, TelaLoginMedicoController.getcrmLogado());
			preparedStatement.setDate(2, inicio);
			preparedStatement.setDate(3, fim);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					int id = resultSet.getInt("id");
					String cpf = resultSet.getString("cpf_paciente");
					Date data = resultSet.getDate("dataConsulta");
					Consulta consulta = new Consulta(id, cpf, data);
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

		String selectQuery = "SELECT * FROM agendamentos WHERE crm_medico = ? AND data_consulta BETWEEN ? AND ? ";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

			preparedStatement.setString(1, TelaLoginMedicoController.getcrmLogado());
			preparedStatement.setDate(2, inicio);
			preparedStatement.setDate(3, fim);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					int id = resultSet.getInt("id");
					String cpf = resultSet.getString("cpf_paciente");
					Date data = resultSet.getDate("data_consulta");
					Consulta consulta = new Consulta(id, cpf, data);
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
}